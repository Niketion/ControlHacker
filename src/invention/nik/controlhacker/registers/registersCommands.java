package invention.nik.controlhacker.registers;

import invention.nik.controlhacker.cmds.cmdsControl;
import invention.nik.controlhacker.cmds.cmdsFinish;
import invention.nik.controlhacker.cmds.cmdsSetLocation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

public class registersCommands
{
	public static void load()
	{
		try
		{
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.register("control", new cmdsControl("control"));
			commandMap.register("setlocation", new cmdsSetLocation("setlocation"));
			commandMap.register("finish", new cmdsFinish("finish"));
		}
		catch (Exception e)
		{
			e.getMessage();
			Bukkit.getConsoleSender().sendMessage("§4Inv-ControlHacker: Error - Load all commands.");
		}
	}
}
