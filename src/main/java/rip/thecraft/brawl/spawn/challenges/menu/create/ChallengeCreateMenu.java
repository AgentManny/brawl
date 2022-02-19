package rip.thecraft.brawl.spawn.challenges.menu.create;

import gg.manny.streamline.menu.Menu;
import gg.manny.streamline.menu.MenuButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.spawn.challenges.Challenge;
import rip.thecraft.brawl.spawn.challenges.ChallengeHandler;
import rip.thecraft.brawl.spawn.challenges.ChallengeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeCreateMenu extends Menu {

    private final ChallengeHandler challengeHandler;

    private String name;
    private Challenge challenge;

    private Map<String, String> properties = new HashMap<>();

    public ChallengeCreateMenu(ChallengeHandler challengeHandler, String name) {
        super("Challenge Builder - Type");
        this.challengeHandler = challengeHandler;
        this.challenge = new Challenge();
        this.challenge.setName(this.name = name);
        properties.put("Name", name);
    }

    @Override
    public void init(Player player, Map<Integer, MenuButton> buttons) {
        buttons.put(4, preview());
        int x = 1;
        int y = 1;
        for (ChallengeType type : ChallengeType.values()) {
            MenuButton button = new MenuButton(type.getIcon(), ChatColor.YELLOW + type.getDisplayName())
                    .setClick((clicker, clickData) -> {
                        if (clickData.getClickType().isLeftClick()) {
                            player.sendMessage(ChatColor.YELLOW + "Open menu to something else");
                            challenge.setType(type);
                            properties.put("Type", type.getDisplayName());
                            new DurationMenu().open(clicker);
                        }
                    });
            buttons.put(getSlot(x, y), button);
            if (x++ >= 7) {
                x = 1;
                y++;
            }
        }
    }

    private MenuButton preview() {
        List<String> lore = new ArrayList<>();
        properties.forEach((key, value) -> lore.add(ChatColor.GRAY + key + ": " + ChatColor.WHITE + value));
        return new MenuButton(Material.NETHER_STAR, ChatColor.GREEN.toString() + ChatColor.BOLD + challenge.getName(), lore.toArray(new String[] { }));
    }

    private class DurationMenu extends Menu {

        public DurationMenu() {
            super("Challenge Builder - Duration");
        }

        @Override
        public void init(Player player, Map<Integer, MenuButton> buttons) {
            buttons.put(4, preview());
            int x = 12;
            for (Challenge.Duration type : Challenge.Duration.values()) {
                MenuButton button = new MenuButton(Material.WATCH, ChatColor.YELLOW + type.getDisplayName())
                        .setClick((clicker, clickData) -> {
                            if (clickData.getClickType().isLeftClick()) {
                                player.sendMessage(ChatColor.YELLOW + "Open menu to something else");
                                challenge.setDuration(type);
                                properties.put("Duration", type.getDisplayName());
                                new DescriptionMenu().open(player);
                            }
                        });
                buttons.put(x++, button);
            }
        }

        @Override
        public int size(Map<Integer, MenuButton> buttons) {
            return 27;
        }
    }

    private class DescriptionMenu extends Menu {

        public DescriptionMenu() {
            super("Challenge Builder - Description");
        }

        @Override
        public void init(Player player, Map<Integer, MenuButton> buttons) {
            buttons.put(4, preview());
            buttons.put(12, new MenuButton(Material.STAINED_CLAY, 5, ChatColor.GREEN + "Custom Description", ChatColor.GRAY + "Add a custom description")
                    .setClick((clicker, clickData) -> {
                        clicker.sendMessage(ChatColor.GREEN + "Type your custom description");
                    }));

            buttons.put(14, new MenuButton(Material.STAINED_CLAY, 14, ChatColor.RED + "No Description", ChatColor.GRAY + "Descriptions are automatically generated based")
                    .setClick((clicker, clickData) -> clicker.sendMessage("Cancelled custom desc")));
        }

        @Override
        public int size(Map<Integer, MenuButton> buttons) {
            return 27;
        }
    }
}
