package me.iantapply.dungeons.dungeons.indevCommands;

import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.dungeons.listeners.BuildRoomListeners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExitCMD extends GKCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(p.getWorld().getName().startsWith("test_")) {
            BuildRoomListeners.exitWorld(p, p.getWorld());
        }
        return false;
    }
}
