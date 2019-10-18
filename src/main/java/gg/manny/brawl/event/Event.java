package gg.manny.brawl.event;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;

public abstract class Event {

    public abstract void start(Player host);

    public abstract void finish(Player winner);

    public abstract BasicDBObject serialize();
    public abstract void deserialize(BasicDBObject object);

}
