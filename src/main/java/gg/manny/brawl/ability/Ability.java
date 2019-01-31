package gg.manny.brawl.ability;

import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public interface Ability {

    String getName();

    default String getKey() {
       return  "ability_" + this.getName();
    }

    default void onActivate(Player player) {

    }

    default void onDeactivate(Player player) {

    }

    default long getCooldown() {
        return TimeUnit.SECONDS.toMillis(25L);
    }


    /*
    public boolean hasCooldown(Player player) {
        return plugin.getPlayerDataHandler().getPlayerData(player).hasCooldown(this.key);
    }

    public ItemStack getItem(PlayerInteractEvent event, String displayName, boolean handleCooldown) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem()) {
                ItemStack item = event.getItem();
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(displayName)) {
                        Player player = event.getPlayer();
                        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData(player);
                        if (handleCooldown && playerData.hasCooldown(this.key)) {
                            player.sendMessage(Locale.PLAYER_ABILITY_COOLDOWN.format(playerData.getCooldownMap().get(this.key).getTimeLeft()));
                            return null;
                        }


                        return item;
                    }
                }
            }
        }
        return null;
    }

    public ItemStack getItem(PlayerInteractEvent event, String displayName) {
        return this.getItem(event, displayName, true);
    }
    */

}
