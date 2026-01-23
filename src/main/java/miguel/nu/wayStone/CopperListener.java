package miguel.nu.wayStone;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CopperListener implements Listener {

    private boolean isCopperArmor(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.COPPER_HELMET || type == Material.COPPER_BOOTS;
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (!isCopperArmor(event.getItem())) {
            return;
        }

        if (event.getTargetEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        Player player = event.getPlayer();
        ItemStack playerItem = event.getPlayerItem();
        ItemStack armorStandItem = event.getArmorStandItem();
        EquipmentSlot slot = event.getSlot();

        if (isCopperArmor(playerItem)) {
            if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if (isCopperArmor(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) return;

        ItemStack helmet = equipment.getHelmet();
        ItemStack boots = equipment.getBoots();

        if (isCopperArmor(helmet)) {
            equipment.setHelmet(null);
        }
        if (isCopperArmor(boots)) {
            equipment.setBoots(null);
        }
    }
}
