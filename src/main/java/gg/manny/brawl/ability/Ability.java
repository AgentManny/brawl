package gg.manny.brawl.ability;

import com.google.gson.JsonObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.duelarena.match.Match;
import gg.manny.brawl.kit.Kit;
import gg.manny.brawl.player.PlayerData;
import gg.manny.brawl.region.RegionType;
import gg.manny.pivot.util.Cooldown;
import gg.manny.pivot.util.ItemBuilder;
import gg.manny.server.util.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public abstract class Ability {

    protected int cooldown = 25;

    public String getName() {
        return getClass().getSimpleName();
    }

    public Material getType() {
        return null;
    }

    public byte getData() {
        return 0;
    }

    public ItemStack getIcon() {
        if (getType() == null) return null;

        return new ItemBuilder(getType())
                .name(CC.GRAY + "\u00bb " + getColor() + CC.BOLD + getName() + CC.GRAY + " \u00ab")
                .data(getData())
                .create();

    }

    public ChatColor getColor() {
        return ChatColor.DARK_PURPLE;
    }


    public void onApply(Player player) {

    }

    public void onRemove(Player player) {

    }

    public void onActivate(Player player) {

    }

    public void onDeactivate(Player player) {

    }

    /**
     * Called upon killing a player
     * @param player Killer
     */
    public void onKill(Player player) {

    }

    /**
     * Triggers when a projectile is launched
     * @param player Attacked
     * @param entityType Attacked by
     * @return Whether or not it should be cancelled
     */
    public boolean onProjectileLaunch(Player player, EntityType entityType) {
        return false;
    }

    public boolean onProjectileHit(Player shooter, Player victim, EntityDamageByEntityEvent event) {
        return false;
    }

    public Map<String, String> getProperties(Player player) {
        return new HashMap<>();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", this.getName());
        object.addProperty("cooldown", this.cooldown);
        return object;
    }

    public void fromJson(JsonObject object) {
        this.cooldown = object.get("cooldown").getAsInt();
    }

    public boolean hasEquipped(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Match match = Brawl.getInstance().getMatchHandler().getMatch(player);
        Kit selectedKit = match != null && match.getKit() != null ? match.getKit() : playerData.getSelectedKit();
        return !RegionType.SAFEZONE.appliesTo(player.getLocation()) && selectedKit != null && selectedKit.getAbilities().contains(this) ;
    }

    private long getCooldown() {
        return TimeUnit.SECONDS.toMillis(this.cooldown);
    }

    public boolean hasCooldown(Player player, boolean notify) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);

        String key = "ABILITY_" + this.getName();
        Cooldown cooldown = playerData.getCooldown(key);
        boolean active = playerData.hasCooldown(key);

        if (active && notify) {
            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + cooldown.getTimeLeft() + ChatColor.RED + " before using this again.");
        }
        return active;
    }

    public void addCooldown(Player player) {
        addCooldown(player, getCooldown());
    }

    public void addCooldown(Player player, long countdown) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        playerData.addCooldown("ABILITY_" + this.getName(), countdown);
        if (playerData.getEnderpearlTask() == null) {
            playerData.setEnderpearlTask(new BukkitRunnable() {

                final Cooldown cooldown = toCooldown(playerData);

                @Override
                public void run() {
                    if (playerData.getEnderpearlTask() == null) {
                        cancel();
                        return;
                    }

                    int timeLeft = (int) TimeUnit.MILLISECONDS.toSeconds(cooldown.getRemaining());
                    if (timeLeft <= 0 && playerData.getEnderpearlTask() != null) {
                        if (!cooldown.isNotified()) {
                            cooldown.setNotified(true);
                            player.sendMessage(ChatColor.GREEN + "You can now use " + ChatColor.BOLD + getName() + ChatColor.GREEN + " again.");
                        }
                        this.cancel();
                        return;
                    }
                    player.setLevel(timeLeft);
                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();
                    player.setLevel(0);
                    player.setExp(0);
                    playerData.setEnderpearlTask(null);
                }
            }.runTaskTimer(Brawl.getInstance(), 10L, 10L));
        }
    }

    public Cooldown toCooldown(PlayerData playerData) {
        return playerData.getCooldown("ABILITY_" + this.getName());
    }

}
