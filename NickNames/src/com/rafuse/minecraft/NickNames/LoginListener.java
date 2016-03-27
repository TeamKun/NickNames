package com.rafuse.minecraft.NickNames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;

/**
 * Created by Sierra6767 on 3/27/2016.
 */
public class LoginListener implements Listener
{
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event)
    {


        File playerInfo = new File(new File("").getAbsolutePath()
                +"/plugins/NickNames/"+event.getPlayer().getName().toLowerCase()+".yml");

        if(playerInfo.isFile())
        {
            String newName;
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader
                        (playerInfo));
                newName = reader.readLine();

                event.getPlayer().setDisplayName(newName);
                event.getPlayer().setPlayerListName(newName);

                reader.close();
            }
            catch(IOException e)
            {
                new Main().getLogger().warning(e+"");
            }
        }
    }
}
