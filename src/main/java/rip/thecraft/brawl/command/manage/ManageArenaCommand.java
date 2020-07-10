package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.arena.Arena;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class ManageArenaCommand {

    @Command(names = "arena create", permission = "op")
    public static void create(Player sender, String name, @Param(defaultValue = "NORMAL", name  = "arenaType") String type) {
        ArenaType arenaType;
        try {
            arenaType = ArenaType.valueOf(type);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Arena type " + type + " not found.");
            return;
        }

        Arena arena = Arena.create(name, arenaType, sender.getLocation(), sender.getLocation());
        DuelArenaHandler ah = Brawl.getInstance().getMatchHandler();

        ah.getArenas().add(arena);
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Created " + CC.LIGHT_PURPLE + name + CC.YELLOW + " arena with values: [" + arena.toString() + "]");
        sender.sendMessage(CC.YELLOW + "What's next?");
        sender.sendMessage(CC.YELLOW + " - Set first location " + CC.LIGHT_PURPLE + "/arena setfirstloc " + CC.BOLD + arena.getName());
        sender.sendMessage(CC.YELLOW + " - Set second location " + CC.LIGHT_PURPLE + "/arena setsecondloc " + CC.BOLD + arena.getName());
        sender.sendMessage(CC.YELLOW + " - Set arena type " + CC.LIGHT_PURPLE + "/arena settype " + CC.BOLD + arena.getName() + CC.LIGHT_PURPLE + " [arenaType]");
    }

    @Command(names = "arena settype", permission = "op")
    public static void create(Player sender, Arena arena, @Param(defaultValue = "NORMAL", name = "arenaType") String type) {
        ArenaType arenaType;
        try {
            arenaType = ArenaType.valueOf(type);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Arena type " + type + " not found.");
            return;
        }

        arena.setArenaType(arenaType);
        sender.sendMessage(CC.YELLOW + "Set " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " type to " + CC.LIGHT_PURPLE + arenaType.name() + CC.YELLOW + ".");
    }

    @Command(names = "arena list", permission = "op")
    public static void list(Player sender) {
        DuelArenaHandler ah = Brawl.getInstance().getMatchHandler();
        sender.sendMessage(CC.DARK_PURPLE + "Arenas (" + ah.getArenas().size() + "):");
        for (Arena arena : ah.getArenas()) {
            arena.getFancyDisplay().send(sender);
        }
    }

    @Command(names = "arena remove", permission = "op")
    public static void remove(Player sender, Arena arena) {
        DuelArenaHandler ah = Brawl.getInstance().getMatchHandler();

        ah.getArenas().remove(arena);
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Removed " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "arena setfirstloc", permission = "op")
    public static void setfirstloc(Player sender, Arena arena) {
        DuelArenaHandler ah = Brawl.getInstance().getMatchHandler();

        arena.getLocations()[0] = sender.getLocation();
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Updated first location for " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "arena setsecondloc", permission = "op")
    public static void setsecondloc(Player sender, Arena arena) {
        DuelArenaHandler ah = Brawl.getInstance().getMatchHandler();

        arena.getLocations()[1] = sender.getLocation();
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Updated second location for " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "arena toggle", permission = "op")
    public static void toggle(Player sender, Arena arena) {
        arena.setEnabled(!arena.isEnabled());
        sender.sendMessage(CC.YELLOW + "You have " + (arena.isEnabled() ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.YELLOW + " arena " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + ".");
    }

}
