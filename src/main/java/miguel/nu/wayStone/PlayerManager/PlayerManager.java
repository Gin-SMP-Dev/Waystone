package miguel.nu.wayStone.PlayerManager;

import com.google.gson.*;
import miguel.nu.regula.Classes.Role;
import miguel.nu.regula.Main;
import miguel.nu.regula.commands.NicknameCommand;
import miguel.nu.regula.utils.LuckyPerms;
import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.WaystoneManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PlayerManager {
    static Path file = Paths.get("plugins", "WayStone", "bans.json");

    static JsonObject rootArray;
    private static JsonObject getRootArray() {
        if(rootArray != null) return rootArray;
        Gson gson = new Gson();

        try {
            Files.createDirectories(file.getParent());

            if (Files.exists(file)) {
                try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    JsonObject root = gson.fromJson(r, JsonObject.class);
                    rootArray = (root != null) ? root : new JsonObject();
                    return rootArray;
                }
            } else {
                rootArray = new JsonObject();
                try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    gson.toJson(rootArray, w);
                }
                return rootArray;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }
    public static void saveRoot(JsonObject rootJson) {
        if (rootJson == null) return;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(rootJson, writer);
                rootArray = rootJson;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Waystone[] getPlayerBans(UUID playerUuid) {
        JsonObject root = getRootArray();

        if (root.has(playerUuid.toString()) && root.get(playerUuid.toString()).isJsonArray()) {

            JsonArray arr = root.getAsJsonArray(playerUuid.toString());
            List<Waystone> waystones = new ArrayList<>();
            for (JsonElement e : arr) {
                if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                    waystones.add(WaystoneManager.getWaystone(e.getAsString()));
                }
            }

            return waystones.toArray(new Waystone[0]);
        }

        // return empty array
        return new Waystone[0];
    }


    public static void addWaystoneBan(UUID playerUuid, String waystone){
        JsonObject rootJson = getRootArray();
        JsonArray roles = rootJson.has(playerUuid.toString()) && rootJson.get(playerUuid.toString()).isJsonArray()
                ? rootJson.getAsJsonArray(playerUuid.toString())
                : new JsonArray();

        if (!arrayContains(roles, waystone)) {
            roles.add(roleRaw);

            Role role = Role.getRole(roleRaw);
            if(role != null){
                List<String> oldPerms = getPlayerPermission(playerUuid);
                List<String> newPerms = role.getMinecraftPermissions();
                List<String> changedPerms = new ArrayList<>();

                for(String permRaw : newPerms){
                    if (!oldPerms.contains(permRaw)){
                        changedPerms.add(permRaw);
                    }
                }

                for(String newPerm : changedPerms){
                    Main.luckyPerms.givePermToUuid(UUID.fromString(playerUuid), newPerm);
                }
            }
        }
        rootJson.add(playerUuid, roles);
        saveRoot(rootJson);
        updateTabListPrefix(Bukkit.getOfflinePlayer(UUID.fromString(playerUuid)));

        new LuckyPerms().syncPermOfUuid(UUID.fromString(playerUuid));
    }

    public static void removePlayerRole(String playerUuid, String roleRaw) {
        if (playerUuid == null || playerUuid.isEmpty() || roleRaw == null || roleRaw.isEmpty())
            return;

        Role role = Role.getRole(roleRaw);
        if(role != null){
            List<String> oldPerms = getPlayerPermission(playerUuid);
            List<String> newPerms = role.getMinecraftPermissions();
            List<String> changedPerms = new ArrayList<>();

            for(String permRaw : newPerms){
                if (oldPerms.contains(permRaw)){
                    changedPerms.add(permRaw);
                }
            }

            for(String newPerm : changedPerms){
                Main.luckyPerms.removePermFromUuid(UUID.fromString(playerUuid), newPerm);
            }
        }

        JsonObject rootJson = getRootArray();
        JsonArray roles = rootJson.has(playerUuid) && rootJson.get(playerUuid).isJsonArray()
                ? rootJson.getAsJsonArray(playerUuid)
                : new JsonArray();

        JsonArray updatedRoles = new JsonArray();

        for (JsonElement e : roles) {
            if (e.isJsonPrimitive()) {
                String existingRole = e.getAsString();
                if (!existingRole.equalsIgnoreCase(roleRaw)) {
                    updatedRoles.add(existingRole);
                }
            }
        }

        if (updatedRoles.isEmpty()) {
            rootJson.remove(playerUuid);
        } else {
            rootJson.add(playerUuid, updatedRoles);
        }
        saveRoot(rootJson);
        updateTabListPrefix(Bukkit.getOfflinePlayer(UUID.fromString(playerUuid)));
        new LuckyPerms().syncPermOfUuid(UUID.fromString(playerUuid));
    }

    public static boolean hasPlayerPermission(String playerUuid, String permission){
        String[] playerRoles = getPlayerRoles(playerUuid);
        for(String role : playerRoles){
            if(Role.hasPermission(permission, Role.getRole(role)) || Role.hasPermission("ADMIN", Role.getRole(role))){
                return true;
            }
        }
        return false;
    }

    private static boolean arrayContains(JsonArray arr, String value) {
        for (JsonElement e : arr) {
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString() && value.equals(e.getAsString())) {
                return true;
            }
        }
        return false;
    }
}
