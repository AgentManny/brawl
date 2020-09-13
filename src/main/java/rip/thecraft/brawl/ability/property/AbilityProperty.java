package rip.thecraft.brawl.ability.property;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbilityProperty<T> {

    @NonNull protected T value;

    public abstract T value();

    public abstract void set(T newValue);

    public abstract T parse(String value);

    public String toString() {
        return String.valueOf(value);
    }

}
