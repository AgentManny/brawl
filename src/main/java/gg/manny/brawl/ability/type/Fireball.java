package gg.manny.brawl.ability.type;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.ability.Ability;
import gg.manny.pivot.util.inventory.ItemBuilder;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Fireball extends Ability {

    public Fireball() {
        super("Fireball", new ItemBuilder(Material.FIREBALL)
                .name(CC.GRAY + "\u00bb " + CC.GOLD + CC.BOLD + "Fireball" + CC.GRAY + " \u00ab")
                .create());
    }

    private boolean aimAssist = true;
    private int duration = 4;

    @Override
    public void onActivate(Player player) {
        if (this.hasCooldown(player, true)) return;
        this.addCooldown(player);

        new BukkitRunnable() {

            final Random r = Brawl.RANDOM;
            int time = 0;

            @Override
            public void run() {
                Location location = player.getLocation();

                if (time++ > duration) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < 5; i ++) {
                    FallingBlock block = location.getWorld().spawnFallingBlock(location.clone().add(r.nextInt(2) - 1, r.nextInt(2) - 1, r.nextInt(2) - 1), Material.FIRE, (byte) 0);
                    block.setVelocity(location.getDirection().multiply(5));
                    block.setDropItem(false);
                    block.setFireTicks(200);
                }
            }

        }.runTaskTimer(Brawl.getInstance(), 4L, 4L);
    }
}
