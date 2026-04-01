package miguel.nu.wayStone.API;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.WaystoneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaystoneAPI {
    public static boolean isPlayerBanned(UUID uuid, String waystoneName){
        Waystone waystone = WaystoneManager.getWaystone(waystoneName);
        if (waystone == null){
            return false;
        }
        return waystone.isBanned(uuid.toString());
    }
    public static void banPlayer(UUID uuid, String waystoneName){
        Waystone waystone = WaystoneManager.getWaystone(waystoneName);
        if (waystone == null){
            return;
        }
        waystone.addBannedPlayer(uuid.toString());
    }
    public static void unbanPlayer(UUID uuid, String waystoneName){
        Waystone waystone = WaystoneManager.getWaystone(waystoneName);
        if (waystone == null){
            return;
        }
        waystone.removeBannedPlayer(uuid.toString());
    }
    public static List<UUID> getWaystoneBanList(String waystoneName){
        Waystone waystone = WaystoneManager.getWaystone(waystoneName);
        if (waystone == null){
            return null;
        }
        List<UUID> bannedPlayers = new ArrayList<>();
        for (String bannedPlayer : waystone.getBannedPlayers()){
            bannedPlayers.add(UUID.fromString(bannedPlayer));
        }
        return bannedPlayers;
    }
    public static Waystone[] getWaystones(){
        return WaystoneManager.getAllWaystone();
    }
}
