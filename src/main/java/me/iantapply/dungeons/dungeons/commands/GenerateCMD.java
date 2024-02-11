package me.iantapply.dungeons.dungeons.commands;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.developerkit.utils.duplet.Duplet;
import me.iantapply.dungeons.developerkit.utils.duplet.Tuple;
import me.iantapply.dungeons.dungeons.generation.Directions;
import me.iantapply.dungeons.dungeons.generation.Generator;
import me.iantapply.dungeons.dungeons.generation.Preset;
import me.iantapply.dungeons.dungeons.utils.misc.EmptyChunkGenerator;
import me.iantapply.dungeons.dungeons.utils.rooms.Room;
import me.iantapply.dungeons.dungeons.utils.rooms.RoomTypes;
import me.iantapply.dungeons.dungeons.utils.schematics.Schematic;
import me.iantapply.dungeons.dungeons.utils.worlds.Dungeon;
import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GenerateCMD extends GKCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());

        /*if (!XMaterial.supports(13)) {
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.FLAT);
            wc.generateStructures(false);
            wc.generatorSettings("2;0;1;");
        } else {
        */
        wc.generator(new EmptyChunkGenerator());
        //}

        World world = wc.createWorld();
        world.setGameRuleValue("doMobSpawning", "false");

        doneRooms.put(world, new ArrayList<>());

        Preset floor = null;
        if (!args[0].isEmpty()) {
            if (args[0].equalsIgnoreCase("1")) {
                floor = Preset.FLOOR1;
            } else if (args[0].equalsIgnoreCase("2")) {
                floor = Preset.FLOOR2;
            } else if (args[0].equalsIgnoreCase("3")) {
                floor = Preset.FLOOR3;
            } else if (args[0].equalsIgnoreCase("4")) {
                floor = Preset.FLOOR4;
            } else if (args[0].equalsIgnoreCase("5")) {
                floor = Preset.FLOOR5;
            } else if (args[0].equalsIgnoreCase("6")) {
                floor = Preset.FLOOR6;
            } else if (args[0].equalsIgnoreCase("7")) {
                floor = Preset.FLOOR7;
            }
            generateWorld(p, world, floor);
        } else {
            floor = Preset.FLOOR7;
            generateWorld(p, world, floor);
        }

        return true;
    }

    // Generate the world using the generator code
    public static void generateWorld(Player p, World world, Preset preset) {
        Generator gen = new Generator(preset);
        gen.setup();
        boolean valid = !gen.generatedSuccessfully
                || gen.maxPuzzleRooms < gen.amountOfPuzzleRooms() ||
                !gen.doNotRestart; //true || true
        while (valid) {
            try {
                gen = new Generator(preset);
                gen.setup();
                valid = !gen.generatedSuccessfully
                        || gen.maxPuzzleRooms < gen.amountOfPuzzleRooms() ||
                        !gen.doNotRestart;

            } catch (StackOverflowError ignored) {
            }
        }
        final Generator generator = gen;

        Dungeon dung = new Dungeon(world);

        int roomSize = 31; //TODO: implement proper thing for this

        ArrayList<ArrayList<?>> roomMethods = new ArrayList<>();

        String randomSpawnRoomName = "";

        p.sendMessage("§9§m------------------------------------------");
        p.sendMessage("§b" + p.getName() + " §eentered §cThe Catacombs§e, Floor " + preset.getRomanNumeral() + "!");
        p.sendMessage("§9§m------------------------------------------");

        for (int x = 0; x < generator.sizeX; x++) {
            for (int y = 0; y < generator.sizeY; y++) {
                if (!doneRooms.get(world).contains(generator.co(x, y))) {

                    RoomTypes roomType = RoomTypes.getRoomType(generator.types[y][x]);
                    int[] pasteOffset;
                    Duplet<File, String> rum = Room.randomRoom(roomType, x, y, generator);
                    String name = rum.getSecond();

                    int inputX = x;
                    int inputY = y;

                    ArrayList<int[]> rooms = new ArrayList<>(generator.getExtendedRooms(x, y));
                    if (roomType == RoomTypes._1x1) {
                        pasteOffset = Room.getPasteCornerOffset((int) GKBase.random(4), name);
                        if(generator.getSpawnRoom()[0] == x && generator.getSpawnRoom()[1] == y) {
                            randomSpawnRoomName = name;
                        }
                    } else if (roomType == RoomTypes.L) {
                        ArrayList<int[]> extended = generator.getExtendedRooms(x, y);

                        int[] middlePieceCords = generator.getMiddlePieceOfL(extended);
                        inputX = middlePieceCords[0];
                        inputY = middlePieceCords[1];
                        Directions[] die = getDirections(generator, inputX, inputY);
                        int correspondingRotation = correspondingRotations(die);

                        int[] pasteCords = getLPaste(extended, correspondingRotation);
                        inputX = pasteCords[0];
                        inputY = pasteCords[1];

                        pasteOffset = Room.getPasteCornerOffset(correspondingRotation, name);
                    } else {
                        ArrayList<int[]> extended = generator.getExtendedRooms(x, y);
                        int[] lowestCords = getLowest(roomType, getDirections(generator, x, y), extended, x, y);
                        inputX = lowestCords[0];
                        inputY = lowestCords[1];
                        Directions[] die = getDirections(generator, inputX, inputY);

                        Directions pasteDir;
                        pasteDir = getPasteDir(die);

                        pasteOffset = Room.getPasteCornerOffset(
                                name,
                                pasteDir,
                                roomSize, roomSize);
                    }

                    Room room = new Room(name, roomType,
                            Room.getSizeX(name),
                            Room.getSizeZ(name), rum.getFirst());

                    Location pasteLoc = new Location(world,
                            (inputX * (roomSize + 1)) + pasteOffset[0],
                            Room.getPasteYLevel(name),
                            (inputY * (roomSize + 1)) + pasteOffset[1]);

                    roomMethods.add(GKBase.arrayList(room.getSchematicFile(), dung, pasteLoc, pasteOffset[2]));

                    rooms.forEach(room1 -> doneRooms.get(world).add(generator.co(room1[0], room1[1])));
                }
            }
        }

        roomMethods.forEach(arr -> Schematic.paste((File) arr.get(0), (Dungeon) arr.get(1), (Location) arr.get(2), (Integer) arr.get(3)));

        placeDoors(generator, world);

        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new File("plugins/Dungeons/data.yml")
        );

        int[] spawnRoom = generator.getSpawnRoom();
        Location spawnLoc;

        if (config.get("room." + randomSpawnRoomName + ".spawnX") != null) {
            int spawnX = config.getInt("room." + randomSpawnRoomName + ".spawnX");
            int spawnY = config.getInt("room." + randomSpawnRoomName + ".spawnY");
            int spawnZ = config.getInt("room." + randomSpawnRoomName + ".spawnZ");
            spawnLoc = new Location(world,
                    spawnRoom[0] * (roomSize + 1) + spawnX + 0.5,
                    Room.getPasteYLevel(randomSpawnRoomName) + 1 + spawnY,
                    spawnRoom[1] * (roomSize + 1) + spawnZ + 0.5
            );
        } else {
            spawnLoc = new Location(world,
                    spawnRoom[0] * (roomSize + 1) + 15.5,
                    69,
                    spawnRoom[1] * (roomSize + 1) + 15.5
            );
        }
        BuildRoomCMD.movePlayer(p, spawnLoc, false, false);
    }

    // Place doorways
    // TODO: make proper doorways and line them up
    public static void placeDoors(Generator gen, World world) {
        for (String name : gen.doors.keySet()) {
            String[] nameSplit = name.split(" ");

            int x1 = Integer.parseInt(nameSplit[0]);
            int y1 = Integer.parseInt(nameSplit[1]);

            for (String connected : gen.doors.get(name)) {
                String[] connectedSplit = connected.split(" ");

                int x2 = Integer.parseInt(connectedSplit[0]);
                int y2 = Integer.parseInt(connectedSplit[1]);

                int mcCordsX1 = x1 * 32;
                int mcCordsY1 = y1 * 32;

                int mcCordsX2 = x2 * 32;
                int mcCordsY2 = y2 * 32;

                int gradient1X1 = x1 * 32;
                int gradient1Y1 = y1 * 32;
                int gradient1X2 = x1 * 32;
                int gradient1Y2 = y1 * 32;

                int gradient2X1 = x2 * 32;
                int gradient2Y1 = y2 * 32;
                int gradient2X2 = x2 * 32;
                int gradient2Y2 = y2 * 32;

                int doorPasteX = x1 * 32;
                int doorPasteY = y1 * 32;

                int rotation = 0;

                Directions dir = gen.directionGoingIn(x1, y1, x2, y2);

                switch (dir) {
                    case RIGHT:
                        mcCordsX1 += 29;
                        mcCordsY1 += 13;

                        mcCordsX2 += 1;
                        mcCordsY2 += 17;

                        gradient1X1 += 28;
                        gradient1Y1 += 13;
                        gradient1X2 = gradient1X1;
                        gradient1Y2 = gradient1Y1 + 4;

                        gradient2X1 += 2;
                        gradient2Y1 += 17;
                        gradient2X2 = gradient2X1;
                        gradient2Y2 = gradient2Y1 - 4;

                        doorPasteX += 28;
                        doorPasteY += 13;
                        rotation = 270;
                        break;
                    case LEFT:
                        mcCordsX1 += 1;
                        mcCordsY1 += 13;

                        mcCordsX2 += 29;
                        mcCordsY2 += 17;

                        gradient1X1 += 2;
                        gradient1Y1 += 13;
                        gradient1X2 = gradient1X1;
                        gradient1Y2 = gradient1Y1 + 4;

                        gradient2X1 += 28;
                        gradient2Y1 += 17;
                        gradient2X2 = gradient2X1;
                        gradient2Y2 = gradient2Y1 - 4;

                        doorPasteX += 2;
                        doorPasteY += 17;
                        rotation = 90;
                        break;
                    case DOWN:
                        mcCordsX1 += 13;
                        mcCordsY1 += 1;

                        mcCordsX2 += 17;
                        mcCordsY2 += 29;

                        gradient1X1 += 13;
                        gradient1Y1 += 2;
                        gradient1X2 = gradient1X1 + 4;
                        gradient1Y2 = gradient1Y1;

                        gradient2X1 += 17;
                        gradient2Y1 += 28;
                        gradient2X2 = gradient2X1 - 4;
                        gradient2Y2 = gradient2Y1;

                        doorPasteX += 13;
                        doorPasteY += 2;
                        rotation = 180;
                        break;
                    case UP:
                        mcCordsX1 += 13;
                        mcCordsY1 += 29;

                        mcCordsX2 += 17;
                        mcCordsY2 += 1;

                        gradient1X1 += 13;
                        gradient1Y1 += 28;
                        gradient1X2 = gradient1X1 + 4;
                        gradient1Y2 = gradient1Y1;

                        gradient2X1 += 17;
                        gradient2Y1 += 2;
                        gradient2X2 = gradient2X1 - 4;
                        gradient2Y2 = gradient2Y1;

                        doorPasteX += 17;
                        doorPasteY += 28;
                        break;
                }

                Location loc1 = new Location(world, mcCordsX1, 67, mcCordsY1);
                Location loc2 = new Location(world, mcCordsX2, 66, mcCordsY2);


                blocksFromTwoPoints(loc1, loc2).forEach(b -> b.setType(XMaterial.BEDROCK.parseMaterial()));

                Location gradient1Loc1 = new Location(world, gradient1X1, 68, gradient1Y1);
                Location gradient1Loc2 = new Location(world, gradient1X2, 68, gradient1Y2);
                Location gradient2Loc1 = new Location(world, gradient2X1, 68, gradient2Y1);
                Location gradient2Loc2 = new Location(world, gradient2X2, 68, gradient2Y2);

                ArrayList<Duplet<Material, Byte>> materials = new ArrayList<>();

                blocksFromTwoPoints(gradient1Loc1, gradient1Loc2).forEach(b -> {
                    Duplet<Material, Byte> material = Tuple.of(b.getType(), b.getData());
                    if(!materials.contains(material)) materials.add(material);
                });
                blocksFromTwoPoints(gradient2Loc1, gradient2Loc2).forEach(b -> {
                    Duplet<Material, Byte> material = Tuple.of(b.getType(), b.getData());
                    if (!materials.contains(material)) materials.add(material);
                });

                loc1.setY(68);
                loc2.setY(68);

                blocksFromTwoPoints(loc1, loc2).forEach(b -> {
                    Duplet<Material, Byte> thing = GKBase.getRandom(materials);
                    b.setType(thing.getFirst());
                    GKBase.setData(b, thing.getSecond());
                });

                //choose door file
            }
        }
    }

    public static int correspondingRotations(Directions[] dirs) {
        if (dirs[0] == Directions.RIGHT && dirs[1] == Directions.UP) {
            return 2;
        }else if (dirs[0] == Directions.RIGHT && dirs[1] == Directions.DOWN) {
            return 3;
        }else if (dirs[0] == Directions.LEFT && dirs[1] == Directions.DOWN) {
            return 0;
        } else { //LEFT && UP
            return 1;
        }

    }

    // Get the past directions for the schematics
    public static Directions getPasteDir(Directions[] dirs) {
        if (dirs[0] == null || dirs[1] == null) {
            return dirs[0] == null ? dirs[1] : dirs[0];
        } else {
            if(dirs[0] == Directions.RIGHT && dirs[1] == Directions.UP) {
                return Directions.UP;
            } else if (dirs[0] == Directions.RIGHT && dirs[1] == Directions.DOWN) {
                return Directions.RIGHT;
            } else if (dirs[0] == Directions.LEFT && dirs[1] == Directions.DOWN) {
                return Directions.DOWN;
            } else {
                return Directions.LEFT;
            }
        }
    }

    public static int[] getLPaste(ArrayList<int[]> extended, int rotation) {
        int[] returnValue = new int[2];
        switch (rotation) {
            case 2:
                returnValue[0] = getHighestX(extended);
                returnValue[1] = getHighestY(extended);
                break;
            case 3:
                returnValue[0] = getLowestX(extended);
                returnValue[1] = getHighestY(extended);
                break;
            case 0:
                returnValue[0] = getLowestX(extended);
                returnValue[1] = getLowestY(extended);
                break;
            case 1:
                returnValue[0] = getHighestX(extended);
                returnValue[1] = getLowestY(extended);
                break;

        }
        return returnValue;
    }

    public static int[] getLowest(RoomTypes type, Directions[] dirs, ArrayList<int[]> list, int baseX, int baseY) {
        switch (type) {
            default:
                Directions dir = getPasteDir(dirs);
                switch (dir) {
                    case UP:
                        return combineXY(baseX, getHighestY(list));
                    case DOWN:
                        return combineXY(baseX, getLowestY(list));
                    case LEFT:
                        return combineXY(getLowestX(list), baseY);
                    case RIGHT:
                        return combineXY(getHighestX(list), baseY);
                }
                break;
            case L:
            case _2x2:
                int[] returnValue = new int[2];
                Directions dirUpDown = dirs[1];
                Directions dirLeftRight = dirs[0];
                if(dirUpDown == Directions.UP) {
                    if(dirLeftRight == Directions.LEFT) {
                        returnValue[0] = getLowestX(list);
                        returnValue[1] = getLowestY(list);
                    } else {
                        returnValue[0] = getLowestX(list);
                        returnValue[1] = getHighestY(list);
                    }
                } else {
                    if(dirLeftRight == Directions.LEFT) {
                        returnValue[0] = getHighestX(list);
                        returnValue[1] = getLowestY(list);
                    } else {
                        returnValue[0] = getHighestX(list);
                        returnValue[1] = getHighestY(list);
                    }
                }
                return returnValue;
        }
        return new int[]{0, 0};
    }

    // Combine the x and y inputted values
    public static int[] combineXY(int x, int y) {
        return new int[]{x, y};
    }

    // Get lowest y value of a set of rooms
    public static int getLowestY(ArrayList<int[]> list) {
        int lowest = Integer.MAX_VALUE;
        for(int[] room : list) {
            if(room[1] < lowest) {
                lowest = room[1];
            }
        }
        return lowest;
    }

    // Get highest y value of a set of rooms
    public static int getHighestY(ArrayList<int[]> list) {
        int highest = Integer.MIN_VALUE;
        for(int[] room : list) {
            if(room[1] > highest) {
                highest = room[1];
            }
        }
        return highest;
    }

    //Get lowest x value of a set of rooms
    public static int getLowestX(ArrayList<int[]> list) {
        int lowest = Integer.MAX_VALUE;
        for(int[] room : list) {
            if(room[0] < lowest) {
                lowest = room[0];
            }
        }
        return lowest;
    }

    // Get highest x value of a set of rooms
    public static int getHighestX(ArrayList<int[]> list) {
        int highest = Integer.MIN_VALUE;
        for(int[] room : list) {
            if(room[0] > highest) {
                highest = room[0];
            }
        }
        return highest;
    }

    public static HashMap<World, ArrayList<String>> doneRooms = new HashMap<>();

    public static Directions[] getDirections(Generator gen, int x, int y) {
        Directions[] directions = new Directions[2];
        ArrayList<int[]> list = gen.getExtendedRooms(x, y);
        final int[] baseCords = !list.isEmpty() ? list.get(0) : new int[]{x, y};
        list.remove(baseCords);
        for (int[] cords : list) {
            int x1 = cords[0];
            int y1 = cords[1];
            if (x1 < x) {
                directions[0] = Directions.LEFT;
            } else if (x1 > x) {
                directions[0] = Directions.RIGHT;
            }
            if (y1 < y) {
                directions[1] = Directions.UP;
            } else if (y1 > y) {
                directions[1] = Directions.DOWN;
            }
        }
        return directions;
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
