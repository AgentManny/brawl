package rip.thecraft.brawl.ability.property.codec;

public interface Codec<T> {

    T decode(String value);

    String encode(Object value);

}
