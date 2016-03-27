package com.rafuse.minecraft.NickNames;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Sierra6767 on 3/26/2016.
 */
public class Main extends JavaPlugin
{
    private File data;
    @Override
    public void onEnable()
    {
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
                try
                {
                    player.setDisplayName(newName);
                    player.setPlayerListName(newName);
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

                for(Player p : getServer().getOnlinePlayers())
                {
                    if(p.getName().equals(playerToChange))
                    {
                        try
                        {
                            p.setDisplayName(newName);
                            p.setPlayerListName(newName);
                            getLogger().info("Changed ");
                        }
                        catch(IllegalArgumentException e)
                        {
                            getLogger().warning(e.getMessage());
                        }
                    }
                }
            }
            else
            {
                sender.sendMessage(ChatColor.DARK_RED+"Syntax Error!");
                sender.sendMessage("");
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
