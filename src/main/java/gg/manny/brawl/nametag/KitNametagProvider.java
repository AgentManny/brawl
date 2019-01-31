package gg.manny.brawl.nametag;

import gg.manny.brawl.Brawl;
import gg.manny.pivot.Pivot;
import gg.manny.pivot.nametag.Nametag;
import gg.manny.pivot.nametag.NametagProvider;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class KitNametagProvider extends NametagProvider {

    private final Brawl plugin;

    public KitNametagProvider(Brawl plugin) {
        super("KitPvP Nametag Provider", 50);

        this.plugin = plugin;
    }

    @Override
    public Nametag fetchNametag(Player toRefresh, Player refreshFor) {

        Scoreboard scoreboard = toRefresh.getScoreboard();
        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective("health") == null ? scoreboard.registerNewObjective("health", "health") : scoreboard.getObjective("health");
            objective.setDisplayName(CC.DARK_RED + "\u2764");
            objective.getScore(refreshFor).setScore((int) toRefresh.getHealth());
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        return createNametag(Pivot.getPlugin().getProfileHandler().getProfile(toRefresh).getRank().getColor(), "");
    }


}
