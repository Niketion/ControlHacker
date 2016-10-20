package invention.nik.controlhacker.cmds;

import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.others.othersArrayList;
import invention.nik.controlhacker.perms.permsMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class cmdsFinish extends BukkitCommand
{
	public cmdsFinish(String name)
	{
		super(name);
		this.description = "A command of InvControlHacker";
		this.usageMessage = "/finish <player>";
	}

	permsMain perm = new permsMain();
	msgsCommand msg = new msgsCommand();

	public boolean execute(CommandSender s, String l, String[] args)
	{
		if (s.hasPermission(perm.mod))
		{
			if (args.length > 0)
			{
				Player t = Bukkit.getPlayerExact(args[0]);
				if (othersArrayList.hacker.contains(t))
				{
					othersArrayList.hacker.remove(t);
					t.performCommand(msg.commandAfterControl.replaceAll("/", ""));
					for (int x = 0; x < 150; x++)
					{
						t.sendMessage("");
					}
					t.removePotionEffect(PotionEffectType.BLINDNESS);
					t.sendMessage(msg.prefixNormal + msg.controlFinish);
					s.sendMessage(msg.prefixNormal + msg.controlFinish);
					return true;
				}
				else
				{
					s.sendMessage(msg.prefixError + msg.playerNotControlled);
					return true;
				}
			}
			else
			{
				s.sendMessage(msg.prefixNormal + msg.playerNotFound);
				return true;
			}
		}
		else
		{
			s.sendMessage(msg.prefixError + msg.noPermission);
			return true;
		}
	}
}
