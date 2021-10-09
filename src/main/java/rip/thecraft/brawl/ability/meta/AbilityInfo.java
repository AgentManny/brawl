package rip.thecraft.brawl.ability.meta;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AbilityInfo {

    String name();

    String description() default "";

    ChatColor color();

    Material icon();

}
