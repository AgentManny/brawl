package gg.manny.brawl.entity.type;

import net.minecraft.server.v1_7_R4.EntitySilverfish;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomEntitySilverfish extends EntitySilverfish {

    private String displayName = "";

    private double speed = 0.7D;
    private double maxHealth = 20D;
    private double knockbackResistance = 0D;
    private double followRange = 32D;
    private double strength = 2D;

    public CustomEntitySilverfish(World world) {
        super(world);

        if (!this.displayName.isEmpty()) {
            this.setCustomName(this.displayName);
            this.setCustomNameVisible(true);
        }
    }

    public CustomEntitySilverfish(Location location) {
        super(((CraftWorld)location.getWorld()).getHandle());

        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        if (!this.displayName.isEmpty()) {
            this.setCustomName(this.displayName);
            this.setCustomNameVisible(true);
        }
    }

    @Override
    protected void aD() {
        super.aD();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(this.maxHealth);
        this.getAttributeInstance(GenericAttributes.b).setValue(this.followRange);
        this.getAttributeInstance(GenericAttributes.c).setValue(this.knockbackResistance);
        this.getAttributeInstance(GenericAttributes.d).setValue(this.speed);
        this.getAttributeInstance(GenericAttributes.e).setValue(this.strength);
    }

    public CustomEntitySilverfish speed(double speed) {
        this.speed = speed;
        return this;
    }

    public CustomEntitySilverfish maxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public CustomEntitySilverfish followRange(double followRange) {
        this.followRange = followRange;
        return this;
    }

    public CustomEntitySilverfish knockbackResistance(double knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public CustomEntitySilverfish strength(double strength) {
        this.strength = strength;
        return this;
    }

    public CustomEntitySilverfish displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }


    public void spawn(Location location){
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void spawn(Player player){
        this.spawn(player.getLocation());
    }

    public void spawn() {
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
