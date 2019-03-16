package gg.manny.brawl.entity.type;

import net.minecraft.server.v1_7_R4.EntityMonster;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;


public class CustomMonsterEntity extends EntityMonster {

    private String displayName = "";

    private double speed = 0.7D;
    private double maxHealth = 20D;
    private double knockbackResistance = 0D;
    private double followRange = 32D;
    private double strength = 2D;

    public CustomMonsterEntity(World world) {
        super(world);

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

    public void spawn(Location location){
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void spawn(Player player){
        this.spawn(player.getLocation());
    }

    @Deprecated
    public void spawn() {
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
