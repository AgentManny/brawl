package rip.thecraft.brawl.ability.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;

@NoArgsConstructor
@RequiredArgsConstructor
public abstract class AbilityProperty<T> {

    @NonNull protected T value;

    @Getter private transient String description = null;

    public abstract T value();

    public abstract void set(T newValue);

    public abstract AbilityProperty<T> parse(String value) throws PropertyParseException;

    public String toString() {
        return String.valueOf(value);
    }

    public AbilityProperty<T> description(String description) {
        this.description = description;
        return this;
    }

}
