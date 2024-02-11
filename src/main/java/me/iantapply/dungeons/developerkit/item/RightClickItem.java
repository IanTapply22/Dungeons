package me.iantapply.dungeons.developerkit.item;

import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public abstract class RightClickItem {
    @Getter private static final HashMap<String, RightClickItem> items = new HashMap<>();

    private final String uuid;

    public RightClickItem(ItemStack is) {
        uuid = UUID.randomUUID().toString();
        this.is = is;
        items.put(uuid, this);
    }

    private final ItemStack is;

    public ItemStack getItem() {
        return new ItemBuilder(is)
                .setString(
                        "RightClickItem",
                        uuid,
                        "ExtraAttributes"
                ).build();
    }

    public abstract void run(PlayerInteractEvent e);
}
