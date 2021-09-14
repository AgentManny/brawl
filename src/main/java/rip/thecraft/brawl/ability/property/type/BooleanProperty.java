package rip.thecraft.brawl.ability.property.type;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import rip.thecraft.brawl.ability.property.AbilityProperty;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;

@NoArgsConstructor
public class BooleanProperty extends AbilityProperty<Boolean> {

    public BooleanProperty(@NonNull Boolean value) {
        super(value);
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public void set(Boolean newValue) {
        this.value = newValue;
    }

    @Override
    public AbilityProperty<Boolean> parse(String value) throws PropertyParseException {
        try {
            return new BooleanProperty(Boolean.parseBoolean(value));
        } catch (NumberFormatException e) {
            throw new PropertyParseException(value + " is not a valid boolean!");
        }
    }

}
