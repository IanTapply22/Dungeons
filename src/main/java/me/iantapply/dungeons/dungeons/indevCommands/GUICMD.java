package me.iantapply.dungeons.dungeons.indevCommands;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.developerkit.gui.GUIClickableItem;
import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import me.iantapply.dungeons.dungeons.commands.BuildRoomCMD;
import me.iantapply.dungeons.dungeons.generation.SpecialType;
import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public class GUICMD extends GKCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Must be a player!");
            return false;
        }

        Player p = (Player) sender;

        p.openInventory(displayRoomsAndDoors(true));

        return true;
    }

    public Inventory displayRoomsAndDoors(boolean rooms) {
        String s = rooms ? "room" : "doors";
        String displayName = rooms ? "Room" : "Door";
        String otherDisplay = rooms ? "Door" : "Room";

        Inventory inv = Bukkit.createInventory(null, 54, displayName + "s");
        GKBase.setBorder(inv);
        GKBase.set(inv, GUIClickableItem.getCloseItem(49));
        GKBase.set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().openInventory(
                        rooms ?
                                chooseRoom(e.getInventory()) :
                                chooseDoor(e.getInventory())
                        );
            }

            @Override
            public int getSlot() {
                return 50;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.BRICKS)
                        .setName(ChatColor.GREEN + "Create " + displayName)
                        .build();
            }
        });
        GKBase.set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().openInventory(displayRoomsAndDoors(!rooms));
            }

            @Override
            public int getSlot() {
                return 53;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.PAPER)
                        .setName(ChatColor.GREEN + "List " + otherDisplay + "s")
                        .build();
            }
        });

        File data = new File("plugins/Dungeons/data.yml");
        FileConfiguration dFC = YamlConfiguration.loadConfiguration(data);

        ArrayList<String> keys = new ArrayList<>();

        for (String key : dFC.getKeys(true)) {
            if (key.startsWith(s + ".")) {
                String[] thing = key.split("\\.");
                if (!keys.contains(thing[1])) {
                    keys.add(thing[1]);
                }
            }
        }

        for (String room : keys) {
            String path = s + "." + room;
            String type = dFC.getString(path + ".type");

            ItemBuilder iB = new ItemBuilder(SpecialType.matchMaterial(type)).setName(ChatColor.GREEN + room);
            ArrayList<String> lore = new ArrayList<>();

            if(rooms) {
                lore.add(ChatColor.DARK_GRAY +
                        (SpecialType.matchType(type) == null ? type : SpecialType.matchType(type)) + " Room"
                );
            } else {
                lore.add(ChatColor.DARK_GRAY +
                        GKBase.capitalize(type) + " Door"
                );
            }
            iB.setLore(lore);

            if (inv.firstEmpty() != -1) {
                GKBase.set(inv, GUIClickableItem.cantPickup(iB.build(), inv.firstEmpty()));
            }
        }

        return inv;
    }

    public Inventory chooseDoor(Inventory back) {
        Inventory inv = Bukkit.createInventory(null, 54, "Choose Door Type");
        GKBase.fill(inv, GUIClickableItem.getBorderItem(0).getFinishedItem());

        GKBase.set(inv, GUIClickableItem.getCloseItem(49));
        GKBase.set(inv, GUIClickableItem.goBackItem(48, back));

        int[] places = new int[] {20, 22, 24};
        ItemStack[] materials = new ItemStack[] {
                XMaterial.BRICKS.parseItem(),
                XMaterial.BLACK_WOOL.parseItem(),
                XMaterial.RED_WOOL.parseItem()
        };
        String[] name = new String[] {"Normal", "Wither", "Blood"};

        for(int i = 0; i < 3; i++) {
            int finalI = i;
            GKBase.set(inv, new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    //send to make door
                    e.getWhoClicked().closeInventory();
                }

                @Override
                public int getSlot() {
                    return places[finalI];
                }

                @Override
                public ItemStack getItem() {
                    return new ItemBuilder(materials[finalI])
                            .setName(ChatColor.GREEN + space(name[finalI]))
                            .build();
                }
            });
        }
        return inv;
    }

    public Inventory chooseRoom(Inventory back) {
        Inventory inv = Bukkit.createInventory(null, 54, "Choose Room Type");
        GKBase.fill(inv, GUIClickableItem.getBorderItem(0).getFinishedItem());

        GKBase.set(inv, GUIClickableItem.getCloseItem(49));
        GKBase.set(inv, GUIClickableItem.goBackItem(48, back));

        int[][] places = new int[][] {
                new int[]{10, 11, 12, 14, 15, 16},
                new int[]{28, 29, 30, 32, 33, 34}
        };

        String[][] keys = new String[][] {
                new String[]{"1x1", "2x1", "2x2", "3x1", "4x1", "L"},
                new String[]{"MiniBoss", "Puzzle", "Trap", "Spawn", "Fairy", "Boss"}
        };

        ItemStack[][] materials = new ItemStack[][] {
                GKBase.recursiveArray(6, XMaterial.BRICKS.parseItem()),
                new ItemStack[] {
                        XMaterial.YELLOW_WOOL.parseItem(),
                        XMaterial.PURPLE_WOOL.parseItem(),
                        XMaterial.ORANGE_WOOL.parseItem(),
                        XMaterial.LIME_WOOL.parseItem(),
                        XMaterial.PINK_WOOL.parseItem(),
                        XMaterial.RED_WOOL.parseItem(),
                }
        };

        for(int j = 0; j < 2; j++) {
            int finalJ = j;

            for (int i = 0; i < 6; i++) {
                int finalI = i;

                String key = keys[j][i];

                GKBase.set(inv, new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        BuildRoomCMD.makeRoom(key, (Player) e.getWhoClicked());
                        e.getWhoClicked().closeInventory();
                    }

                    @Override
                    public int getSlot() {
                        return places[finalJ][finalI];
                    }

                    @Override
                    public ItemStack getItem() {
                        return new ItemBuilder(materials[finalJ][finalI])
                                .setName(ChatColor.GREEN + space(key))
                                .build();
                    }
                });
            }
        }

        return inv;
    }
}
