package invention.nik.controlhacker.locations;

import invention.nik.controlhacker.files.filesLocation;
import invention.nik.controlhacker.msgs.msgsCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class locationsHackerStaffer
{
	public static void getLocation(String name, Player p, msgsCommand msg)
	{
		p.sendMessage(msg.prefixOther + "One second...");

		Location l = p.getLocation();
		String world = p.getWorld().getName();
		double x = l.getX();
		double y = l.getY();
		double z = l.getZ();
		double yaw = l.getYaw();
		double pitch = l.getPitch();

		FileConfiguration cfg = filesLocation.getConfig();

		cfg.set(name + ".X", x);
		cfg.set(name + ".Y", y);
		cfg.set(name + ".Z", z);
		cfg.set(name + ".Yaw", (long) yaw);
		cfg.set(name + ".Pitch", (long) pitch);
		cfg.set(name + ".World", world);

		p.sendMessage(msg.prefixNormal + "Location of " + name + " set!");

		filesLocation.saveConfig();
	}

	public static void teleportToLocation(Player p, Player t, msgsCommand msg)
	{
		FileConfiguration cfg = filesLocation.getConfig();
		if (cfg.getString("HACKER.X") != null)
		{
			if (cfg.getString("STAFFER.X") != null)
			{
				int XStaffer = cfg.getInt("STAFFER.X"), YStaffer = cfg.getInt("STAFFER.Y"), ZStaffer = cfg.getInt("STAFFER.Z"), YawStaffer = cfg.getInt("STAFFER.Yaw"), PitchStaffer = cfg.getInt("STAFFER.Pitch");
				String worldStaffer = cfg.getString("STAFFER.World");
				p.teleport(new Location(Bukkit.getWorld(worldStaffer), XStaffer, YStaffer, ZStaffer, YawStaffer, PitchStaffer));

				int XHacker = cfg.getInt("HACKER.X"), YHacker = cfg.getInt("HACKER.Y"), ZHacker = cfg.getInt("HACKER.Z"), YawHacker = cfg.getInt("HACKER.Yaw"), PitchHacker = cfg.getInt("HACKER.Pitch");
				String worldHacker = cfg.getString("HACKER.World");
				t.teleport(new Location(Bukkit.getWorld(worldHacker), XHacker, YHacker, ZHacker, YawHacker, PitchHacker));
			}
			else
			{
				p.sendMessage(msg.prefixError + "Location of staffer not set.");
			}
		}
		else
		{
			p.sendMessage(msg.prefixError + "Location of hacker not set.");
		}
	}
}
