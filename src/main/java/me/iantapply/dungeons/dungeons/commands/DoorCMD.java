package me.iantapply.dungeons.dungeons.commands;

import me.iantapply.dungeons.developerkit.commands.GKCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static me.iantapply.dungeons.dungeons.utils.doors.DoorListener.doorSpeed;

public class DoorCMD extends GKCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if(args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Please put one double.");
            return false;
        }

        try {
            doorSpeed = Double.parseDouble(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Please put one double.");
            return false;
        }

         */

        sender.sendMessage(ChatColor.GREEN + "Set door speed to " + doorSpeed);

        return true;
    }
}
