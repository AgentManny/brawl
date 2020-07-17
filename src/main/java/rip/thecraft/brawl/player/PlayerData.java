package rip.thecraft.brawl.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.challenges.Challenge;
import rip.thecraft.brawl.challenges.PlayerChallenge;
import rip.thecraft.brawl.duelarena.queue.QueueData;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.data.SpawnData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.nametag.NametagHandler;
import rip.thecraft.spartan.util.Cooldown;

import java.util.*;

@Setter
@Getter
@RequiredArgsConstructor
public class PlayerData {

    public static final int BASE_EXPERIENCE = 25;

    private final UUID uuid;
    private final String name;

    private boolean duelArena = false;
    private boolean event = false;
    private boolean spectator = false;

    private boolean spawnProtection = true;
    private boolean noFallDamage = false;

    private boolean warping = false;

    private Kit selectedKit;
    private Kit previousKit;

    private RefillType refillType = RefillType.SOUP;

    // Perk data
    private Set<Perk> unlockedPerks = new HashSet<>();
    private Perk[] activePerks = new Perk[3]; // You can only have 3 perks

    private long combatTaggedTil;

    private boolean teamChat = false;

    // Used for Revenge perk
    private UUID previousDeath;

    private UUID previousKill;
    private int killTracker = 0;

    private Level level = new Level(this);

    private Map<String, Long> kitRentals = new HashMap<>();
    private Map<String, Long> gameRentals = new HashMap<>();

    private PlayerStatistic statistic = new PlayerStatistic(this);

    private SpawnData spawnData = new SpawnData(this);

    private QueueData queueData = new QueueData();

    private Map<String, Cooldown> cooldownMap = new HashMap<>();

    private BukkitTask enderpearlTask;
    private BukkitRunnable tpTask;

    private long lastAction = System.currentTimeMillis();
    private Location lastLocation = null;
    private boolean duelsEnabled = true;

    // Challenges
    private List<PlayerChallenge> challenges = new ArrayList<>();

    private boolean needsSaving;
    private boolean loaded;

    public void load() {
        Player player = getPlayer();
        if (player == null) return;
    }

    public Document toDocument() {
        Map<String, Document> cooldownMap = new HashMap<>();
        this.cooldownMap.forEach((name, cooldown) -> cooldownMap.put(name, cooldown.toDocument()));

        List<String> unlockedPerks = new ArrayList<>();
        this.unlockedPerks.forEach(perk -> unlockedPerks.add(perk.name()));

        Map<String, String> activePerks = new HashMap<>();
        for (int i = 0; i < this.activePerks.length; i++) {
            Perk perk = this.activePerks[i];
            activePerks.put(String.valueOf(i), perk == null ? null : perk.name());
        }

        return new Document("uuid", this.uuid.toString())
                .append("username", this.name)
                .append("previous-kit", this.previousKit == null ? null : this.previousKit.getName())
                .append("cooldown", cooldownMap)
                .append("rentals", this.kitRentals)
                .append("gameRentals", this.gameRentals)
                .append("statistic", this.statistic.getData())
                .append("level", this.level.toDocument())
                .append("kill-tracker", killTracker)
                .append("healing-method", refillType.name())
                .append("previous-death", previousDeath == null ? null : previousDeath.toString())
                .append("previous-kill", previousKill == null ? null : previousKill.toString())
                .append("unlocked-perks", unlockedPerks)
                .append("active-perks", activePerks);
    }

    public void fromDocument(Document document) {
        if (document == null) {
            this.loaded = true;
            this.statistic.load(null);
            this.save();
            return;
        }

        this.previousKit = Brawl.getInstance().getKitHandler().getKit(document.getString("previous-kit"));

        Map<String, Document> cooldowns = (Map<String, Document>) document.get("cooldown");
        cooldowns.forEach((name, cooldownDocument) -> this.cooldownMap.put(name, new Cooldown(cooldownDocument)));

        this.kitRentals.putAll((Map<String, Long>) document.get("rentals"));
        this.gameRentals.putAll((Map<String, Long>) document.get("gameRentals"));

        statistic.load((Document) document.get("statistic"));

        if (document.containsKey("challenges")) {
            List<Document> challengesDoc = (List<Document>) document.get("challenges");
            challengesDoc.forEach(doc -> {
                PlayerChallenge challenge = new PlayerChallenge(document);
                if (challenge.isActive()) {
                    challenges
                }
            });
        }

        if (document.containsKey("level")) {
            level.load((Document) document.get("level"));
        }

        killTracker = document.getInteger("kill-tracker", 0);

        if (document.containsKey("previous-death") && document.get("previous-death") != null) {
            previousDeath = BrawlUtil.isUUID("previous-death") ? UUID.fromString(document.getString("previous-death")) : null;
        }

        if (document.containsKey("previous-kill") && document.get("previous-kill") != null) {
            previousKill = BrawlUtil.isUUID("previous-kill") ? UUID.fromString(document.getString("previous-kill")) : null;
        }

        if (document.containsKey("healing-method")) {
            refillType = RefillType.valueOf(document.getString("healing-method"));
        }

        if (document.containsKey("unlocked-perks")) {
            List<String> unlockedPerks = (List<String>) document.get("unlocked-perks");
            unlockedPerks.forEach(perk -> this.unlockedPerks.add(Perk.getPerk(perk)));
        }

        if (document.containsKey("active-perks")) {
            Map<String, String> activePerks = (Map<String, String>) document.get("active-perks");
            activePerks.forEach((id, perk) -> {
                if (perk != null) {
                    this.activePerks[Integer.parseInt(id)] = Perk.getPerk(perk);
                }
            });
        }

        this.loaded = true;
    }

    public void markForSave() {
        this.needsSaving = true;
    }

    public void save() {
        needsSaving = false;
        Brawl.getInstance().getPlayerDataHandler().setDocument(this.toDocument(), this.uuid);
    }

    public PlayerState getPlayerState() {
        if (Brawl.getInstance().getSpectatorManager().isSpectating(getPlayer())) {
            return PlayerState.SPECTATING;
        } else if (Brawl.getInstance().getGameHandler().getLobby() != null && Brawl.getInstance().getGameHandler().getLobby().getPlayers().contains(this.uuid)) {
            return PlayerState.GAME_LOBBY;
        } else if (Brawl.getInstance().getGameHandler().getActiveGame() != null && Brawl.getInstance().getGameHandler().getActiveGame().containsPlayer(this.getPlayer()) && Brawl.getInstance().getGameHandler().getActiveGame().getGamePlayer(this.getPlayer()).isAlive()) {
            return PlayerState.GAME;
        } else if (Brawl.getInstance().getMatchHandler().isInMatch(this.getPlayer())) {
            return PlayerState.MATCH;
        } else if (this.duelArena) {
            return PlayerState.ARENA;
        } else if (this.spawnProtection) {
            return PlayerState.SPAWN;
        }
        return PlayerState.FIGHTING;
    }

    public boolean hasCombatLogged() {
        return combatTaggedTil > System.currentTimeMillis() && !spawnProtection;
    }

    public void spawn() {
        Player player = getPlayer();
        if (player == null) return;

        this.spawnProtection = true;
        this.duelArena = false;

        if (this.selectedKit == null) {
            if (!player.hasMetadata("staffmode")) {
                Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);
            }
            NametagHandler.reloadPlayer(player);
            NametagHandler.reloadOthersFor(player);
        } else {
            player.sendMessage(ChatColor.GREEN + "Clear your kit by using " + ChatColor.WHITE + "/clearkit" + ChatColor.GREEN + ".");
        }
    }

    public boolean canWarp() {
        int max = 32;
        Player player = getPlayer();

        List<Entity> nearbyEntities = player.getNearbyEntities(max, max, max);
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        if (RegionType.SAFEZONE.appliesTo(player.getLocation())) return true;

        for (Entity entity : nearbyEntities) {
            if ((entity instanceof Player)) {
                Player other = (Player) entity;
                if (!other.canSee(player)) {
                    return true;
                }
                if (!player.canSee(other)) {
                    continue;
                }

                PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(other.getUniqueId());
                if (pd.isSpawnProtection()) {
                    return true;
                }
            }
        }

        return this.spawnProtection;
    }

    public void warp(String name, Location loc, int seconds, Runnable... onTp) {

        if (canWarp()) {
            for (Runnable rb : onTp) {
                rb.run();
            }

            combatTaggedTil = -1;
            getCooldownMap().remove("ENDERPEARL");
            getPlayer().teleport(loc);
            return;
        }

        getPlayer().sendMessage(CC.YELLOW + "Warping to " + name.toLowerCase() + CC.YELLOW + " in " + CC.LIGHT_PURPLE + seconds + " seconds" + CC.YELLOW + ". Do not move or take damage.");

        if (tpTask != null) {
            tpTask.cancel();
        }

        tpTask = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = getPlayer();

                cancel();
                tpTask = null;

                if (player != null && player.isOnline()) {
                    combatTaggedTil = -1;
                    getCooldownMap().remove("ENDERPEARL");
                    player.sendMessage(ChatColor.YELLOW + "Warped to " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + ".");

                    if (onTp != null && onTp.length > 0) {
                        for (Runnable rb : onTp) {
                            rb.run();
                        }
                    }
                    getPlayer().teleport(loc);
                }

            }
        };

        tpTask.runTaskLater(Brawl.getInstance(), seconds * 20L);
    }

    public boolean isWarping() {
        return tpTask != null;
    }

    public void cancelWarp() {
        getPlayer().sendMessage(ChatColor.RED + "Warp cancelled!");
        tpTask.cancel();
        tpTask = null;
    }

    public Team getTeam() {
        return Brawl.getInstance().getTeamHandler().getPlayerTeam(getPlayer());
    }

    public boolean usingPerk(Perk perk) {
        for (Ability ability : selectedKit.getAbilities()) {
            for (Perk disabledPerk : ability.getDisabledPerks()) {
                if (disabledPerk.equals(perk)) {
                    return false;
                }
            }
        }
        return perk.contains(activePerks);
    }

    public boolean hasPerk(Perk perk) {
        return !unlockedPerks.isEmpty() && unlockedPerks.contains(perk);
    }

    public boolean hasKit(Kit kit) {
        if (this.kitRentals.containsKey(kit.getName()) && this.kitRentals.get(kit.getName()) < System.currentTimeMillis()) {
            this.kitRentals.remove(kit.getName());
        }

        if ((kitRentals.containsKey(kit.getName()) && kitRentals.get(kit.getName()) > System.currentTimeMillis()) || this.getPlayer().isOp() || this.getPlayer().hasPermission("kit." + kit.getName().toLowerCase())) {
            return true;
        }

        if (kit.getRankType() != RankType.NONE && !this.getPlayer().hasPermission("rank." + kit.getRankType().getName().toLowerCase())) {
            return false;
        }

        return kit.isFree();
    }

    public boolean hasGame(GameType gameType) {
        if (this.gameRentals.containsKey(gameType.getName()) && this.gameRentals.get(gameType.getName()) < System.currentTimeMillis()) {
            this.gameRentals.remove(gameType.getName());
        }

        return this.getPlayer().isOp() ||this.getPlayer().hasPermission("rank." + gameType.getRankType().getName().toLowerCase()) || this.getPlayer().hasPermission("game." + gameType.getName().toLowerCase()) ||  (gameRentals.containsKey(gameType.getName()) && gameRentals.get(gameType.getName()) > System.currentTimeMillis());
    }

    private void validateChallenges() {
        this.challenges.removeIf(challenge -> !challenge.isActive());
    }

    public void generateChallenges() {
        validateChallenges();
        this.challenges.clear();

        Set<Challenge> challenges = new HashSet<>();
        int weeklyCount = 0;
        int dailyCount = 0;

        // We iterate 10 times to try to get unique challenges (and not repeating)
        // If it fails, it means there aren't enough challenges in the pool
        for (int i = 0; i < 15; i++) {
            Challenge challenge = Challenge.getRandomChallenge();
            if (!challenges.contains(challenge)) {
                if (challenge.getDuration() == Challenge.Duration.WEEKLY && weeklyCount < Challenge.MAX_WEEKLY_CHALLENGES) {
                    ++weeklyCount;
                } else if (challenge.getDuration() == Challenge.Duration.DAILY && dailyCount < Challenge.MAX_DAILY_CHALLENGES) {
                    ++dailyCount;
                }
                challenges.add(challenge);
            }
        }

        for (Challenge challenge : challenges) {
            PlayerChallenge playerChallenge = new PlayerChallenge(challenge);
            Player player = getPlayer();
            if (player != null && player.isOp()) { // DEBUG - Remove
                player.sendMessage(ChatColor.GRAY + "[DEBUG] Added " + challenge.name());
            }
            this.challenges.add(playerChallenge);
        }
    }

    public List<PlayerChallenge> getChallenges() {
        validateChallenges();
        return challenges;
    }

    public Multimap<Challenge.Duration, PlayerChallenge> getAllChallenges() {
        validateChallenges();
        Multimap<Challenge.Duration, PlayerChallenge> challenges = ArrayListMultimap.create();
        this.challenges.forEach(challenge -> challenges.put(challenge.getChallenge().getDuration(), challenge));
        return challenges;
    }

    public void setSelectedKit(Kit selectedKit) {
        if (this.selectedKit != null) {
            this.selectedKit.getAbilities().forEach(ability -> {
                ability.onRemove(this.getPlayer());
                String cooldown = "ABILITY_" + ability.getName();
                Cooldown cd = this.getCooldown(cooldown);
                if (cd != null) {
                    cd.setExpire(0);
                    cd.setNotified(true);
                }
            });
        }
        this.selectedKit = selectedKit;
    }

    public Cooldown addCooldown(String cooldownName, long time) {
        Cooldown cooldown = this.getCooldown(cooldownName.toUpperCase());
        if (cooldown != null) {
            cooldown.setExpire(cooldown.getExpire() + time);
        } else {
            cooldown = new Cooldown(time);
        }

        return this.cooldownMap.put(cooldownName.toUpperCase(), cooldown);
    }

    public Cooldown setCooldown(String cooldownName, long time) {
        Cooldown cooldown = this.getCooldown(cooldownName.toUpperCase());
        if (cooldown != null) {
            cooldown.setExpire(time);
        } else {
            cooldown = new Cooldown(time);
        }

        return this.cooldownMap.put(cooldownName.toUpperCase(), cooldown);
    }

    public Cooldown getCooldown(String cooldownName) {
        Cooldown cooldown = null;

        if (cooldownMap.containsKey(cooldownName.toUpperCase())) {
            cooldown = cooldownMap.get(cooldownName.toUpperCase());

            if (cooldown.hasExpired()) {
                cooldownMap.remove(cooldownName.toUpperCase());
                return null;
            }
        }

        return cooldown;
    }

    public void setPreviousKit(Kit previousKit) {
        this.previousKit = previousKit;
        this.markForSave();
    }

    public void setTeamChat(boolean teamChat) {
        this.teamChat = teamChat;
        this.markForSave();
    }

    public void setPreviousKill(UUID previousKill) {
        this.previousKill = previousKill;
        this.markForSave();
    }

    public void setKillTracker(int killTracker) {
        this.killTracker = killTracker;
        this.markForSave();
    }

    public boolean hasCooldown(String cooldownName) {
        return this.getCooldown(cooldownName.toUpperCase()) != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }
    
    @Override
    public String toString() {
        return "uuid=" + uuid.toString() + ";name=" + name;
    }
}