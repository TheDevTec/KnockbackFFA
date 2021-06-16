package me.devtec.knockbackffa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
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
		new Tasker(){
			List<Location>remove=new ArrayList<>();
			public void run() {
				for(Entry<Location, BlockStateRemove> ll:KnockEvents.blocky.entrySet()){
					Location l=ll.getKey();
					BlockStateRemove r = ll.getValue();
					if(r.i==0){
						++r.i;
						BlocksAPI.set(l.getBlock(),Material.YELLOW_TERRACOTTA);
						continue;
					}
					if(r.i==1){
						++r.i;
						BlocksAPI.set(l.getBlock(),Material.ORANGE_TERRACOTTA);
						continue;
					}
					if(r.i==2){
						++r.i;
						BlocksAPI.set(l.getBlock(),Material.RED_TERRACOTTA);
						continue;
					}
					if(r.i==3){
						++r.i;
						BlocksAPI.set(l.getBlock(),Material.LIGHT_BLUE_TERRACOTTA);
						continue;
					}
					if(r.i==4){
						BlocksAPI.set(l.getBlock(), Material.AIR);
						if(KnockEvents.blocky.get(l).giveBack)
							TheAPI.giveItem(KnockEvents.blocky.get(l).player, ItemCreatorAPI.create(Material.WHITE_TERRACOTTA,1,"&cBlocks"));
						remove.add(l);
					}
				}
				for(Location s:remove){
					KnockEvents.blocky.remove(s);
				}
				remove.clear();
			}
		}.runRepeating(0,100);
	}
	
	public void onDisable() {
		Scheduler.cancelTask(arenaChangeTask);
	}
}
