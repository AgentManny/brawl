package gg.manny.brawl.event.koth.command;

import gg.manny.brawl.Brawl;
import gg.manny.brawl.event.EventHandler;
import gg.manny.brawl.event.koth.KOTH;
import gg.manny.brawl.region.selection.Selection;
import gg.manny.brawl.util.cuboid.Cuboid;
import gg.manny.pivot.util.TimeUtils;
import gg.manny.quantum.command.Command;
import gg.manny.server.util.chatcolor.CC;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@Command(names = "koth", permission = "brawl.koth")
public class KOTHCommands {

    @Command(names = "create", permission = "op")
    public void create(Player sender, String name) {
        if (Brawl.getInstance().getEventHandler().getKOTHByName(name) != null) {
            sender.sendMessage(CC.RED + "KOTH " + name + ChatColor.RED + " already exists.");
            return;
        }

        Brawl.getInstance().getEventHandler().createKOTH(name);
        sender.sendMessage(KOTH.PREFIX + name + ChatColor.YELLOW + " has been created.");
        sender.sendMessage(ChatColor.YELLOW + "Type '/koth setcapturezone' to set the capture zone.");
    }

    @Command(names = "delete", permission = "op")
    public void delete(Player sender, KOTH koth) {
        ConversationFactory factory = new ConversationFactory(Brawl.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to delete " + koth.getName() + "? Type §byes§a to confirm or §cno§a to quit.";
            }

            public Prompt acceptInput(ConversationContext ChatColor, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    EventHandler gh = Brawl.getInstance().getEventHandler();
                    if (gh.getActiveKOTH() == koth) {
                        gh.getActiveKOTH().finish(sender);
                    }
                    gh.getKOTHS().remove(koth.getName());
                    gh.getKOTHS().entrySet().removeIf((entry) -> entry.getValue() == koth);
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.equalsIgnoreCase("no")) {
                    ChatColor.getForWhom().sendRawMessage(CC.GREEN + "KOTH removal cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                ChatColor.getForWhom().sendRawMessage(CC.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Command(names = "setcapdelay", permission = "brawl.koth")
    public void execute(Player sender, String time) {
        EventHandler gh = Brawl.getInstance().getEventHandler();
        KOTH koth = gh.getActiveKOTH();
        if (koth == null) {
            sender.sendMessage(ChatColor.RED + "There is no active koth.");
            return;
        }

        int seconds = TimeUtils.parseTime(time);

        KOTH.DEFAULT_CAPTURE_TIME = seconds;
        koth.setCaptureTime(seconds);

        sender.sendMessage(KOTH.PREFIX + ChatColor.YELLOW + "Updated capture delay to " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoMMSS(seconds) + ChatColor.YELLOW + ".");
    }

    @Command(names = "start", permission = "brawl.koth")
    public void execute(Player sender, KOTH koth) {
        EventHandler gh = Brawl.getInstance().getEventHandler();
        if (gh.getActiveKOTH() != null) {
            sender.sendMessage(ChatColor.RED + "There is already an active koth.");
            return;
        }

        koth.start(sender);
    }

    @Command(names = "stop", permission = "brawl.koth")
    public void execute(Player sender) {
        EventHandler gh = Brawl.getInstance().getEventHandler();
        if (gh.getActiveKOTH() == null) {
            sender.sendMessage(ChatColor.RED + "There are no active koths.");
            return;
        }

        gh.getActiveKOTH().finish(sender);
        gh.setActiveKOTH(null);
    }

    @Command(names = "setcapzone", permission = "op")
    public void setCapzone(Player sender, KOTH koth) {
        Selection sel = Selection.createOrGetSelection(sender);

        if (!sel.isFullObject()) {
            sender.sendMessage(ChatColor.RED + "Please select a region.");
            return;
        }

        Cuboid cuboid = new Cuboid(sel.getPoint1(), sel.getPoint2());
        koth.setCaptureZone(cuboid);

        Brawl.getInstance().getGameHandler().save();
        sender.sendMessage(KOTH.PREFIX + ChatColor.WHITE + koth.getName() + ChatColor.YELLOW + " capture zone has been updated.");

    }


}
