package rip.thecraft.brawl.kit;

import com.google.gson.JsonArray;
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
import rip.thecraft.spartan.Spartan;
import rip.thecraft.spartan.util.ItemBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitHandler {

    private final Brawl plugin;

    @Getter
    private List<Kit> kits = new ArrayList<>();

    public KitHandler(Brawl plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load() {
        File file = getFile();
        try (FileReader reader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(reader).getAsJsonArray();

            for (Object object : array) {
                JsonObject jsonObject = (JsonObject) object;
                String name = jsonObject.get("name").getAsString();
                this.registerKit(new Kit(jsonObject));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getDefaultKit();
        this.save();
    }

    public void save() {
        File file = getFile();

        try (FileWriter writer = new FileWriter(file)) {

            Spartan.GSON.toJson(toJson(), writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (Kit kit : this.kits) {
            jsonArray.add(kit.toJson());
        }
        return jsonArray;
    }

    private File getFile() {
        File file = new File(Brawl.getInstance().getDataFolder() + File.separator + "kits.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
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
        Kit kit = this.getKit("PvP");
        if (kit == null) {
            kit = new Kit("PvP");

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
        }
        return this.getKit("PVP");
    }

    public Kit getKit(String name) {
        return this.kits.stream().
                filter(kit -> kit.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
}