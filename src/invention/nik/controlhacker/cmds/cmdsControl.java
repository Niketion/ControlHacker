package invention.nik.controlhacker.cmds;

import invention.nik.controlhacker.locations.locationsHackerStaffer;
import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.others.othersArrayList;
import invention.nik.controlhacker.perms.permsMain;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class cmdsControl extends BukkitCommand
{
	public cmdsControl(String name)
	{
		super(name);
		this.description = "A command of InvControlHacker";
		this.usageMessage = "/control <player>";
	}

	msgsCommand msg = new msgsCommand();
	permsMain perm = new permsMain();

	public boolean execute(CommandSender s, String l, String[] args)
	{
		if (s instanceof Player)
		{
			Player p = ((Player) s).getPlayer();
			if (p.hasPermission(perm.mod))
			{
				if (args.length > 0)
				{
					Player t = Bukkit.getPlayerExact(args[0]);
					if (t != null)
					{
						if (p != t)
						{
							if (msg.controlOtherStaffer.equalsIgnoreCase("false"))
							{
								if (!t.hasPermission(perm.mod))
								{
									locationsHackerStaffer.teleportToLocation(p, t, msg);
									msg.msgsTeleportHacker(p, t);

									othersArrayList.hacker.add(t);
									t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20000000, 1));

									return true;
								}
								else
								{
									p.sendMessage(msg.prefixError + msg.controlOtherStafferMessage);
									return true;
								}
							}
							else
							{
								locationsHackerStaffer.teleportToLocation(p, t, msg);
								msg.msgsTeleportHacker(p, t);

								othersArrayList.hacker.add(t);
								t.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20000000, 1));

								return true;
							}
						}
						else
						{
							p.sendMessage(msg.prefixError + msg.hackerEqualsStaffer);
							return true;
						}
					}
					else
					{
						p.sendMessage(msg.prefixNormal + msg.playerNotFound);
						return true;
					}
				}
				else
				{
					s.sendMessage(msg.prefixNormal + msg.argsNotFound);
					s.sendMessage(msg.prefixOther + msg.usageControl);
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
