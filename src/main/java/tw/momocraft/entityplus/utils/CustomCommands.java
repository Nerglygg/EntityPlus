package tw.momocraft.entityplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tw.momocraft.entityplus.handlers.ConfigHandler;
import tw.momocraft.entityplus.handlers.ServerHandler;


public class CustomCommands {
    public static void executeMultipleCmds(CommandSender sender, String input, boolean placeholder) {
        if (placeholder) {
            input = Utils.translateLayout(input, (Player) sender);
        }
        if (input.contains(";")) {
            String[] cmds = input.split(";");
            for (String cmd : cmds) {
                executeCommands(sender, cmd);
            }
        } else {
            executeCommands(sender, input);
        }
    }

    public static void executeCommands(CommandSender sender, String input) {
        if (sender == null || sender instanceof ConsoleCommandSender) {
            executeCommands(input);
        } else {
            Player player = (Player) sender;
            if (input.startsWith("custom:")) {
                input = input.replace("custom: ", "");
                dispatchCustomCommand(player, input);
                return;
            } else if (input.startsWith("log:")) {
                input = input.replace("log: ", "");
                ServerHandler.sendConsoleMessage(input);
                return;
            } else if (input.startsWith("broadcast:")) {
                input = input.replace("broadcast: ", "");
                Bukkit.broadcastMessage(input);
                return;
            } else if (input.startsWith("console:")) {
                input = input.replace("console: ", "");
                dispatchConsoleCommand(player, input);
                return;
            } else if (input.startsWith("bungee:")) {
                input = input.replace("bungee: ", "");
                dispatchBungeeCordCommand(player, input);
                return;
            } else if (input.startsWith("op:")) {
                input = input.replace("op: ", "");
                dispatchOpCommand(player, input);
                return;
            } else if (input.startsWith("player:")) {
                input = input.replace("player: ", "");
                dispatchPlayerCommand(player, input);
                return;
            } else if (input.startsWith("chat:")) {
                input = input.replace("chat: ", "");
                dispatchChatCommand(player, input);
                return;
            } else if (input.startsWith("message:")) {
                input = input.replace("message: ", "");
                dispatchMessageCommand(player, input);
                return;
            }
            dispatchConsoleCommand(null, input);
        }
    }

    public static void executeCommands(String input) {
        if (input.startsWith("custom:")) {
            input = input.replace("custom: ", "");
            dispatchCustomCommand(null, input);
            return;
        } else if (input.startsWith("log:")) {
            input = input.replace("log: ", "");
            ServerHandler.sendConsoleMessage(input);
            return;
        } else if (input.startsWith("broadcast:")) {
            input = input.replace("broadcast: ", "");
            Bukkit.broadcastMessage(input);
            return;
        } else if (input.startsWith("console:")) {
            input = input.replace("console: ", "");
            dispatchConsoleCommand(null, input);
            return;
        } else if (input.startsWith("bungee:")) {
            dispatchBungeeCordCommand(null, input);
            return;
        } else if (input.startsWith("op:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&eop: " + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("player:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&eplayer:" + input + "&c\" &8- &cCan not find the execute target.");
            return;
        } else if (input.startsWith("chat:")) {
            ServerHandler.sendErrorMessage("&cThere is an error while execute command \"&echat:" + input + "&c\" &8- &cCan not find the execute target.");
            return;
        }
        dispatchConsoleCommand(null, input);
    }

    /**
     * To execute custom command.
     * <p>
     * custom: group, arg1
     * group: "console: say %cmd_arg1%"
     */
    private static void dispatchCustomCommand(CommandSender sender, String input) {
        String[] placeHolderArr = input.split(", ");
        String newCmd = ConfigHandler.getConfigPath().getCustomCmdProp().get(placeHolderArr[0]);
        if (newCmd == null) {
            ServerHandler.sendErrorMessage("&cCan not find the custom command group: " + placeHolderArr[0]);
            return;
        }
        for (int i = 1; i < +placeHolderArr.length; i++) {
            newCmd = newCmd.replace("%cmd_arg" + i + "%", placeHolderArr[i]);
        }
        executeCommands(sender, newCmd);
    }

    /**
     * To execute console command.
     */
    private static void dispatchConsoleCommand(Player player, String command) {
        if (player != null && !(player instanceof ConsoleCommandSender)) {
            try {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                ServerHandler.sendErrorMessage("&cThere was an issue executing a console command, if this continues please report it to the developer!");
                ServerHandler.sendDebugTrace(e);
            }
        } else {
            try {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                ServerHandler.sendErrorMessage("&cThere was an issue executing a console command, if this continues please report it to the developer!");
                ServerHandler.sendDebugTrace(e);
            }
        }
    }

    /**
     * To execute operator command.
     */
    private static void dispatchOpCommand(Player player, String command) {
        boolean isOp = player.isOp();
        try {
            player.setOp(true);
            player.chat("/" + command);
        } catch (Exception e) {
            ServerHandler.sendDebugTrace(e);
            player.setOp(isOp);
            ServerHandler.sendErrorMessage("&cAn error has occurred while setting " + player.getName() + " status on the OP list, to ensure server security they have been removed as an OP.");
        } finally {
            player.setOp(isOp);
        }
    }

    /**
     * To execute player command.
     */
    private static void dispatchPlayerCommand(Player player, String command) {
        try {
            player.chat("/" + command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    /**
     * To send chat message form player.
     */
    private static void dispatchChatCommand(Player player, String command) {
        try {
            player.chat(command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a player command, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    /**
     * To send message to player.
     */
    private static void dispatchMessageCommand(Player player, String command) {
        try {
            player.chat(command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing a command to send a message, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }

    /**
     * To execute BungeeCord command.
     */
    private static void dispatchBungeeCordCommand(Player player, String command) {
        try {
            BungeeCord.ExecuteCommand(player, command);
        } catch (Exception e) {
            ServerHandler.sendErrorMessage("&cThere was an issue executing an item's command to BungeeCord, if this continues please report it to the developer!");
            ServerHandler.sendDebugTrace(e);
        }
    }
}
