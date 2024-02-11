package me.iantapply.dungeons.dungeons.utils.doors;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.developerkit.runnable.GKRunnable;
import me.iantapply.dungeons.developerkit.utils.duplet.Duplet;
import me.iantapply.dungeons.developerkit.utils.duplet.Tuple;
import me.iantapply.dungeons.dungeons.Dungeons;
import me.iantapply.dungeons.dungeons.utils.misc.CustomFallingBlock;
import me.iantapply.dungeons.developerkit.GKBase;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DoorListener extends GKListener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //If player right clicks a block
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //If player is clicking a COAL block
            Block clicked = e.getClickedBlock();

            XMaterial type;
            //Need this so that blocks like filled end portal frames don't error, it's weird XSeries thing
            try {

                type = XMaterial.matchXMaterial(
                    new ItemStack(clicked.getType(), 1, clicked.getData())
                );
            } catch (Exception ex) {
                type = XMaterial.matchXMaterial(
                        new ItemStack(clicked.getType())
                );
            }

            if(GKBase.checkEquals(type, XMaterial.COAL_BLOCK, XMaterial.RED_TERRACOTTA)) {
                openDoor(p, clicked);
            }
        }
    }

    //Velocity of which the door falls
    public static double doorSpeed = 0.1;

    //Check if door is valid
    public void openDoor(Player p, Block clicked) {
        Block center = null;
        for (int x = -2; x < 2; x++) {
            for (int y = -2; y < 2; y++) {
                for (int z = -2; z < 2; z++) {
                    Block relative = clicked.getRelative(x, y, z);
                    if (relative.getType().equals(XMaterial.END_PORTAL_FRAME.parseMaterial())) {
                        // Each end portal frame is the center of the door
                        // If it's found, use that as a base for spawning
                        // falling blocks

                        center = relative;
                    }
                }
            }
        }

        final ArrayList<Duplet<ArmorStand, CustomFallingBlock>> blocks = new ArrayList<>();

        //Start the animation
        if(center != null) {

            //This is the wrong sound, will need to do right ones
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 100, 1);

            //Message when opening a door, is the same for everyone
            p.getWorld().getPlayers().forEach(player ->
                    player.sendMessage("§7" + p.getName() + " §7opened a §8§lWITHER §7door!")
            );

            for (int x = -3; x < 3; x++) {
                for (int y = -3; y < 3; y++) {
                    for (int z = -3; z < 3; z++) {
                        Block relative = center.getRelative(x, y, z);
                        if (relative.getType() == XMaterial.COAL_BLOCK.parseMaterial() ||
                                relative.getType() == XMaterial.RED_TERRACOTTA.parseMaterial()
                        ) {
                            //Get the location that the entities will be spawned at
                            double entityX = relative.getX() + .5;
                            double entityY = relative.getY();
                            double entityZ = relative.getZ() + .5;

                            //Variable Location version
                            Location loc = new Location(p.getWorld(), entityX, entityY, entityZ);

                            //Spawn the falling block (it's custom because of spawning and falling block axis snapping)
                            CustomFallingBlock fB = new CustomFallingBlock(
                                    p.getWorld(),
                                    entityX,
                                    entityY,
                                    entityZ,
                                    relative
                            );
                            //To make sure it doesn't disappear
                            fB.ticksLived = -Integer.MAX_VALUE;

                            //I don't think I need this
                            fB.noclip = true;

                            //Calls method the does the normal nms spawning
                            fB.spawn();

                            //No NMS cuz it's not needed
                            ArmorStand aS = p.getWorld().spawn(loc.add(0, -1.5, 0), ArmorStand.class);
                            aS.setVisible(false);
                            //Sike, need it to allow the falling block to clip through the floor
                            EntityArmorStand cAS = ((CraftArmorStand) aS).getHandle();
                            cAS.noclip = true;
                            //Puts the falling block on the armorstand
                            aS.setPassenger(fB.getBukkitEntity());

                            //Now, the falling block should avoid collisions

                            blocks.add(Tuple.of(aS, fB));

                            relative.setType(XMaterial.AIR.parseMaterial());
                        }
                    }
                }
            }
            center.setType(XMaterial.AIR.parseMaterial());
        }

        new GKRunnable(() ->
                blocks.forEach(
                        b -> b.getFirst().setVelocity(new Vector(0, -doorSpeed, 0))
                ),
                20L * 3, //Time before cancel (I still need to push correct runnable code)
                //On cancel task, this'll run when the runnable gets cancelled
                () -> blocks.forEach(b -> {
                        b.getSecond().die();
                        b.getFirst().remove();
                })
        ).runTaskTimer(Dungeons.getMain(), 0L, 1L);

            /*BukkitTask bR = new BukkitRunnable() {
                @Override
                public void run() {
                    blocks.forEach(b -> b.getFirst().setVelocity(new Vector(0, -doorSpeed 0)));
                }
            }.runTaskTimer(Dungeons.getMain(), 0L, 1L);

            Bukkit.getScheduler().runTaskLater(Dungeons.getMain(), () -> {
                bR.cancel();
                blocks.forEach(b -> {
                    b.getSecond().die();
                    b.getFirst().remove();
                });
            }, 20L * 3);

             */
    }
}
