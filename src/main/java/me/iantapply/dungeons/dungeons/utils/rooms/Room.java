package me.iantapply.dungeons.dungeons.utils.rooms;

import me.iantapply.dungeons.developerkit.utils.duplet.Duplet;
import me.iantapply.dungeons.developerkit.utils.duplet.Tuple;
import me.iantapply.dungeons.dungeons.generation.Directions;
import me.iantapply.dungeons.dungeons.generation.Generator;
import me.iantapply.dungeons.dungeons.generation.SpecialType;
import lombok.Getter;
import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Room {
    @Getter String name;
    @Getter
    RoomTypes roomType;
    @Getter int sizeX;
    @Getter int sizeZ;

    public Room(String name, RoomTypes roomType, int sizeX, int sizeZ, File schematic) {
        this.name = name;
        this.roomType = roomType;
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        this.schematicFile = schematic;
    }

    public static Duplet<File, String> randomRoom(RoomTypes roomType, int x, int y, Generator generator) {
        SpecialType sT = getSpecialType(generator, x, y);
        File f = sT == null ?
                new File("plugins/Dungeons/samples/" + roomType.name().replace("_", "")) :
                new File("plugins/Dungeons/samples/specialTypes/" +
                        sT.getFileName().toLowerCase()
                );
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            File file = files[(int) GKBase.random(files.length)];
            return Tuple.of(file, file.getName().replace(".schematic", ""));
        }
        return Tuple.of(getSampleFile(roomType, sT), "SAMPLE123192745981724_" + roomType.name().replace("_", ""));
    }

    public static File getSampleFile(RoomTypes roomType, SpecialType specialType) {
        if(specialType == null) {
            return new File("plugins/Dungeons/samples/" + roomType.name().replace("_", "") + ".schematic");
        } else {
            return new File("plugins/Dungeons/samples/specialTypes/" + specialType.getFileName() + ".schematic");
        }
    }

    public static SpecialType getSpecialType(Generator gen, int x, int y) {
        if(gen.specialTypes[y][x] != null) {
            return gen.specialTypes[y][x];
        } else if(gen.rgbMatches(x, y, 255, 64, 255)) {
            return SpecialType.FAIRY;
        } else if (gen.rgbMatches(x, y, 255, 0, 0)) {
            return SpecialType.BOSS;
        } else if (gen.rgbMatches(x, y, 0, 255, 0)) {
            return SpecialType.SPAWN;
        }
        return null;
    }

    @Getter File schematicFile;

    public static int getPasteYLevel(String name) {
        File f = new File("plugins/Dungeons/data.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
        if(name.startsWith("SAMPLE123192745981724_")) {
            return 63;
        }
        return 69 - yaml.getInt("room." + name + ".yLevel") + 1;
    }

    public static int[] getPasteCornerOffset(int i, String name) {
        int[] offset = new int[3];
        int sizeX = 31; //getSizeX(name);
        int sizeZ = 31; //getSizeZ(name);
        offset[2] = getRotation(i);
        switch (i) {
            case 0:
            default:
                //offset[1] = 1;
                break;
            case 1:
                offset[0] = sizeX - 1;
                break;
            case 2:
                offset[0] = sizeX - 1;
                offset[1] = sizeZ - 1;
                break;
            case 3:
                //offset[0] = 1;
                offset[1] = sizeZ - 1;
                break;
        }
        /*if (name.startsWith("SAMPLE123192745981724_")) {
            offset[0] += 2;
            offset[1] += 2;
        }

         */
        return offset;
    }

    public static int[] getPasteCornerOffset(String name, Directions dirs, int sizeX, int sizeZ) {
        int[] offset = new int[3];
        switch (dirs) {
            case UP:
            default:
                offset[1] = sizeZ - 1;
                offset[2] = 270;
                break;
            case LEFT:
                offset[0] = sizeX - 1;
                offset[1] = sizeZ - 1;
                offset[2] = 180;
                break;
            case DOWN:
                offset[0] = sizeX - 1;
                offset[2] = 90;
                break;
            case RIGHT:
                break;
        }
        /*if (name.startsWith("SAMPLE123192745981724_")) {
            offset[0] += 2;
            offset[1] += 2;
        }

         */

        return offset;
    }

    public static int getRotation(int i) {
        switch (i) {
            case 0:
            default:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
        }
    }

    public static int getSizeX(String name) {
        File f = new File("plugins/Dungeons/data.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
        if (name.startsWith("SAMPLE123192745981724_")) {
            switch (name.replace("SAMPLE123192745981724_", "").toLowerCase()) {
                case "1x1":
                    return 31;
                case "2x1":
                case "l":
                case "2x2":
                    return 63;
                case "3x1":
                    return 95;
                case "4x1":
                    return 127;
                default:
                    return 0;
            }
        }
        return yaml.getInt("room." + name + ".baseSizeX");
    }

    public static int getSizeZ(String name) {
        File f = new File("plugins/Dungeons/data.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
        if (name.startsWith("SAMPLE123192745981724_")) {
            switch (name.replace("SAMPLE123192745981724_", "").toLowerCase()) {
                case "1x1":
                case "2x1":
                case "3x1":
                case "4x1":
                    return 31;
                case "l":
                case "2x2":
                    return 63;
                default:
                    return 0;
            }
        }
        return yaml.getInt("room." + name + ".baseSizeZ");
    }
}
