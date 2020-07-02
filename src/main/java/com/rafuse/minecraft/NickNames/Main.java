package com.rafuse.minecraft.NickNames;

import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Sierra6767 on 3/26/2016.
 * Edited by Kamesuta on 7/02/2020.
 */
public class Main extends JavaPlugin implements Listener
{
    private NameChanger nameChanger;

    // This prefix is the universal prefix for the plugin.
    public static final String PREFIX = ChatColor.RESET + "[" + ChatColor
            .GOLD + "NickNames" + ChatColor.RESET + "]";

    @Override
    public void onEnable()
    {
        // register the login listener to listen for player login events.
        getServer().getPluginManager().registerEvents(this, this);
        // Make the plugin directory if it doesn't exist
        new File(getDataFolder()+"/").mkdir();

        File config = new File(getDataFolder()+"/config.yml");
        if(!config.isFile())
        {
            try
            {
                config.createNewFile();
                getLogger().info("Creating config.yml");
            }
            catch(IOException e)
            {
                getLogger().severe("Unable to create config.yml");
            }
        }

        nameChanger = new NameChanger(this);
    }

    @Override
    public void onDisable() { }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        // The different commands in the class
        if (command.getName().equalsIgnoreCase("nick"))
        {
            return nick(sender, command, label, args);
        }
        else if (command.getName().equalsIgnoreCase("reset"))
        {
            return reset(sender, command, label, args);
        }
        else if (command.getName().equalsIgnoreCase("color") || command.getName().equalsIgnoreCase("colour"))
        {
            return colour(sender, command, label, args);
        }
        else if (command.getName().equalsIgnoreCase("realnicks"))
        {
            return realnicks(sender, command, label, args);
        }
        return false;
    }

    /**
     * Nicknames a given user, be it the command sender or another target.
     */
    public boolean nick(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        // Command usage
        if (args.length == 0 && (sender.hasPermission("nicknames.nick") || sender.hasPermission("nicknames.nick.other")))
        {
            sender.sendMessage("Usage:");
            sender.sendMessage("/nick [Player] [Nickname] - Nick another player");
            sender.sendMessage("/nick [Nickname] - Nick yourself");
        }
        // Setting your own nickname
        else if (args.length == 1 && sender.hasPermission("nicknames.nick"))
        {
            // This if statement ensures the console or command block cannot
            // Set it's own nick name.
            if (!(sender instanceof Player)) return false;

            // Cast the player to the correct object
            Player player = (Player) sender;

            // Get the name of the target player
            String targetPlayer = args[0];

            //Make the syntax correct. & is a popular way to set colours,
            // but bukkit only accept the §.
            String newName = targetPlayer.replace("&", "§") + "§f§r";

            // Set the nickname
            setNick(player, newName);
            sendNickWithFeedback(sender, targetPlayer, newName);
        }
        // Setting someone else's nickname
        else if (args.length == 2 && sender.hasPermission("nicknames.nick.other"))
        {
            // Get the name of the target player
            String targetPlayer = args[0];

            //Make the syntax correct. & is a popular way to set colours, but bukkit only accepts the §.
            String newName = args[1].replace("&", "§") + "§f§r";

            // Target starts as null.
            List<Player> targets = Arrays.stream(CommandUtils.getTargets(sender, targetPlayer))
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());

            // If the search was successful run the commmand
            if (!targets.isEmpty())
            {
                for (Player target : targets)
                    setNick(target, newName);
                sendNickWithFeedback(sender, targetPlayer, newName);
            }
            // Else throw an error
            else
            {
                sender.sendMessage("That player is not currently online.");
            }
        }
        // Insufficient permissions?
        else if (!sender.hasPermission("nicknames.nick") && !sender.hasPermission("nicknames.nick.other"))
        {
            sender.sendMessage(PREFIX + " I'm sorry, you do not have access to this command.");
        }
        // Too many arguments -> Syntax Error
        else if (args.length > 2)
        {
            sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
            sender.sendMessage("Usage:");
            sender.sendMessage("/nick [Nickname] - Nick yourself");
            sender.sendMessage("/nick [Player] [Nickname] - Nick another player");
        }
        return true;
    }

    /**
     * Resets a player's name to the default.
     */
    public boolean reset(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if (args.length == 0 && sender.hasPermission("nicknames.reset"))
        {
            // Only players can run this command on themselves.
            if (!(sender instanceof Player)) return false;

            Player player = (Player) sender;

            // Set the player's saved nickname to null, deleting it
            getConfig().set("players."+player.getName().toLowerCase(), null);
            saveConfig();

            applyNick(player, player.getName());

            // Notify the server
            sender.sendMessage(PREFIX + " " + player.getName() + "'s nickname has been reset to default.");
        }
        // Reset another user
        else if (args.length == 1 && sender.hasPermission("nicknames.reset" +
                ".other"))
        {
            // Get the target of the reset
            String targetPlayer = args[0];

            getConfig().set("players."+targetPlayer.toLowerCase(), null);
            saveConfig();

            // Target starts off null
            List<Player> targets = Arrays.stream(CommandUtils.getTargets(sender, targetPlayer))
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());

            // If the player is on, reset their names.
            if (!targets.isEmpty())
            {
                for (Player target : targets)
                    applyNick(target, target.getName());
            }

            // Notify the server
            sender.sendMessage(PREFIX + " " + targetPlayer + "'s " +
                    "nickname has been reset to default.");
        }
        // Insufficient permissions
        else if (!sender.hasPermission("nicknames.reset") ||
                !sender.hasPermission("nicknames.reset.other"))
        {
            sender.sendMessage(PREFIX + " I'm sorry, you do not have access " +
                    "to this command.");
        }
        // Syntax Error
        else if (args.length > 1)
        {
            sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
            sender.sendMessage("Usage:");
            sender.sendMessage("/reset - Reset your name.");
            sender.sendMessage("/reset [Player] - Reset another player's " +
                    "name.");
        }
        return true;
    }

    public boolean colour(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        // Legacy code
        /**
         if(args.length == 1 && args[0].length() == 1)
         return legacyColour(sender, command, label, args);
         else if (args.length == 2 && args[1].length() == 1)
         return legacyColour(sender, command, label, args);
         **/


        if (args.length == 0 && (sender.hasPermission("nicknames.color") ||
                sender.hasPermission("nicknames.color.other")))
        {
            sender.sendMessage("Usage:");
            sender.sendMessage("/colour [colour] - Set your name's colour");
            sender.sendMessage("see /colour list for list of colours.");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list") &&
                sender.hasPermission("nicknames.color"))
        {
            sender.sendMessage(PREFIX + " The following colours are " +
                    "availiable:");
            sender.sendMessage(ChatColor.BLACK + "black " + ChatColor
                    .DARK_BLUE + "dark-blue " + ChatColor
                    .DARK_GREEN + "dark-green " + ChatColor
                    .DARK_AQUA + "dark-aqua " + ChatColor.DARK_RED + "dark-red ");
            sender.sendMessage(ChatColor.DARK_PURPLE + "dark-purple " +
                    "" + ChatColor.GOLD + "gold " + ChatColor.GRAY + "gray " +
                    "" + ChatColor.DARK_GRAY + "dark-gray " + ChatColor
                    .BLUE + "blue ");
            sender.sendMessage(ChatColor.GREEN + "green " + ChatColor
                    .AQUA + "aqua " + ChatColor.RED + "red " + ChatColor
                    .LIGHT_PURPLE + "light-purple " + ChatColor.YELLOW + "yellow " +
                    "" + ChatColor.WHITE + "white ");
            sender.sendMessage("");
            sender.sendMessage(PREFIX + " Other uses:");
            sender.sendMessage(ChatColor.STRIKETHROUGH + "strikethough " +
                    ChatColor.RESET +
                    " " + ChatColor.UNDERLINE + "underline" + ChatColor
                    .RESET + " " + ChatColor
                    .BOLD + "bold" + ChatColor.RESET + " " + ChatColor
                    .ITALIC + "italic" +
                    ChatColor.RESET);
        }
        // Changing your own color
        else if (args.length == 1 && sender.hasPermission("nicknames.color"))
        {
            // Only players can change their names/colors
            if (!(sender instanceof Player)) return false;

            // Cast the player froms sender
            Player player = (Player) sender;

            // Get the target of the reset
            String targetPlayer = args[0];

            String newName = null;
            for (ColorOptions c : ColorOptions.values())
            {
                if (targetPlayer.equalsIgnoreCase(c.getName()))
                {
                    newName =
                            c + "" + player.getName().toUpperCase().charAt(0)
                                    + player.getName().substring(1) + ChatColor.RESET;
                }
            }
            if (newName == null)
            {
                sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
                sender.sendMessage("Usage:");
                sender.sendMessage("/colour [colour] - Set your name's colour");
                sender.sendMessage("see /colour list for list of colours.");
            } else
            {
                setNick(player, newName);
                sendNickWithFeedback(sender, targetPlayer, newName);
            }
        }
        // Changing another player's color
        else if (args.length == 2 && sender.hasPermission("nicknames.color" +
                ".other"))
        {
            // Check if the color chosen is a real supported color
            Optional<ColorOptions> colorOptional = Arrays.stream(ColorOptions.values())
                    .filter(e -> args[1].equalsIgnoreCase(e.getName()))
                    .findFirst();

            if (!colorOptional.isPresent()) {
                sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
                sender.sendMessage("Usage:");
                sender.sendMessage("/colour [colour] - Set your name's colour");
                sender.sendMessage("/colour [player] [colour] - Set another" +
                        " player's colour");
                sender.sendMessage("see /colour list for list of colours.");
            } else {
                // Get the target player's name
                String targetPlayer = args[0];

                // Target starts off null
                List<Player> targets = Arrays.stream(CommandUtils.getTargets(sender, targetPlayer))
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .collect(Collectors.toList());

                if (targets.isEmpty()) {
                    sender.sendMessage(PREFIX+" Player is not currently " +
                            "online.");
                } else {
                    // If it is a supported color and the player is online
                    colorOptional.ifPresent(color -> {
                        for (Player target : targets) {
                            String newName = color + "" + target.getName().toUpperCase().charAt(0) + target.getName().substring(1) + ChatColor.RESET;
                            setNick(target, newName);
                        }
                        sendNickWithFeedback(sender, targetPlayer, color.getName());
                    });
                }
            }
        }
        // Insufficient permissions
        else if (!sender.hasPermission("nicknames.color") || !sender
                .hasPermission("nicknames.color.other"))
        {
            sender.sendMessage(PREFIX + " I'm sorry, you do not have access " +
                    "to this command.");
        }
        // Syntax Error
        else if (args.length > 2 || args[0].length() > 1)
        {
            sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
            sender.sendMessage("Usage:");
            sender.sendMessage("/colour [colour] - Set your name's colour");
            sender.sendMessage("/colour [player] [colour] - Set another" +
                    " player's colour");
            sender.sendMessage("see /colour list for list of colours.");
        }
        return true;
    }

    /**
     * Shows real names and aliases for all users online.
     */
    public boolean realnicks(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        // TODO read up about configuration API for bukkit
        // Make sure the user has sufficient perms
        if (sender.hasPermission("nicknames.realnicks"))
        {
            // Header
            sender.sendMessage(PREFIX + " Identities of online players:");

            // Each individual player
            for (Player p : getServer().getOnlinePlayers())
            {
                sender.sendMessage("Display Name: " + p.getDisplayName());
                // Indent for easier reading
                sender.sendMessage("     Real Name: " + p.getName());
            }

            // End line
            sender.sendMessage(PREFIX + " End of online players.");
        }
        // Insufficient Permissions
        else
        {
            sender.sendMessage(PREFIX + " I'm sorry, you do not have access " +
                    "to this command.");
        }
        return true;
    }

    /**
     * Sets the nickname of a given user.
     * @param player the target player
     * @param newName the new name
     */
    private void setNick(
            Player player,
            String newName
    )
    {
        getConfig().set("players."+player.getName().toLowerCase(), newName);
        saveConfig();

        // Change the display name of the user to the new name
        applyNick(player, newName);
    }

    private void sendNickWithFeedback(
            CommandSender sender,
            String targetName,
            String newName
    )
    {
        // Mention it in the logs
        getLogger().info("Changed " + targetName
                + ChatColor.RESET + "'s name to " + newName
                + ChatColor.RESET + ".");

        // Feedback
        sender.sendMessage(PREFIX + " Changed " + targetName
                + ChatColor.RESET + "'s name to " + newName
                + ChatColor.RESET + ".");
    }

    private void applyNick(
           Player player,
           String newName
    )
    {
        // Change the display name of the user to the new name
        player.setDisplayName(newName != null ? newName : player.getName());
        player.setPlayerListName(newName != null ? newName : player.getName());
        nameChanger.changeName(player, newName);
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event)
    {
        String newName = (String) getConfig().get("players."+event.getPlayer().getName().toLowerCase());

        if(newName != null)
        {
            applyNick(event.getPlayer(), newName);
        }
    }
}
