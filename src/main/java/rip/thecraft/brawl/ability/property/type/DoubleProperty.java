package rip.thecraft.brawl.ability.property.type;

import lombok.NonNull;
import rip.thecraft.brawl.ability.property.AbilityProperty;

public class DoubleProperty extends AbilityProperty<Double> {

    public DoubleProperty(@NonNull Double value) {
        super(value);
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public void set(Double newValue) {
        this.value = newValue;
    }

    @Override
    public Double parse(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
