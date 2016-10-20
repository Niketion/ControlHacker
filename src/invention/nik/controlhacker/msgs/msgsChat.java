package invention.nik.controlhacker.msgs;

import invention.nik.controlhacker.Main;
import org.bukkit.entity.Player;

public class msgsChat
{
	private static Main plugin = Main.getInstance();

	public static void msgChat(Player operator, Player p, String msg)
	{
		if (operator != p)
		{
			String chatFormat = plugin.getConfig().getString("chat.format.hacker").replaceAll("%p", p.getName()).replaceAll("&", "§").replaceAll("%msg", msg);
			operator.sendMessage(chatFormat);
			p.sendMessage(chatFormat);
		}
		else
		{
			String chatFormat = plugin.getConfig().getString("chat.format.hacker").replaceAll("%p", p.getName()).replaceAll("&", "§").replaceAll("%msg", msg);
			operator.sendMessage(chatFormat);
		}
	}
}
