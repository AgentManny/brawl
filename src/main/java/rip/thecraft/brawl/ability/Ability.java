package rip.thecraft.brawl.ability;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.event.AbilityCooldownEvent;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.brawl.util.SchedulerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.Cooldown;
import rip.thecraft.spartan.util.ItemBuilder;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class Ability {

    protected static final int EFFECT_DISTANCE = 25;

    public static boolean DEBUG = false;

    /** Returns the name of the ability */
    private String name = getClass().getSimpleName();

    /** Returns the color displayed for an ability */
    private ChatColor color = ChatColor.DARK_PURPLE;

    /** Returns the description for an ability */
    private String description = null;

    /** Returns the icon displayed for an ability */
    private Material icon = null;

    /** Returns the data for an ability */
    private byte data = 0;

    /** Returns the cooldown timer set for an ability */
    @AbilityProperty(id = "cooldown", description = "Timer before ability can be used again")
    public int cooldown = 15;

    /**
     * Loads ability data from AbilityData
     * @param data Ability data to load
     */
    public void load(AbilityData data) {
        if (!data.name().isEmpty()) {
            this.name = data.name();
        }
        if (data.color() != null) {
            this.color = data.color();
        }
        if (!data.description().isEmpty()) {
            this.description = data.description();
        }

        if (data.icon() != Material.AIR) {
            this.icon = data.icon();
            this.data = data.data();
        }
    }

    public Document serialize() {
        Document document = new Document();
        document.put("name", name);

        Document properties = new Document();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    document.put(property.id().isEmpty() ? field.getName() : property.id(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        document.put("properties", properties);
        return document;
    }

    public void deserialize(Document document) {
        if (document.containsKey("properties")) {
            Document properties = document.get("properties", Document.class);
            for (Field field : getClass().getFields()) {
                try {
                    AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                    if (property != null && properties.containsKey(property.id().isEmpty() ? field.getName() : property.id())) {
                        field.set(this, properties.get(property.id().isEmpty() ? field.getName() : property.id()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when the player equips a kit
     *
     * @param player Player applying kit
     */
    public void onApply(Player player) {

    }

    /**
     * Called when the player removes a kit
     * through clearing, elimination or dying
     *
     * @param player Player removing kit
     */
    public void onRemove(Player player) {

    }

    /**
     * Called when a player activates an ability
     * through interacting with the icon
     *
     * @param player Player using ability
     */
    public void onActivate(Player player) {

    }

    /**
     * Called when an ability is deactivated manually
     * or through the kit being removed
     * @param player Player using ability
     */
    public void onDeactivate(Player player) {

    }

    /**
     * Removes cache data on server shutdown or
     * server clean up
     */
    public void cleanup() {

    }

    public void onCooldownExpire(Player player) {

    }

    public boolean onInteractItem(Player player, Action action, ItemStack item) {
        return false; // True to cancel
    }

    /**
     * Checks whether a player has this ability equipped
     * @param player Player using ability
     * @return Whether player has ability equipped
     */
    public boolean hasEquipped(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Match match = Brawl.getInstance().getMatchHandler().getMatch(player);
        Kit selectedKit = match != null && match.getKit() != null ? match.getKit() : playerData.getSelectedKit();
        return !RegionType.SAFEZONE.appliesTo(player.getLocation()) && selectedKit != null && selectedKit.getAbilities().contains(this) ;
    }

    /**
     * Should this ability be permitted in regions that
     * abilities aren't allowed (useful for passive abilities)
     * @return Ability access
     */
    public boolean bypassAbilityPreventZone() {
        return false;
    }

    /**
     * @return Cooldown timer in millis
     */
    private long getCooldown() {
        return TimeUnit.SECONDS.toMillis(cooldown);
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

        for (PlayerChallenge challenge : playerData.getChallengeTracker().getChallenges().values()) {
            if (challenge.isActive() && challenge.getChallenge().getType() == ChallengeType.ABILITY) {
                challenge.increment(player, 1);
            }
        }

        Cooldown cooldown = toCooldown(playerData);
        SchedulerUtil.runTaskLater(() -> {
            if (player == null || cooldown == null) return;

            if (!cooldown.isNotified()) {
                player.sendMessage(ChatColor.GREEN + "You can now use " + ChatColor.BOLD + getName() + ChatColor.GREEN + " again.");
                cooldown.setNotified(true);
                onCooldownExpire(player);
            }
        }, 20L * this.cooldown, false);
    }

    public Cooldown toCooldown(PlayerData playerData) {
        return playerData.getCooldown("ABILITY_" + this.getName());
    }

    public ItemStack getIcon() {
        if (icon == null || icon == Material.AIR) return null;
        ItemBuilder item = new ItemBuilder(icon)
                .name(CC.GRAY + "\u00bb " + getColor() + CC.BOLD + getName() + CC.GRAY + " \u00ab")
                .data(data);

        if (getDescription() != null) {
            item.lore(ItemBuilder.wrap(getDescription(), ChatColor.GRAY.toString()));
        }
        return item.create();
    }

    public Perk[] getDisabledPerks() {
        return new Perk[]{ };
    }
}