package niketion.github.controlhacker.bukkit.title;

import net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ControlHacker_1_9_R2 implements Title {

    @Override
    public void sendTitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")));
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);

    }

    @Override
    public void sendSubtitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")));
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
    }
}
