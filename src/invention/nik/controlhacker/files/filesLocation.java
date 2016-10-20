package invention.nik.controlhacker.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class filesLocation
{

	private static YamlConfiguration myConfig;
	private static File configFile;
	private static boolean loaded = false;

	public static YamlConfiguration getConfig()
	{
		if (!loaded)
		{
			loadConfig();
		}
		return myConfig;
	}

	public static File getConfigFile()
	{
		return configFile;
	}

	public static void saveConfig()
	{
		try
		{
			myConfig.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadConfig()
	{
		configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("Invention-ControlHacker").getDataFolder(), "location.yml");
		if (configFile.exists())
		{
			myConfig = new YamlConfiguration();
			try
			{
				myConfig.load(configFile);
			}
			catch (FileNotFoundException ex)
			{
				// TODO: Log exception
			}
			catch (IOException ex)
			{
				// TODO: Log exception
			}
			catch (InvalidConfigurationException ex)
			{
				// TODO: Log exception
			}
			loaded = true;
		}
		else
		{
			try
			{
				Bukkit.getServer().getPluginManager().getPlugin("Invention-ControlHacker").getDataFolder().mkdir();
				InputStream jarURL = filesLocation.class.getResourceAsStream("/location.yml");
				copyFile(jarURL, configFile);
				myConfig = new YamlConfiguration();
				myConfig.load(configFile);
				loaded = true;
				// TODO: Log that config has been loaded
			}
			catch (Exception e)
			{
				// TODO: Log exception
			}
		}
	}

	static private void copyFile(InputStream in, File out) throws Exception
	{
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try
		{
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, i);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (fis != null)
			{
				fis.close();
			}
			if (fos != null)
			{
				fos.close();
			}
		}
	}

	private filesLocation()
	{
	}
}
