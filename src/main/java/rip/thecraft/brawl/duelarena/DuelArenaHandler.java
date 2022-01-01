package rip.thecraft.brawl.duelarena;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.arena.Arena;
import rip.thecraft.brawl.duelarena.arena.ArenaType;
import rip.thecraft.brawl.duelarena.loadout.MatchLoadout;
import rip.thecraft.brawl.duelarena.loadout.custom.Arcade;
import rip.thecraft.brawl.duelarena.loadout.custom.Sumo;
import rip.thecraft.brawl.duelarena.loadout.type.*;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.duelarena.match.MatchSnapshot;
import rip.thecraft.brawl.duelarena.match.invite.PlayerMatchInvite;
import rip.thecraft.brawl.duelarena.match.queue.QueueType;
import rip.thecraft.brawl.duelarena.queue.QueueData;
import rip.thecraft.brawl.duelarena.queue.QueueSearchTask;
import rip.thecraft.brawl.server.item.type.InventoryType;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.player.PlayerState;
import rip.thecraft.brawl.util.PlayerUtil;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.Spartan;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DuelArenaHandler {

    public static final int INVITE_TIMEOUT = 30;
    public static final int REMATCH_TIMEOUT = 15;

    public Set<Match> matches = new HashSet<>();

    private List<Arena> arenas = new ArrayList<>();
    private List<MatchLoadout> loadouts = new ArrayList<>();

    private Set<PlayerMatchInvite> playerMatchInvites = new HashSet<>();

    private BiMap<MatchLoadout, UUID> unrankedQueue = HashBiMap.create();
    private Multimap<MatchLoadout, UUID> rankedQueue = ArrayListMultimap.create();

    private Map<String, MatchSnapshot> snapshotMap = new HashMap<>(); // TODO make this expire

    private Map<UUID, BukkitTask> queueTask = new HashMap<>();

    private UUID quickmatch;

    public DuelArenaHandler() {
        loadouts.addAll(Arrays.asList(
                new Elite(),
                new Speed(),

                new Refill(),
                new HG(),

                new Sumo(),
                new Tank(),
                new Arcade()
        ));

        loadArenas();

        this.startTask();
    }

    public void onDisable() {
        saveArenas();
    }

    public boolean hasQuickmatch() {
        return quickmatch != null || !unrankedQueue.isEmpty();
    }

    public void refreshQuickqueue() {
        for (Player other : Bukkit.getOnlinePlayers()) {
            PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(other);
            if (targetData.isDuelArena() && !isInMatch(other)) {
                targetData.getQueueData().updateQuickQueue(other);
            }
        }
    }

    public void createMatch(Player playerTwo, Player playerOne, MatchLoadout loadout, QueueType queueType) {
        List<Player> players = Arrays.asList(playerOne, playerTwo);
        players.forEach(player -> cleanup(player.getUniqueId()));

        Arena arena = getArena(loadout.getArena());
        if (arena == null) {
            players.forEach(player -> {
                player.sendMessage(ChatColor.RED + "There are no arenas available for " + ChatColor.YELLOW + WordUtils.capitalizeFully(loadout.getArena().name().toLowerCase()) + ChatColor.RED + ". Please queue again later.");
                DuelArena.respawn(player, false);
            });
            refreshQuickqueue();
            return;
        }

        for (Location loc : arena.getLocations()) {
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
        }

        Kit kit = null;
        if (loadout.getArena() == ArenaType.ARCADE) {
            kit = Brawl.getInstance().getKitHandler().getRandomAbilityKit();
            arena.setPlayable(false); // Prevents others from joining.
        }

        refreshQuickqueue();

        Match match = new Match(new ObjectId().toHexString(), queueType, arena, playerOne.getUniqueId(), playerTwo.getUniqueId(), loadout, kit);

        for (Player player : players) {
            Player opponent = Bukkit.getPlayer(match.getOpposite(player.getUniqueId()));

            PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
            PlayerData opponentData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(opponent);
            playerData.setDuelArena(false);
            PlayerUtil.resetInventory(player);
            if (queueType != QueueType.DUEL) {
                player.sendMessage(CC.YELLOW + CC.BOLD + "Match found! " + CC.GREEN +
                        player.getName() + (queueType == QueueType.RANKED ? " (" + playerData.getStatistic().get(loadout) + ")" : "") +
                        CC.YELLOW + " vs. " + CC.RED +
                        opponent.getName() + (queueType == QueueType.RANKED ? " (" + opponentData.getStatistic().getArenaStatistics().get(loadout) + ")" : ""));
            }
        }

        match.broadcast(ChatColor.YELLOW + "You are playing on arena " + ChatColor.LIGHT_PURPLE + match.getArena().getName() + ChatColor.YELLOW + ".");
        match.setMatchAmount(arena.getArenaType() == ArenaType.SUMO ? 3 : 1);

        match.setup();
        this.matches.add(match);

    }

    public Arena getArena(ArenaType type) {
        List<Arena> arenas = this.arenas.stream()
                .filter(Arena::isEnabled)
                .filter(Arena::isPlayable)
                .filter(arena -> arena.getArenaType() == type)
                .collect(Collectors.toList());

        if (arenas.isEmpty()) {
            return null;
        }

        return arenas.get(Brawl.RANDOM.nextInt(arenas.size()));
    }

    public void sendDuel(Player player, Player target, MatchLoadout loadout) {
        if (player == null || target == null) return;

        if (isInMatch(target) || isInMatch(player) || isInQueue(player) || isInQueue(target)) return;

        if (hasPlayerInvite(player.getUniqueId(), target.getUniqueId(), loadout)) {
            player.sendMessage(ChatColor.YELLOW + "You have already requested " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " to a " + ChatColor.LIGHT_PURPLE + loadout.getName() + ChatColor.YELLOW + " duel.");
            return;
        }

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20F, 0.5F);

        PlayerMatchInvite pmi = PlayerMatchInvite.createMatchInvite(player.getUniqueId(), target.getUniqueId(), loadout, false);
        registerInvitation(pmi);
    }

    public void registerInvitation(PlayerMatchInvite newInvite) {
        playerMatchInvites.removeIf(invite -> invite.getSender().equals(newInvite.getSender()) && invite.getTarget().equals(newInvite.getTarget()) && invite.getKitType().equals(newInvite.getKitType()));

        Player sender = Bukkit.getPlayer(newInvite.getSender());
        Player target = Bukkit.getPlayer(newInvite.getTarget());
        if (sender == null || target == null) {
            playerMatchInvites.remove(newInvite);
            return;
        }

        PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(target);
        if (!targetData.isDuelArena()) {
            sender.sendMessage(target.getDisplayName() + ChatColor.RED + " is not in the duel arena.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "You have sent a " + ChatColor.LIGHT_PURPLE + newInvite.getKitType().getName() + ChatColor.YELLOW + (newInvite.isRematch() ? " rematch " : " duel ") + "request to " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + ".");
        new FancyMessage(sender.getDisplayName())
                .color(ChatColor.YELLOW)
                .then(" has sent you a ")
                .color(ChatColor.YELLOW)
                .then(newInvite.getKitType().getName())
                .color(ChatColor.LIGHT_PURPLE)
                .then((newInvite.isRematch() ? " rematch " : " duel ") + "request.")
                .color(ChatColor.YELLOW)
                .then(" [Click to accept]")
                .color(ChatColor.GREEN)
                .tooltip(ChatColor.GREEN + "Click to accept")
                .command("/accept " + sender.getName())
                .send(target);
        playerMatchInvites.add(newInvite);
    }

    public void acceptInvitation(PlayerMatchInvite acceptedInvite) {
        Player senderPlayer = Bukkit.getPlayer(acceptedInvite.getSender());
        Player targetPlayer = Bukkit.getPlayer(acceptedInvite.getTarget());
        if (senderPlayer != null && targetPlayer != null) {

            PlayerData senderData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(senderPlayer);
            if (senderData.getPlayerState() != PlayerState.ARENA) {
                targetPlayer.sendMessage(senderPlayer.getDisplayName() + ChatColor.RED + " is no longer in the duel arena.");
                return;
            }

            PlayerData targetData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(targetPlayer);
            if (targetData.getPlayerState() != PlayerState.ARENA) {
                targetPlayer.sendMessage(ChatColor.RED + "You must be in the Duel Arena to accept duel requests.");
                return;
            }

            playerMatchInvites.removeIf(invite -> invite.getSender().equals(acceptedInvite.getSender()) && invite.getTarget().equals(acceptedInvite.getTarget()) && invite.getKitType().equals(acceptedInvite.getKitType()));
            senderPlayer.sendMessage(ChatColor.LIGHT_PURPLE + targetPlayer.getName() + ChatColor.YELLOW + " has accepted your " + ChatColor.LIGHT_PURPLE + acceptedInvite.getKitType().getName() + ChatColor.YELLOW + (acceptedInvite.isRematch() ? " rematch " : " duel ") + "request.");
            targetPlayer.sendMessage(ChatColor.YELLOW + "You have accepted " + ChatColor.LIGHT_PURPLE + senderPlayer.getName() + ChatColor.YELLOW + " with kit " + ChatColor.LIGHT_PURPLE + acceptedInvite.getKitType().getName() + ChatColor.YELLOW + ".");
            createMatch(senderPlayer, targetPlayer, acceptedInvite.getKitType(), QueueType.DUEL);
        }
    }

    public void joinQuickQueue(Player clicker) {
        MatchLoadout ml = null;

        for (PlayerMatchInvite pmi : getAllPlayerInvites(clicker.getUniqueId())) {
            UUID sender = pmi.getSender();
            Player player = Bukkit.getPlayer(sender);

            if (player != null) {
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                if (!isInMatch(player) && playerData.isDuelArena()) {
                    quickmatch = sender;
                    ml = pmi.getKitType();
                }
            }
        }

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(clicker);
        QueueData queueData = playerData.getQueueData();
        /* Standard Matches */
        if (quickmatch == null) {
            for (MatchLoadout loadout : this.getLoadouts()) {
                if (unrankedQueue.containsKey(loadout) && unrankedQueue.get(loadout) != clicker.getUniqueId()) {
                    quickmatch = unrankedQueue.get(loadout);
                    ml = loadout;
                }
            }
        }
        if (quickmatch == clicker.getUniqueId()) {
            leaveQueue(clicker);

        } else if (quickmatch == null) {
            quickmatch = clicker.getUniqueId();
            queueData.setQueueTime(System.currentTimeMillis());

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == clicker) continue;
                PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                if (pd.isDuelArena() && !isInMatch(player)) {
                    pd.getQueueData().updateQuickQueue(player);
                }
            }

            Brawl.getInstance().getItemHandler().apply(clicker, InventoryType.QUEUE);
            clicker.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.LIGHT_PURPLE + "Quickmatch" + ChatColor.GREEN + " queue.");
            clicker.playSound(clicker.getLocation(), Sound.NOTE_PIANO, 20F, 15F);
        } else {
            Player existing = Bukkit.getPlayer(quickmatch);

            // Local matchloadout is only null if no other queues or requests were able to be found
            // meaning that the found quickmatch is in fact another queued quickmatch

            if (ml == null) {
                ml = getLoadoutFromClass(Elite.class);
            }

            if (existing != null) {

                createMatch(existing, clicker, ml, QueueType.UNRANKED);
            }
        }

    }

    public void joinUnrankedQueue(Player clicker, MatchLoadout loadout) {
        if (this.isInQueue(clicker) || this.isInMatch(clicker)) return;

        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(clicker);
        QueueData queueData = playerData.getQueueData();

        queueData.setQueueTime(System.currentTimeMillis());

        UUID waiting = unrankedQueue.get(loadout);

        if (waiting == null) {
            for (PlayerMatchInvite pmi : getAllPlayerInvites(clicker.getUniqueId())) {
                if (pmi.getKitType() == loadout) {
                    waiting = pmi.getSender();
                    break;
                }
            }
        }

        if (waiting == null && quickmatch != null) {
            waiting = quickmatch;
        }

        if (waiting != null) {
            Player waiter = Bukkit.getPlayer(waiting);


            if (waiter != null) {
                createMatch(waiter, clicker, loadout, QueueType.UNRANKED);
                return;
            }

            unrankedQueue.remove(loadout);
        }

        unrankedQueue.entrySet().removeIf(e -> e.getValue() == clicker.getUniqueId());
        unrankedQueue.put(loadout, clicker.getUniqueId());

        refreshQuickqueue();

        clicker.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.LIGHT_PURPLE + loadout.getColor() + loadout.getName() + ChatColor.GREEN + " queue.");
        Brawl.getInstance().getItemHandler().apply(clicker, InventoryType.QUEUE);
    }

    private static boolean RANKED_DISABLED  = true;

    public void joinRankedQueue(Player clicker, MatchLoadout loadout) {
        if (RANKED_DISABLED) {
            clicker.sendMessage(ChatColor.RED + "Ranked is currently disabled.");
            return;
        }
        if (this.isInQueue(clicker) || this.isInMatch(clicker)) return;

        cleanup(clicker.getUniqueId());

        this.rankedQueue.get(loadout).add(clicker.getUniqueId());

        PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(clicker.getUniqueId());
        pd.getQueueData().setQueueTime(System.currentTimeMillis());


        QueueSearchTask task = new QueueSearchTask(clicker, loadout);
        pd.getQueueData().setTask(task);

        task.runTaskTimer(Brawl.getInstance(), 20L, 20L);

        clicker.sendMessage(ChatColor.GREEN + "You have joined the " + ChatColor.LIGHT_PURPLE + loadout.getColor() + "Ranked " + loadout.getName() + ChatColor.GREEN + " queue.");
        Brawl.getInstance().getItemHandler().apply(clicker, InventoryType.QUEUE);
    }

    public void leaveQueue(Player player) {
        QueueType type;
        MatchLoadout loadout = null;

        if (quickmatch == player.getUniqueId()) {

            type = QueueType.QUICKMATCH;
        } else if (unrankedQueue.containsValue(player.getUniqueId())) {
            loadout = unrankedQueue.inverse().get(player.getUniqueId());
            type = QueueType.UNRANKED;
        } else if (rankedQueue.containsValue(player.getUniqueId())) {
            loadout = rankedQueue.entries().stream().filter(e -> e.getValue() == player.getUniqueId()).findAny().orElse(null).getKey();
            type = QueueType.RANKED;
        } else return; // weird thing


        player.sendMessage(ChatColor.RED + "You have left the " + (loadout == null ? ChatColor.LIGHT_PURPLE + type.getName() : loadout.getColor() + (type == QueueType.RANKED ? type.getName() + " " : "") + loadout.getName()) + ChatColor.RED + " queue.");
        cleanup(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other == player) continue;
            PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(other);
            if (pd.isDuelArena() && !isInMatch(other)) {
                pd.getQueueData().updateQuickQueue(other);
            }
        }
        Brawl.getInstance().getItemHandler().apply(player, InventoryType.ARENA);
    }

    public String getFriendlyQueue(Player player) {
        QueueType type = null;
        MatchLoadout loadout = null;

        if (quickmatch == player.getUniqueId()) {
            type = QueueType.QUICKMATCH;
        } else if (unrankedQueue.containsValue(player.getUniqueId())) {
            loadout = unrankedQueue.inverse().get(player.getUniqueId());
            type = QueueType.UNRANKED;
        } else if (rankedQueue.containsValue(player.getUniqueId())) {
            loadout = rankedQueue.entries().stream().filter(e -> e.getValue() == player.getUniqueId()).findAny().orElse(null).getKey();
            type = QueueType.RANKED;
        }


        return type.getName() + " " + (loadout == null ? "" : loadout.getName());
    }

    public void cleanup(UUID uuid) {
        unrankedQueue.entrySet().removeIf(entry -> entry.getValue() == uuid);
        rankedQueue.entries().removeIf(entry -> entry.getValue() == uuid);

        if (quickmatch == uuid) {
            quickmatch = null;
        }

        PlayerData pd = Brawl.getInstance().getPlayerDataHandler().getPlayerData(uuid);
        if (pd != null) {
            QueueSearchTask task = pd.getQueueData().getTask();
            if (task != null) {
                pd.getQueueData().getTask().cancel();
                pd.getQueueData().setTask(null);
            }

            pd.getQueueData().setQueueTime(-1L);
        }
    }

    public boolean isInMatch(Player player) {
        return matches.stream().anyMatch(m -> m.contains(player));
    }

    public boolean isInQueue(Player player) {
        UUID uuid = player.getUniqueId();
        return quickmatch == uuid || unrankedQueue.values().contains(uuid) || rankedQueue.values().contains(uuid);
    }


    public QueueType getQueue(Player player) {
        UUID uuid = player.getUniqueId();
        return quickmatch == uuid ? QueueType.QUICKMATCH : unrankedQueue.containsValue(uuid) ? QueueType.UNRANKED : rankedQueue.containsValue(QueueType.RANKED) ? QueueType.RANKED : null;
    }

    public Match getMatch(Player player) {
        for (Match match : matches) {
            if (match != null && match.contains(player)) {
                return match;
            }
        }
        return null;
    }

    public Match getSpectatingMatch(Player player) {
        for (Match match : matches) {
            if (match != null && match.getMatchData().getSpectators().contains(player.getUniqueId())) {
                return match;
            }
        }
        return null;
    }

    public Match containsPlayer(Player player, boolean spectating) {
        for (Match match : matches) {
            if (match != null && (match.contains(player) || (spectating && match.getMatchData().getSpectators().contains(player.getUniqueId())))) {
                return match;
            }
        }
        return null;
    }

    public int getPlaying(MatchLoadout loadout) {
        return (int) matches.stream().filter(m -> m.getLoadout().equals(loadout)).count();
    }

    public int getQueued(MatchLoadout loadout, QueueType queueType) {
        return queueType == QueueType.RANKED ? rankedQueue.get(loadout).size() : queueType == QueueType.UNRANKED && unrankedQueue.containsKey(loadout) ? 1 : 0;
    }


    public boolean hasPlayerInvite(UUID sender, UUID target) {
        return (hasPlayerInvite(sender, target, null));
    }

    public boolean hasPlayerInvite(UUID sender, UUID target, MatchLoadout kitType) {
        return (getPlayerInvite(sender, target, kitType) != null);
    }

    public PlayerMatchInvite getPlayerInvite(UUID sender, UUID target) {
        return (getPlayerInvite(sender, target, null));
    }

    public PlayerMatchInvite[] getAllPlayerInvites(UUID target) {
        return playerMatchInvites.stream().filter(i -> i.getTarget() == target && i.isValid()).toArray(PlayerMatchInvite[]::new);
    }

    public PlayerMatchInvite getPlayerInvite(UUID sender, UUID target, MatchLoadout kitType) {
        for (PlayerMatchInvite invite : playerMatchInvites) {
            if ((sender == null || invite.getSender().equals(sender)) && invite.getTarget().equals(target) && (kitType == null || invite.getKitType().equals(kitType)) && invite.isValid()) {
                return (invite);
            }
        }

        return (null);
    }

    public MatchLoadout getLoadoutFromClass(Class<? extends MatchLoadout> clazz) {
        return loadouts.stream().filter( c->c.getClass() == clazz).findFirst().orElse(null);
    }

    public boolean inQuickQueue(UUID uuid) {
        return quickmatch == uuid;
    }

    public void dispose(Match match) {
        match.getArena().setPlayable(true);
        matches.removeIf(m -> m.getPlayer1() == match.getPlayer1() && m.getPlayer2() == match.getPlayer2());
    }

    public void addSnapshot(MatchSnapshot snapshot) {
        this.snapshotMap.put(snapshot.getId(), snapshot);
    }

    private void loadArenas() {
        try {
            File file = getFile();
            String payload = FileUtils.readFileToString(file);

            if (!payload.isEmpty()) {
                BasicDBObject data = BasicDBObject.parse(payload);

                BasicDBList koths = (BasicDBList) data.get("arenas");
                if (koths != null) {
                    for (Object object : koths) {
                        BasicDBObject dbo = (BasicDBObject) object;
                        this.arenas.add(new Arena(dbo));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveArenas() {
        File file = getFile();

        BasicDBList arenas = new BasicDBList();
        this.arenas.forEach(a -> arenas.add(a.toJson()));

        try {
            FileUtils.write(file, Spartan.GSON.toJson(new JsonParser().parse(new BasicDBObject("arenas", arenas).toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<PlayerMatchInvite> playerInviteIterator = playerMatchInvites.iterator();

                while (playerInviteIterator.hasNext()) {
                    PlayerMatchInvite invite = playerInviteIterator.next();

                    Player inviter = Bukkit.getPlayer(invite.getSender());
                    Player target = Bukkit.getPlayer(invite.getTarget());


                    if (!invite.isValid() || inviter == null || target == null || Brawl.getInstance().getPlayerDataHandler().getPlayerData(target).getPlayerState() != PlayerState.ARENA) {
                        playerInviteIterator.remove();
                    }
                }


            }

        }.runTaskTimer(Brawl.getInstance(), 20L, 20L);
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "arenas.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
