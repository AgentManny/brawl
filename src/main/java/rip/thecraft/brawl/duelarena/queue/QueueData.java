package rip.thecraft.brawl.duelarena.queue;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.server.item.type.MetadataType;
import rip.thecraft.brawl.util.HiddenStringUtils;
import rip.thecraft.server.util.chatcolor.CC;

import java.util.List;

@Data
public class QueueData {

    private QueueSearchTask task;

    private long queueTime = -1L;

    public void updateQuickQueue(Player player) {
        boolean quickQueue = Brawl.getInstance().getMatchHandler().hasQuickmatch();
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.hasItemMeta()) {
                List<String> lore = item.getItemMeta().getLore();
                if (lore != null && lore.size() > 0 && HiddenStringUtils.hasHiddenString(lore.get(0))) {
                    String metaData = HiddenStringUtils.extractHiddenString(lore.get(0));
                    if (MetadataType.isMetadata(metaData) && MetadataType.fromMetadata(metaData) == MetadataType.DUEL_ARENA_QUICK_QUEUE) {
                        item.setDurability((short) (quickQueue ? 5 : 8));
                        String displayName = CC.translate("&7» &" + (quickQueue ? "5" : "a") + "&lQuick Queue &7«");
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName(displayName);
                        item.setItemMeta(itemMeta);
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
