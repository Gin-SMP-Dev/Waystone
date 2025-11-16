package miguel.nu.wayStone.Menu;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.Main;
import miguel.nu.wayStone.Menu.MenuHolder;
import miguel.nu.wayStone.WaystoneManager;
import miguel.nu.wayStone.utils.NamespaceKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GuiListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder() instanceof MenuHolder holder) {
            switch (holder.getId()) {
                case "WAYSTONE_MENU" -> {
                    event.setCancelled(true);
                    PersistentDataContainer data = null;
                    if(event.getCurrentItem() != null){
                        data = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
                    }

                    if (data != null && data.has(NamespaceKey.getNamespacedKey("WAYSTONE_NAME"), PersistentDataType.STRING)) {
                        String waystoneName = data.get(NamespaceKey.getNamespacedKey("WAYSTONE_NAME"), PersistentDataType.STRING);
                        Waystone waystone = WaystoneManager.getWaystone(waystoneName);

                        if(waystone == null){
                            Main.plugin.getLogger().severe(Main.config.getString("message.waystone-not-found"));
                            return;
                        }

                        WaystoneManager.teleportToWaystone((Player) event.getWhoClicked(), waystone);
                        event.getInventory().close();
                    }
                }
            }
        }
    }
}
