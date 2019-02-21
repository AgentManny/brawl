package gg.manny.brawl.item.type;

import lombok.Getter;

public enum MetadataType {

    KIT_SELECTOR,
    PREVIOUS_KIT,
    EVENT_SELECTOR,
    SHOP,
    DUEL_ARENA;

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
