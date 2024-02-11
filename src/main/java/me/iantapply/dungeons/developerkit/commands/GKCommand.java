package me.iantapply.dungeons.developerkit.commands;

import me.iantapply.dungeons.developerkit.GKBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class GKCommand extends GKBase implements CommandExecutor {

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
