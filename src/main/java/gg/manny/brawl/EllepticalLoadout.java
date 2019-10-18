package gg.manny.brawl;

import gg.manny.server.knockback.loadout.KnockbackLoadout;
import net.minecraft.server.*;
import net.minecraft.server.v1_7_R4.*;

public class EllepticalLoadout extends KnockbackLoadout {

    public EllepticalLoadout() {
        super("Elleptical");
    }

    @Override
    public void load() {
        addValue("horizontal", "Applies to [x, z]", 0.35);

        addValue("vertical", "Applies to [y]", 0.35);
        addValue("vertical limit", "Limits amount of [y]", 0.4);

        addValue("friction", "Applies to [x, y, z]", 2.0);

        addValue("horizontal wtap", "Applies to [x, z] while sprinting", 0.1);
        addValue("vertical wtap", "Applies to [y] while sprinting", 0.1);

    }

    @Override
    public void onAttack(EntityLiving entityLiving, Entity entity, double d0, double d1) {
        float magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

        double horizontal = (double) getValue("horizontal").get();
        double vertical = (double) getValue("vertical").get();
        double verticalLimit = (double) getValue("vertical limit").get();

        double friction = (double) getValue("friction").get();

        horizontal = (float) (horizontal);
        vertical = (float) (vertical);

        entityLiving.motX /= friction;
        entityLiving.motY /= friction;
        entityLiving.motZ /= friction;

        entityLiving.motX -= d0 / magnitude * horizontal;
        entityLiving.motY += vertical;
        entityLiving.motZ -= d1 / magnitude * horizontal;
        if (entityLiving.motY > verticalLimit) {
            entityLiving.motY = verticalLimit;
        }
    }

    @Override
    public void onAttackSprint(EntityHuman entityHuman, Entity entity, float i) {
        double horizontal = (double) getValue("horizontal").get();
        double vertical = (double) getValue("vertical").get() + ((double) getValue("vertical wtap").get());

        double velX = -MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F;
        double velZ = MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F;

        velX *= (double) getValue("horizontal wtap").get();
        velZ *= (double) getValue("horizontal wtap").get();

        entity.g(
                velX,
                0.1,
                velZ
        );

        entityHuman.motX *= horizontal;
        if (vertical != 0) {
            entityHuman.motY += vertical;
        }
        entityHuman.motZ *= horizontal;
    }

    @Override
    public void onAttackVelocityChange(EntityHuman entityHuman, Entity entity, float i) {
    }
}
