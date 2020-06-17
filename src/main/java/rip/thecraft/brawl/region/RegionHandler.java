package rip.thecraft.brawl.region;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import rip.thecraft.brawl.Brawl;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class RegionHandler implements Closeable {

    private final Brawl plugin;

    @Getter
    List<Region> regions = new ArrayList<>();

    @Getter
    private MongoCollection mongoCollection;

    public RegionHandler(Brawl plugin) {
        this.plugin = plugin;

        this.mongoCollection = plugin.getMongoDatabase().getCollection("regions");
        this.mongoCollection.find().iterator().forEachRemaining(object -> {
            Document document = (Document) object;
            Region region = new Region(document);
            this.regions.add(region);
        });
    }

    public void add(Region region) {
        this.regions.add(region);
    }

    public void remove(Region region) {
        if(regions.removeIf(region::equals)) {
            this.mongoCollection.deleteOne(Filters.eq("name", region.getName()));
        }
    }

    public void remove(String name) {
        if(regions.removeIf(rg -> rg.getName().equalsIgnoreCase(name))) {
            this.mongoCollection.deleteOne(Filters.eq("name", name));
        }
    }

    public Region get(Location location) {
        for (Region reg : regions) {
            if (reg.contains(location)) {
                return reg;
            }
        }
        return null;
    }

    public Region get(String name) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }
        return null;
    }

    @Override
    public void close() {
        this.regions.forEach(Region::save);
    }
}