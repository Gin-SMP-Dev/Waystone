package miguel.nu.wayStone;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.Menu.WaystoneMenu;
import miguel.nu.wayStone.utils.NamespaceKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

public class EventListener implements Listener {
    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e){
        Player player = e.getPlayer();
        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Entity clicked = e.getRightClicked();
        if (!(clicked instanceof Interaction interaction)) {
            return;
        }


        if(!interaction.getPersistentDataContainer().getOrDefault(NamespaceKey.getNamespacedKey("IS_WAYSTONE"), PersistentDataType.BOOLEAN, false)){
            return;
        }

        WaystoneMenu.open(player);
    }

    boolean firstPlayer;
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!firstPlayer){
            firstPlayer = true;
            Main.plugin.reloadConfig();
            Main.config = Main.plugin.getConfig();
            WaystoneManager.refreshWaystone();
            System.out.println("Loaded");
        }
        for(Waystone waystone : WaystoneManager.getAllWaystone()){
            waystone.updateVisibilityForPlayer(event.getPlayer());
        }
    }
}
