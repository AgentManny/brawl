package rip.thecraft.brawl.ability;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.event.AbilityCooldownEvent;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.Cooldown;
import rip.thecraft.spartan.util.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public abstract class Ability {

    protected static final int EFFECT_DISTANCE = 25;

    @Setter protected int cooldown = 25;

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

    public Perk[] getDisabledPerks() {
        return new Perk[]{ };
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

    public void onGround(Player player, boolean onGround) {

    }

    public void onSneak(Player player, boolean sneaking) {

    }

    public void onCooldownExpire(Player player) {

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

    public boolean onInteractItem(Player player, Action action, ItemStack item) {
        return false; // True to cancel
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

    public boolean bypassAbilityPreventZone() {
        return false;
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

        Bukkit.getServer().getPluginManager().callEvent(new AbilityCooldownEvent(player, this, countdown));

        if (playerData.getEnderpearlTask() == null) {
            playerData.setEnderpearlTask(new BukkitRunnable() {

                final Cooldown cooldown = toCooldown(playerData);

                @Override
                public void run() {
                    if (playerData == null || cooldown == null || playerData.getEnderpearlTask() == null) {
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
