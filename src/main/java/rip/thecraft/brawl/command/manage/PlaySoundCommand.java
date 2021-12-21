package rip.thecraft.brawl.command.manage;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.Param;

public class PlaySoundCommand {

    @Command(names = "playsound", permission = "op")
    public static void playSound(Player sender, Sound sound, @Param(defaultValue = "1") float pitch, @Param(defaultValue = "1") float volume) {
        sender.sendMessage(ChatColor.GREEN + "Playing sound: " + sound.name() + " (" + volume + " vol : " + pitch + " pitch)");
        sender.playSound(sender.getLocation(), sound, volume, pitch);
    }

}
