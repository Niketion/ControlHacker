package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.others.othersArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class listsCommand implements Listener
{
	msgsCommand msg = new msgsCommand();

	@EventHandler(priority = EventPriority.HIGHEST) public void onPreCommand(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();

		if (othersArrayList.hacker.contains(p))
		{
			e.setCancelled(true);
			p.sendMessage(msg.prefixError + msg.cmdDisabled);
			return;
		}
		else
		{
			return;
		}
	}
}
