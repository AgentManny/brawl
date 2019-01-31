package gg.manny.brawl.kit.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.kit.Kit;
import gg.manny.quantum.command.Command;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class KitCommand {

    private final Brawl brawl;

    @Command(names = { "kit", "k" })
    public void apply(Player player, Kit kit) {
        kit.apply(player);
    }
}
