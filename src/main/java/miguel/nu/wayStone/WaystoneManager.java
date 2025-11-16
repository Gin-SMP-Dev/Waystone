package miguel.nu.wayStone;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.utils.PlaceholderSetter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WaystoneManager {
    static YamlConfiguration waystoneFile;
    static File file;

    public static void init(){
        if (!Main.plugin.getDataFolder().exists()) {
            Main.plugin.getDataFolder().mkdirs();
        }

        file = new File(Main.plugin.getDataFolder(), "waystones.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        waystoneFile = YamlConfiguration.loadConfiguration(file);
        loadWaystone();
    }

    static List<Waystone> loadedWaystones = new ArrayList<>();
    public static boolean createWaystone(Location location, String name, Material material){
        if(getWaystone(name) != null) return false;

        Waystone waystone = new Waystone();
        waystone.setName(name);
        location.setPitch(0);
        waystone.setStatueLocation(location);
        waystone.setPlaceholder(material);

        loadedWaystones.add(waystone);

        waystone.spawn();

        saveWaystone(waystone);
        return true;
    }
    public static boolean deleteWaystone(String name){
        Waystone waystone = getWaystone(name);
        if(waystone == null) return false;
        waystone.despawn();

        loadedWaystones.remove(waystone);

        deleteSavedWaystone(waystone);
        return true;
    }
    public static Waystone getWaystone(String name){
        for (Waystone waystone : loadedWaystones) {
            if(Objects.equals(waystone.getName(), name)) {
                return waystone;
            }
        }
        return null;
    }
    public static Waystone[] getAllWaystone(){
        return loadedWaystones.toArray(new Waystone[0]);
    }

    public static void saveWaystone(Waystone waystone){
        if(waystoneFile == null || waystone == null || waystone.getName() == null){
            Main.plugin.getLogger().severe(PlaceholderSetter.setPlaceholder(Main.config.getString("message.saving-error"), null, waystone));
            return;
        }
        String base = "waystone." + waystone.getName();
        waystoneFile.set(base + ".name", waystone.getName());
        waystoneFile.set(base + ".material", waystone.getPlaceholder().name());
        waystoneFile.set(base + ".statue.world", waystone.getStatueLocation().getWorld().getName());
        waystoneFile.set(base + ".statue.x", waystone.getStatueLocation().getX());
        waystoneFile.set(base + ".statue.y", waystone.getStatueLocation().getY());
        waystoneFile.set(base + ".statue.z", waystone.getStatueLocation().getZ());
        waystoneFile.set(base + ".statue.yaw", waystone.getStatueLocation().getYaw());
        waystoneFile.set(base + ".statue.pitch", waystone.getStatueLocation().getPitch());
        saveFile();
    }
    public static void deleteSavedWaystone(Waystone waystone){
        if(waystoneFile == null || waystone == null || waystone.getName() == null){
            Main.plugin.getLogger().severe(PlaceholderSetter.setPlaceholder(Main.config.getString(Main.config.getString("message.delete-error")), null, waystone));
            return;
        }
        String base = "waystone." + waystone.getName();
        waystoneFile.set(base, null);

        saveFile();
    }
    public static void loadWaystone(){
        loadedWaystones.clear();

        if(waystoneFile == null){
            Main.plugin.getLogger().severe(PlaceholderSetter.setPlaceholder(Main.config.getString("message.load-error"), null, null));
            return;
        }

        ConfigurationSection root = waystoneFile.getConfigurationSection("waystone");

        if(root == null){
            Main.plugin.getLogger().severe(PlaceholderSetter.setPlaceholder(Main.config.getString("message.load-error"), null, null));
            return;
        }

        Set<String> keys = root.getKeys(false);
        for(String key : keys){
            String base = "waystone." + key;

            Location statueLocation = new Location(
                    Bukkit.getWorld(waystoneFile.getString(base + ".statue.world")),
                    waystoneFile.getDouble(base + ".statue.x"),
                    waystoneFile.getDouble(base + ".statue.y"),
                    waystoneFile.getDouble(base + ".statue.z"),
                    (float) waystoneFile.getDouble(base + ".statue.yaw"),
                    (float) waystoneFile.getDouble(base + ".statue.pitch")
            );

            Waystone waystone = new Waystone();

            waystone.setStatueLocation(statueLocation);
            waystone.setName(waystoneFile.getString(base + ".name"));
            waystone.setPlaceholder(Material.getMaterial(waystoneFile.getString(base + ".material")));

            loadedWaystones.add(waystone);
            waystone.spawn();
        }
    }

    public static void saveFile(){
        if(waystoneFile == null || file == null) return;
        try {
            waystoneFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void teleportToWaystone(Player player, Waystone waystone){
        player.teleport(waystone.getStatueLocation());
        player.sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.waystone_teleported"), player, waystone));
    }
}
