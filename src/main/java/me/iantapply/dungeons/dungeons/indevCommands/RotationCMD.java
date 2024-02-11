package me.iantapply.dungeons.dungeons.indevCommands;

import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.dungeons.utils.worlds.Dungeon;
import me.iantapply.dungeons.dungeons.utils.schematics.Schematic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class RotationCMD extends GKCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File schematic = new File("plugins/WorldEdit/schematics/" + args[0] + ".schematic");
        Player player = (Player) sender;
        Schematic.paste(schematic, new Dungeon(player.getWorld()), player.getLocation(), Double.parseDouble(args[1]), Boolean.parseBoolean(args[2]));
        return false;
    }
}
