package me.iantapply.dungeons.dungeons.indevCommands;

import me.iantapply.dungeons.developerkit.commands.GKCommand;
import me.iantapply.dungeons.dungeons.utils.npcs.MortNPC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnMortCMD extends GKCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        if (args.length != 3) {
            MortNPC.spawnMortGateKepper(p.getLocation());
        }

        if (args.length == 3) {
            MortNPC.spawnMortGateKepper(new Location(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
        }

        p.sendMessage("Successfully spawned mort!");
        return false;
    }
}
