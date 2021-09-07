package rip.thecraft.brawl.kit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.thecraft.brawl.Brawl;
import rip.thecraft.brawl.duelarena.match.Match;
import rip.thecraft.brawl.item.item.Armor;
import rip.thecraft.brawl.item.item.Items;
import rip.thecraft.brawl.kit.command.BukkitCommand;
import rip.thecraft.brawl.player.PlayerData;
import rip.thecraft.brawl.region.RegionType;
import rip.thecraft.brawl.util.BrawlUtil;
import rip.thecraft.spartan.util.ItemBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitHandler {

    protected static final File KIT_DIRECTORY;

    private final Brawl plugin;

    @Getter
    private List<Kit> kits = new ArrayList<>();

    public KitHandler(Brawl plugin) {
        this.plugin = plugin;
        this.load();
    }

    static {
        KIT_DIRECTORY = new File(Brawl.getInstance().getDataFolder(), "kits");
        KIT_DIRECTORY.mkdirs();
    }

    private void load() {
        if (KIT_DIRECTORY.exists()) {
            plugin.getLogger().info("[Kit Manager] Loading kits...");
            File[] kits = KIT_DIRECTORY.listFiles((dir, name) -> name.endsWith(".json"));
            if (kits == null) {
                plugin.getLogger().info("[Kit Manager] No kits found. (registering default kit)");
                createDefaultKit();
                return;
            }
            for (File kitFile : kits) {
                try (FileReader reader = new FileReader(kitFile)) {
                    JsonElement parse = new JsonParser().parse(reader);
                    if (parse.isJsonNull()) {
                        plugin.getLogger().severe("[Kit Manager] Failed to load " + kitFile.getName() + ": corrupted (null)");
                        continue;
                    }

                    if (!parse.isJsonObject()) {
                        plugin.getLogger().severe("[Kit Manager] Failed to load " + kitFile.getName() + ": corrupted (not a object)");
                        continue;
                    }

                    JsonObject jsonObject = parse.getAsJsonObject();
                    this.registerKit(new Kit(jsonObject));
                    //plugin.getLogger().info("[Kit Manager] Registered kit " + jsonObject.get("name").getAsString() + " (" + kitFile.getName() + ").");
                } catch (Exception e) {
                    plugin.getLogger().severe("[Kit Manager] Failed to load " + kitFile.getName() + ":");
                    e.printStackTrace();
                }
            }

            plugin.getLogger().info("[Kit Manager] Loaded " + this.kits.size() + " kits.");
        } else {
            plugin.getLogger().info("[Kit Manager] Created /kits/ directory");
        }

        getDefaultKit();
    }

    public void save() {
        for (Kit kit : kits) {
            kit.save();
        }
        plugin.getLogger().info("[Kit Manager] Saved " + this.kits.size() + " kits.");
    }

    public void registerKit(Kit kit) {
        this.kits.add(kit);
        this.kits.sort(Kit::compareTo);
        new BukkitCommand(kit.getName());
    }

    public void unregisterKit(Kit kit) {
        this.kits.remove(kit);
        //todo unregister command
    }

    public static Kit getEquipped(Player player) {
        PlayerData playerData = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
        Match match = Brawl.getInstance().getMatchHandler().getMatch(player);
        Kit selectedKit = match != null && match.getKit() != null ? match.getKit() : playerData.getSelectedKit();
        return !RegionType.SAFEZONE.appliesTo(player.getLocation()) ? selectedKit : null;
    }


    public Kit getDefaultKit() {
        if (this.getKit("PvP") == null) {
            createDefaultKit();
        }
        return this.getKit("PVP");
    }

    private void createDefaultKit() {
        Kit kit = new Kit("PvP");
        kit.setIcon(BrawlUtil.create(Material.DIAMOND_SWORD));
        kit.setDescription("Basic PvP class.");
        kit.setPotionEffects(Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0)));

        Armor armor = kit.getArmor();
        armor.setHelmet(BrawlUtil.create(Material.IRON_HELMET));
        armor.setChestplate(BrawlUtil.create(Material.IRON_CHESTPLATE));
        armor.setLeggings(BrawlUtil.create(Material.IRON_LEGGINGS));
        armor.setBoots(BrawlUtil.create(Material.IRON_BOOTS));

        kit.setWeight(-1);

        kit.setItems(new Items(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.DURABILITY, 3).create()));
        this.registerKit(kit);
        kit.save();
    }

    public Kit getKit(String name) {
        return this.kits.stream().
                filter(kit -> kit.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
}