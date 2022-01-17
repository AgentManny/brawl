package rip.thecraft.brawl.spawn.event.events;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.kit.ability.property.AbilityProperty;
import rip.thecraft.brawl.spawn.event.EventType;
import rip.thecraft.brawl.spawn.event.type.TimeEvent;
import rip.thecraft.spartan.util.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Schematic extends TimeEvent {

    @AbilityProperty
    public Location location;

    @AbilityProperty
    public String schematic;

    private transient EditSession session;

    public Schematic(String name) {
        super(name, EventType.SCHEMATIC);
    }

    @Override
    public void start() {
        String[] message = {
                " ",
                type.getColor().toString() + ChatColor.BOLD + name.toUpperCase() + " Event",
                ChatColor.GRAY + type.getDescription(),
                " ",
                ChatColor.GRAY + "Location: " + ChatColor.WHITE + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")",
                ChatColor.GRAY + "Duration: " + type.getColor() + TimeUtils.formatLongIntoDetailedString(TimeUnit.MILLISECONDS.toSeconds(duration)),
                ""
        };
        broadcast(false, message);
        pasteSchematic();
    }

    @Override
    public void end() {
        WorldEditPlugin worldEdit = Brawl.getInstance().getWorldEdit();
        EditSession editSession = worldEdit.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);
        session.undo(editSession);

        super.end();
    }

    @Override
    public void finish() {

    }

    @Override
    public boolean isSetup() {
        return location != null && Brawl.getInstance().getWorldEdit() != null && getDirectory() != null;
    }

    private File getDirectory() {
        if (schematic == null) return null;
        File file = new File(Brawl.getInstance().getDataFolder(), "schematics/" + schematic + ".schematic");
        return file.exists() ? file : null;
    }

    private void pasteSchematic() {
        WorldEditPlugin worldEdit = Brawl.getInstance().getWorldEdit();
        EditSession editSession = worldEdit.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);
        File directory = getDirectory();
        if (directory == null) return;

        SchematicFormat schematic = SchematicFormat.getFormat(directory);
        CuboidClipboard clipboard;
        try {
            clipboard = schematic.load(directory);
            clipboard.paste(editSession, BukkitUtil.toVector(location), true);
            editSession.flushQueue();
            this.session = editSession;
            location.getWorld().playSound(location, Sound.ZOMBIE_REMEDY, 1f, 1f);
        } catch (IOException | DataException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}