package invention.nik.controlhacker.registers;

import invention.nik.controlhacker.Main;
import invention.nik.controlhacker.lists.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class registersListeners
{
	public static void load(Main plugin)
	{
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new listsCommand(), plugin);
		pm.registerEvents(new listsBlock(), plugin);
		pm.registerEvents(new listsDrop(), plugin);
		pm.registerEvents(new listsChat(), plugin);
		pm.registerEvents(new listsQuit(), plugin);
	}
}
