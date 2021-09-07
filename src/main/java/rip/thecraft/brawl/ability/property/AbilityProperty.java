package rip.thecraft.brawl.ability.property;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import rip.thecraft.brawl.ability.property.exception.PropertyParseException;
import rip.thecraft.brawl.ability.property.type.BooleanProperty;
import rip.thecraft.brawl.ability.property.type.DoubleProperty;
import rip.thecraft.brawl.ability.property.type.IntegerProperty;
import rip.thecraft.brawl.ability.property.type.StringProperty;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@RequiredArgsConstructor
public abstract class AbilityProperty<T> {

    // TODO find a better system to handle properties
    private static Map<Class<?>, AbilityProperty<?>> PROPERTIES = new HashMap<>();

    static {
        PROPERTIES.put(BooleanProperty.class, new BooleanProperty());
        PROPERTIES.put(DoubleProperty.class, new DoubleProperty());
        PROPERTIES.put(IntegerProperty.class, new IntegerProperty());
        PROPERTIES.put(StringProperty.class, new StringProperty());
    }

    public static AbilityProperty<?> create(String source) {
        AbilityProperty<?> property = null;
        for (Map.Entry<Class<?>, AbilityProperty<?>> entry : PROPERTIES.entrySet()) {
            try {
                property = entry.getValue().parse(source);
            } catch (PropertyParseException ignored) { }
        }
        return property;
    }

    @NonNull protected T value;

    public abstract T value();

    public abstract void set(T newValue);

    public abstract AbilityProperty<T> parse(String value) throws PropertyParseException;

    public String toString() {
        return String.valueOf(value);
    }

}
