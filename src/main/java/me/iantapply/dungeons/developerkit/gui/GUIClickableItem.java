package me.iantapply.dungeons.developerkit.gui;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.GKBase;
import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public abstract class GUIClickableItem extends GKBase implements GUIItem {

    public static HashMap<String, GUIClickableItem> map = new HashMap<>();

    public GUIClickableItem() {
        uuid = UUID.randomUUID().toString();
        map.put(uuid, this);
    }

    private final String uuid;

    public ItemStack getFinishedItem() {
        return new ItemBuilder(getItem()).setString("ClickItem", uuid, "Internal").build();
    }

    public abstract void run(InventoryClickEvent e);

    public static GUIClickableItem getCloseItem(int slot) {
        return new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().closeInventory();
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(XMaterial.BARRIER.parseMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Close");
                item.setItemMeta(meta);
                return item;
            }
        };
    }

    public static GUIClickableItem cantPickup(ItemStack is, int slot) {
        return new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                return is;
            }
        };
    }

    public static GUIClickableItem getBorderItem(int slot) {
        return new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                ItemStack item = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(" ");
                item.setItemMeta(meta);
                return item;
            }
        };
    }

    public static GUIClickableItem goBackItem(int slot, Inventory back) {
        return new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.getWhoClicked().openInventory(back);
            }

            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                ItemStack item = XMaterial.ARROW.parseItem();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Go Back");
                meta.setLore(arrayList(ChatColor.GRAY + "To " + back.getName()));
                item.setItemMeta(meta);
                return item;
            }
        };
    }

    static GUIClickableItem createGUIOpenerItem(Inventory inv, Player player, String name, int slot, Material type, short data, String... lore) {
        return new GUIClickableItem() {
            @Override
            public ItemStack getItem() {
                ItemStack im = new ItemStack(type, 1, data);
                ItemMeta meta = im.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(new ArrayList<>(Arrays.asList(lore)));
                im.setItemMeta(meta);
                return im;
            }

            @Override
            public void run(InventoryClickEvent e) {
                if (inv == null) return;
                player.openInventory(inv);
            }

            @Override
            public int getSlot() {
                return slot;
            }
        };
    }

    static GUIClickableItem createGUIOpenerItem(Inventory guiType, Player player, String name, int slot, Material type, String... lore) {
        return createGUIOpenerItem(guiType, player, name, slot, type, (short) 0, lore);
    }
}
