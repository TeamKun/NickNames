package com.rafuse.minecraft.NickNames;

import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

/**
 * Created by Sierra6767 on 3/26/2016.
 */
public class Main extends JavaPlugin
{

    // This prefix is the universal prefix for the plugin.
    public static final String PREFIX = ChatColor.RESET + "[" + ChatColor
            .GOLD + "NickNames" + ChatColor.RESET + "]";

    @Override
    public void onEnable()
    {
        // register the login listener to listen for player login events.
        getServer().getPluginManager().registerEvents(new LoginListener(),
                this);
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

            //Make the syntax correct. & is a popular way to set colours,
            // but bukkit only accept the §.
            String newName = args[0].replace("&", "§") + "§f§r";

            // Set the nickname
            setNick(player, newName);
        }
        // Setting someone else's nickname
        else if (args.length == 2 && sender.hasPermission("nicknames.nick.other"))
        {
            // Get the name of the target player
            String targetPlayer = args[0];

            //Make the syntax correct. & is a popular way to set colours, but bukkit only accepts the §.
            String newName = args[1].replace("&", "§") + "§f§r";

            // Target starts as null.
            Player target = null;

            // This runs through all online players.
            for (Player p : getServer().getOnlinePlayers())
            {
                // Check for a matching player
                if (p.getName().equalsIgnoreCase(targetPlayer))
                {
                    // If found, set target
                    target = p;
                    break;
                }
            }

            // If the search was successful run the commmand
            if (target != null)
            {
                setNick(target, newName);
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

            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());

            // Notify the server
            Bukkit.broadcastMessage(PREFIX + " " + player.getName() + "'s nickname has been reset to default.");
        }
        // Reset another user
        else if (args.length == 1 && sender.hasPermission("nicknames.reset" +
                ".other"))
        {
            // Get the target of the reset
            String targetPlayer = args[0];

            getConfig().set("players."+targetPlayer.toLowerCase(), null);
            saveConfig();

            Player target = null;
            for (Player p : getServer().getOnlinePlayers())
            {
                if (p.getName().equalsIgnoreCase(targetPlayer))
                {
                    target = p;
                }
            }

            // If the player is on, reset their names.
            if (target != null)
            {
                target.setDisplayName(target.getName());
                target.setPlayerListName(target.getName());
            }

            // Notify the server
            Bukkit.broadcastMessage(PREFIX + " " + targetPlayer + "'s " +
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

            String newName = null;
            for (ColorOptions c : ColorOptions.values())
            {
                if (args[0].equalsIgnoreCase(c.getName()))
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
            }
        }
        // Changing another player's color
        else if (args.length == 2 && sender.hasPermission("nicknames.color" +
                ".other"))
        {
            // Get the target player's name
            String targetPlayer = args[0];

            // Target starts off null
            Player target = null;

            // Check if player is online
            for (Player p : getServer().getOnlinePlayers())
            {
                if (p.getName().equalsIgnoreCase(targetPlayer))
                {
                    target = p;
                    break;
                }
            }

            // Check if the color chosen is a real supported color
            String newName = null;
            for (ColorOptions c : ColorOptions.values())
            {
                if (args[1].equalsIgnoreCase(c.getName()))
                {
                    newName =
                            c + "" + target.getName().toUpperCase().charAt(0) +
                                    target.getName().substring(1) + ChatColor.RESET;
                }
            }

            // If it is a supported color and the player is online
            if (newName != null && target != null)
            {
                setNick(target, newName);
            }
            // If not, syntax error or player isn't online
            else
            {
                if (target == null)
                {
                    sender.sendMessage(PREFIX+" Player is not currently " +
                            "online.");
                } else
                {
                    sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
                    sender.sendMessage("Usage:");
                    sender.sendMessage("/colour [colour] - Set your name's colour");
                    sender.sendMessage("/colour [player] [colour] - Set another" +
                            " player's colour");
                    sender.sendMessage("see /colour list for list of colours.");
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
     * Old version of color. took a number or char instead of string input.
     * retired for better version.
     */
    @Deprecated
    public boolean legacyColour(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (args.length == 0 && (player.hasPermission("nicknames.color") ||
                player.hasPermission("nicknames.color.other")))
        {
            player.sendMessage("Usage:");
            player.sendMessage("/colour [colour] - Set your name's colour");
            player.sendMessage("/colour [player] [colour] - Set another" +
                    " player's colour");
            player.sendMessage("see /colour list for list of colours.");
        } else if (args.length == 1 && args[0].length() == 1 && player
                .hasPermission("nicknames.color"))
        {
            String newName = null;
            char newColour = args[0].charAt(0);

            for (ChatColor c : ChatColor.values())
            {
                if (c.getChar() == newColour)
                {
                    newName = c + player.getName() + ChatColor.RESET;
                }
            }
            if (newName != null)
            {
                String[] newArgs = new String[1];
                newArgs[0] = newName;
                setNick(player, newName);
            } else
            {
                sender.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [colour] - Set your name's colour");
                player.sendMessage("/colour [player] [colour] - Set another" +
                        " player's colour");
                player.sendMessage("see /colour list for list of colours.");
            }
        } else if (args.length == 2 && args[1].length() == 1 && player
                .hasPermission("nicknames.color.other"))
        {

            String playerToChange = args[0];
            Player target = null;

            for (Player p : getServer().getOnlinePlayers())
            {
                if (p.getName().equalsIgnoreCase(playerToChange))
                {
                    target = p;
                    break;
                }
            }

            String newName = null;
            char newColour = args[1].charAt(0);

            for (ChatColor c : ChatColor.values())
            {
                if (c.getChar() == newColour)
                {
                    newName = c + target.getName() + ChatColor.RESET;
                }
            }

            if (newName != null)
            {
                String[] newArgs = new String[1];
                newArgs[0] = newName;
                setNick(target, newName);
            } else
            {
                player.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                        "colour");
            }
        } else if (!player.hasPermission("nicknames.color") || !player
                .hasPermission("nicknames.color.other"))
        {
            player.sendMessage(PREFIX + " I'm sorry, you do not have access " +
                    "to this command.");
        } else if (args.length > 2 || args[0].length() > 1)
        {
            player.sendMessage(ChatColor.DARK_RED + "Syntax Error!");
            player.sendMessage("Usage:");
            player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                    "colour");
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
        player.setDisplayName(newName);
        player.setPlayerListName(newName);

        // Mention it in the logs
        getLogger().info("Changed " + player.getName() + ChatColor
                .RESET + "'s name to " + player.getDisplayName() +
                ChatColor.RESET + ".");

        // Broadcast to all players
        Bukkit.broadcastMessage(PREFIX + " Changed " + player.getName()
                + ChatColor.RESET + "'s name to " + player
                .getDisplayName() + ChatColor.RESET + ".");
    }
}
