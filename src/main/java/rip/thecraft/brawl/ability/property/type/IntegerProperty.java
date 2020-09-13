package rip.thecraft.brawl.ability.property.type;

import lombok.NonNull;
import rip.thecraft.brawl.ability.property.AbilityProperty;

public class IntegerProperty extends AbilityProperty<Integer> {

    public IntegerProperty(@NonNull Integer value) {
        super(value);
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public void set(Integer newValue) {
        this.value = newValue;
    }

    @Override
    public Integer parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
