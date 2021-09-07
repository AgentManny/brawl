package rip.thecraft.brawl.ability.property.type;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;

@NoArgsConstructor
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
    public AbilityProperty<Double> parse(String value) throws PropertyParseException {
        try {
            return new DoubleProperty(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            throw new PropertyParseException(value + " is not a valid number (double)!");
        }
    }

}
