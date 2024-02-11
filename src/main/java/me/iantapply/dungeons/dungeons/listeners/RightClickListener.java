package me.iantapply.dungeons.dungeons.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.gui.GUIClickableItem;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import me.iantapply.dungeons.developerkit.utils.nbt.NBTItem;
import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RightClickListener extends GKListener {

    public static ItemStack getSecretStick() {
        return new ItemBuilder(XMaterial.STICK)
                .setName("§aSecret Stick")
                .setLore(
                        "§7Right click to place",
                        "§7dungeons secrets"
                ).setString(
                        "ability",
                        "secretstick",
                        "ExtraAttributes"
                ).build();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getItem() != null &&
                new NBTItem(e.getItem()).getCompound("ExtraAttributes").hasKey("ability") &&
                new NBTItem(e.getItem()).getCompound("ExtraAttributes")
                        .getString("ability").equals("secretstick")) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                p.openInventory(getInventory(0, e));
            } else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                p.sendMessage("§cAim at a block to place a secret!");
            }
        }
    }

    public Inventory getInventory(int page, PlayerInteractEvent event) {
        Inventory inv = Bukkit.createInventory(null, 27, "Secret Stick");
        if (page == 0) {
            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public ItemStack getItem() {
                    return setTexture(new ItemBuilder(
                            XMaterial.PLAYER_HEAD,
                            (short) 3
                    ).setName("§6Wither Essence")
                            .setLore(
                                    "§7Appears on the ground.",
                                    "§7Must be right clicked.",
                                    "",
                                    "§7Can be used to upgrade",
                                    "§7weapons, armor and",
                                    "§7perks."
                            ).build(),
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBmYjg3OGFhMjhiZGQ3NzQ1ZDM4Mjg2MTFkNzI2ZDk2NDYxNDQxZTA2MjY4OWIyODY2YmNmYTkwYjU5YjAwMyJ9fX0=");
                }

                @Override
                public void run(InventoryClickEvent e) {
                    Block b = event.getPlayer().getWorld().getBlockAt(
                            event.getClickedBlock().getLocation().add(0, 1, 0));
                    b.setType(Material.SIGN_POST);
                    Sign sign = (Sign) b.getState();
                    sign.setLine(0, "secret");
                    sign.setLine(1, "wither_essence");
                    sign.update();
                }

                @Override
                public int getSlot() {
                    return 10;
                }

            });

            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public ItemStack getItem() {
                    return new ItemBuilder(XMaterial.BAT_SPAWN_EGG)
                            .setName("§6Bat")
                            .setLore(
                                    "§7Must be killed by a",
                                    "§7player.",
                                    "§7If killed otherwise,",
                                    "§7it will not count.",
                                    "",
                                    "§7Drops items."
                            ).build();
                }

                @Override
                public void run(InventoryClickEvent e) {
                    Block b = event.getPlayer().getWorld().getBlockAt(
                            event.getClickedBlock().getLocation().add(0, 1, 0));
                    b.setType(Material.SIGN_POST);
                    Sign sign = (Sign) b.getState();
                    sign.setLine(0, "secret");
                    sign.setLine(1, "bat");
                    sign.update();
                }

                @Override
                public int getSlot() {
                    return 12;
                }

            });

            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public ItemStack getItem() {
                    return new ItemBuilder(XMaterial.ENDER_PEARL)
                            .setName("§6Item Drops")
                            .setLore(
                                    "§7Must be picked up.",
                                    "",
                                    "§7Includes decoys,",
                                    "§7traps, training",
                                    "§7weights, spirit",
                                    "§7leaps, and more."
                            ).build();
                }

                @Override
                public void run(InventoryClickEvent e) {
                    Block b = event.getPlayer().getWorld().getBlockAt(
                            event.getClickedBlock().getLocation().add(0, 1, 0));
                    b.setType(Material.SIGN_POST);
                    Sign sign = (Sign) b.getState();
                    sign.setLine(0, "secret");
                    sign.setLine(1, "item");
                    sign.update();
                }

                @Override
                public int getSlot() {
                    return 14;
                }

            });

            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public ItemStack getItem() {
                    return new ItemBuilder(XMaterial.CHEST)
                            .setName("§6Chests")
                            .setLore(
                                    "§7Must be opened.",
                                    "§7Double chests",
                                    "§7are counted on",
                                    "§7both sides.",
                                    "",
                                    "§7Contains items",
                                    "§7and blessings."
                            ).build();
                }

                @Override
                public void run(InventoryClickEvent e) {
                    Block b = event.getPlayer().getWorld().getBlockAt(
                            event.getClickedBlock().getLocation().add(0, 1, 0));
                    b.setType(Material.SIGN_POST);
                    Sign sign = (Sign) b.getState();
                    sign.setLine(0, "secret");
                    sign.setLine(1, "chest");
                    sign.update();
                }

                @Override
                public int getSlot() {
                    return 16;
                }

            });
        }
        return inv;
    }
}
