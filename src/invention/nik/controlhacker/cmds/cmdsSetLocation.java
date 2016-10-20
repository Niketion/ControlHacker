package invention.nik.controlhacker.cmds;

import invention.nik.controlhacker.locations.locationsHackerStaffer;
import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.perms.permsMain;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class cmdsSetLocation extends BukkitCommand
{
	public cmdsSetLocation(String name)
	{
		super(name);
		this.description = "A command of InvControlHacker";
		this.usageMessage = "/setlocation <arg>";
	}

	msgsCommand msg = new msgsCommand();
	permsMain perm = new permsMain();

	public boolean execute(CommandSender s, String l, String[] args)
	{
		if (s instanceof Player)
		{
			Player p = ((Player) s).getPlayer();
			if (p.hasPermission(perm.operator))
			{
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("hacker"))
					{
						locationsHackerStaffer.getLocation("HACKER", p, msg);
						return true;
					}
					else if (args[0].equalsIgnoreCase("staffer"))
					{
						locationsHackerStaffer.getLocation("STAFFER", p, msg);
						return true;
					}
					else
					{
						s.sendMessage(msg.prefixNormal + msg.argsNotFound);
						s.sendMessage(msg.prefixOther + msg.usageSetLocation);
						return true;
					}
				}
				else
				{
					s.sendMessage(msg.prefixNormal + msg.argsNotFound);
					s.sendMessage(msg.prefixOther + msg.usageSetLocation);
					return true;
				}
			}
			else
			{
				s.sendMessage(msg.prefixError + msg.noPermission);
				return true;
			}
		}
		else
		{
			s.sendMessage(msg.prefixError + msg.noPlayerButConsole);
			return true;
		}
	}
}
