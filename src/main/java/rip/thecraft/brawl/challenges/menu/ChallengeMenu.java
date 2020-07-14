package rip.thecraft.brawl.challenges.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;
import rip.thecraft.spartan.menu.Menu;
import rip.thecraft.spartan.menu.menus.ConfirmMenu;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.List;
import java.util.Map;

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

        buttonMap.put(11, new DailyChallengeButton());
        buttonMap.put(15, new WeeklyChallengeButton());

        int dailyChallenges = 18;
        for (Challenge challenge : Brawl.getInstance().getChallengeHandler().getDailyChallenges()) {
            buttonMap.put(++dailyChallenges, new ChallengeButton(challenge));
        }

        int weeklyChallenges = 22;
        for (Challenge challenge : Brawl.getInstance().getChallengeHandler().getWeeklyChallenges()) {
            buttonMap.put(++weeklyChallenges, new ChallengeButton(challenge));
        }

        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    private class DailyChallengeButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.BLUE + "Daily Challenges";
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_BARDING;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = Lists.newArrayList();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            lore.add(CC.GRAY + "Select a challenge to complete");
            lore.add(CC.GRAY + "within " + CC.WHITE + "24 hours" + CC.GRAY + " to receive a reward.");

            if (playerData.hasActiveDailyChallenge()) {
                Challenge challenge = playerData.getDailyChallenge();
                lore.add(" ");
                lore.add(CC.GRAY + "Progress: " + CC.WHITE + playerData.getChallengeProgress(challenge.getChallengeType()) + "% " + CC.GRAY + "(" + challenge.getCurrentProgress() + "/" + challenge.getMaxProgress() + ")");
                lore.add(CC.GRAY + "You have " + CC.WHITE + TimeUtils.formatLongIntoDetailedString(((challenge.getTimestamp() + challenge.getChallengeType().getMillis()) - System.currentTimeMillis()) / 1000) + " " + CC.GRAY + "remaining.");
            }

            return lore;
        }
    }

    private class WeeklyChallengeButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.AQUA + "Weekly Challenges";
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_BARDING;
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = Lists.newArrayList();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            lore.add(CC.GRAY + "Select a challenge to complete");
            lore.add(CC.GRAY + "within " + CC.WHITE + "7 days" + CC.GRAY + " to receive a reward.");

            if (playerData.hasActiveWeeklyChallenge()) {
                Challenge challenge = playerData.getWeeklyChallenge();
                lore.add(" ");
                lore.add(CC.GRAY + "Progress: " + CC.WHITE + playerData.getChallengeProgress(challenge.getChallengeType()) + "% " + CC.GRAY + "(" + challenge.getCurrentProgress() + "/" + challenge.getMaxProgress() + ")");
                lore.add(CC.GRAY + "You have " + CC.WHITE + TimeUtils.formatLongIntoDetailedString(((challenge.getTimestamp() + challenge.getChallengeType().getMillis()) - System.currentTimeMillis()) / 1000) + " " + CC.GRAY + "remaining.");
            }

            return lore;
        }
    }

    @AllArgsConstructor
    private class ChallengeButton extends Button {

        private Challenge challenge;

        @Override
        public String getName(Player player) {
            return (challenge.getChallengeType() == ChallengeType.DAILY ? CC.BLUE : CC.AQUA) + challenge.getDisplayName();
        }

        @Override
        public Material getMaterial(Player player) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            if (challenge.getChallengeType() == ChallengeType.DAILY && playerData.hasActiveDailyChallenge() && playerData.getDailyChallenge().getName().equalsIgnoreCase(challenge.getName())) {
                return Material.EMPTY_MAP;
            }

            if (challenge.getChallengeType() == ChallengeType.WEEKLY && playerData.hasActiveWeeklyChallenge() && playerData.getWeeklyChallenge().getName().equalsIgnoreCase(challenge.getName())) {
                return Material.EMPTY_MAP;
            }
            return Material.PAPER;
        }


        @Override
        public List<String> getDescription(Player player) {
            List<String> lines = Lists.newArrayList();
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

            if ((challenge.getChallengeType() == ChallengeType.DAILY && !playerData.hasActiveDailyChallenge()) || (challenge.getChallengeType() == ChallengeType.WEEKLY && !playerData.hasActiveWeeklyChallenge())){
                lines.add(CC.GRAY + "Description: " + CC.WHITE + challenge.getDescription());
                lines.add(" ");
                lines.add(CC.GRAY + "\u00bb " + CC.YELLOW + "Click to activate this challenge" + CC.GRAY + " \u00ab");
                lines.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
                lines.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
                return lines;
            }

            if (challenge.getChallengeType() == ChallengeType.DAILY && playerData.hasActiveDailyChallenge() && playerData.getDailyChallenge().getName().equalsIgnoreCase(challenge.getName())) {
                lines.add(CC.GREEN + "This challenge is currently activated.");
            }

            if (challenge.getChallengeType() == ChallengeType.WEEKLY && playerData.hasActiveWeeklyChallenge() && playerData.getWeeklyChallenge().getName().equalsIgnoreCase(challenge.getName())) {
                lines.add(CC.GREEN + "This challenge is currently activated.");
            }

            lines.add(" ");
            lines.add(CC.GRAY + "Description: " + CC.WHITE + challenge.getDescription());
            lines.add(CC.GRAY + "Progress: " + CC.WHITE + playerData.getChallengeProgress(challenge.getChallengeType()) + "% " + CC.GRAY + "(" + challenge.getCurrentProgress() + "/" + challenge.getMaxProgress() + ")");
            lines.add(" ");
            lines.add(CC.GRAY + "You have " + CC.WHITE + TimeUtils.formatLongIntoDetailedString(playerData.getChallengeTimeRemaining(challenge.getChallengeType()) / 1000) + " " + CC.GRAY + "remaining.");

            lines.add(0, CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            lines.add(CC.GRAY + CC.STRIKETHROUGH + Strings.repeat("-", 31));
            return lines;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            challenge.setTimestamp(System.currentTimeMillis());
            if ((challenge.getChallengeType() == ChallengeType.DAILY && playerData.hasActiveDailyChallenge()) ||  (challenge.getChallengeType() == ChallengeType.WEEKLY && playerData.hasActiveWeeklyChallenge())) {
                // open confirm menu
                new ConfirmMenu("Are you sure?", data -> {
                    if (data) {
                        setChallenge(playerData, challenge);
                    } else {
                        player.closeInventory();
                    }
                }).openMenu(player);
                player.sendMessage(CC.RED + CC.BOLD + "WARNING! " + CC.RED + "Activating a new challenge while you already have one active will reset its progress.");
                return;
            }


            setChallenge(playerData, challenge);

            player.closeInventory();
        }

        private void setChallenge(PlayerData playerData, Challenge challenge) {
            challenge.setTimestamp(System.currentTimeMillis());
            challenge.setCurrentProgress(0);
            if (challenge.getChallengeType() == ChallengeType.DAILY) {
                playerData.setDailyChallenge(challenge);
            } else {
                playerData.setWeeklyChallenge(challenge);
            }
            playerData.getPlayer().sendMessage(CC.GREEN + "You have activated the " + challenge.getDisplayName() + " challenge.");
        }
    }
}
