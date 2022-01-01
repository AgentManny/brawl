package rip.thecraft.brawl.spawn.event.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.spawn.event.EventHandler;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;

public class EventSaveCommand {

    @Command(names = "event save", permission = "op", description = "Saves events to disk")
    public static void set(CommandSender sender) {
        EventHandler eh = Brawl.getInstance().getEventHandler();
        long startMs = System.currentTimeMillis();

        eh.save();

        int time = (int) (System.currentTimeMillis() - startMs);
        Brawl.broadcastOps(ChatColor.GREEN + "Saved events (Completed: " + CC.WHITE + time + "ms" + CC.GREEN + ")");
    }
}