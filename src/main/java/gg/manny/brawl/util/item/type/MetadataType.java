package gg.manny.brawl.util.item.type;

public enum MetadataType {

    KIT_SELECTOR,
    EVENT_SELECTOR;

    public String toMetadata() {
        return this.name();
    }

    public static MetadataType fromMetadata(String source) {
        return MetadataType.valueOf(source.toUpperCase());
    }

}
