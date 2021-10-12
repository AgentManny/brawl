package rip.thecraft.brawl.ability.property.codec.exception;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CodecParseException extends Exception {

    private String value;
    private String message;

    public CodecParseException(String value, String message) {
        super(value + ": " + message);
        this.value = value;
        this.message = message;
    }

    public void sendError(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.YELLOW + value + ChatColor.RED + " " + message);
    }

}
