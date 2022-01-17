package rip.thecraft.brawl.spawn.challenges.menu;

import gg.manny.streamline.menu.Menu;
import gg.manny.streamline.menu.MenuButton;
import gg.manny.streamline.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.spawn.challenges.Challenge;
import rip.thecraft.brawl.spawn.challenges.player.ChallengeTracker;
import rip.thecraft.brawl.spawn.challenges.player.PlayerChallenge;

import java.util.Map;

public class QuestMenu extends Menu {

    public QuestMenu() {
        super("Quest Master");
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        ChallengeTracker challengeTracker = playerData.getChallengeTracker();
        MenuButton infoButton = new MenuButton(Material.NETHER_STAR,
                ChatColor.DARK_PURPLE + "Quests",
                ChatColor.GRAY + "Complete objectives to receive rewards"
        );

        buttons.put(12, infoButton);

        int i = 20;
        for (Map.Entry<Challenge.Duration, PlayerChallenge> entry : challengeTracker.getChallenges().entries()) {
            Challenge.Duration duration = entry.getKey();
            PlayerChallenge challengeData = entry.getValue();
            Challenge challenge = challengeData.getChallenge();

            ItemStack icon = new ItemBuilder(Material.PAPER)
                    .name(ChatColor.LIGHT_PURPLE + duration.getDisplayName() + ": " + challenge.getName())
                    .description(challenge.getDescription(), ChatColor.GRAY.toString())
                    .build();
            MenuButton challengeButton = new MenuButton(icon);
            challengeButton.setClick(new ProcessChallenge());
            buttons.put(i++, challengeButton);
        }
    }

    private class ProcessChallenge implements MenuButton.ButtonClick {

        @Override
        public void click(Player player, MenuButton.ClickData clickData) {

        }
    }

}
