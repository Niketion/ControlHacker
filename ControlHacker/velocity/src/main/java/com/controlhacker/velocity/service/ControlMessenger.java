package com.controlhacker.velocity.service;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class ControlMessenger {
    private final MinecraftChannelIdentifier channel;

    public void sendStart(Player player, UUID staffId, String staffName, UUID targetId) {
        send(player, "START", staffId, staffName, targetId, null);
    }

    public void sendFinish(Player player, UUID staffId, String staffName, UUID targetId, String actionId) {
        send(player, "FINISH", staffId, staffName, targetId, actionId);
    }

    private void send(Player player, String type, UUID staffId, String staffName, UUID targetId, String actionId) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            dataOutput.writeUTF(type);
            dataOutput.writeUTF(staffId.toString());
            dataOutput.writeUTF(staffName);
            dataOutput.writeUTF(targetId.toString());
            if (actionId != null) {
                dataOutput.writeUTF(actionId);
            }
            player.sendPluginMessage(channel, outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
