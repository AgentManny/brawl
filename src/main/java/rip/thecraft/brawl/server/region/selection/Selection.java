package rip.thecraft.brawl.server.region.selection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.server.region.Region;
import gg.manny.streamline.util.cuboid.Cuboid;

/**
 * Represents a region, either completely selection, or not, that has not been created and saved yet.
 */
@RequiredArgsConstructor
public class Selection {

    public static final int         MAX_SELECTION_AREA      = 10_000;                // Max area for a cross-section of the selection (100x100)
    public static final String      SELECTION_METADATA_KEY  = "_selection"; // Key to store the Selection object under in a player's metadata
    public static final ItemStack   SELECTION_WAND;

    // The two corners of the selection
    @NonNull @Getter private Location point1;
    @NonNull @Getter private Location point2;

    static {

        SELECTION_WAND = new ItemStack(Material.DIAMOND_AXE);

        ItemMeta meta = SELECTION_WAND.getItemMeta();
        meta.setDisplayName("§bSelection Wand");

        SELECTION_WAND.setItemMeta(meta);
    }

    /**
     * Private, so that we can create a new instance in the Selection#createOrGetSelection method.
     */
    private Selection() {}

    /**
     * @param point1 the new point1
     * @return a result based on the circumstances
     */
    public void setPoint1(Location point1) {
        this.point1 = point1;
    }

    /**
     * @param point2 the new point1
     * @return a result based on the circumstances
     */
    public void setPoint2(Location point2) {
        this.point2 = point2;
    }

    /**
     * @return if the Selection can form a full cuboid object
     */
    public boolean isFullObject() {
        return point1 != null && point2 != null;
    }

    /**
     * Resets both locations in the Selection
     */
    public void clear() {
        point1 = null;
        point2 = null;
    }

    /**
     * @return null if both corners are not set, else a Cuboid object representing this selection
     */
    public Region getCuboid() {
        if (!isFullObject()) {
            return null;
        }

        return new Region(point1, point2);
    }

    public Cuboid createCuboid() {
        if (!isFullObject()) {
            return null;
        }

        return new Cuboid(point1, point2);
    }

    /**
     * Selections are stored in the player's metadata. This method removes the need
     * to use Bukkit Metadata API calls all over the place.
     * <p>
     * This method can be modified structurally as needed, the plugin only access Selection objects
     * via this method.
     *
     * @param player the player for whom to grab the Selection object for
     * @return selection object, either new or created
     */
    public static Selection createOrGetSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }
        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(Brawl.getInstance(), selection));

        return selection;
    }
}