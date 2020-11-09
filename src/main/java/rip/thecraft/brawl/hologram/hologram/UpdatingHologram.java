package rip.thecraft.brawl.hologram.hologram;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.thecraft.spartan.Spartan;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UpdatingHologram extends BaseHologram {

    private long interval;
    private Consumer<Hologram> updateFunction;
    private boolean showing;

    public UpdatingHologram(UpdatingHologramBuilder builder) {
        super(builder);
        this.interval = 1L;
        this.showing = false;
        this.interval = builder.getInterval();
        this.updateFunction = builder.getUpdateFunction();
    }

    @Override
    public void send() {
        if (this.showing) {
            this.update();
            return;
        }
        super.send();
        this.showing = true;
        new BukkitRunnable() {
            public void run() {
                if (!showing) {
                    cancel();
                } else {
                    update();
                }
            }
        }.runTaskTimerAsynchronously(Spartan.getInstance(), 0L, this.interval * 20L);
    }

    @Override
    public void setLine(int index, String line) {
        if (index > this.rawLines().size() - 1) {
            this.rawLines().add(new HologramLine(line));
        } else if (this.rawLines().get(index) != null) {
            this.rawLines().get(index).setText(line);
        } else {
            this.rawLines().set(index, new HologramLine(line));
        }
    }

    @Override
    public void setLines(Collection<String> lines) {
        Collection<UUID> viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf(Spartan.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.destroy0(player);
            }
        }
        this.rawLines().clear();
        for (String line : lines) {
            this.rawLines().add(new HologramLine(line));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.showing = false;
    }

    @Override
    public void update() {
        this.updateFunction.accept(this);
        if (!this.showing) {
            return;
        }
        Collection<UUID> viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf(Spartan.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                this.update(player);
            }
        }
        this.lastLines = this.lines;
    }

}
