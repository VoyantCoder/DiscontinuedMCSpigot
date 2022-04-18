// Author: Dashie
// Version: 1.0

package com.recoded;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class GlobalEvents implements Listener
{
    @EventHandler public void PlayerLogin(final PlayerPreLoginEvent e)
    {
        DashSecurity.iplock.CheckPlayer(e);
    };
};