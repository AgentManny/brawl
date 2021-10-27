package rip.thecraft.brawl.command.manage;

import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.ability.Ability;
import rip.thecraft.brawl.ability.AbilityHandler;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.codec.Codec;
import rip.thecraft.brawl.ability.property.codec.Codecs;
import rip.thecraft.spartan.command.Command;
import rip.thecraft.spartan.command.MCommandHandler;
import rip.thecraft.spartan.command.ParameterType;

import java.lang.reflect.Field;
import java.util.Map;

public class AbilityCommand {

    private static final AbilityHandler ah = Brawl.getInstance().getAbilityHandler();

    private static String getFriendlyName(String id) {
        return WordUtils.capitalizeFully(id.replace("-", " "));
    }

    @Command(names = {"ability"}, permission = "op", description = "Manage abilities")
    public static void manage(Player sender, Ability ability) {
        Map<String, Field> properties = ability.getProperties();
        sender.sendMessage(ChatColor.YELLOW + "Properties of " + ChatColor.LIGHT_PURPLE + ability.getName() + ChatColor.YELLOW + " (" + ChatColor.LIGHT_PURPLE + properties.size() + ChatColor.YELLOW + "):");
        for (Map.Entry<String, Field> entry : ability.getProperties().entrySet()) {
            String key = entry.getKey();
            Field value = entry.getValue();
            try {
                AbilityProperty property = value.getAnnotation(AbilityProperty.class);
                Object propertyValue = value.get(ability);
                new FancyMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + getFriendlyName(key) + ChatColor.GRAY + " (" + value.getType().getSimpleName() + ")" + ChatColor.YELLOW + ": " + ChatColor.LIGHT_PURPLE + propertyValue)
                        .tooltip(
                                ChatColor.GRAY + "Property " + ChatColor.WHITE + key,
                                ChatColor.GRAY + "Description: " + (property.description().isEmpty() ? ChatColor.RED + "None" : ChatColor.WHITE + property.description())
                        ).suggest("/ability set " + ability.getName().toLowerCase().replace(" ", "") + " " + key + " " + propertyValue.toString())
                        .send(sender);
            } catch (IllegalAccessException ignored) {
            }
        }
        sender.sendMessage(ChatColor.GRAY + "Hover a property for more information");
        sender.sendMessage(ChatColor.RED + "Usage: /ability set " + ability.getName().toLowerCase().replace(" ", "") + " <property> <newValue>");
    }

    @Command(names = {"ability list"}, permission = "op", description = "List all abilities")
    public static void manage(Player sender) {
        Map<String, Ability> abilities = ah.getAbilities();
        sender.sendMessage(ChatColor.YELLOW + "Abilities ( " + ChatColor.LIGHT_PURPLE + abilities.size() + ChatColor.YELLOW + "):");
        abilities.forEach((key, ability) -> sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + ability.getName()));
    }

    @Command(names = {"ability set"}, permission = "op", description = "Manage abilities")
    public static void set(Player sender, Ability ability, String property, String newValue) {
        Map<String, Field> properties = ability.getProperties();
        for (Map.Entry<String, Field> entry : properties.entrySet()) {
            String id = entry.getKey();
            Field field = entry.getValue();
            Class<?> type = field.getType();
            if (id.replace("-", "").equalsIgnoreCase(property.replace("-", ""))) {
                Codec<?> codec = Codecs.getCodecByClass(type);
                ParameterType<?> parameterType = MCommandHandler.getParameterType(type);
                if (codec != null) {
                    try {
                        Object decode = codec.decode(newValue);
                        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + ability.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.RED + field.get(ability) + ChatColor.GRAY + " --> " + ChatColor.GREEN + newValue);
                        field.set(ability, decode);
                        ah.save();
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                        return;
                    }
                } else if (parameterType != null) {
                    Object transform = parameterType.transform(sender, newValue);
                    if (transform != null) {
                        try {
                            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.WHITE + ability.getName() + ChatColor.YELLOW + " property to " + ChatColor.LIGHT_PURPLE + getFriendlyName(id) + ChatColor.YELLOW + ": " + ChatColor.RED + field.get(ability) + ChatColor.GRAY + " --> " + ChatColor.GREEN + newValue);
                            field.set(ability, transform);
                            ah.save();
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + newValue + ChatColor.RED + " is not a valid " + type.getSimpleName().toLowerCase() + ".");
                            e.printStackTrace();
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + id + ChatColor.RED + " property is not editable.");
                }
                return;
            }
        }
    }
}