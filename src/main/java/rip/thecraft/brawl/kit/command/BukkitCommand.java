package rip.thecraft.brawl.kit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.util.BrawlUtil;

public class BukkitCommand extends Command {

    public BukkitCommand(String name) {
        super(name.toLowerCase());

        BrawlUtil.registerCommand(this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        ((Player)sender).chat("/kit " + this.getName());
        return true;
    }
}
