package rip.thecraft.brawl.item.type;

import lombok.Getter;

public enum MetadataType {

    KIT_SELECTOR,
    PREVIOUS_KIT,
    EVENT_SELECTOR,
    EVENT_LEAVE,
    EVENT_VOTE,
    EVENT_VOTE_SELECTED,
    SHOP,

    DUEL_ARENA,
    DUEL_ARENA_LEAVE,
    DUEL_ARENA_RANKED,
    DUEL_ARENA_UNRANKED,
    DUEL_ARENA_QUICK_QUEUE,

    QUEUE_LEAVE,

    SPECTATOR_LEAVE,

    LEADERBOARDS,
    LEADERBOARDS_ELO,

    DISABLED;

    @Getter
    private boolean cancellable;

    MetadataType() {
        this.cancellable = true;
    }

    MetadataType(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public String toMetadata() {
        return this.name();
    }

    public static MetadataType fromMetadata(String source) {
        return MetadataType.valueOf(source.toUpperCase());
    }

    public static boolean isMetadata(String source) {
        MetadataType type;
        try {
            type = fromMetadata(source);
        } catch (Exception ignored) {
            return false;
        }
        return type != null;
    }

}
