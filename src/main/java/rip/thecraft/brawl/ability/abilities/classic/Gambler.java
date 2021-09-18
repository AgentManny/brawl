package rip.thecraft.brawl.ability.abilities.classic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.util.BukkitUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Gambler extends Ability {

    // Gambler - Random potion effects
    // Passive (Right Click) / Attack ability (Left Click)
    //
    // Reduce the potency of negative effects (and cooldown applied to it by 25%)
    //

    public Gambler() {
        addProperty("passive-negative-cooldown", 10., "Cooldown applied to negative effects applied to actor.");

    }

    public Material getType() {
        return Material.POTION;
    }

    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public String getDescription() {
        return "Gives you random effects that can help you positively or negatively";
    }

    public void onActivate(Player player) {
        if (hasCooldown(player, true)) {
            return;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        GamblerEffect randomEffect = GamblerEffect.values()[random.nextInt(0, GamblerEffect.values().length - 1)];

        Double cooldown = randomEffect.isNegative() ? getProperty("passive-negative-cooldown") : getProperty("cooldown");
        addCooldown(player, TimeUnit.SECONDS.toMillis(cooldown.longValue()));

        PotionEffect potionEffect = randomEffect.applyTo(random, player);
        player.sendMessage(ChatColor.YELLOW + "You've taken a gamble and received " + BukkitUtil.getFriendlyName(potionEffect) + ChatColor.YELLOW + ".");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private enum GamblerEffect {

        // Positive effects
        SPEED,
        INCREASE_DAMAGE,
        HEAL,
        JUMP,
        REGENERATION,
        DAMAGE_RESISTANCE,
        HEALTH_BOOST(),
        ABSORPTION,


        SATURATION(2, 2, 10, false), // This effect will give you extra soups/potions instead

        // Neutral effects -- Effects that are just added that prolly do nothing
        WATER_BREATHING,
        FAST_DIGGING,
        NIGHT_VISION,
        INVISIBILITY,
        FIRE_RESISTANCE,

        // Negative effects
        SLOW(true),
        SLOW_DIGGING(true),
        HARM(0, 10, 20, true),
        CONFUSION(2, 20, 120, true),
        BLINDNESS(true),
        HUNGER(1, 2, 3, true), // This effect will remove extra soups/potions instead
        WEAKNESS(true),
        POISON(true),
        WITHER(true),
        ;

        /** Returns the max potency of effect */
        private int maxAmplifier = 3;

        /** Returns the min duration of effect */
        private int minDuration = 80;

        /** Returns the max duration of effect */
        private int maxDuration = 300; // 300 = 15 seconds

        /** Returns whether an effect is negative */
        @Getter private boolean negative = false;

        GamblerEffect(boolean negative) {
            this.negative = negative;
        }

        public PotionEffectType getPotion() {
            return PotionEffectType.getByName(name());
        }

        /**
         * Apply an effect to a player
         * @param player Player to apply potion effect to
         */
        public PotionEffect applyTo(ThreadLocalRandom random, Player player) {
            int duration = random.nextInt(minDuration, maxDuration);
            int amplifier = random.nextInt(0, maxAmplifier);

            PotionEffect potionEffect = new PotionEffect(getPotion(), duration, amplifier);

            if (this == SATURATION || this == HUNGER) {
                PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
                ItemStack item = playerData.getRefillType().getItem();
                for (int i = 0; i < (duration * amplifier); i++) {
                    if (negative) {
                        player.getInventory().remove(item);
                    } else {
                        player.getInventory().addItem(item);
                    }
                }
            } else {
                player.addPotionEffect(potionEffect);
                if (this == HEALTH_BOOST) {
                    double healthDiff = player.getMaxHealth() - player.getHealth();
                    player.setHealth(Math.max(player.getMaxHealth(), player.getHealth() + healthDiff));
                }
            }
            return potionEffect;
        }
    }
}
