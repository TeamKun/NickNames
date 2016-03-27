package com.rafuse.minecraft.NickNames;

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
            if(!(sender instanceof Player)) return false;

            Player player = (Player) sender;

            if(args.length == 0)
            {
                sender.sendMessage("Usage:");
                sender.sendMessage("/nick [Player] [Nickname] - Nick " +
                        "another player");
                sender.sendMessage("/nick [Nickname] - Nick yourself");
            }
            else if(args.length==1)
            {
                String newName = args[0].replace("&", "§")+"§f§r";

                File userFile = new File(new File("").getAbsolutePath()
                        +"/plugins/NickNames/"+player.getName()+".yml");
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

                    writer.close();
                }

                try
                {
                    player.setDisplayName(newName);
                    player.setPlayerListName(newName);
                    getLogger().info("Changed "+player.getName()+ChatColor
                            .RESET+"'s name to " + player.getDisplayName() +
                            ChatColor.RESET + ".");
                    Bukkit.broadcastMessage(ChatColor.DARK_RED+"FRED: "+
                            ChatColor.RESET+"Changed " + ""+player
                            .getName() +ChatColor.RESET+ "'s name to " +
                            player.getDisplayName() + ChatColor.RESET + ".");
                }
                catch(IllegalArgumentException e)
                {
                    getLogger().warning(e.getMessage());
                }
            }
            else if(args.length == 2)
            {
                String playerToChange = args[0];
                String newName = args[1].replace("&", "§")+"§f§r";

                Player target = null;

                for(Player p : getServer().getOnlinePlayers())
                {
                    if(p.getName().equals(playerToChange))
                    {
                        target = p;
                        break;
                    }
                }

                File userFile = new File(new File("").getAbsolutePath()
                        +"/plugins/NickNames/"+target.getName()+".yml");
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

                    writer.close();
                }

                try
                {
                    target.setDisplayName(newName);
                    target.setPlayerListName(newName);
                    getLogger().info("Changed "+target.getName()+ChatColor
                            .RESET+"'s name to " + target.getDisplayName() +
                            ChatColor.RESET + ".");
                    Bukkit.broadcastMessage(ChatColor.DARK_RED+"FRED: "+
                            ChatColor.RESET+"Changed " + ""+player
                            .getName() +ChatColor.RESET+ "'s name to " +
                            player.getDisplayName() + ChatColor.RESET + ".");
                }
                catch(IllegalArgumentException e)
                {
                    getLogger().warning(e.getMessage());
                }
            }
            else
            {
                sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                sender.sendMessage("Usage:");
                sender.sendMessage("/nick [Player] [Nickname] - Nick " +
                        "another player");
                sender.sendMessage("/nick [Nickname] - Nick yourself");
            }
            return true;
        }
        return false;
    }


}
