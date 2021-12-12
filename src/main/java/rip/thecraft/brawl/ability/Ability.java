package rip.thecraft.brawl.ability;

import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.event.AbilityCooldownEvent;
import rip.thecraft.brawl.ability.property.AbilityData;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.codec.Codec;
import rip.thecraft.brawl.ability.property.codec.Codecs;
import rip.thecraft.brawl.ability.task.AbilityTasks;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class Ability {

    protected static final int EFFECT_DISTANCE = 25;

    public static boolean DEBUG = false;

    /** Returns the name of the ability */
    private String name = getClass().getSimpleName();

    protected AbilityTasks tasks = new AbilityTasks();

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

    public boolean displayIcon = false;

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
            this.displayIcon = data.displayIcon();
        }
    }

    public Document serialize() {
        Document document = new Document();
        document.put("name", name);
        document.put("properties", serializeProperties());
        return document;
    }

    protected Document serializeProperties() {
        Document properties = new Document();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    Class<?> type = field.getType();
                    Codec<?> codec = Codecs.getCodecByClass(type);
                    Object value = field.get(this);
                    properties.put(property.id(), codec != null ? codec.encode(value) : value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public void deserialize(Document document) {
        if (!document.containsKey("properties")) return;

        Document properties = document.get("properties", Document.class);
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null && properties.containsKey(property.id())) {
                    Class<?> type = field.getType();
                    Codec<?> codec = Codecs.getCodecByClass(type);
                    Object value = codec != null ? codec.decode(properties.getString(property.id())) : properties.get(property.id());
                    field.set(this, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get properties of abilities that are configurable
     * @return Ability properties
     */
    public Map<String, Field> getProperties() {
        Map<String, Field> properties = new HashMap<>();
        for (Field field : getClass().getFields()) {
            try {
                AbilityProperty property = field.getAnnotation(AbilityProperty.class);
                if (property != null) {
                    properties.put(property.id(), field);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
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
     * Called when a player activates an ability that
     * has different stages.
     *
     * TODO add more infrastructure for Kit Leveling
     *
     * @param player Player using ability
     * @param level Level of ability
     */
    public void onActivate(Player player, int level) {

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

    /**
     * Removes cache data when ability is deactivated
     * or through kit being removed
     * @param player Player using ability
     */
    public void cleanup(Player player) {
        tasks.clear(player.getUniqueId());
    }

    public void onCooldownExpire(Player player) {

    }

    /**
     * Checks whether a player has this ability equipped
     * @param player Player using ability
     * @return Whether player has ability equipped
     */
    public boolean hasEquipped(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        if (playerData == null) return false;
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
        if (playerData == null) return false;

        Cooldown cooldown = playerData.getCooldown(getCooldownId());
        boolean active = playerData.hasCooldown(getCooldownId());

        if (active && notify) {
            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + cooldown.getTimeLeft() + ChatColor.RED + " before using this again.");
        }
        return active;
    }

    public String getCooldownId() {
        return "ABILITY_" + getName();
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

            if (hasCooldown(player, false)) {
                if (!cooldown.isNotified()) {
                    player.sendMessage(ChatColor.GREEN + "You can now use " + ChatColor.BOLD + getName() + ChatColor.GREEN + " again.");
                    cooldown.setNotified(true);
                    onCooldownExpire(player);
                }
            }
        }, 20L * TimeUnit.MILLISECONDS.toSeconds(countdown), false);
    }

    public Cooldown toCooldown(PlayerData playerData) {
        return playerData.getCooldown("ABILITY_" + this.getName());
    }

    public ItemStack getItem() {
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