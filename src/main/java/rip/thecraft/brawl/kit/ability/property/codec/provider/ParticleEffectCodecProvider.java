package rip.thecraft.brawl.kit.ability.property.codec.provider;

import rip.thecraft.brawl.kit.ability.property.codec.Codec;
import rip.thecraft.brawl.util.ParticleEffect;

public class ParticleEffectCodecProvider implements Codec<ParticleEffect> {

    @Override
    public ParticleEffect decode(String value) {
        return ParticleEffect.valueOf(value.toUpperCase());
    }

    @Override
    public String encode(Object value) {
        return ((ParticleEffect)value).name();
    }

}
