package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.util.BrawlUtil;
import gg.manny.quantum.command.Command;
import gg.manny.server.config.MineConfig;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

@RequiredArgsConstructor
public class BrawlCommand {

    private final Brawl plugin;

    @Command(names = "brawl")
    public void execute(CommandSender sender) {
        sender.sendMessage(CC.GOLD + "Brawl " + CC.GRAY + "(Version: " + CC.WHITE + plugin.getDescription().getVersion() + CC.GRAY + ")");
        sender.sendMessage(CC.GOLD + "Created by " + CC.WHITE + String.join(", ", plugin.getDescription().getAuthors()));
    }

    @Command(names = "brawl optimise", permission = "op")
    public void optimise(CommandSender sender) {
        sender.sendMessage(CC.GOLD + "Optimising workload suited towards Brawl");
        if (BrawlUtil.isMineSpigot()) {

            sender.sendMessage(CC.GREEN + "Injected mSpigot optimisations");
            long time = System.currentTimeMillis();
            MineConfig config = MineConfig.get();

            header(sender, "Timings", () -> {
                execute(sender, "Block Tick", "false", () -> config.setDisableBlockTick(true));
                execute(sender, "Tile Entities Tick", "false", () -> config.setDisableTileEntityTick(true));
                execute(sender, "Recheck Gaps", "false", () -> config.setDisableRecheckGaps(true));
                execute(sender, "Weather", "false", () -> config.setDisableTickingWeather(true));
                execute(sender, "Biome Cache", "false", () -> config.setDisableTickingWeather(true));
                execute(sender, "Sleep Check", "false", () -> config.setDisableTickingSleepCheck(true));
                execute(sender, "Villages", "false", () -> config.setDisableTickingVillages(true));
                execute(sender, "Chunks", "false", () -> config.setDisableTickingChunks(true));
                execute(sender, "Player Maps", "false", () -> config.setDisableTickingMaps(true));
            });

            header(sender, "Enderpearls", () -> {
                execute(sender, "Taliban", "false", () -> config.setEnderpearlTaliban(false));
                execute(sender, "Pearl through gates", "false", () -> config.setEnderpearlGates(false));
                execute(sender, "Pearl through tripwires", "false", () -> config.setEnderpearlTripwire(false));
            });

            header(sender, "Entities", () -> {
                execute(sender, "Mob Stacking", "false", () -> config.setMobStackingEnabled(false));

                execute(sender, "Entity Collisions", "false", () -> config.setDisableEntityCollisions(true));
                execute(sender, "Entity AI", "true", () -> config.setDisableEntityAI(false));
            });

            header(sender, "Events", () -> {
                execute(sender, "Bukkit PlayerMoveEvent", "false", () -> config.setDisableEventPlayerMove(true));
                execute(sender, "Bukkit PlayerInteractEvent Block", "true", () -> config.setDisableEventLeftClickBlock(true));
                execute(sender, "Bukkit PlayerInteractEvent Air", "true", () -> config.setDisableEventLeftClickAir(true));
            });

            header(sender, "Other", () -> {
                execute(sender, "Hide Players Tab", "false", () -> config.setHidePlayersFromTab(false));
                execute(sender, "Chunk Unloading", "false", () -> config.setDisableUnloadingChunks(false));
                execute(sender, "Weather Changing", "false", () -> config.setDisableTickingWeather(true));
            });
            config.saveConfig();
            sender.sendMessage(CC.GREEN + "Finished & Saved in " + CC.WHITE + (System.currentTimeMillis() - time) + "ms" + CC.GREEN + ".");
        }
    }

    private static void execute(CommandSender sender, String name, String value, Runnable runnable) {
        sender.sendMessage(CC.GOLD + "Updated " + CC.WHITE + name + CC.GOLD + " to " + value);
        Brawl.getInstance().getLogger().info("> " + name + ": " + value);
        runnable.run();
    }

    private static void header(CommandSender sender, String name, Runnable runnable) {
        sender.sendMessage(CC.GOLD + "--- [" + CC.WHITE + name + CC.GOLD + "] ---");
        Brawl.getInstance().getLogger().info("-------- [" + name + "] --------");
        runnable.run();
    }

    @Command(names = "brawl reload", permission = "brawl.command.config")
    public void reload(CommandSender sender) {
        try {
            plugin.getMainConfig().getConfiguration().load(plugin.getMainConfig().getFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Failed to reload Brawl.");
        } finally {
            sender.sendMessage(ChatColor.GREEN + "Reloaded Brawl.");
        }
    }
}
