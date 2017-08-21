package niketion.github.controlhacker.bukkit.title;

import org.bukkit.entity.Player;

public interface Title {

    public void sendTitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut);
    public void sendSubtitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut);
}
