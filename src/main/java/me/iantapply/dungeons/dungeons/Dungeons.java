package me.iantapply.dungeons.dungeons;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.iantapply.dungeons.dungeons.commands.BuildRoomCMD;
import me.iantapply.dungeons.dungeons.commands.DoorCMD;
import me.iantapply.dungeons.dungeons.commands.GenerateCMD;
import me.iantapply.dungeons.dungeons.indevCommands.ExitCMD;
import me.iantapply.dungeons.dungeons.indevCommands.GUICMD;
import me.iantapply.dungeons.dungeons.indevCommands.RotationCMD;
import me.iantapply.dungeons.dungeons.indevCommands.SpawnMortCMD;
import me.iantapply.dungeons.dungeons.listeners.BuildRoomListeners;
import me.iantapply.dungeons.dungeons.listeners.MortClickEvent;
import me.iantapply.dungeons.dungeons.listeners.RightClickListener;
import me.iantapply.dungeons.dungeons.utils.doors.DoorListener;
import me.iantapply.dungeons.dungeons.utils.files.PluginFolder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Dungeons extends JavaPlugin {

    @Getter public static Dungeons main;
    @Getter public static WorldEditPlugin worldEdit;

    @Override
    public void onEnable() {
        main = this;

        // Listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new RightClickListener(), this);
        pm.registerEvents(new BuildRoomListeners(), this);
        pm.registerEvents(new DoorListener(), this);
        pm.registerEvents(new MortClickEvent(), this);

        // Commands
        getCommand("generate").setExecutor(new GenerateCMD());
        getCommand("buildroom").setExecutor(new BuildRoomCMD());
        getCommand("rotation").setExecutor(new RotationCMD());
        getCommand("exit").setExecutor(new ExitCMD());
        getCommand("guitest").setExecutor(new GUICMD());
        getCommand("door").setExecutor(new DoorCMD());
        getCommand("spawnmort").setExecutor(new SpawnMortCMD());

        // Hook into WorldEdit
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        // File creation/updating
        PluginFolder.copyFiles();

        // Send enabled message
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Dungeons has been enabled!");
    }

    @SneakyThrows @Override
    public void onDisable() {

        // Unload every test world and exit the players
        for(World world : Bukkit.getWorlds()) {
            if(world.getName().startsWith("test_") || world.getName().startsWith("buildroom_")) {
                for (Player p : world.getPlayers()) {
                    BuildRoomListeners.exitWorld(p, world);
                }
                Bukkit.getServer().unloadWorld(world, true);
                File dir = new File(world.getName());
                if (dir.exists()) {
                    org.apache.commons.io.FileUtils.deleteDirectory(dir);
                }
            }
        }

        // Send disabled message
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Dungeons has been disabled!");
    }

    public static Dungeons getInstance() {
        return main;
    }
}
