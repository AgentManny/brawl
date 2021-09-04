package rip.thecraft.brawl.ability.property.type;

import lombok.NonNull;
import org.apache.http.ParseException;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;

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
    public Integer parse(String value) throws PropertyParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new PropertyParseException(value + " is not a valid number (integer)!");
        }
    }
}
