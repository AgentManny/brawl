package rip.thecraft.brawl.kit.ability.property.codec;

public interface Codec<T> {

    T decode(String value);

    String encode(Object value);

}
