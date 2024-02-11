package me.iantapply.dungeons.dungeons.generation;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public enum SpecialType {
    MINIBOSS("MiniBoss", XMaterial.YELLOW_WOOL.parseItem()),
    PUZZLE("Puzzle", XMaterial.PURPLE_WOOL.parseItem()),
    TRAP("Trap", XMaterial.ORANGE_WOOL.parseItem()),
    FAIRY("Fairy", XMaterial.PINK_WOOL.parseItem()),
    BOSS("Boss", XMaterial.RED_WOOL.parseItem()),
    SPAWN("Spawn", XMaterial.LIME_WOOL.parseItem());

    @Getter String fileName;
    @Getter ItemStack block;

    SpecialType(String fileName, ItemStack block) {
        this.fileName = fileName;
        this.block = block;
    }

    public static String matchType(String key) {
        try {
            return SpecialType.valueOf(
                    key.replace("specialTypes/", "").toUpperCase()
            ).getFileName().replaceAll("(.)([A-Z])", "$1 $2");
        } catch (Exception ex) {
            return null;
        }
    }

    public static ItemStack matchMaterial(String key) {
        try {
            return SpecialType.valueOf(
                    key.replace("specialTypes/", "")
            ).getBlock();
        } catch (Exception ex) {
            return XMaterial.BRICKS.parseItem();
        }
    }
}
