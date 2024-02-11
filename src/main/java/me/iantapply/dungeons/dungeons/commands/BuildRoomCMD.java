package me.iantapply.dungeons.dungeons.commands;

import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.dungeons.generation.SpecialType;
import me.iantapply.dungeons.dungeons.listeners.BuildRoomListeners;
import me.iantapply.dungeons.dungeons.utils.schematics.Schematic;
import me.iantapply.dungeons.dungeons.utils.rooms.RoomTypes;
import me.iantapply.dungeons.dungeons.utils.worlds.BuildRoomWorld;
import me.iantapply.dungeons.dungeons.utils.worlds.Dungeon;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class BuildRoomCMD extends GKCommand {
    public static int amountOfRoomsBeingBuilt = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        if(args.length != 1) {
            p.sendMessage("§cUsage: /buildroom <room type>");
            p.sendMessage("§cValid room types: " +
                    "1x1, " +
                    "2x1, " +
                    "2x2, " +
                    "3x1, " +
                    "4x1, " +
                    "L, " +
                    "MiniBoss, " +
                    "Puzzle, " +
                    "Trap, " +
                    "Fairy, " +
                    "Boss, " +
                    "Spawn");
            return true;
        }

        return makeRoom(args[0], p);
    }

    public static boolean makeRoom(String key, Player p) {
        WorldCreator wc = new WorldCreator("buildroom_" + amountOfRoomsBeingBuilt);
        amountOfRoomsBeingBuilt++;

        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generatorSettings("minecraft:bedrock,2;minecraft:dirt,minecraft:grass;");

        File file;
        RoomTypes roomType = null;
        SpecialType specialType = null;
        int sizeX;
        int sizeZ;

        String fileName;

        switch (key.toUpperCase()) {
            case "1X1":
                fileName = "1x1";
                roomType = RoomTypes._1x1;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "2X1":
                fileName = "2x1";
                roomType = RoomTypes._2x1;
                sizeX = 63;
                sizeZ = 31;
                break;
            case "2X2":
                fileName = "2x2";
                roomType = RoomTypes._2x2;
                sizeX = 63;
                sizeZ = 63;
                break;
            case "3X1":
                fileName = "3x1";
                roomType = RoomTypes._3x1;
                sizeX = 95;
                sizeZ = 31;
                break;
            case "4X1":
                fileName = "4x1";
                roomType = RoomTypes._4x1;
                sizeX = 127;
                sizeZ = 31;
                break;
            case "L":
                fileName = "L";
                roomType = RoomTypes.L;
                sizeX = 63;
                sizeZ = 63;
                break;
            case "MINIBOSS":
                fileName = "specialTypes/MiniBoss";
                specialType = SpecialType.MINIBOSS;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "PUZZLE":
                fileName = "specialTypes/Puzzle";
                specialType = SpecialType.PUZZLE;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "TRAP":
                fileName = "specialTypes/Trap";
                specialType = SpecialType.TRAP;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "FAIRY":
                fileName = "specialTypes/Fairy";
                specialType = SpecialType.FAIRY;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "BOSS":
                fileName = "specialTypes/Boss";
                specialType = SpecialType.BOSS;
                sizeX = 31;
                sizeZ = 31;
                break;
            case "SPAWN":
                fileName = "specialTypes/Spawn";
                specialType = SpecialType.SPAWN;
                sizeX = 31;
                sizeZ = 31;
                break;
            default:
                p.sendMessage("§cInvalid room type. Valid room types are: " +
                        "1x1, " +
                        "2x1, " +
                        "2x2, " +
                        "3x1, " +
                        "4x1, " +
                        "L, " +
                        "MiniBoss, " +
                        "Puzzle, " +
                        "Trap, " +
                        "Fairy, " +
                        "Boss, " +
                        "Spawn");
                p.sendMessage("§cUsage: /buildroom <room type>");
                return false;
        }
        file = new File("plugins/Dungeons/samples/" + fileName + ".schematic");

        World world = wc.createWorld();
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");

        if (specialType == null) new BuildRoomWorld(world, roomType, sizeX, sizeZ);
        else new BuildRoomWorld(world, specialType, sizeX, sizeZ);

        Dungeon dung = new Dungeon(world);

        Schematic.paste(file, dung, new Location(world, 3, 4, 3), 0);

        BuildRoomListeners.specialTypes.put(world, specialType);

        movePlayer(p, new Location(world, 0.5, 4, 0.5, 0, 0), true, true);

        return true;
    }

    public static void movePlayer(Player p, Location loc, boolean star, boolean inv) {
        lastLocationMap.put(p, p.getLocation());
        if(inv) inventoryHashMap.put(p, p.getInventory().getContents());

        p.teleport(loc);
        if(inv) p.getInventory().clear();
        if(!star) return;
        p.getInventory().setItem(8, BuildRoomListeners.buildStar());
    }

    public static HashMap<Player, Location> lastLocationMap = new HashMap<>();
    public static HashMap<Player, ItemStack[]> inventoryHashMap = new HashMap<>();

    /*public void actionBarLoop(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(world.getPlayers().isEmpty()) cancel();
                for(Player p : world.getPlayers()) {
                    ActionBar.sendActionBar(p,
                            "§aRoom Size: " + BuildRoomWorld.getWorld(world).getRoomSizeX()
                    );
                }
            }
        }.runTaskTimer(Dungeons.getMain(), 0, 1L);
    }

     */
}
