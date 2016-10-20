package invention.nik.controlhacker.lists;

import invention.nik.controlhacker.msgs.msgsCommand;
import invention.nik.controlhacker.others.othersArrayList;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class listsBlock implements Listener
{
	msgsCommand msg = new msgsCommand();

	@EventHandler(priority = EventPriority.HIGHEST) public void onBreak(BlockBreakEvent e)
	{
		if (othersArrayList.hacker.contains(e.getPlayer()))
		{
			e.getPlayer().sendMessage(msg.prefixError + msg.blockBreak);
			e.setCancelled(true);
			e.getPlayer().playEffect(e.getBlock().getLocation().add(0, 1, 0), Effect.LAVA_POP, 2003);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST) public void onPlace(BlockPlaceEvent e)
	{
		if (othersArrayList.hacker.contains(e.getPlayer()))
		{
			e.getPlayer().sendMessage(msg.prefixError + msg.blockBreak);
			e.setCancelled(true);
			e.getPlayer().playEffect(e.getBlock().getLocation().add(0, 1, 0), Effect.LAVA_POP, 2003);
		}
	}

}
