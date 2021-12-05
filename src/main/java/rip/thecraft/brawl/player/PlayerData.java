package rip.thecraft.brawl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.challenges.player.ChallengeTracker;
import rip.thecraft.brawl.duelarena.queue.QueueData;
import rip.thecraft.brawl.game.GameType;
import rip.thecraft.brawl.item.type.InventoryType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.event.KitActivateEvent;
import rip.thecraft.brawl.kit.event.KitDeactivateEvent;
import rip.thecraft.brawl.kit.type.RankType;
import rip.thecraft.brawl.kit.type.RefillType;
import rip.thecraft.brawl.levels.Level;
import rip.thecraft.brawl.player.achievements.Achievement;
import rip.thecraft.brawl.player.data.SpawnData;
import rip.thecraft.brawl.player.statistic.PlayerStatistic;
import rip.thecraft.brawl.player.statistic.StatisticType;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.team.Team;
import rip.thecraft.brawl.upgrade.perk.Perk;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.falcon.Falcon;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.util.Cooldown;
import rip.thecraft.spartan.util.TimeUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private boolean warp = false;

    private boolean spawnProtection = true;
    private boolean noFallDamage = false;

    private boolean warping = false;

    private Kit selectedKit;
    private Kit previousKit;

    private RefillType refillType = RefillType.SOUP;

    private Set<String> achievements = new HashSet<>();

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

    private int globalKitPass = 0; // TODO Use any kits for X amount of time
    private int kitPasses = 0; // Choose any kit for trialing

    private Kit unlockingKit; // Kit that player is trying to unlock
    private List<String> unlockedKits = new ArrayList<>();

    private Map<String, Long> kitRentals = new HashMap<>();
    private Map<String, Long> gameRentals = new HashMap<>();

    private PlayerStatistic statistic = new PlayerStatistic(this);
    private ChallengeTracker challengeTracker = new ChallengeTracker(this);

    private SpawnData spawnData = new SpawnData(this);
    private QueueData queueData = new QueueData();
    private long teleportDuration = 0;

    // Configurability
    private boolean gameMessages = true;
    private boolean killstreakMessages = true;
//    private boolean bountyMessages = true;

    private Map<String, Cooldown> cooldownMap = new HashMap<>();

    private BukkitTask enderpearlTask;
    private BukkitRunnable tpTask;

    private long lastAction = System.currentTimeMillis();
    private Location lastLocation = null;
    private boolean duelsEnabled = true;

    private long lastVoteRewards = -1L;

    private boolean needsSaving;
    private boolean loaded;

    public void load() {
        Player player = getPlayer();
        if (player == null) return;

        if (statistic.get(StatisticType.LEVEL) == 0) {
            statistic.set(StatisticType.LEVEL, 1);
        }

        level.updateExp(player);
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

        Document messageData = new Document("game", gameMessages)
                .append("killstreak", killstreakMessages);

        return new Document("uuid", this.uuid.toString())
                .append("username", this.name)
                .append("previous-kit", this.previousKit == null ? null : this.previousKit.getName())
                .append("unlocking-kit", this.unlockingKit == null ? null : this.unlockingKit.getName())
                .append("unlocked-kits", this.unlockedKits)
                .append("kit-passes", kitPasses)
                .append("cooldown", cooldownMap)
                .append("rentals", this.kitRentals)
                .append("gameRentals", this.gameRentals)
                .append("statistic", this.statistic.getData())
                .append("level", this.level.toDocument())
                .append("challenges", this.challengeTracker.toDocument())
                .append("kill-tracker", killTracker)
                .append("healing-method", refillType.name())
                .append("previous-death", previousDeath == null ? null : previousDeath.toString())
                .append("previous-kill", previousKill == null ? null : previousKill.toString())
                .append("unlocked-perks", unlockedPerks)
                .append("active-perks", activePerks)
                .append("last-vote-rewards", lastVoteRewards)
                .append("message-data", messageData);
    }

    public void fromDocument(Document document) {
        if (document == null) {
            this.loaded = true;
            this.statistic.load(null);
            this.challengeTracker.load(null);
            this.save();
            return;
        }

        this.previousKit = Brawl.getInstance().getKitHandler().getKit(document.getString("previous-kit"));

        if (document.containsKey("unlocking-kit")) {
            this.unlockingKit = Brawl.getInstance().getKitHandler().getKit(document.getString("unlocking-kit"));
        }

        if (document.containsKey("unlocked-kits")) {
            this.unlockedKits = (List<String>) document.get("unlocked-kits");
        }

        if (document.containsKey("message-data")) {
            Document messageData = (Document) document.get("message-data");
            this.killstreakMessages = messageData.get("killstreak", true);
            this.gameMessages = messageData.get("game", true);
        }

        this.kitPasses = document.getInteger("kit-passes", 0);

        Map<String, Document> cooldowns = (Map<String, Document>) document.get("cooldown");
        cooldowns.forEach((name, cooldownDocument) -> this.cooldownMap.put(name, new Cooldown(cooldownDocument)));

        this.kitRentals.putAll((Map<String, Long>) document.get("rentals"));
        this.gameRentals.putAll((Map<String, Long>) document.get("gameRentals"));

        statistic.load((Document) document.get("statistic"));
        challengeTracker.load((Document) document.get("challenges"));

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

        if (document.containsKey("last-vote-rewards")) {
            this.lastVoteRewards = document.getLong("last-vote-rewards");
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

    public boolean isSpectating() {
        Player player = getPlayer();
        return player != null && Brawl.getInstance().getSpectatorManager().isSpectating(player);
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

    public boolean hasAchievement(Achievement achievement) {
        return achievements.contains(achievement.getId());
    }

    public boolean hasCombatLogged() {
        return combatTaggedTil > System.currentTimeMillis() && !spawnProtection;
    }

    public void spawn() {
        Player player = getPlayer();
        if (player == null) return;

        this.spawnProtection = true;
        this.duelArena = false;
        this.event = false;
        this.warp = false;

        if (this.selectedKit == null) {
            if (!player.hasMetadata("staffmode")) {
                Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);
            }
        } else {
            if (!Brawl.getInstance().getKillstreakHandler().hasKillstreakItem(player)) {
                clearKit(false);
            } else {
                player.sendMessage(ChatColor.GREEN + "Clear your kit by using " + ChatColor.WHITE + "/clearkit" + ChatColor.GREEN + ".");
            }
        }
    }

    public void clearKit(boolean sendMessage) {
        Player player = getPlayer();
        if (player == null) return;

        if (selectedKit == null) {
            if (sendMessage) {
                player.sendMessage(ChatColor.RED + "You don't have a kit equipped.");
            }
            return;
        }

        if (!spawnProtection) {
            if (sendMessage) {
                player.sendMessage(ChatColor.RED + "You must have spawn protection to clear your kit.");
            }
            return;
        }

        selectedKit.getAbilities().forEach(ability -> removeCooldown(ability.getCooldownId()));

        previousKit = selectedKit;
        selectedKit = null;

        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);

        spawnProtection = true;

        if (!player.hasMetadata("staffmode")) {
            Brawl.getInstance().getItemHandler().apply(player, InventoryType.SPAWN);
        }
    }

    public boolean canWarp() {
        int max = 32;
        Player player = getPlayer();

        List<Entity> nearbyEntities = player.getNearbyEntities(max, max, max);
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        if (RegionType.SAFEZONE.appliesTo(player.getLocation())) return true;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                Player other = (Player) entity;
                if (other == player || !player.canSee(other)) {
                    continue;
                }

                if (!other.canSee(player)) {
                    return true;
                }

                PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(other.getUniqueId());
                if (pd == null) continue;

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
            teleportDuration = 0;
        }

        teleportDuration = teleportDuration = System.currentTimeMillis() + (seconds * 1000L);;

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
                    teleportDuration = 0;
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
        teleportDuration = 0;
        tpTask.cancel();
        tpTask = null;
    }

    public Team getTeam() {
        return Brawl.getInstance().getTeamHandler().getPlayerTeam(getPlayer());
    }

    public boolean usingPerk(Perk perk) {
        if (selectedKit == null) return false;
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
        if (this.unlockedKits.contains(kit.getName())) {
            return true;
        }

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
        if (this.gameRentals.containsKey(gameType.name()) && this.gameRentals.get(gameType.name()) < System.currentTimeMillis()) {
            this.gameRentals.remove(gameType.name());
        }

        return this.getPlayer().isOp() ||this.getPlayer().hasPermission("rank." + gameType.getRankType().getName().toLowerCase()) || this.getPlayer().hasPermission("rank." + gameType.getRankType().name()) || this.getPlayer().hasPermission("game." + gameType.name().toLowerCase()) ||  (gameRentals.containsKey(gameType.name()) && gameRentals.get(gameType.name()) > System.currentTimeMillis());
    }

    public void addUnlockedKit(Kit kit) {
        if (unlockedKits.contains(kit.getName())) {
            return;
        }
        unlockedKits.add(kit.getName());
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "KIT UNLOCKED! " + ChatColor.GRAY + "You now have access to " + ChatColor.WHITE + kit.getName() + ChatColor.GRAY + ".");
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
        }
    }

    public void addRentalKit(Kit kit, int time, TimeUnit unit) {
        long millis = unit.toMillis(time);
        kitRentals.put(kit.getName(), System.currentTimeMillis() + millis);
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "KIT UNLOCKED! " + ChatColor.GRAY + "You now have access to " + ChatColor.WHITE + kit.getName() + ChatColor.GRAY + " for " + ChatColor.YELLOW + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(millis)) + ChatColor.GRAY + ".");
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
        }
    }

    public void addRentalGame(GameType game, int time, TimeUnit unit) {
        long millis = unit.toMillis(time);
        gameRentals.put(game.name(), System.currentTimeMillis() + millis);
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "GAME UNLOCKED! " + ChatColor.GRAY + "You now have access to " + ChatColor.WHITE + game.getName() + ChatColor.GRAY + " for " + ChatColor.AQUA + TimeUtils.formatIntoSimplifiedString((int) TimeUnit.MILLISECONDS.toSeconds(millis)) + ChatColor.GRAY + ".");
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
        }
    }

    public void setSelectedKit(Kit selectedKit) {
        Player player = getPlayer();
        if (this.selectedKit != null) {
            if (player != null) {
                this.selectedKit.getAbilities().forEach(ability -> {
                    ability.onRemove(player);
                    ability.onDeactivate(player);

                    String cooldown = "ABILITY_" + ability.getName();
                    Cooldown cd = this.getCooldown(cooldown);
                    if (cd != null) {
                        cd.setExpire(0);
                        cd.setNotified(true);
                    }
                });
                if (selectedKit != this.selectedKit) {
                    Brawl.getInstance().getServer().getPluginManager().callEvent(new KitDeactivateEvent(player, selectedKit));
                }
            }
        }
        if (selectedKit != null && player != null) {
            Brawl.getInstance().getServer().getPluginManager().callEvent(new KitActivateEvent(player, selectedKit));
        }
        this.selectedKit = selectedKit;
    }

    public void removeCooldown(String id) {
        cooldownMap.remove(id.toUpperCase());
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

    public Group getPrimaryGroup() {
        User user = Falcon.getInstance().getLuckPerms().getUserManager().getUser(uuid);
        String primaryGroup = user.getPrimaryGroup();
        return primaryGroup == null ? null : Falcon.getInstance().getLuckPerms().getGroupManager().getGroup(primaryGroup);
    }

    public String getDisplayName() {
        Group primaryGroup = getPrimaryGroup();

        CachedMetaData metaData = primaryGroup.getCachedData().getMetaData();
        String playerListPrefix = metaData.getMetaValue("tab_prefix");
        if (playerListPrefix == null) {
            playerListPrefix = metaData.getMetaValue("color");
            if (playerListPrefix == null) {
                playerListPrefix = ChatColor.WHITE.toString();
            }
        }

        return ChatColor.translateAlternateColorCodes('&', playerListPrefix) + this.name;
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

    public Optional<Player> fetchPlayer() {
        return Optional.of(getPlayer());
    }

    @Override
    public String toString() {
        return "uuid=" + uuid.toString() + ";name=" + name;
    }
}