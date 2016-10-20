package invention.nik.controlhacker;

import invention.nik.controlhacker.files.filesLocation;
import invention.nik.controlhacker.msgs.msgsMain;
import invention.nik.controlhacker.others.othersArrayList;
import invention.nik.controlhacker.registers.registersCommands;
import invention.nik.controlhacker.registers.registersListeners;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	private static Main instance;

	public static Main getInstance()
	{
		return instance;
	}

	private msgsMain strings = new msgsMain(this);

	public void onEnable()
	{
		instance = this;
		enable(Bukkit.getConsoleSender());
	}

	public void onDisable()
	{
		disable(Bukkit.getConsoleSender());
	}

	private void enable(ConsoleCommandSender con)
	{
		saveDefaultConfig();
		filesLocation.loadConfig();
		load();
		con.sendMessage(strings.prefixNormal + getDescription().getName() + " enabled.");
		con.sendMessage(strings.prefixOther + "Author - Version: " + getDescription().getAuthors() + " - " + getDescription().getVersion());
	}

	private void disable(ConsoleCommandSender con)
	{
		con.sendMessage(strings.prefixError + getDescription().getName() + " disabled.");
		con.sendMessage(strings.prefixOther + "Author - Version: " + getDescription().getAuthors() + " - " + getDescription().getVersion());

		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (othersArrayList.hacker.contains(p))
			{
				Bukkit.dispatchCommand(con, "finish " + p.getName());
				othersArrayList.hacker.remove(p);
				con.sendMessage("§cInvControlHacker: WARN - A staffer was checking a hacker... The hacker is liberated.");
			}
		}
	}

	private void load()
	{
		registersCommands.load();
		registersListeners.load(this);
	}
}
