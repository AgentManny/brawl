package gg.manny.brawl.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.player.PlayerData;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class BuildCommand {

    private final Brawl plugin;

    @Command(names = "build", permission = "op")
    public void execute(Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
        playerData.setBuild(!playerData.isBuild());
        player.sendMessage(CC.GOLD + "Set build to " + (playerData.isBuild() ? CC.GREEN + "enabled" : CC.RED + "disabled") + CC.GOLD + ".");
    }
}
