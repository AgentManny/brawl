package rip.thecraft.brawl.ability.property.codec.provider;

import org.bukkit.Sound;
import rip.thecraft.brawl.ability.property.codec.Codec;

public class SoundCodecProvider implements Codec<Sound> {

    @Override
    public Sound decode(String value) {
        return Sound.valueOf(value.toUpperCase());
    }

    @Override
    public String encode(Object value) {
        return ((Sound)value).name();
    }

}
