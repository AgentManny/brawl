package rip.thecraft.brawl.kit.ability.property;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AbilityData {

    /** Returns the name of ability */
    String name() default "";

    /** Returns the description of ability */
    String description() default "";

    /** Returns the color of ability */
    ChatColor color() default ChatColor.DARK_PURPLE;

    /** Returns the icon of ability */
    Material icon() default Material.AIR;

    boolean displayIcon() default true;

    /** Returns icon data of ability */
    byte data() default 0;

}
