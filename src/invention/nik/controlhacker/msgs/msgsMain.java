package invention.nik.controlhacker.msgs;

import invention.nik.controlhacker.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class msgsMain
{
	public String prefixNormal;
	public String prefixError;
	public String prefixOther;

	public msgsMain(Main plugin)
	{
		FileConfiguration cfg = plugin.getConfig();
		prefixNormal = cfg.getString("prefix.prefixNormal").replaceAll("&", "§");
		prefixError = cfg.getString("prefix.prefixError").replaceAll("&", "§");
		prefixOther = cfg.getString("prefix.prefixOther").replaceAll("&", "§");
	}
}
