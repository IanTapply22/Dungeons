package me.iantapply.dungeons.dungeons.utils.npcs;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class FloatingNametag {

    public static void createFloatingName(String name, Location location) {

        ArmorStand armorStand = location.getWorld().spawn(location.add(0, 0.1, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
    }
}
