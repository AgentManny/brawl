package rip.thecraft.brawl.util.conversation;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import rip.thecraft.falcon.Falcon;

import java.util.function.Consumer;

public class PromptBuilder {

    private final Player player;

    private String text;
    private Consumer<String> acceptInput;

    public PromptBuilder(Player player, String message) {
        this.player = player;
        this.text = message;
    }

    public PromptBuilder input(Consumer<String> input) {
        this.acceptInput = input;
        return this;
    }

    public Prompt build() {
        return new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return text;
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                if (acceptInput != null) {
                    acceptInput.accept(s);
                }
                return Prompt.END_OF_CONVERSATION;
            }
        };
    }

    public Conversation createConversation() {
        return new ConversationFactory(Falcon.getInstance())
                .withLocalEcho(false)
                .withFirstPrompt(build())
                .thatExcludesNonPlayersWithMessage("Go away evil console!")
                .buildConversation(player);
    }

    public void start() {
        player.beginConversation(createConversation());
    }


}
