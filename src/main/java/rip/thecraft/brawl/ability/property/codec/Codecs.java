package rip.thecraft.brawl.ability.property.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import rip.thecraft.brawl.ability.property.codec.provider.ParticleEffectCodecProvider;
import rip.thecraft.brawl.ability.property.codec.provider.SoundCodecProvider;
import rip.thecraft.brawl.util.ParticleEffect;

@AllArgsConstructor
public enum Codecs {

    PARTICLE_EFFECT(ParticleEffect.class, new ParticleEffectCodecProvider()),
    SOUND(Sound.class, new SoundCodecProvider());

    private Class<?> encoderClass;

    @Getter private Codec<?> codec;

    public static Codec<?> getCodecByClass(Class<?> clazz) {
        for (Codecs codec : values()) {
            if (codec.encoderClass.equals(clazz)) {
                return codec.getCodec();
            }
        }
        return null;
    }
}