package gg.manny.brawl.duelarena.queue;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.item.type.MetadataType;
import gg.manny.brawl.util.HiddenStringUtils;
import gg.manny.pivot.util.chatcolor.CC;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class QueueData {

    private QueueSearchTask task;

    private long queueTime = -1L;

    public void updateQuickQueue(Player player) {
        boolean quickQueue = Brawl.getInstance().getMatchHandler().getQuickmatch() != null;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.hasItemMeta()) {
                List<String> lore = item.getItemMeta().getLore();
                if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                    String metaData = HiddenStringUtils.extractHiddenString(lore.get(0));
                    if (MetadataType.isMetadata(metaData) && MetadataType.fromMetadata(metaData) == MetadataType.DUEL_ARENA_QUICK_QUEUE) {
                        item.setDurability((short) (quickQueue ? 5 : 8));
                        String displayName = CC.translate("&7» &" + (quickQueue ? "5" : "a") + "&lQuick Queue &7«");
                        item.getItemMeta().setDisplayName(displayName);
                        break;
                    }
                }
            }
        }
    }

    public boolean inQueue() {
        return queueTime > -1;
    }

}
