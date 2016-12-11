package dev.nikho.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AngelJ extends JavaPlugin implements Listener{
	
	private ParticleEffect effect;
	public HashMap<Player, Integer> god = new HashMap<Player, Integer>();
	public FileConfiguration Config;
	public File customConfig;
	private Random ran = new Random();
	
	@Override
	public void onEnable(){
		initEvents();
		init();
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public void initEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	@SuppressWarnings("deprecation")
	public void init(){
		customConfig = new File(this.getDataFolder(), "data.yml");
		Config = YamlConfiguration.loadConfiguration(customConfig);
		
		reloadDataConfig();
		Config.options().copyDefaults(true);
	    saveDataConfig();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable(){
			@Override
			public void run() {
				for(Player p : god.keySet()){
					int time = god.get(p);
					if(time != 0){
						time--;
						god.put(p, time);
					}else{
						p.sendMessage(Config.getString("GodFinish").replace("&", "§"));
						god.remove(p);
					}
				}
			}
		}, 0, 20);
	}
	
	@EventHandler
	public void onFirst(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(!p.hasPlayedBefore()){
			god.put(p, Config.getInt("GodTime"));
			p.sendMessage(Config.getString("GodMessage").replace("&", "§"));
		}
	}
	
	@EventHandler
	public void onHurt(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(god.containsKey(p)){
				for(Player pls : Bukkit.getOnlinePlayers()){
					Location location1 = p.getEyeLocation();
	                Location location2 = p.getEyeLocation();
	                Location location3 = p.getEyeLocation();
	                int particles = 50;
	                float radius = 0.7f;
	                for (int i = 0; i < particles; i++) {
	                    double angle, x, z;
	                    angle = 2 * Math.PI * i / particles;
	                    x = Math.cos(angle) * radius;
	                    z = Math.sin(angle) * radius;
	                    location1.add(x, 0, z);
	                    location2.add(x, -0.66, z);
	                    location3.add(x, -1.33, z);
	                    int c1 = ran.nextInt(255);
	                    int c2 = ran.nextInt(255);
	                    int c3 = ran.nextInt(255);
	                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(c1, c2, c3), location1, pls);
	                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(c3, c1, c2), location2, pls);
	                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(c2, c3, c1), location3, pls);
	                    location1.subtract(x, 0, z);
	                    location2.subtract(x, -0.66, z);
	                    location3.subtract(x, -1.33, z);
	                }
				}
				e.setCancelled(true);
			}
		}
	}
	public void reloadDataConfig(){
	    InputStream localInputStream = this.getResource("config.yml");
	    if(localInputStream != null){
	      YamlConfiguration localYamlConfiguration = YamlConfiguration.loadConfiguration(localInputStream);
	      Config.setDefaults(localYamlConfiguration);
	    }
	} 
	public void saveDataConfig(){
	    try{
	    	Config.save(customConfig);
	    }
	    catch (IOException localIOException){
	    	this.getLogger().info("Couldn't save config.yml!");
	    }
	}
}