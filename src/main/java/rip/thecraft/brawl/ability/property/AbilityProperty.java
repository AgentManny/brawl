package rip.thecraft.brawl.ability.property;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;

@RequiredArgsConstructor
public abstract class AbilityProperty<T> {

    @NonNull protected T value;

    public abstract T value();

    public abstract void set(T newValue);

    public abstract T parse(String value) throws PropertyParseException;

    public String toString() {
        return String.valueOf(value);
    }


}
