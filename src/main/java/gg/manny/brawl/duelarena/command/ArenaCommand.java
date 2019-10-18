package gg.manny.brawl.duelarena.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.DuelArenaHandler;
import gg.manny.brawl.duelarena.arena.Arena;
import gg.manny.brawl.duelarena.arena.ArenaType;
import gg.manny.pivot.util.chatcolor.CC;
import gg.manny.quantum.command.Command;
import gg.manny.quantum.command.Parameter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Command(names = "arena", permission = "op")
public class ArenaCommand {

    private final Brawl plugin;

    @Command(names = "create", permission = "op")
    public void create(Player sender, String name, @Parameter(value = "NORMAL", name = "arenaType") String type) {
        ArenaType arenaType;
        try {
            arenaType = ArenaType.valueOf(type);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Arena type " + type + " not found.");
            return;
        }

        Arena arena = Arena.create(name, arenaType, sender.getLocation(), sender.getLocation());
        DuelArenaHandler ah = plugin.getMatchHandler();

        ah.getArenas().add(arena);
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Created " + CC.LIGHT_PURPLE + name + CC.YELLOW + " arena with values: [" + arena.toString() + "]");
        sender.sendMessage(CC.YELLOW + "What's next?");
        sender.sendMessage(CC.YELLOW + " - Set first location " + CC.LIGHT_PURPLE + "/arena setfirstloc " + CC.BOLD + arena.getName());
        sender.sendMessage(CC.YELLOW + " - Set second location " + CC.LIGHT_PURPLE + "/arena setsecondloc " + CC.BOLD + arena.getName());
        sender.sendMessage(CC.YELLOW + " - Set arena type " + CC.LIGHT_PURPLE + "/arena settype " + CC.BOLD + arena.getName() + CC.LIGHT_PURPLE + " [arenaType]");
    }

    @Command(names = "settype", permission = "op")
    public void create(Player sender, Arena arena, @Parameter(value = "NORMAL", name = "arenaType") String type) {
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

    @Command(names = "list", permission = "op")
    public void list(Player sender) {
        DuelArenaHandler ah = plugin.getMatchHandler();
        sender.sendMessage(CC.DARK_PURPLE + "Arenas (" + ah.getArenas().size() + "):");
        for (Arena arena : ah.getArenas()) {
            arena.getFancyDisplay().send(sender);
        }
    }

    @Command(names = "remove", permission = "op")
    public void remove(Player sender, Arena arena) {
        DuelArenaHandler ah = plugin.getMatchHandler();

        ah.getArenas().remove(arena);
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Removed " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "setfirstloc", permission = "op")
    public void setfirstloc(Player sender, Arena arena) {
        DuelArenaHandler ah = plugin.getMatchHandler();

        arena.getLocations()[0] = sender.getLocation();
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Updated first location for " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "setsecondloc", permission = "op")
    public void setsecondloc(Player sender, Arena arena) {
        DuelArenaHandler ah = plugin.getMatchHandler();

        arena.getLocations()[1] = sender.getLocation();
        ah.saveArenas();

        sender.sendMessage(CC.YELLOW + "Updated second location for " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + " arena.");
    }

    @Command(names = "toggle", permission = "op")
    public void toggle(Player sender, Arena arena) {
        arena.setEnabled(!arena.isEnabled());
        sender.sendMessage(CC.YELLOW + "You have " + (arena.isEnabled() ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.YELLOW + " arena " + CC.LIGHT_PURPLE + arena.getName() + CC.YELLOW + ".");
    }

}
