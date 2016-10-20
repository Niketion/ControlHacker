package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.msgs.msgsChat;
import invention.nik.controlhacker.others.othersArrayList;
import invention.nik.controlhacker.perms.permsMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class listsChat implements Listener
{
	permsMain perm = new permsMain();

	@EventHandler(priority = EventPriority.HIGHEST) public void onChat(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		if (othersArrayList.hacker.contains(p))
		{
			for (Player operator : Bukkit.getOnlinePlayers())
			{
				if (operator.hasPermission(perm.mod))
				{
					e.setCancelled(true);
					msgsChat.msgChat(operator, p, e.getMessage());
				}
			}
		}
	}
}
