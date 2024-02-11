package me.iantapply.dungeons.dungeons.utils.schematics;

import me.iantapply.dungeons.developerkit.GKBase;
import me.iantapply.dungeons.dungeons.utils.jnbt.*;
import me.iantapply.dungeons.dungeons.utils.worlds.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schematic extends GKBase {

    public static void paste(File f, Dungeon w, Location l, double r) {
        paste(f, w, l, r, true);
    }

    public static void paste(File f, Dungeon world, Location originLoc, double rotation, boolean testing) {
        try {
            if (testing) {
                originLoc = getBlockLocationCentered(originLoc);
                int rot = (int) rotation;
                switch (rot) {
                    case 0:
                        originLoc.setX(originLoc.getX() + 0.3);
                        originLoc.setZ(originLoc.getZ() + 0.3);
                        break;
                    case 90:
                        originLoc.setX(originLoc.getX() - 0.4);
                        originLoc.setZ(originLoc.getZ() + 0.3);
                        break;
                    case 180:
                        originLoc.setX(originLoc.getX() - 0.4);
                        originLoc.setZ(originLoc.getZ() - 0.4);
                        break;
                    case 270:
                        originLoc.setX(originLoc.getX() + 0.3);
                        originLoc.setZ(originLoc.getZ() - 0.4);
                        break;
                }
            }

            FileInputStream fis = new FileInputStream(f);
            NBTInputStream nbt = new NBTInputStream(fis);

            CompoundTag backuptag = (CompoundTag) nbt.readTag();
            Map<String, Tag> tagCollection = backuptag.getValue();

            short width = (Short) getChildTag(tagCollection, "Width", ShortTag.class).getValue();
            short height = (Short) getChildTag(tagCollection, "Height", ShortTag.class).getValue();
            short length = (Short) getChildTag(tagCollection, "Length", ShortTag.class).getValue();

            int weOffsetX = (Integer) getChildTag(tagCollection, "WEOffsetX", IntTag.class).getValue();
            int weOffsetY = (Integer) getChildTag(tagCollection, "WEOffsetY", IntTag.class).getValue();
            int weOffsetZ = (Integer) getChildTag(tagCollection, "WEOffsetZ", IntTag.class).getValue();

            byte[] blocks = (byte[]) getChildTag(tagCollection, "Blocks", ByteArrayTag.class).getValue();
            byte[] data = (byte[]) getChildTag(tagCollection, "Data", ByteArrayTag.class).getValue();
            List<?> entities = (List<?>) getChildTag(tagCollection, "Entities", ListTag.class).getValue();
            List<?> tileentities = (List<?>) getChildTag(tagCollection, "TileEntities", ListTag.class).getValue();
            nbt.close();
            fis.close();
            HashMap<Location, MaterialData> blocksHashMap = new HashMap<>();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        int index = y * width * length + z * width + x;

                        int moveX = x + originLoc.getBlockX() + weOffsetX;
                        int moveY = y + originLoc.getBlockY() + weOffsetY;
                        int moveZ = z + originLoc.getBlockZ() + weOffsetZ;

                        Location block = new Location(
                                originLoc.getWorld(),
                                moveX,
                                moveY,
                                moveZ
                        );
                        block = rotateLocXZ(block, originLoc, Math.toRadians(rotation));
                        block.setY(moveY);

                        MaterialData matData = data[index] == 0
                                ? new MaterialData(Material.getMaterial(blocks[index] & 0xFF))
                                : new MaterialData(Material.getMaterial(blocks[index] & 0xFF), data[index]);
                        if (matData.getItemType() == Material.AIR || blockMatches(matData, block.getBlock())) continue;
                        blocksHashMap.put(block, matData);
                    }
                }
            }
            ArrayList<Object> chunks = new ArrayList<>();
            for(Location locs : blocksHashMap.keySet()) {
                try {
                    Object chunk = BlockChanger.setSectionBlock(locs, new ItemStack(
                                    blocksHashMap.get(locs).getItemType(),
                                    1,
                                    blocksHashMap.get(locs).getData()
                            ), true
                    );
                    if (!chunks.contains(chunk)) chunks.add(chunk);
                } catch (IllegalArgumentException error) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was a block (water, fire, lava, etc) that could not be translated to and itemstack! The block will not be pasted.");
                }
            }
            BlockChanger.updateLight(chunks);
            /*world.roomsBeingMade++;
            WorkloadRunnable workloadRunnable = new WorkloadRunnable(world);
            workloadRunnable.runTaskTimer(Dungeons.getMain(), 1, 1L);
            new DistributedFiller(workloadRunnable).fill(blocksHashMap, world);

             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location rotateLocXZ(Location loc, Location axis, double angle) {
        //angle *= -1; // By default, we use counterclockwise rotations. Uncomment to use clcokwise rotations instead.
        //angle *= 180 / Math.PI; // By default, angle is in radians. Uncomment to use degrees instead.
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Vector r1 = new Vector(cos, 0, -sin); // Bukkit vectors need 3 components, so set the y-component to 0
        Vector r2 = new Vector(sin, 0, cos);
        Vector v = loc.clone().subtract(axis).toVector();
        Vector rotated = new Vector(r1.dot(v), 0, r2.dot(v)); // Perform the matrix multiplication
        return rotated.add(axis.toVector()).toLocation(loc.getWorld());
    }


    private static Tag getChildTag(Map<String, Tag> items, String key, Class<? extends Tag> expected) {
        return items.get(key);
    }

    public static ArrayList<HashMap<Location, MaterialData>> split(int times, HashMap<Location, MaterialData> blocks) {
        int splitSize = (int) Math.ceil(blocks.size() / (double) times);
        ArrayList<HashMap<Location, MaterialData>> split = new ArrayList<>();

        ArrayList<Location> locations = new ArrayList<>(blocks.keySet());

        for (int i = 0; i < times; i++) {
            HashMap<Location, MaterialData> splitMap = new HashMap<>();
            for (int j = 0; j < splitSize; j++) {
                try {
                    Location loc = locations.get(i * splitSize + j);
                    splitMap.put(loc, blocks.get(loc));
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                //blocks.remove(loc);
            }
            split.add(splitMap);
        }
        return split;
    }
}
