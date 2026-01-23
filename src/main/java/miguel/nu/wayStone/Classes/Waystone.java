package miguel.nu.wayStone.Classes;

import miguel.nu.wayStone.CustomModelData;
import miguel.nu.wayStone.Main;
import miguel.nu.wayStone.utils.BedrockUtil;
import miguel.nu.wayStone.utils.NamespaceKey;
import miguel.nu.wayStone.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class Waystone {
    // Java display entities
    private UUID itemDisplay;
    private UUID hitbox;
    private UUID pedestal;

    // Bedrock fallback entities (armor stands)
    private UUID bedrockStatue;
    private UUID bedrockPedestal;
    private UUID bedrockHitbox;

    String name;
    Location statueLocation;
    Material placeholder;

    public void spawn() {
        ItemDisplay itemDisplay = (ItemDisplay) statueLocation.getWorld().spawnEntity(
                statueLocation,
                EntityType.ITEM_DISPLAY
        );

        List<Double> statueOffset = Main.config.getDoubleList("options.statue.offset");
        List<Double> statueSize   = Main.config.getDoubleList("options.statue.size");

        Transformation statueTransformation = new Transformation(
                new Vector3f(
                        statueOffset.get(0).floatValue(),
                        statueOffset.get(1).floatValue(),
                        statueOffset.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(
                        statueSize.get(0).floatValue(),
                        statueSize.get(1).floatValue(),
                        statueSize.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1)
        );

        itemDisplay.setTransformation(statueTransformation);
        itemDisplay.setItemStack(CustomModelData.addCustomModelData(
                "horsewaystone",
                new ItemStack(Material.DIAMOND_BLOCK)
        ));
        this.itemDisplay = itemDisplay.getUniqueId();

        List<Double> interactionOffset = Main.config.getDoubleList("options.hitbox.offset");
        Location interactionLocation = statueLocation.clone().add(
                interactionOffset.get(0),
                interactionOffset.get(1),
                interactionOffset.get(2)
        );

        Interaction interaction = (Interaction) statueLocation.getWorld().spawnEntity(
                interactionLocation,
                EntityType.INTERACTION
        );

        List<Double> interactionSize = Main.config.getDoubleList("options.hitbox.size");
        interaction.setInteractionWidth(interactionSize.get(0).floatValue());
        interaction.setInteractionHeight(interactionSize.get(1).floatValue());

        interaction.getPersistentDataContainer().set(
                NamespaceKey.getNamespacedKey("IS_WAYSTONE"),
                PersistentDataType.BOOLEAN,
                true
        );
        this.hitbox = interaction.getUniqueId();

        ItemDisplay pedestal = (ItemDisplay) statueLocation.getWorld().spawnEntity(
                statueLocation,
                EntityType.ITEM_DISPLAY
        );

        List<Double> pedestalOffset = Main.config.getDoubleList("options.pedestal.offset");
        List<Double> pedestalSize   = Main.config.getDoubleList("options.pedestal.size");

        Transformation pedestalTransformation = new Transformation(
                new Vector3f(
                        pedestalOffset.get(0).floatValue(),
                        pedestalOffset.get(1).floatValue(),
                        pedestalOffset.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(
                        pedestalSize.get(0).floatValue(),
                        pedestalSize.get(1).floatValue(),
                        pedestalSize.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1)
        );

        pedestal.setTransformation(pedestalTransformation);
        pedestal.setItemStack(CustomModelData.addCustomModelData(
                "pedestalwaystone",
                new ItemStack(Material.DIAMOND_BLOCK)
        ));
        this.pedestal = pedestal.getUniqueId();

        ArmorStand bedrockStatue = (ArmorStand) statueLocation.getWorld().spawnEntity(
                statueLocation.clone().add(
                        statueOffset.get(0),
                        statueOffset.get(1) - 1.1,
                        statueOffset.get(2)
                ),
                EntityType.ARMOR_STAND
        );
        bedrockStatue.setInvisible(true);
        bedrockStatue.setMarker(true);
        bedrockStatue.setGravity(false);
        bedrockStatue.setSmall(false);
        bedrockStatue.setArms(false);

        bedrockStatue.getEquipment().setHelmet(new ItemStack(Material.COPPER_BOOTS));

        this.bedrockStatue = bedrockStatue.getUniqueId();

        ArmorStand bedrockPedestal = (ArmorStand) statueLocation.getWorld().spawnEntity(
                statueLocation.clone().add(
                        pedestalOffset.get(0),
                        pedestalOffset.get(1) - 0.5,
                        pedestalOffset.get(2)
                ),
                EntityType.ARMOR_STAND
        );
        bedrockPedestal.setInvisible(true);
        bedrockPedestal.setMarker(true);
        bedrockPedestal.setGravity(false);
        bedrockPedestal.setSmall(false);
        bedrockPedestal.setArms(false);

        bedrockPedestal.getEquipment().setHelmet(new ItemStack(Material.COPPER_HELMET));

        this.bedrockPedestal = bedrockPedestal.getUniqueId();

        ArmorStand bedrockHitbox = (ArmorStand) statueLocation.getWorld().spawnEntity(
                interactionLocation,
                EntityType.ARMOR_STAND
        );
        bedrockHitbox.setInvisible(true);
        bedrockHitbox.setMarker(false);
        bedrockHitbox.setGravity(false);
        bedrockHitbox.setSmall(true);
        bedrockHitbox.setCustomNameVisible(false);
        bedrockHitbox.setCollidable(true);

        bedrockHitbox.getPersistentDataContainer().set(
                NamespaceKey.getNamespacedKey("IS_WAYSTONE"),
                PersistentDataType.BOOLEAN,
                true
        );
        this.bedrockHitbox = bedrockHitbox.getUniqueId();

        Utils.tagWaystoneEntity(itemDisplay, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(interaction, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(pedestal, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockHitbox, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockPedestal, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockStatue, "WAYSTONE_ENTITY");

        updateVisibilityForAllPlayers();
    }


    public void despawn() {
        removeEntity(itemDisplay);
        removeEntity(hitbox);
        removeEntity(pedestal);

        removeEntity(bedrockStatue);
        removeEntity(bedrockPedestal);
        removeEntity(bedrockHitbox);
    }

    private void removeEntity(UUID uuid) {
        if (uuid == null) return;

        Entity e = Bukkit.getEntity(uuid);
        if (e == null) return;

        miguel.nu.wayStone.utils.RegionSchedulers.runOnEntity(e, e::remove);
    }


    public void updateVisibilityForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateVisibilityForPlayer(player);
        }
    }

    public void updateVisibilityForPlayer(Player player) {
        final boolean isBedrock = BedrockUtil.isBedrockPlayer(player.getUniqueId());

        final Entity javaStatue = itemDisplay != null ? Bukkit.getEntity(itemDisplay) : null;
        final Entity javaPedestal = pedestal != null ? Bukkit.getEntity(pedestal) : null;
        final Entity javaHitbox = hitbox != null ? Bukkit.getEntity(hitbox) : null;

        final Entity bedrockStatueEntity = bedrockStatue != null ? Bukkit.getEntity(bedrockStatue) : null;
        final Entity bedrockPedestalEntity = bedrockPedestal != null ? Bukkit.getEntity(bedrockPedestal) : null;
        final Entity bedrockHitboxEntity = bedrockHitbox != null ? Bukkit.getEntity(bedrockHitbox) : null;

        java.util.function.BiConsumer<Entity, Boolean> setVisible = (entity, visible) -> {
            if (entity == null) return;
            entity.getScheduler().run(Main.plugin, task -> {
                if (!player.isOnline() || !entity.isValid()) return;

                if (visible) {
                    player.showEntity(Main.plugin, entity);
                } else {
                    player.hideEntity(Main.plugin, entity);
                }
            }, null);
        };

        if (isBedrock) {
            setVisible.accept(javaStatue, false);
            setVisible.accept(javaPedestal, false);
            // setVisible.accept(javaHitbox, false);

            setVisible.accept(bedrockStatueEntity, true);
            setVisible.accept(bedrockPedestalEntity, true);
            setVisible.accept(bedrockHitboxEntity, true);
        } else {
            setVisible.accept(javaStatue, true);
            setVisible.accept(javaPedestal, true);
            setVisible.accept(javaHitbox, true);

            setVisible.accept(bedrockStatueEntity, false);
            setVisible.accept(bedrockPedestalEntity, false);
            setVisible.accept(bedrockHitboxEntity, false);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getStatueLocation() {
        return statueLocation;
    }

    public void setStatueLocation(Location statueLocation) {
        this.statueLocation = statueLocation;
    }

    public Material getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Material placeholder) {
        this.placeholder = placeholder;
    }

    public UUID getItemDisplay() {
        return itemDisplay;
    }

    public void setItemDisplay(UUID itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    public UUID getHitbox() {
        return hitbox;
    }

    public void setHitbox(UUID hitbox) {
        this.hitbox = hitbox;
    }

    public UUID getPedestal() {
        return pedestal;
    }

    public void setPedestal(UUID pedestal) {
        this.pedestal = pedestal;
    }
}
