package rip.thecraft.brawl.ability;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

@Getter
@RequiredArgsConstructor
public class CustomAbility {

    /** Returns the name of the sub-ability */
    private final String name;

    /** Returns the parent ability instance */
    @NonNull private Ability parent;

    @Setter private int weight = 0;

    public CustomAbility(String name, Document document) {
        this.name = name;
        try {
            Class<? extends Ability> ability = (Class<Ability>) Class.forName(document.getString("parent"));
            parent = ability.newInstance();
            if (document.containsKey("properties")) {
                parent.deserialize((Document) document.get("properties"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.weight = document.getInteger("weight", 0);
    }

    public Document serialize() {
        Document document = new Document("name", name);
        document.put("parent", parent.getClass().getSimpleName());
        document.put("properties", parent.getProperties());
        document.put("weight", weight);
        return document;
    }

}
