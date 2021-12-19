package rip.thecraft.brawl.event.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.event.menu.EventsMenu;
import rip.thecraft.spartan.command.Command;

public class EventCommand {

    @Command(names = { "event", "events" }, permission = "op")
    public static void execute(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** General commands ***");
        sender.sendMessage(ChatColor.GRAY + "/event list - List all events");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** Manage commands ***");
        sender.sendMessage(ChatColor.GRAY + "/event create <eventType> <name> - Creates the event");
        sender.sendMessage(ChatColor.GRAY + "/event remove <name> - Removes an event");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** KOTH commands ****");
        sender.sendMessage(ChatColor.GRAY + "/koth setcapdelay <name<> <time> - Sets the capture delay");
        sender.sendMessage(ChatColor.GRAY + "/koth setcapzone - Sets the capture zone (/region wand)");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** Kill THe King commands ****");
        sender.sendMessage(ChatColor.GRAY + "/ktk start <player> - Manually chose a player.");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** High Ground commands ****");
        sender.sendMessage(ChatColor.GRAY + "/highground setcapzone - Sets the capture zone (/region wand)");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.DARK_PURPLE + "*** Hosting commands ***");
        sender.sendMessage(ChatColor.GRAY + "/event start <event> - Starts an event.");
        sender.sendMessage(ChatColor.GRAY + "/event stop <event> - Stops an active event. (forcefully)");
        sender.sendMessage(" ");
    }

    @Command(names = { "event list", "events list" }, permission = "op")
    public static void eventList(Player sender) {
        new EventsMenu().open(sender);
    }
}
