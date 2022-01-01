package rip.thecraft.brawl.challenges.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import gg.manny.streamline.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.player.ChallengeTracker;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChallengeMenu extends Menu {

    {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Challenges";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();



        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    @AllArgsConstructor
    private class ChallengeButton extends Button {

        private PlayerData playerData;

        private ChallengeTracker tracker;
        private PlayerChallenge playerChallenge;
        private Challenge challenge;

        @Override
        public String getName(Player player) {
            return ChatColor.LIGHT_PURPLE + challenge.getDuration().getDisplayName() + " Challenge: " + ChatColor.WHITE + challenge.getName();
        }

        @Override
        public Material getMaterial(Player player) {
            return playerChallenge.isActive() ? Material.PAPER : Material.EMPTY_MAP;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = ItemBuilder.wrap(challenge.getDescription(), CC.GRAY, 30, false);

            String timeLeft = ChatColor.GRAY + "Expires " + (challenge.getDuration() == Challenge.Duration.WEEKLY ? "at " + ChatColor.WHITE + WordUtils.capitalizeFully(ChallengeTracker.WEEKLY_RESET_DAY.name().toLowerCase()) + " at " + ChallengeTracker.RESET_TIME.format(ChallengeTracker.TIME_FORMATTER) : ChatColor.GRAY + "in " + ChatColor.WHITE + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(tracker.getDailyExpiry())));
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Progress: " + ChatColor.WHITE + playerChallenge.getProgress() + "%" + ChatColor.GRAY + " (" + ChatColor.WHITE + playerChallenge.getProgress() + "/" + challenge.getMaxValue() + ChatColor.GRAY + ")");
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Rewards:");
            challenge.getRewards().forEach((reward, amount) -> lore.add(ChatColor.GRAY + " +" + reward.getColor() + amount + ChatColor.GRAY + " " + reward.getName()));

            lore.add(" ");
            if (playerChallenge.isActive()) {
                lore.add(CC.GRAY + "\u00bb " + CC.GREEN + "Challenge active: " + ChatColor.WHITE + timeLeft + CC.GRAY + " \u00ab");
            } else {
                lore.add(CC.GRAY + "\u00bb " + CC.RED + "Challenge already completed" + CC.GRAY + " \u00ab");
            }

            lore.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            lore.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return lore;
        }
    }
}
