package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.others.othersArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class listsDrop implements Listener
{
	@EventHandler public void onDrop(PlayerDropItemEvent e)
	{
		if (othersArrayList.hacker.contains(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
}
