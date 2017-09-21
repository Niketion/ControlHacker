package niketion.github.controlhacker.bukkit.commands;

import niketion.github.controlhacker.bukkit.Main;
import niketion.github.controlhacker.bukkit.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandControl implements CommandExecutor {

    /**
     * Get instance of main
     */
    private Main main = Main.getInstance();

    /**
     * The various command functions
     */
    private CommandFuctions commandFuctions = new CommandFuctions();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandFuctions.isPlayer(commandSender)))
            return false;


        if (!(commandFuctions.hasPermission(commandSender.getName(), Permissions.CONTROL)))
            return false;

        if (!(strings.length > 0)) {
            commandSender.sendMessage(main.format(commandFuctions.getString("usage-control")));
            return false;
        }

        // Check if commandSender and target is equal
        if (!commandFuctions.checkYourself(commandSender, strings[0]))
            return false;

        // Check if target is online
        if (!commandFuctions.foundPlayer(commandSender, strings[0]))
            return false;

        // Check if zones are set
        if (!commandFuctions.isSet("cheater") || !commandFuctions.isSet("checker"))
            return false;

        Player target = Bukkit.getPlayerExact(strings[0]);

        // Teleport cheater and checker to zone
        target.teleport(commandFuctions.getZone("cheater"));
        ((Player) commandSender).teleport(commandFuctions.getZone("checker"));

        // Check if version is 1.8+
        if (main.rightVersion()) {
            // Title
            main.getTitle().sendTitle(target, commandFuctions.getString("title.title-message"), 2, 99999, 2);
            main.getTitle().sendSubtitle(target, commandFuctions.getString("title.subtitle-message").replaceAll("%player%", commandSender.getName()), 2, 99999, 2);
        }

        // Clear previous chat
        for (int i=0; i<150; i++)
            target.sendMessage("");

        for (String message : main.getConfig().getStringList("cheater-message"))
            target.sendMessage(main.format(message.replaceAll("%player%", commandSender.getName())));
        commandSender.sendMessage(main.format(commandFuctions.getString("checker-message").replaceAll("%player%", strings[0])));

        // The API of sound from 1.9 has changed, so check what sound to use
        if (Bukkit.getBukkitVersion().contains("1.7") || Bukkit.getBukkitVersion().contains("1.8")) {
            target.getWorld().playSound(target.getLocation(), Sound.valueOf("LEVEL_UP"), 10, 3);
            target.getWorld().playSound(target.getLocation(), Sound.valueOf("IRONGOLEM_HIT"), 10, 2);
        } else {
            target.getWorld().playSound(target.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 10, 3);
            target.getWorld().playSound(target.getLocation(), Sound.valueOf("ENTITY_IRONGOLEM_HURT"), 10, 2);
        }

        // Particle
        for(int i = 0; i <360; i+=5){
            target.getLocation().setY(target.getLocation().getY() + Math.cos(i)*5);
            target.getLocation().getWorld().playEffect(target.getLocation(), Effect.FLAME, 51);
        }

        // Put checker and cheater to "inCheck" hashMap
        main.getInCheck().put(target.getName(), commandSender.getName());

        return true;
    }

}
