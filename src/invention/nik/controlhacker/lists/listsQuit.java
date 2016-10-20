package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.others.othersArrayList;
import invention.nik.controlhacker.perms.permsMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class listsQuit implements Listener
{
	permsMain perm = new permsMain();
	msgsCommand msg = new msgsCommand();

	@EventHandler public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		if (othersArrayList.hacker.contains(p))
		{
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "finish " + p.getName());
			othersArrayList.hacker.remove(p);
			for (Player staffer : Bukkit.getOnlinePlayers())
			{
				if (staffer.hasPermission(perm.mod))
				{
					msg.msgQuitPlayer(p, staffer);
				}
			}
		}
	}
}
