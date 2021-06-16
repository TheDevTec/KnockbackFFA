package me.devtec.knockbackffa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.datakeeper.Data;

public class Loader extends JavaPlugin {
	int arenaChangeTask;
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new KnockEvents(), this);
		//load aren do mapy
		File fr = new File("plugins/KnockbackFFA/Arenas");
		if(!fr.exists())fr.mkdirs();
		for(File f : fr.listFiles())
			API.arenas.put(f.getName().substring(0, f.getName().length()-5), new Arena(new Data(f)));
		
		Arena arena = API.nextArena(); //load prvni areny
		//teleport online hraci do areny
		if(arena!=null)
		for(Player p : TheAPI.getOnlinePlayers())
			p.teleport(arena.spawn);
		//task na change areny
		arenaChangeTask=new Tasker() {
			public void run() {
				API.arena.moveAll(API.nextArena());
			}
		}.runRepeating(20*60*15, 20*60*15);
		List<Location>remove=new ArrayList<>();
		new Tasker(){
			@Override
			public void run() {
				for(Location l:KnockEvents.blocky.keySet()){
					if(l.getBlock().getType().equals(Material.WHITE_TERRACOTTA)){
						BlocksAPI.set(l.getBlock(),Material.YELLOW_TERRACOTTA);
						continue;
					}
					if(l.getBlock().getType().equals(Material.YELLOW_TERRACOTTA)){
						BlocksAPI.set(l.getBlock(),Material.ORANGE_TERRACOTTA);
						continue;
					}
					if(l.getBlock().getType().equals(Material.ORANGE_TERRACOTTA)){
						BlocksAPI.set(l.getBlock(),Material.RED_TERRACOTTA);
						continue;
					}
					if(l.getBlock().getType().equals(Material.PINK_TERRACOTTA)){
						BlocksAPI.set(l.getBlock(),Material.LIGHT_BLUE_TERRACOTTA);
						continue;
					}
					if(l.getBlock().getType().equals(Material.RED_TERRACOTTA)){
						BlocksAPI.set(l.getBlock(), Material.AIR);
						if(KnockEvents.blocky.get().giveBack)
							TheAPI.giveItem(KnockEvents.blocky.get(l).player, ItemCreatorAPI.create(Material.WHITE_TERRACOTTA,1,"&cBlocks"));
						remove.add(l);
					}
				}
				for(Location s:remove){
					KnockEvents.blocky.remove(s);
				}
				remove.clear();
			}
		}.runRepeating(100,100);

	}
	
	public void onDisable() {
		Scheduler.cancelTask(arenaChangeTask);
	}
}
