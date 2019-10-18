package gg.manny.brawl.event;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import gg.manny.brawl.Brawl;
import gg.manny.brawl.event.king.KillTheKing;
import gg.manny.brawl.event.koth.KOTH;
import gg.manny.brawl.event.koth.command.KOTHCommands;
import gg.manny.brawl.event.koth.command.adapter.KOTHCommandAdapter;
import gg.manny.pivot.Pivot;
import gg.manny.quantum.Quantum;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class EventHandler {

    private final Map<String, KOTH> KOTHS = new HashMap<>();
    private final Map<String, KillTheKing> KINGS = new HashMap<>();

    @Setter
    private KOTH activeKOTH;

    public EventHandler() {
        this.load();

        Quantum quantum = Pivot.getInstance().getQuantum();
        quantum.registerParameterType(KOTH.class, new KOTHCommandAdapter());
        quantum.registerCommand(new KOTHCommands());

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

            FileUtils.write(file, Brawl.GSON.toJson(new JsonParser().parse(new BasicDBObject("koths", koths)
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
