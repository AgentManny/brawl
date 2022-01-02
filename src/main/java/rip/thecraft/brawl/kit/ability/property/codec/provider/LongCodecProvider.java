package rip.thecraft.brawl.kit.ability.property.codec.provider;

import rip.thecraft.brawl.kit.ability.property.codec.Codec;

public class LongCodecProvider implements Codec<Long> {

    @Override
    public Long decode(String value) {
        return Long.parseLong(value);
    }

    @Override
    public String encode(Object value) {
        return value.toString();
    }
}
