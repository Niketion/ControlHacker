package niketion.github.controlhacker.bukkit.title;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ControlHacker_1_8_R1 implements Title {

    @Override
    public void sendTitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE,
                ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")));
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);

    }

    @Override
    public void sendSubtitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
                ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")));
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
    }
}
