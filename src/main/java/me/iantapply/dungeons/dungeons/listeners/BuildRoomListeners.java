package me.iantapply.dungeons.dungeons.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.iantapply.dungeons.developerkit.item.RightClickItem;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.dungeons.Dungeons;
import me.iantapply.dungeons.dungeons.commands.BuildRoomCMD;
import me.iantapply.dungeons.dungeons.generation.SpecialType;
import me.iantapply.dungeons.dungeons.utils.worlds.BuildRoomWorld;
import me.iantapply.dungeons.developerkit.gui.GUIClickableItem;
import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import lombok.SneakyThrows;
import me.iantapply.dungeons.developerkit.GKBase;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildRoomListeners extends GKListener {

    public static HashMap<World, SpecialType> specialTypes = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getWorld().getName().startsWith("buildroom_")) {
            BuildRoomWorld bRW = BuildRoomWorld.getWorld(e.getTo().getWorld());
            if (e.getTo().getBlockX() < -1 || e.getTo().getBlockZ() < -1 ||
                    e.getTo().getBlockX() > bRW.getRoomSizeX() + 6 || e.getTo().getBlockZ() > bRW.getRoomSizeZ() + 6) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (e.getBlock().getWorld().getName().startsWith("buildroom_")) {
            BuildRoomWorld bRW = BuildRoomWorld.getWorld(e.getBlock().getWorld());
            if (e.getBlock().getX() < 0 || e.getBlock().getZ() < 0 ||
                    e.getBlock().getX() > bRW.getRoomSizeX() + 5 || e.getBlock().getZ() > bRW.getRoomSizeZ() + 5) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        try {
            if (e.getPlayer().getWorld().getName().startsWith("buildroom_") &&
                    !e.getPlayer().getWorld().getName().startsWith("buildroom_") &&
                    !BuildRoomCMD.lastLocationMap.containsKey(e.getPlayer())) {
                Player p = e.getPlayer();
                BuildRoomCMD.lastLocationMap.put(p, p.getLocation());
                BuildRoomCMD.inventoryHashMap.put(p, p.getInventory().getContents());

                //p.teleport(new Location(e.getTo().getWorld(), 0.5, 4, 0.5));
                p.getInventory().clear();
                p.getInventory().setItem(8, new ItemBuilder(XMaterial.NETHER_STAR)
                        .setName("§aRoom Menu")
                        .setLore("§7Click to open the room menu")
                        .setString("ability", "buildRoomMenu", "ExtraAttributes")
                        .build()
                );
            }
            World from = e.getFrom();
            if (from.getName().startsWith("test_")) {
                handlePlayerExit(e.getPlayer(), from);
            }
        } catch (NullPointerException ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getInventory() == e.getWhoClicked().getInventory()) {
            if(e.getWhoClicked().getWorld().getName().startsWith("buildroom_")) {
                if (e.getWhoClicked().getInventory().getItem(8) == null ||
                        !e.getWhoClicked().getInventory().getItem(8).hasItemMeta() ||
                        !e.getWhoClicked().getInventory().getItem(8).getItemMeta().hasDisplayName() ||
                        !e.getWhoClicked().getInventory().getItem(8).getItemMeta().getDisplayName()
                                .equals("§aRoom Menu")) {
                    e.getWhoClicked().getInventory().setItem(8, buildStar());
                    for (int i = 0; i < e.getWhoClicked().getInventory().getContents().length; i++) {
                        if (i == 8) continue;
                        if (e.getWhoClicked().getInventory().getItem(i) != null &&
                                e.getWhoClicked().getInventory().getItem(i).getType() != XMaterial.AIR.parseMaterial() &&
                                e.getWhoClicked().getInventory().getItem(i).getItemMeta().hasDisplayName() &&
                                e.getWhoClicked().getInventory().getItem(i)
                                        .getItemMeta().getDisplayName().equals("§aRoom Menu")) {
                            e.getWhoClicked().getInventory().setItem(i, null);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null &&
                e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§aRoom Menu")) {
            e.setCancelled(true);
        }
    }

    static ArrayList<Player> chatInputList = new ArrayList<>();

    @SneakyThrows
    @EventHandler
    public void onMessageSend(PlayerChatEvent e) {
        Player p = e.getPlayer();
        if(chatInputList.contains(p)) {
            e.setCancelled(true);

            World world = p.getWorld();
            if(e.getMessage().equals("CANCEL")) {
                chatInputList.remove(p);
                p.sendMessage(ChatColor.RED + "Cancelled!");
                p.openInventory(getInventory(p));
                return;
            }

            String message = e.getMessage();
            File[] dir = new File("plugins/Dungeons/samples/").listFiles();
            if(dir != null && dir.length != 0) {
                for (File folders : dir) {
                    if(folders.isDirectory() && folders.listFiles() != null) {
                        for(File files : folders.listFiles()) {
                            if (files.getName().equals(message + ".schematic")) {
                                p.sendMessage(ChatColor.RED +
                                        "Room with this name already exists! Type another name!");
                                return;
                            }
                        }
                    }
                }
            }

            int roomSizeX = BuildRoomWorld.getWorld(world).getRoomSizeX();
            int roomSizeZ = BuildRoomWorld.getWorld(world).getRoomSizeZ();

            BuildRoomWorld bRW = BuildRoomWorld.getWorld(world);
            File file = new File("plugins/Dungeons/samples/" +
                    bRW.getTypeSave() + "/" + message + ".schematic");

            com.sk89q.worldedit.world.World worldEditWorld = new BukkitWorld(world);
            Vector pos1 = new Vector(3, 6, 3);
            Vector pos2 = new Vector(roomSizeX + 2, 255, roomSizeZ + 2);
            CuboidRegion region = new CuboidRegion(
                    worldEditWorld, pos1, pos2
            );
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            EditSession editSession = Dungeons.getWorldEdit().createEditSession(p);

            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());

            Operations.complete(copy);
            try (ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(new FileOutputStream(file))) {
                writer.write(clipboard, worldEditWorld.getWorldData());
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            chatInputList.remove(p);
            p.sendMessage(ChatColor.GREEN + "Room saved!");

            int y = bRW.getDoorY();
            File f = new File("plugins/Dungeons/data.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            yaml.set("room." + message + ".yLevel", y);
            yaml.set("room." + message + ".type", bRW.getTypeSave());
            yaml.set("room." + message + ".baseSizeX", roomSizeX);
            yaml.set("room." + message + ".baseSizeZ", roomSizeZ);
            if(bRW.getSpawnX() != -Integer.MAX_VALUE) {
                yaml.set("room." + message + ".spawnX", bRW.getSpawnX());
                yaml.set("room." + message + ".spawnY", bRW.getSpawnY());
                yaml.set("room." + message + ".spawnZ", bRW.getSpawnZ());
            }
            yaml.save(f);
            exitWorld(p, world);
        }
    }

    public static ItemStack buildStar() {
        return new RightClickItem(new ItemBuilder(XMaterial.NETHER_STAR)
                .setName("§aRoom Menu")
                .setLore("§7Click to open the room menu")
                .build()
        ) {
            @Override
            public void run(PlayerInteractEvent e) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Player p = e.getPlayer();
                    p.openInventory(BuildRoomListeners.getInventory(p));
                }
            }
        }.getItem();
    }

    public static void exitWorld(Player p, World world) {
        List<World> worlds = Bukkit.getWorlds();
        worlds.removeIf(w -> w.getName().startsWith("test_"));
        p.teleport(BuildRoomCMD.lastLocationMap.getOrDefault(p, worlds.get(0).getSpawnLocation()));

        handlePlayerExit(p, world);
    }

    @SneakyThrows
    public static void handlePlayerExit(Player p, World world) {
        p.getInventory().setContents(
                BuildRoomCMD.inventoryHashMap.getOrDefault(p, p.getInventory().getContents())
        );

        BuildRoomCMD.lastLocationMap.remove(p);
        BuildRoomCMD.inventoryHashMap.remove(p);
        BuildRoomWorld.removeWorld(world);

        List<Player> players = world.getPlayers();
        players.removeIf(player -> player.equals(p));
        if (players.isEmpty()) {
            Bukkit.getServer().unloadWorld(world, false);
            File dir = new File(world.getName());
            if (dir.exists()) {
                FileUtils.deleteDirectory(dir);
            }
        }
    }

    public static Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, 4 * 9, "Build Room");

        GUIClickableItem exitEditMode = new GUIClickableItem() {
            @Override
            public int getSlot() {
                return 30;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder item = new ItemBuilder(XMaterial.NETHER_STAR);
                item.setName(ChatColor.RED + "Exit Edit Mode").setLore(
                        ChatColor.DARK_GRAY + "Click to exit edit mode",
                        "",
                        ChatColor.GRAY + "This will save the changes",
                        ChatColor.GRAY + "you made to the room as a",
                        ChatColor.GRAY + "new schematic that will be",
                        ChatColor.GRAY + "used in the room pool for",
                        ChatColor.GRAY + "generation."
                );
                return item.build();
            }

            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().closeInventory();
                chatInputList.add((Player) e.getWhoClicked());
                e.getWhoClicked().sendMessage(ChatColor.RED + "To cancel this, type CANCEL");
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Type the name of the room: ");
            }
        };
        GKBase.set(inv, exitEditMode);

        //Create exitEditMode but without saving
        GUIClickableItem exitWithoutSaving = new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().closeInventory();
                exitWorld((Player) e.getWhoClicked(), e.getWhoClicked().getWorld());
            }

            @Override
            public int getSlot() {
                return 32;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder item = new ItemBuilder(XMaterial.NETHER_STAR);
                item.setName(ChatColor.RED + "Exit Without Saving");
                item.setLore(
                        ChatColor.DARK_GRAY + "Click to exit without saving",
                        "",
                        ChatColor.GRAY + "This will exit the edit mode",
                        ChatColor.GRAY + "and remove the room you are",
                        ChatColor.GRAY + "currently editing."
                );
                return item.build();
            }
        };
        GKBase.set(inv, exitWithoutSaving);

        GKBase.set(inv, GUIClickableItem.getCloseItem(31));

        boolean special = specialTypes.getOrDefault(p.getWorld(), null) == SpecialType.SPAWN;

        GUIClickableItem placeSecret = new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                Player p = (Player) e.getWhoClicked();
                if(p.getInventory().firstEmpty() != -1) {
                    p.getInventory().addItem(RightClickListener.getSecretStick());
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have enough space in your inventory!");
                }
            }

            @Override
            public int getSlot() {
                return !special ? 12 : 11;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.STICK)
                        .setName(ChatColor.GREEN + "Secret Stick")
                        .setLore(
                                ChatColor.DARK_GRAY + "Click to obtain a secret stick",
                                "",
                                ChatColor.GRAY + "This stick will allow you to",
                                ChatColor.GRAY + "place secret rooms in the room",
                                ChatColor.GRAY + "you're currently editing."
                        ).build();
            }
        };
        GKBase.set(inv, placeSecret);

        GUIClickableItem doorY = new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                BuildRoomWorld.getWorld(e.getWhoClicked().getWorld())
                        .setDoorY(e.getWhoClicked().getLocation().getBlockY());
                e.getWhoClicked().sendMessage(
                        ChatColor.GREEN + "Set door Y to " + e.getWhoClicked().getLocation().getBlockY()
                );
            }

            @Override
            public int getSlot() {
                return !special ? 14 : 13;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder item = new ItemBuilder(XMaterial.OAK_DOOR)
                        .setName(ChatColor.GREEN + "Set Door Y Level")
                        .setLore(
                                ChatColor.DARK_GRAY + "Click to set the door Y level",
                                "",
                                ChatColor.GRAY + "This will be the Y level that",
                                ChatColor.GRAY + "doors will generate at."
                        );
                return item.build();
            }
        };
        GKBase.set(inv, doorY);

        if(special) {
            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public int getSlot() {
                    return 15;
                }

                @Override
                public ItemStack getItem() {
                    return new ItemBuilder(XMaterial.SPAWNER)
                            .setName(ChatColor.GREEN + "Set Spawn")
                            .setLore(
                                    ChatColor.DARK_GRAY + "Click to set the spawn",
                                    "",
                                    ChatColor.GRAY + "This will be the spawn point",
                                    ChatColor.GRAY + "for the room/when you start",
                                    ChatColor.GRAY + "the dungeon."
                            ).build();
                }

                @Override
                public void run(InventoryClickEvent e) {
                    Player p = (Player) e.getWhoClicked();
                    BuildRoomWorld bRW = BuildRoomWorld.getWorld(p.getWorld());
                    Location loc = p.getLocation();
                    bRW.setSpawnX(loc.getBlockX() - 3);
                    bRW.setSpawnY(loc.getBlockY() - 6);
                    bRW.setSpawnZ(loc.getBlockZ() - 3);
                    p.sendMessage(
                            ChatColor.GREEN + "Set spawn to " +
                                    loc.getBlockX() + ", " +
                                    loc.getBlockY() + ", " +
                                    loc.getBlockZ()
                    );
                }
            });
        }

        //TODO: Maybe use this at a future date?
        /*GUIClickableItem increaseRoomSize = new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                BuildRoomWorld.getWorld(e.getWhoClicked().getWorld())
                        .setRoomSize(
                                BuildRoomWorld.getWorld(
                                        e.getWhoClicked().getWorld()
                                ).getRoomSizeX() + 1,
                                BuildRoomWorld.getWorld(
                                        e.getWhoClicked().getWorld()
                                ).getRoomSizeZ() + 1
                        );
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Increased room size by 1!");
                e.getWhoClicked().sendMessage(
                        ChatColor.RED + "Note: This will not affect the size of the room schematic.");
            }

            @Override
            public int getSlot() {
                return 15;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder item = new ItemBuilder(XMaterial.LIME_WOOL)
                        .setName(ChatColor.GREEN + "Increase Room Size")
                        .setLore(
                                ChatColor.DARK_GRAY + "Click to increase the room size",
                                "",
                                ChatColor.GRAY + "This will increase the room size",
                                ChatColor.GRAY + "by 1 block."
                        );
                return item.build();
            }
        };
        set(inv, increaseRoomSize);
        
        GUIClickableItem decreaseRoomSize = new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                BuildRoomWorld.getWorld(e.getWhoClicked().getWorld())
                        .setRoomSize(
                                BuildRoomWorld.getWorld(
                                        e.getWhoClicked().getWorld()
                                ).getRoomSizeX() - 1, BuildRoomWorld.getWorld(
                                        e.getWhoClicked().getWorld()
                                ).getRoomSizeZ() - 1
                        );
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Decreased room size by 1!");
                e.getWhoClicked().sendMessage(
                        ChatColor.RED + "Note: This will not affect the size of the room schematic.");
            }

            @Override
            public int getSlot() {
                return 17;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder item = new ItemBuilder(XMaterial.RED_WOOL)
                        .setName(ChatColor.GREEN + "Decrease Room Size")
                        .setLore(
                                ChatColor.DARK_GRAY + "Click to decrease the room size",
                                "",
                                ChatColor.GRAY + "This will decrease the room size",
                                ChatColor.GRAY + "by 1 block."
                        );
                return item.build();
            }
        };
        set(inv, decreaseRoomSize);

         */

        return inv;
    }

}
