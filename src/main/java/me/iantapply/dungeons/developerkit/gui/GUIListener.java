package me.iantapply.dungeons.developerkit.gui;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.developerkit.utils.nbt.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener extends GKListener {
    public static void startup() {
        Bukkit.getPluginManager().registerEvents(
                new GUIListener(), getPlugin()
        );
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() != XMaterial.AIR.parseMaterial()) {
            if(new NBTItem(e.getCurrentItem()).getCompound("Internal").hasKey("ClickItem")) {
                GUIClickableItem item =
                        GUIClickableItem.map.get(
                                new NBTItem(e.getCurrentItem()).getCompound("Internal").getString("ClickItem")
                        );
                item.run(e);
                if(!item.canPickup()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
