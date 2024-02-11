package me.iantapply.dungeons.developerkit.utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.iantapply.dungeons.developerkit.GKBase;
import me.iantapply.dungeons.developerkit.utils.nbt.NBTItem;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ItemBuilder extends GKBase {
    protected ItemStack im;

    public ItemBuilder(ItemStack item, short data) {
        im = new ItemStack(item.getType(), 1, data);
    }

    public ItemBuilder(ItemStack item, short data, int amount) {
        im = new ItemStack(item.getType(), amount, data);
    }

    public ItemBuilder(XMaterial material) {
        im = new ItemStack(material.parseMaterial(), 1, material.getData());
    }

    public ItemBuilder(XMaterial material, int amount) {
        im = new ItemStack(material.parseMaterial(), amount, material.getData());
    }

    public ItemBuilder(ItemStack item) {
        im = item;
    }

    public ItemStack build() {
        return im;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = this.im.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        im.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(ArrayList<String> lore) {
        ItemMeta meta = this.im.getItemMeta();
        meta.setLore(lore);
        im.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = this.im.getItemMeta();
        meta.setDisplayName(name);
        im.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setString(String key, String value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setString(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setInt(String key, int value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setInteger(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setBoolean(String key, boolean value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setBoolean(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setDouble(String key, double value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setDouble(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setLong(String key, long value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setLong(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setByte(String key, byte value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setByte(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setShort(String key, short value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setShort(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setFloat(String key, float value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setFloat(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setByteArray(String key, byte[] value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setByteArray(key, value);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setIntArray(String key, int[] value, String compound) {
        NBTItem nbti = new NBTItem(im);
        nbti.getCompound(compound).setIntArray(key, value);
        im = nbti.getItem();
        return this;
    }

    public String getString(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getString(key);
    }

    public int getInt(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getInteger(key);
    }

    public boolean getBoolean(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getBoolean(key);
    }

    public double getDouble(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getDouble(key);
    }

    public long getLong(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getLong(key);
    }

    public byte getByte(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getByte(key);
    }

    public short getShort(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getShort(key);
    }

    public float getFloat(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getFloat(key);
    }

    public byte[] getByteArray(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getByteArray(key);
    }

    public int[] getIntArray(String key, String compound) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).getIntArray(key);
    }

    public boolean hasKey(String compound, String key) {
        NBTItem nbti = new NBTItem(im);
        return nbti.getCompound(compound).hasKey(key);
    }

    public ItemBuilder HideFlags(int flags) {
        NBTItem nbti = new NBTItem(im);
        nbti.setInteger("HideFlags", flags);
        im = nbti.getItem();
        return this;
    }

    public ItemBuilder setUnbreakable(boolean bol) {
        NBTItem nbt = new NBTItem(im);
        nbt.setBoolean("Unbreakable", bol);
        im = nbt.getItem();
        return this;
    }

    public ItemBuilder setTexture(String texture) {
        SkullMeta hm = (SkullMeta) im.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field field = hm.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(hm, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        im.setItemMeta(hm);
        return this;
    }

    public ItemBuilder setColor(Color c) {
        LeatherArmorMeta is = (LeatherArmorMeta) im.getItemMeta();
        is.setColor(c);
        im.setItemMeta(is);
        return this;
    }
}
