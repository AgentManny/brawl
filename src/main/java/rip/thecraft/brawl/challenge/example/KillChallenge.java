package rip.thecraft.brawl.challenge.example;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenge.PlayerChallenge;
import rip.thecraft.brawl.challenge.type.IntegerChallenge;

public class KillChallenge extends IntegerChallenge {

    public KillChallenge() {
        super("Kill enemies", 10);
    }

    @Override
    public void complete(Player player, PlayerChallenge challenge) {
        player.sendMessage(ChatColor.GREEN + "You've completed your " + this.name + " challenge. You've been rewarded");
    }
}
