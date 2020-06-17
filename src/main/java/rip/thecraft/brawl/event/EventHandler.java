package rip.thecraft.brawl.event;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.event.king.KillTheKing;
import rip.thecraft.brawl.event.koth.KOTH;
import rip.thecraft.brawl.event.koth.command.KOTHCommands;
import rip.thecraft.brawl.event.koth.command.adapter.KOTHCommandAdapter;
import rip.thecraft.spartan.Spartan;
import rip.thecraft.spartan.command.MCommandHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EventHandler {

    private final Map<String, KOTH> KOTHS = new HashMap<>();
    private final Map<String, KillTheKing> KINGS = new HashMap<>();
    private KillTheKing currentKingGame = null;

    @Setter
    private KOTH activeKOTH;

    public EventHandler() {
        this.load();

        MCommandHandler.registerParameterType(KOTH.class, new KOTHCommandAdapter());
        MCommandHandler.registerCommand(new KOTHCommands());
    }

    public void load() {
        try {
            File file = getFile();
            String payload = FileUtils.readFileToString(file);

            if (!payload.isEmpty()) {
                BasicDBObject data = BasicDBObject.parse(payload);

                BasicDBList kings = (BasicDBList) data.get("kings");
                if (kings != null) {
                    for (Object object : kings) {
                        BasicDBObject dbo = (BasicDBObject) object;
                        KillTheKing king = this.createKING(dbo.getString("name"));
                        king.deserialize(dbo);
                    }
                }

                BasicDBList koths = (BasicDBList) data.get("koths");
                if (koths != null) {
                    for (Object object : koths) {
                        BasicDBObject dbo = (BasicDBObject) object;
                        KOTH koth = this.createKOTH(dbo.getString("name"));
                        koth.deserialize(dbo);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            File file = getFile();

            BasicDBList koths = new BasicDBList();
            KOTHS.values().forEach(k -> koths.add(k.serialize()));

            BasicDBList kings = new BasicDBList();
            KINGS.values().forEach(k -> kings.add(k.serialize()));

            FileUtils.write(file, Spartan.GSON.toJson(new JsonParser().parse(new BasicDBObject("koths", koths)
                    .append("kings", kings).toString())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public KOTH createKOTH(String name) {
        KOTH koth = new KOTH(name);
        KOTHS.put(name, koth);
        return koth;
    }

    public KillTheKing createKING(String name) {
        KillTheKing killTheKing = new KillTheKing(name);
        KINGS.put(name, killTheKing);
        return killTheKing;
    }

    public KOTH getKOTHByName(String name) {
        return KOTHS.get(name);
    }

    public KillTheKing getKINGbyName(String name) {
        return KINGS.get(name);
    }

    private File getFile() throws IOException {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "events.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

}
