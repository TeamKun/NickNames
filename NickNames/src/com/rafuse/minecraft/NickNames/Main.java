package com.rafuse.minecraft.NickNames;

import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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
                getLogger().info("No file information found for "+player
                        .getName()+": Will create now.");
                try
                {
                    userFile.createNewFile();
                }
                catch(IOException e)
                {
                    getLogger().warning(e+"");
                }

                PrintWriter writer;
                try
                {
                    writer = new PrintWriter(userFile);
                }
                catch(IOException e)
                {
                    getLogger().warning(e + "");
                    writer = null;
                }

                if(writer != null)
                {
                    writer.println(newName);
                }

                try
                {
                    writer.close();
                }
                catch(NullPointerException e)
                {
                    getLogger().warning(e+"");
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

            File userFile = new File(new File("").getAbsolutePath()
                    +"/plugins/NickNames/"+target.getName().toLowerCase()+".yml");
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
                getLogger().info("No file information found for "+target
                        .getName()+": Will create now.");
                try
                {
                    userFile.createNewFile();
                }
                catch(IOException e)
                {
                    getLogger().warning(e+"");
                }

                PrintWriter writer;
                try
                {
                    writer = new PrintWriter(userFile);
                }
                catch(IOException e)
                {
                    getLogger().warning(e + "");
                    writer = null;
                }

                if(writer != null)
                {
                    writer.println(newName);
                }

                try
                {
                    writer.close();
                }
                catch(NullPointerException e)
                {
                    getLogger().warning(""+e);
                }

            }

            try
            {
                target.setDisplayName(newName);
                target.setPlayerListName(newName);
                getLogger().info("Changed "+target.getName()+ChatColor
                        .RESET+"'s name to " + target.getDisplayName() +
                        ChatColor.RESET + ".");
                Bukkit.broadcastMessage(PREFIX+" Changed " + ""+target
                        .getName() + ChatColor.RESET+ "'s name to " +
                        target.getDisplayName() + ChatColor.RESET + ".");
            }
            catch(IllegalArgumentException e)
            {
                getLogger().warning(e.getMessage());
            }
        }
        else if(!player.hasPermission("nicknames.nick") && !player
                .hasPermission("nicknames.nick.other"))
        {
            player.sendMessage("["+ ChatColor.GOLD + "NickNames"+ChatColor
                    .RESET+"] I'm sorry, you do not have access to this" +
                    " command.");
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
        return true;
    }
}
