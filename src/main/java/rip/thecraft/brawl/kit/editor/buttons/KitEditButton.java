package rip.thecraft.brawl.kit.editor.buttons;

import lombok.AllArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.PlayerInventory;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.Kit;
import rip.thecraft.brawl.kit.editor.action.KitEditAction;
import rip.thecraft.brawl.kit.editor.menu.KitAbilityEditMenu;
import rip.thecraft.server.util.chatcolor.CC;
import rip.thecraft.spartan.menu.Button;

import java.util.ArrayList;

@AllArgsConstructor
public class KitEditButton extends Button {

    private Kit kit;
    private KitEditAction action;

    @Override
    public String getName(Player player) {
        return CC.GOLD + action.getDisplay();
    }

    @Override
    public Material getMaterial(Player player) {
        switch (action) {
            case ICON:
                return Material.DIAMOND_SWORD;
            case PRICE:
                return Material.EMERALD;
            case UPDATE:
                return Material.NETHER_STAR;
            case WEIGHT:
                return Material.GOLD_NUGGET;
            case ABILITY:
                return Material.FIREWORK_CHARGE;
            case DESCRIPTION:
                return Material.SIGN;
        }
        return Material.DIRT;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (action == KitEditAction.UPDATE) {
            PlayerInventory inventory = player.getInventory();
            kit.setPotionEffects(new ArrayList<>(player.getActivePotionEffects()));
            kit.setArmor(new Armor(inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()));
            kit.setItems(new Items(inventory.getContents()));
            new FancyMessage(ChatColor.GREEN + "Updated kit. Click Here to return back to spawn.")
                    .tooltip(CC.GRAY + "Click to return to spawn.")
                    .command("/spawn")
                    .send(player);
            Brawl.getInstance().getKitHandler().save();
        } else if (action == KitEditAction.ABILITY) {
            new KitAbilityEditMenu(kit).openMenu(player);
            player.sendMessage(CC.GREEN + "You have opened the ability editor for kit " + kit.getName() + ".");
        } else {
            Conversation conversation = new ConversationFactory(Brawl.getInstance())
                    .withLocalEcho(false)
                    .withFirstPrompt(new KitEditPrompt(player, kit, action))
                    .thatExcludesNonPlayersWithMessage("Go away evil console!")
                    .buildConversation(player);

            player.closeInventory();
            player.beginConversation(conversation);
        }
    }

    @AllArgsConstructor
    private class KitEditPrompt extends StringPrompt {

        private Player player;
        private Kit kit;
        private KitEditAction action;

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return (action != KitEditAction.ICON ? ChatColor.GREEN + "Type a new value for " + kit.getName() + "'s " + action.name().toLowerCase() + "." :
                    ChatColor.GREEN + "The kit icon has been updated to the item in your hand.");
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String s) {
            switch (this.action) {
                case WEIGHT:
                    kit.setWeight(Integer.parseInt(s));
                    player.sendMessage(CC.GREEN + "You have editted " + kit.getName() + "'s " + action.name().toLowerCase() + ".");
                    break;
                case DESCRIPTION:
                    kit.setDescription(CC.translate(s));
                    player.sendMessage(CC.GREEN + "You have editted " + kit.getName() + "'s " + action.name().toLowerCase() + ".");
                    break;
                case PRICE:
                    kit.setPrice(Double.parseDouble(s));
                    player.sendMessage(CC.GREEN + "You have editted " + kit.getName() + "'s " + action.name().toLowerCase() + ".");
                    break;
                case ICON:
                    kit.setIcon(player.getItemInHand());
                    player.sendMessage(CC.GREEN + "You have editted " + kit.getName() + "'s " + action.name().toLowerCase() + ".");
                    break;
                default:
                    break;
            }
            return Prompt.END_OF_CONVERSATION;
        }
    }

}
