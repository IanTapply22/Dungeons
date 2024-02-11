package me.iantapply.dungeons.developerkit.item;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.developerkit.utils.nbt.NBTCompound;
import me.iantapply.dungeons.developerkit.utils.nbt.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickListener extends GKListener {
    public static void startup() {
        Bukkit.getPluginManager().registerEvents(
                new RightClickListener(), getPlugin()
        );
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item != null && item.getType() != XMaterial.AIR.parseMaterial()) {
            NBTItem nbt = new NBTItem(item);
            NBTCompound compound = nbt.getCompound("ExtraAttributes");
            if(compound.hasKey("RightClickItem")) {
                String uuid = compound.getString("RightClickItem");
                RightClickItem rightClickItem = RightClickItem.getItems().getOrDefault(uuid, null);
                if(rightClickItem != null) {
                    rightClickItem.run(e);
                }
            }
        }
    }
}
