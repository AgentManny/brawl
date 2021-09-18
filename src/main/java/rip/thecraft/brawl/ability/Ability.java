package rip.thecraft.brawl.ability;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.lang.Nullable;
import lombok.Getter;
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
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.type.BooleanProperty;
import rip.thecraft.brawl.ability.property.type.DoubleProperty;
import rip.thecraft.brawl.ability.property.type.StringProperty;
import rip.thecraft.brawl.challenges.ChallengeType;
import rip.thecraft.brawl.challenges.player.PlayerChallenge;
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
public abstract class Ability {

    protected static final int EFFECT_DISTANCE = 25;

    public static boolean DEBUG = false;

    public Map<String, AbilityProperty<?>> properties = new HashMap<>();

    public Ability() {
        properties.put("cooldown", new DoubleProperty(getDefaultCooldown()));
    }

    public String getInfo() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public Material getType() {
        return null;
    }

    public byte getData() {
        return 0;
    }

    public double getDefaultCooldown() {
        return 15;
    }

    public void cleanup() {

    }

    public ItemStack getIcon() {
        if (getType() == null) return null;
        ItemBuilder data = new ItemBuilder(getType())
                .name(CC.GRAY + "\u00bb " + getColor() + CC.BOLD + getName() + CC.GRAY + " \u00ab")
                .data(getData());

        if (getDescription() != null) {
            data.lore(ItemBuilder.wrap(getDescription(), ChatColor.GRAY.toString()));
        }
        return data.create();
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

    public void reload() {
        JsonObject jsonObject = toJson();

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

        JsonObject data = new JsonObject();
        properties.forEach((key, value) -> {
            Object valueObj = value.value();
            if (valueObj instanceof Number) {
                data.addProperty(key, (Number) valueObj);
            } else if (valueObj instanceof Boolean) {
                data.addProperty(key, (Boolean) valueObj);
            } else {
                data.addProperty(key, value.toString());
            }
        });
        object.add("properties", data);
        return object;
    }

    public void fromJson(JsonObject object) {
        if (object.has("properties")) {
            JsonObject data = object.getAsJsonObject("properties");
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                AbilityProperty<?> property = null;
                if (value.isJsonPrimitive()) {
                    JsonPrimitive primitive = value.getAsJsonPrimitive();
                    if (primitive.isBoolean()) {
                        property = new BooleanProperty(primitive.getAsBoolean());
                    } else if (primitive.isNumber()) {
                        property = new DoubleProperty(primitive.getAsDouble());
                    } else {
                        property = new StringProperty(primitive.getAsString());
                    }
                }
                properties.put(key, property);
            }
        }
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

    public void setCooldown(int cooldown) {
        AbilityProperty<Integer> property = (AbilityProperty<Integer>) properties.get("cooldown");
        property.set(cooldown);
    }

    private long getCooldown() {
        Double cooldown = (Double) properties.get("cooldown").value();
        if (cooldown == null) {
            cooldown = getDefaultCooldown();
        }
        return TimeUnit.SECONDS.toMillis(cooldown.longValue());
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
                    player.setExp(0);
                    player.setLevel(timeLeft);
                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();

                    playerData.getLevel().updateBar();
                    playerData.setEnderpearlTask(null);
                }
            }.runTaskTimer(Brawl.getInstance(), 10L, 10L));
        }
    }

    public Cooldown toCooldown(PlayerData playerData) {
        return playerData.getCooldown("ABILITY_" + this.getName());
    }

    public void addProperty(String key, double defaultValue, @Nullable String description) {
        DoubleProperty property = new DoubleProperty(defaultValue);
        if (description != null) {
            property.description(description);
        }
        properties.put(key, property);
    }

    public Double getProperty(String key) {
        return (Double) properties.get(key).value();
    }

    public Boolean isProperty(String key) {
        return (Boolean) properties.get(key).value();
    }

    public void sendDebug(@Nullable Player player, String message) {
        if (Ability.DEBUG) {
            String formattedMessage = ChatColor.translateAlternateColorCodes('&', "[Debug] [A:" + getColor() + getName() + ChatColor.RESET + "] " + message);
            if (player == null) {
                Brawl.broadcastOps(formattedMessage);
            } else {
                player.sendMessage(formattedMessage);
            }
        }
    }
}
