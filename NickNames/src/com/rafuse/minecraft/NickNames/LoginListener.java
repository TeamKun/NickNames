package com.rafuse.minecraft.NickNames;

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
        String newName = (String) new Main().getConfig().get("players."+event.getPlayer().getName().toLowerCase());

        if(newName != null)
        {
            event.getPlayer().setDisplayName(newName);
            event.getPlayer().setPlayerListName(newName);
        }
    }
}
