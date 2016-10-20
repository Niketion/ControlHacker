package invention.nik.controlhacker.msgs;

import invention.nik.controlhacker.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class msgsCommand
{
	Main plugin = Main.getInstance();

	public String prefixNormal;
	public String prefixError;
	public String prefixOther;

	public String noPlayerButConsole;
	public String noPermission;
	public String argsNotFound;
	public String usageControl;
	public String usageSetLocation;
	public String playerNotFound;
	public String hackerEqualsStaffer;
	public String hackerTeleported;
	public String alertHacker;
	public String cmdDisabled;
	public String playerNotControlled;
	public String controlFinish;
	public String quitControl;

	public String commandAfterControl;

	public String blockBreak;

	public String controlOtherStaffer;
	public String controlOtherStafferMessage;

	public msgsCommand()
	{
		FileConfiguration cfg = plugin.getConfig();

		prefixNormal = cfg.getString("prefix.prefixNormal").replaceAll("&", "§");
		prefixError = cfg.getString("prefix.prefixError").replaceAll("&", "§");
		prefixOther = cfg.getString("prefix.prefixOther").replaceAll("&", "§");

		noPlayerButConsole = cfg.getString("command.message.noConsole").replaceAll("&", "§");
		noPermission = cfg.getString("command.message.noPermission").replaceAll("&", "§");
		argsNotFound = cfg.getString("command.message.argsNotFound").replaceAll("&", "§");
		usageControl = cfg.getString("command.message.usageControl").replaceAll("&", "§");
		usageSetLocation = cfg.getString("command.message.usageSetLocation").replaceAll("&", "§");
		playerNotFound = cfg.getString("command.message.playerNotFound").replaceAll("&", "§");
		hackerTeleported = cfg.getString("command.message.hackerTeleported").replaceAll("&", "§");
		hackerEqualsStaffer = cfg.getString("command.message.controlSelf").replaceAll("&", "§");
		alertHacker = cfg.getString("command.message.alertHacker").replaceAll("&", "§");
		cmdDisabled = cfg.getString("command.message.cmdDisabled").replaceAll("&", "§");
		playerNotControlled = cfg.getString("command.message.noControlPlayer").replaceAll("&", "§");
		controlFinish = cfg.getString("command.message.controlFinish").replaceAll("&", "§");

		commandAfterControl = cfg.getString("command.command.afterControl");

		blockBreak = cfg.getString("event.breakBlock").replaceAll("&", "§");

		controlOtherStaffer = cfg.getString("command.boolean.controlOtherStaffer").replaceAll("&", "§");
		controlOtherStafferMessage = cfg.getString("command.boolean.message").replaceAll("&", "§");
	}

	public void msgsTeleportHacker(Player p, Player t)
	{
		p.sendMessage(prefixNormal + hackerTeleported);
		t.sendMessage(alertHacker);

		try
		{
			p.getWorld().playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 200, 1);
			t.getWorld().playSound(t.getLocation(), Sound.valueOf("CAT_MEOW"), 200, 1);
		}
		catch (IllegalArgumentException e1)
		{
			Bukkit.getConsoleSender().sendMessage("§cInvControlHacker: Error - Sound not found... Version of server 1.8?");
		}
	}

	public void msgQuitPlayer(Player p, Player staffer)
	{
		quitControl = plugin.getConfig().getString("command.message.quitControl").replaceAll("&", "§").replaceAll("%p", p.getName());
		staffer.sendMessage(prefixNormal + quitControl);
		Bukkit.getConsoleSender().sendMessage(prefixNormal + quitControl);
	}
}
