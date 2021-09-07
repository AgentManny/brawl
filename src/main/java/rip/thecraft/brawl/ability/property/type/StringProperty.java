package rip.thecraft.brawl.ability.property.type;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import rip.thecraft.brawl.ability.property.AbilityProperty;

@NoArgsConstructor
public class StringProperty extends AbilityProperty<String> {

    public StringProperty(@NonNull String value) {
        super(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void set(String newValue) {
        this.value = newValue;
    }

    @Override
    public AbilityProperty<String> parse(String value) {
        return new StringProperty(value);
    }

}