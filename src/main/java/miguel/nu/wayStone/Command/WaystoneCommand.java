package miguel.nu.wayStone.Command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.Main;
import miguel.nu.wayStone.Menu.WaystoneMenu;
import miguel.nu.wayStone.WaystoneManager;
import miguel.nu.wayStone.utils.PlaceholderSetter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class WaystoneCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length < 1) {
            return;
        }

        if(!(source.getSender() instanceof Player)){
            source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.not-player"),null, null));
            return;
        }

        if(!source.getSender().isOp()){
            source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.missing-perm"), (Player) source.getSender(), null));
            return;
        }

        if(args[0].equals("reload")){
            Main.plugin.reloadConfig();
            Main.config = Main.plugin.getConfig();
            WaystoneManager.refreshWaystone();
            source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.config-reload"), (Player) source.getSender(), null));
            return;
        }
        else if (args[0].equals("spawn")) {
            if(args.length < 3) {
                source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.few-arg"), (Player) source.getSender(), null));
                return;
            }
            if(args.length == 3){
                String materialName = args[2].replace("minecraft:", "").toUpperCase();
                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.mat-not-found"), (Player) source.getSender(), null));
                    return;
                }

                if(WaystoneManager.createWaystone(source.getLocation(), args[1], material)) {
                    source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.waystone-created"), (Player) source.getSender(), null));
                } else {
                    source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.waystone-name-conflict"), (Player) source.getSender(), null));
                }
                return;
            } else{
                source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.many-arg"), (Player) source.getSender(), null));
                return;
            }

        }
        else if (args[0].equals("delete")) {
            if(args.length == 2){
                if(WaystoneManager.deleteWaystone(args[1])){
                    source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.waystone-deleted"), (Player) source.getSender(), null));
                } else {
                    source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.waystone-not-found"), (Player) source.getSender(), null));
                }
                return;
            } else{
                source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.many-arg"), (Player) source.getSender(), null));
                return;
            }
        }

        //temp
        else if (args[0].equals("teleport")){
            WaystoneMenu.open((Player) source.getSender());
            return;
        }
        source.getSender().sendMessage(PlaceholderSetter.setPlaceholder(Main.config.getString("message.unknown-command"), (Player) source.getSender(), null));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (!source.getSender().isOp()) {
            return List.of();
        }

        if (args.length == 0) {
            return List.of("reload", "spawn", "delete", "teleport");
        }

        if (args.length == 1) {
            List<String> suggestions = List.of("reload", "spawn", "delete", "teleport");
            String input = args[0].toLowerCase();

            return suggestions.stream()
                    .filter(s -> s.startsWith(input))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            List<Waystone> waystones = List.of(WaystoneManager.getAllWaystone());
            List<String> suggestions = new ArrayList<>();

            for(Waystone waystone : waystones){
                suggestions.add(waystone.getName());
            }

            String input = args[1].toLowerCase();

            return suggestions.stream()
                    .filter(s -> s.startsWith(input))
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("spawn")) {
            String input = args[2].toLowerCase();

            return Arrays.stream(Material.values())
                    .filter(Material::isItem) // only real items
                    .map(m -> "minecraft:" + m.getKey().getKey())
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        }


        return List.of();
    }
}
