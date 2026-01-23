package miguel.nu.wayStone;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.utils.NamespaceKey;
import miguel.nu.wayStone.utils.PlaceholderSetter;
import miguel.nu.wayStone.utils.RegionSchedulers;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.*;

import static miguel.nu.wayStone.utils.Sound.playSound;

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
        location.setYaw(Math.round((double) location.getYaw() / 90) * 90);
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
        waystoneFile.set(base + ".uuid.item-display", waystone.getItemDisplay().toString());
        waystoneFile.set(base + ".uuid.hitbox", waystone.getHitbox().toString());
        waystoneFile.set(base + ".uuid.pedestal", waystone.getPedestal().toString());
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
            Main.plugin.getLogger().severe(
                    PlaceholderSetter.setPlaceholder(Main.config.getString("message.delete-error"), null, waystone)
            );
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
            waystone.setItemDisplay(UUID.fromString(waystoneFile.getString(base + ".uuid.item-display")));
            waystone.setHitbox(UUID.fromString(waystoneFile.getString(base + ".uuid.hitbox")));
            waystone.setPedestal(UUID.fromString(waystoneFile.getString(base + ".uuid.pedestal")));

            loadedWaystones.add(waystone);
        }

        refreshWaystone();
    }

    public static void saveFile(){
        if(waystoneFile == null || file == null) return;
        try {
            waystoneFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void teleportToWaystone(Player player, Waystone waystone) {
        Location base = waystone.getStatueLocation().clone();

        List<Double> offset = Main.config.getDoubleList("options.teleport.offset");
        double forward  = offset.size() > 0 ? offset.get(0) : 0.0;
        double up       = offset.size() > 1 ? offset.get(1) : 0.0;
        double sideways = offset.size() > 2 ? offset.get(2) : 0.0;

        float yaw = base.getYaw();
        double yawRad = Math.toRadians(yaw);
        double dirX = -Math.sin(yawRad);
        double dirZ =  Math.cos(yawRad);
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        base.add(dirX * forward + rightX * sideways, up, dirZ * forward + rightZ * sideways);

        int delay = Main.config.getInt("options.teleport.delay", 0);
        boolean countdown = Main.config.getBoolean("options.teleport.countdown", false);

        if (delay <= 0) {
            RegionSchedulers.runOnEntity(player, () -> {
                if (!player.isOnline()) return;
                player.teleportAsync(base);
                player.sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.teleport_success"), player, waystone));
                playSound(player, Main.config.getString("sound.teleport_success"));
            });
            return;
        }

        RegionSchedulers.runOnEntity(player, () -> {
            if (!player.isOnline()) return;
            player.sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.teleport_start"), player, waystone));
            playSound(player, Main.config.getString("sound.teleport_start"));
        });

        RegionSchedulers.runOnEntity(player, () -> {
            if (!player.isOnline()) return;

            Location startLoc = player.getLocation().clone();
            final int[] secondsLeft = { delay };

            Runnable tick = new Runnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return;

                    Location current = player.getLocation();

                    // Cancel if moved
                    if (current.getBlockX() != startLoc.getBlockX()
                            || current.getBlockY() != startLoc.getBlockY()
                            || current.getBlockZ() != startLoc.getBlockZ()) {

                        player.sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.teleport_cancelled"), player, waystone));
                        playSound(player, Main.config.getString("sound.teleport_cancelled"));
                        return;
                    }

                    // Teleport
                    if (secondsLeft[0] <= 0) {
                        player.teleportAsync(base);
                        player.sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.teleport_success"), player, waystone));
                        playSound(player, Main.config.getString("sound.teleport_success"));
                        return;
                    }

                    // Countdown tick
                    if (countdown) {
                        String msg = Main.config.getString("message.teleport_countdown");
                        if (msg != null) {
                            msg = msg.replace("%countdown_left%", String.valueOf(secondsLeft[0]));
                            player.sendMessage(PlaceholderSetter.setPlaceholder(msg, player, waystone));
                        }
                    }

                    playSound(player, Main.config.getString("sound.teleport_tick"));
                    secondsLeft[0]--;

                    RegionSchedulers.runOnEntityDelayed(player, this, 20L);
                }
            };

            tick.run();
        });
    }


    public static void refreshWaystone() {
        for (Waystone waystone : getAllWaystone()) {
            Location loc = waystone.getStatueLocation();
            if (loc == null || loc.getWorld() == null) continue;

            miguel.nu.wayStone.utils.RegionSchedulers.runOnRegion(loc, () -> {
                waystone.despawn();
                waystone.spawn();
            });
        }
    }

}
