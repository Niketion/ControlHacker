package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.others.othersArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class listsTeleport implements Listener
{
	@EventHandler public void onTeleport(PlayerTeleportEvent e)
	{
		if (othersArrayList.hacker.contains(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
}
