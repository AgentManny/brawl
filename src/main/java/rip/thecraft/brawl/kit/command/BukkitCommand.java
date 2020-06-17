package rip.thecraft.brawl.kit.command;

import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.util.BrawlUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommand extends Command {

    private final Brawl brawl;

    public BukkitCommand(Brawl brawl, String name) {
        super(name.toLowerCase());

        this.brawl = brawl;
        BrawlUtil.registerCommand(this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        ((Player)sender).chat("/kit " + this.getName());
        return true;
    }
}
