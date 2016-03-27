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
    public static final String PREFIX = ChatColor.RESET+"["+ChatColor
        .GOLD+"NickNames" + ChatColor.RESET+ "]";

    private File data;

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new LoginListener(),
                this);
        new File(new File("").getAbsolutePath()+"/plugins/NickNames").mkdir();
    }

    @Override
    public void onDisable()
    {
        data = null;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if(command.getName().equalsIgnoreCase("nick"))
        {
            return nick(sender, command, label, args);
        }
        else if(command.getName().equalsIgnoreCase("reset"))
        {
            return reset(sender, command, label, args);
        }
        else if(command.getName().equalsIgnoreCase("color") || command
                .getName().equalsIgnoreCase("colour"))
        {
            return colour(sender, command, label, args);
        }
        else if(command.getName().equalsIgnoreCase("realnicks"))
        {
            return realnicks(sender, command, label, args);
        }
        return false;
    }

    public boolean nick(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0 && (player.hasPermission("nicknames.nick")
                || player.hasPermission("nicknames.nick.other")))
        {
            sender.sendMessage("Usage:");
            sender.sendMessage("/nick [Player] [Nickname] - Nick " +
                    "another player");
            sender.sendMessage("/nick [Nickname] - Nick yourself");
        }
        else if(args.length==1 && player.hasPermission("nicknames.nick"))
        {
            String newName = args[0].replace("&", "§")+"§f§r";
            setNick(player, newName);
        }
        else if(args.length == 2 && player.hasPermission("nicknames" +
                ".nick.other"))
        {
            String playerToChange = args[0];
            String newName = args[1].replace("&", "§")+"§f§r";

            Player target = null;

            for(Player p : getServer().getOnlinePlayers())
            {
                if(p.getName().equalsIgnoreCase(playerToChange))
                {
                    target = p;
                    break;
                }
            }

            setNick(target, newName);
        }
        else if(!player.hasPermission("nicknames.nick") && !player
                .hasPermission("nicknames.nick.other"))
        {
            player.sendMessage(PREFIX+" I'm sorry, you do not have access " +
                    "to this command.");
        }
        else if(args.length > 2)
        {
            sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
            sender.sendMessage("Usage:");
            sender.sendMessage("/nick [Player] [Nickname] - Nick " +
                    "another player");
            sender.sendMessage("/nick [Nickname] - Nick yourself");
        }
        return true;
    }

    public boolean reset(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        Player player;
        if(sender instanceof Player)
            player = (Player) sender;
        else return false;

        if(args.length == 0 && player.hasPermission("nicknames.reset"))
        {
            File userData = new File(new File("").getAbsolutePath()
                        +"/plugins/NickNames/"+player.getName().toLowerCase()+
                    ".yml");

            if(userData.isFile())
            {
                userData.delete();
                player.setDisplayName(player.getName());
                player.setPlayerListName(player.getName());
                Bukkit.broadcastMessage(PREFIX+" "+player.getName()+"'s " +
                        "nickname has been reset to default.");
            }
            else
            {
                player.sendMessage(PREFIX+" You do not have a nickname. No " +
                        "action was taken.");
            }
        }
        else if(args.length == 1 && player.hasPermission("nicknames.reset" +
                ".other"))
        {
            String reset = args[0];

            File userData = new File(new File("").getAbsolutePath()
                    +"/plugins/NickNames/"+reset.toLowerCase()+".yml");

            if(userData.isFile())
            {
                Player target = null;
                for(Player p : getServer().getOnlinePlayers())
                {
                    if(p.getName().equalsIgnoreCase(reset))
                    {
                        target = p;
                    }
                }
                userData.delete();
                if(target != null)
                {
                    target.setDisplayName(target.getName());
                    target.setPlayerListName(target.getName());
                }
                Bukkit.broadcastMessage(PREFIX+" "+reset+"'s nickname " +
                        "has been reset to default.");
            }
            else
            {
                player.sendMessage(PREFIX+" "+reset+" does no currently " +
                        "have a nickname. No action was taken");
            }
        }
        else if(!player.hasPermission("nicknames.reset") ||
                !player.hasPermission("nicknames.reset.other"))
        {
            player.sendMessage(PREFIX+" I'm sorry, you do not have access " +
                    "to this command.");
        }
        else if(args.length > 1)
        {
            sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
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
        if(args.length == 1 && args[0].length() == 1)
            return legacyColour(sender, command, label, args);
        else if (args.length == 2 && args[1].length() == 1)
            return legacyColour(sender, command, label, args);

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0 && (player.hasPermission("nicknames.color") ||
                player.hasPermission("nicknames.color.other")))
        {
            player.sendMessage("Usage:");
            player.sendMessage("/colour [colour] - Set your name's colour");
            player.sendMessage("see /colour list for list of colours.");
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("list") &&
                player.hasPermission("nicknames.color"))
        {
            player.sendMessage(PREFIX+" The following colours are " +
                    "availiable:");
            player.sendMessage(ChatColor.BLACK+"black "+ChatColor
                    .DARK_BLUE+"dark-blue "+ChatColor
                    .DARK_GREEN+"dark-green "+ChatColor
                    .DARK_AQUA+"dark-aqua "+ChatColor.DARK_RED+"dark-red ");
            player.sendMessage(ChatColor.DARK_PURPLE+"dark-purple " +
                    ""+ChatColor.GOLD+"gold "+ChatColor.GRAY+"gray " +
                    ""+ChatColor.DARK_GRAY+"dark-gray "+ChatColor
                    .BLUE+"blue ");
            player.sendMessage(ChatColor.GREEN+"green "+ChatColor
                    .AQUA+"aqua "+ChatColor.RED+"red "+ChatColor
                    .LIGHT_PURPLE+"light-purple "+ChatColor.YELLOW+"yellow " +
                    ""+ChatColor.WHITE+"white ");
            player.sendMessage(PREFIX+" Other uses:");
            player.sendMessage(ChatColor.STRIKETHROUGH+"strikethough " +
                    ""+ChatColor.RESET +
                    ""+ChatColor.UNDERLINE+"underline "+ChatColor.RESET+ChatColor
                    .BOLD+"bold "+ChatColor.RESET+ChatColor.ITALIC+"italic "+
                    ChatColor.RESET);
        }
        else if(args.length == 1 && player.hasPermission("nicknames.color"))
        {
            String newName = null;
            for(ColorOptions c : ColorOptions.values())
            {
                if(args[0].equalsIgnoreCase(c.getName()))
                {
                    newName = c+player.getName()+ChatColor.RESET;
                }
            }
            if(newName == null)
            {
                sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [colour] - Set your name's colour");
                player.sendMessage("see /colour list for list of colours.");
            }
            else
            {
                setNick(player, newName);
            }
        }
        else if(args.length == 2 && player.hasPermission("nicknames.color" +
                ".other"))
        {
            String playerToChange = args[0];
            Player target = null;

            for(Player p : getServer().getOnlinePlayers())
            {
                if(p.getName().equalsIgnoreCase(playerToChange))
                {
                    target = p;
                    break;
                }
            }

            String newName = null;
            for(ColorOptions c : ColorOptions.values())
            {
                if(args[1].equalsIgnoreCase(c.getName()))
                {
                    newName = c+target.getName()+ChatColor.RESET;
                }
            }

            if(newName != null)
            {
                String[] newArgs = new String[1];
                newArgs[0] = newName;
                setNick(target, newName);
            }
            else
            {
                player.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                        "colour");
            }
        }
        else if(!player.hasPermission("nicknames.color") || !player
                .hasPermission("nicknames.color.other"))
        {
            player.sendMessage(PREFIX+" I'm sorry, you do not have access " +
                    "to this command.");
        }
        else if(args.length > 2 || args[0].length() > 1)
        {
            sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
            player.sendMessage("Usage:");
            player.sendMessage("/colour [colour] - Set your name's colour");
            player.sendMessage("see /colour list for list of colours.");
        }
        return true;
    }

    public boolean legacyColour(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 0 && (player.hasPermission("nicknames.color") ||
                player.hasPermission("nicknames.color.other")))
        {
            player.sendMessage("Usage:");
            player.sendMessage("/colour [1-9, a-f] - Set your name's colour");
        }
        else if(args.length == 1 && args[0].length() == 1 && player
                .hasPermission("nicknames.color"))
        {
            String newName = null;
            char newColour = args[0].charAt(0);

            for(ChatColor c : ChatColor.values())
            {
                if(c.getChar() == newColour)
                {
                    newName = c+player.getName()+ChatColor.RESET;
                }
            }
            if(newName != null)
            {
                String[] newArgs = new String[1];
                newArgs[0] = newName;
                setNick(player, newName);
            }
            else
            {
                player.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                        "colour");
            }
        }
        else if(args.length == 2 && args[1].length() == 1 && player
                .hasPermission("nicknames.color.other"))
        {

            String playerToChange = args[0];
            Player target = null;

            for(Player p : getServer().getOnlinePlayers())
            {
                if(p.getName().equalsIgnoreCase(playerToChange))
                {
                    target = p;
                    break;
                }
            }

            String newName = null;
            char newColour = args[1].charAt(0);

            for(ChatColor c : ChatColor.values())
            {
                if(c.getChar() == newColour)
                {
                    newName = c+target.getName()+ChatColor.RESET;
                }
            }

            if(newName != null)
            {
                String[] newArgs = new String[1];
                newArgs[0] = newName;
                setNick(target, newName);
            }
            else
            {
                player.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                player.sendMessage("Usage:");
                player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                        "colour");
            }
        }
        else if(!player.hasPermission("nicknames.color") || !player
                .hasPermission("nicknames.color.other"))
        {
            player.sendMessage(PREFIX+" I'm sorry, you do not have access " +
                    "to this command.");
        }
        else if(args.length > 2 || args[0].length() > 1)
        {
            player.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
            player.sendMessage("Usage:");
            player.sendMessage("/colour [1-9, a-f] - Set your name's " +
                    "colour");
        }
        return true;
    }

    public boolean realnicks(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    )
    {
        if(sender.hasPermission("nicknames.realnicks"))
        {
            sender.sendMessage(PREFIX+" Identities of online players:");
            for(Player p : getServer().getOnlinePlayers())
            {
                sender.sendMessage("Display Name: "+p.getDisplayName());
                sender.sendMessage("     Real Name: "+p.getName());
            }
            sender.sendMessage(PREFIX+" End of online players.");
        }
        else
        {
            sender.sendMessage(PREFIX+" I'm sorry, you do not have access " +
                    "to this command.");
        }
        return true;
    }

    private void setNick(
            Player player,
            String newName
    )
    {
        File userFile = new File(new File("").getAbsolutePath()
                +"/plugins/NickNames/"+player.getName().toLowerCase()+".yml");
        if(userFile.isFile())
        {
            PrintWriter writer;
            try
            {
                writer = new PrintWriter(userFile);
            }
            catch(IOException e)
            {
                getLogger().warning(e+"");
                writer = null;
            }
            if(writer != null)
            {
                writer.println(newName);
            }
            writer.close();
        }
        else
        {
            getLogger().info("No file information found for " + player
                    .getName() + ": Will create now.");
            try
            {
                userFile.createNewFile();
            } catch (IOException e)
            {
                getLogger().warning(e + "");
            }

            PrintWriter writer;
            try
            {
                writer = new PrintWriter(userFile);
            } catch (IOException e)
            {
                getLogger().warning(e + "");
                writer = null;
            }

            if (writer != null)
            {
                writer.println(newName);
            }

            try
            {
                writer.close();
            } catch (NullPointerException e)
            {
                getLogger().warning(e + "");
            }
        }

        try
        {
            player.setDisplayName(newName);
            player.setPlayerListName(newName);
            getLogger().info("Changed "+player.getName()+ChatColor
                    .RESET+"'s name to " + player.getDisplayName() +
                    ChatColor.RESET + ".");
            Bukkit.broadcastMessage(PREFIX+" Changed "+player.getName()
                    +ChatColor.RESET + "'s name to " + player
                    .getDisplayName() + ChatColor.RESET + ".");
        }
        catch(IllegalArgumentException e)
        {
            getLogger().warning(e.getMessage());
        }
    }
}
