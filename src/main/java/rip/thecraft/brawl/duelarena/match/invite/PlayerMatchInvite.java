package rip.thecraft.brawl.duelarena.match.invite;

import rip.thecraft.brawl.duelarena.DuelArenaHandler;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.server.util.chatcolor.CC;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@Getter
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class PlayerMatchInvite {

    public static final ItemStack REMATCH_ITEM;

    private UUID sender;
    private UUID target;
    private MatchLoadout kitType;
    private long sent;
    private boolean rematch;

    static {
        REMATCH_ITEM = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = REMATCH_ITEM.getItemMeta();
        meta.setDisplayName(CC.GRAY + "\u00bb " + CC.GOLD + CC.BOLD + "Rematch" + CC.GRAY + " \u00ab");
        REMATCH_ITEM.setItemMeta(meta);
    }

    public static PlayerMatchInvite createMatchInvite(UUID sender, UUID target, MatchLoadout detailedKitType, boolean isRematch) {

        if (detailedKitType == null) {
//            detailedKitType = Brawl.getInstance().getMatchHandler().getMatchLoadouts().get(0);
        }

        return new PlayerMatchInvite(sender, target, detailedKitType, System.currentTimeMillis(), isRematch);
    }

    public int getLifetime() {
        return ((int) (System.currentTimeMillis() - sent) / 1000);
    }

    public boolean isValid() {
        return (getLifetime() <= DuelArenaHandler.INVITE_TIMEOUT);
    }

}