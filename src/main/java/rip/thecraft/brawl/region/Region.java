package rip.thecraft.brawl.region;

import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class Region implements Iterable<Location> {

    private String worldName;
    private int x1, y1, z1;
    private int x2, y2, z2;
    private RegionType type;
    private String prefix = "";
    private String name;

    /**
     * Construct a Region given two Location objects which represent any two
     * corners of the Region.
     *
     * @param l1 one of the corners
     * @param l2 the other corner
     */
    public Region(Location l1, Location l2) {
        this(l1.getWorld().getName(),
                l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(),
                l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());

    }

    /**
     * Construct a Region in the given World and xyz coords
     *
     * @param world the Region's world
     * @param x1    X coord of corner 1
     * @param y1    Y coord of corner 1
     * @param z1    Z coord of corner 1
     * @param x2    X coord of corner 2
     * @param y2    Y coord of corner 2
     * @param z2    Z coord of corner 2
     */
    public Region(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(world.getName(), x1, y1, z1, x2, y2, z2);
    }

    /**
     * Construct a Region in the given world name and xyz coords.
     *
     * @param worldName the Region's world name
     * @param x1        X coord of corner 1
     * @param y1        Y coord of corner 1
     * @param z1        Z coord of corner 1
     * @param x2        X coord of corner 2
     * @param y2        Y coord of corner 2
     * @param z2        Z coord of corner 2
     */
    public Region(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Region(JsonObject data) {
        this.worldName = data.get("worldName").getAsString();
        this.x1 = data.get("x1").getAsInt();
        this.x2 = data.get("x2").getAsInt();
        this.y1 = data.get("y1").getAsInt();
        this.y2 = data.get("y2").getAsInt();
        this.z1 = data.get("z1").getAsInt();
        this.z2 = data.get("z2").getAsInt();
        this.name = data.get("name").getAsString();
        this.type = RegionType.valueOf(data.get("type").getAsString());
        if (data.has("prefix")) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', data.get("prefix").getAsString());
        }
    }

    /**
     * Get the Location of the lower northeast corner of the Region (minimum XYZ
     * coords).
     *
     * @return Location of the lower northeast corner
     */
    public Location getLowerCorner() {
        return new Location(getWorld(), x1, y1, z1);
    }

    /**
     * Get the Location of the upper southwest corner of the Region (maximum XYZ
     * coords).
     *
     * @return Location of the upper southwest corner
     */
    public Location getUpperCorner() {
        return new Location(getWorld(), x2, y2, z2);
    }

    /**
     * Get the the center of the Region
     *
     * @return Location at the centre of the Region
     */
    public Location getCenter() {
        return new Location(
                getWorld(),
                x1 + (x2 - x1) / 2,
                y1 + (y2 - y1) / 2,
                z1 + (z2 - z1) / 2);
    }

    /**
     * Get the Region's world.
     *
     * @return the World object representing this Region's world
     * @throws IllegalStateException if the world is not loaded
     */
    public World getWorld() {

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("world '" + worldName + "' is not loaded");
        }
        return world;
    }

    /**
     * Get the size of this Region along the X axis
     *
     * @return Size of Region along the X axis
     */
    public int getSizeX() {
        return (x2 - x1) + 1;
    }

    /**
     * Get the size of this Region along the Y axis
     *
     * @return Size of Region along the Y axis
     */
    public int getSizeY() {
        return (y2 - y1) + 1;
    }

    /**
     * Get the size of this Region along the Z axis
     *
     * @return Size of Region along the Z axis
     */
    public int getSizeZ() {
        return (z2 - z1) + 1;
    }

    /**
     * Get the Blocks at the four corners of the Region, without respect to y-value
     *
     * @return array of Block objects representing the Region corners
     */
    public Location[] getCorners() {
        Location[] res = new Location[4];
        World w = getWorld();
        res[0] = new Location(w, x1, 0, z1); // ++x
        res[1] = new Location(w, x2, 0, z1); // ++z
        res[2] = new Location(w, x2, 0, z2); // --x
        res[3] = new Location(w, x1, 0, z2); // --z
        return res;
    }

    /**
     * Return true if the point at (x,y,z) is contained within this Region.
     *
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     * @return true if the given point is within this Region, false otherwise
     */
    public boolean contains(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    /**
     * Return true if the point at (x,z) is contained within this Region.
     *
     * @param x the X coord
     * @param z the Z coord
     * @return true if the given point is within this Region, false otherwise
     */
    public boolean contains(int x, int z) {
        return x >= x1 && x <= x2 && z >= z1 && z <= z2;
    }

    /**
     * Check if the given Location is contained within this Region.
     *
     * @param l the Location to check for
     * @return true if the Location is within this Region, false otherwise
     */
    public boolean contains(Location l) {
        if (!worldName.equals(l.getWorld().getName())) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    /**
     * Check if the given Block is contained within this Region.
     *
     * @param b the Block to check for
     * @return true if the Block is within this Region, false otherwise
     */
    public boolean contains(Block b) {
        return contains(b.getLocation());
    }

    /**
     * Get the volume of this Region.
     *
     * @return the Region volume, in blocks
     */
    public int volume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    /**
     * Get the Region big enough to hold both this Region and the given one.
     *
     * @param other
     * @return a new Region large enough to hold this Region and the given
     * Region
     */
    public Region getBoundingRegion(Region other) {
        if (other == null) {
            return this;
        }

        int xMin = Math.min(x1, other.x1);
        int yMin = Math.min(y1, other.y1);
        int zMin = Math.min(z1, other.z1);
        int xMax = Math.max(x2, other.x1);
        int yMax = Math.max(y2, other.y1);
        int zMax = Math.max(z2, other.z1);

        return new Region(worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    /**
     * Get a block relative to the lower NE point of the Region.
     *
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     * @return the block at the given position
     */
    public Block getRelativeBlock(int x, int y, int z) {
        return getWorld().getBlockAt(x1 + x, y1 + y, z1 + z);
    }

    /**
     * Get a block relative to the lower NE point of the Region in the given
     * World. This version of getRelativeBlock() should be used if being called
     * many times, to avoid excessive calls to getWorld().
     *
     * @param w the World
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     * @return the block at the given position
     */
    public Block getRelativeBlock(World w, int x, int y, int z) {
        return w.getBlockAt(x1 + x, y1 + y, z1 + z);
    }

    /**
     * Get a list of the chunks which are fully or partially contained in this
     * Region.
     *
     * @return a list of Chunk objects
     */
    public List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<Chunk>();

        World w = getWorld();

        // These operators get the lower bound of the chunk, by complementing 0xf (15) into 16
        // and using an OR gate on the integer coordinate

        int x1 = this.x1 & ~0xf;
        int x2 = this.x2 & ~0xf;
        int z1 = this.z1 & ~0xf;
        int z2 = this.z2 & ~0xf;

        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                chunks.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }

        return chunks;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("worldName", worldName);
        jsonObject.addProperty("x1", x1);
        jsonObject.addProperty("x2", x2);
        jsonObject.addProperty("y1", y1);
        jsonObject.addProperty("y2", y2);
        jsonObject.addProperty("z1", z1);
        jsonObject.addProperty("z2", z2);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("type", type.name());
        if (this.prefix != null && !this.prefix.isEmpty()) {
            jsonObject.addProperty("prefix", this.prefix);
        }
        return jsonObject;
    }

    /**
     * @return read-only location iterator
     */
    public Iterator<Location> iterator() {
        return new LocationRegionIterator(getWorld(), x1, y1, z1, x2, y2, z2);
    }

    @Override
    public String toString() {
        return "Region: " + worldName + "," + x1 + "," + y1 + "," + z1 + "=>" + x2 + "," + y2 + "," + z2;
    }

    public class LocationRegionIterator implements Iterator<Location> {
        private World w;
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;

        public LocationRegionIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            baseX = x1;
            baseY = y1;
            baseZ = z1;
            sizeX = Math.abs(x2 - x1) + 1;
            sizeY = Math.abs(y2 - y1) + 1;
            sizeZ = Math.abs(z2 - z1) + 1;
            x = y = z = 0;
        }

        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        public Location next() {
            Location b = new Location(w, baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return b;
        }

        public void remove() {
        }
    }
}