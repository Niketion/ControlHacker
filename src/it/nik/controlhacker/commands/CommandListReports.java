package it.nik.controlhacker.commands;

import it.nik.controlhacker.Main;
import it.nik.controlhacker.files.FileReport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListReports implements CommandExecutor {
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("controlhacker.mod")) {
            if (strings.length > 0) {
                if (strings[0].equalsIgnoreCase("closeall")) {
                    if (commandSender.hasPermission("controlhacker.admin")) {
                        FileReport.getInstance().getConfig().set("Reports", null);
                        FileReport.getInstance().getConfig().set("MaxID", "0");
                        FileReport.getInstance().saveConfig();
                        commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Report.All-Report-Closed")));
                    } else {
                        commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.Permission-Denied")));
                    }
                } else {
                    commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Report.Usage-Args-List")));
                }
            } else {
                commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Report.Reports-List")));
                allMessage(commandSender);
            }
        } else {
            commandSender.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.Permission-Denied")));
        }
        return true;
    }

    private void allMessage(CommandSender commandSender) {
        int maxID = Integer.valueOf(FileReport.getInstance().getConfig().getString("MaxID"));
        if (maxID > 7) {
            commandSender.sendMessage(messageFormat(8));
            commandSender.sendMessage(messageFormat(7));
            commandSender.sendMessage(messageFormat(6));
            commandSender.sendMessage(messageFormat(5));
            commandSender.sendMessage(messageFormat(4));
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 7) {
            commandSender.sendMessage(messageFormat(7));
            commandSender.sendMessage(messageFormat(6));
            commandSender.sendMessage(messageFormat(5));
            commandSender.sendMessage(messageFormat(4));
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 6) {
            commandSender.sendMessage(messageFormat(6));
            commandSender.sendMessage(messageFormat(5));
            commandSender.sendMessage(messageFormat(4));
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 5) {
            commandSender.sendMessage(messageFormat(5));
            commandSender.sendMessage(messageFormat(4));
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 4) {
            commandSender.sendMessage(messageFormat(4));
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 3) {
            commandSender.sendMessage(messageFormat(3));
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 2) {
            commandSender.sendMessage(messageFormat(2));
            commandSender.sendMessage(messageFormat(1));
        } else if (maxID == 1) {
            commandSender.sendMessage(messageFormat(1));
        } else {
            commandSender.sendMessage(main.formatChat(main.getConfig().getString("Report.No-Reports")));
        }
    }

    private String messageFormat(int number) {
        int ID = (Integer.valueOf(FileReport.getInstance().getConfig().getString("MaxID")) - number) + 1;
        String reporter = FileReport.getInstance().getConfig().getString("Reports." + ID + ".Reporter");
        String reported = FileReport.getInstance().getConfig().getString("Reports." + ID + ".Reported");
        String message = FileReport.getInstance().getConfig().getString("Reports." + ID + ".Reason");
        return main.formatChat(main.getConfig().getString("Report.Report-List-Format"))
                    .replaceAll("%id%", String.valueOf(ID))
                    .replaceAll("%reported%", reported)
                    .replaceAll("%reporter%", reporter)
                    .replaceAll("%reason%", message);
    }
}
