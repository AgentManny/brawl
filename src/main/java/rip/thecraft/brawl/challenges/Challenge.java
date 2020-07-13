package rip.thecraft.brawl.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public abstract class Challenge {

    protected final String name, description;
    protected final ChallengeType challengeType; // to help track timing and keep things organized
    protected final int maxProgress; // max progress is what is required to achieve reward
    @Setter protected int currentProgress; // data tracker for player
    protected long timestamp; // time when activated

    public abstract void increment(Player player);

    public abstract void complete(Player player);

    public String getDisplayName() {
        return this.name.replace("_", " ");
    }


}
