package me.devtec.knockbackffa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.placeholderapi.PlaceholderRegister;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.Data;

public class Loader extends JavaPlugin {
	int arenaChangeTask;
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new KnockEvents(), this);
		//load aren do mapy
		File fr = new File("plugins/KnockbackFFA/Arenas");
		if(!fr.exists())fr.mkdirs();
		for(File f : fr.listFiles())
			API.arenas.put(f.getName().substring(0, f.getName().length()-4), new Arena(new Data(f)));
		
		Arena arena = API.nextArena(); //load prvni areny
		//teleport online hraci do areny
		if(arena!=null)
		for(Player p : TheAPI.getOnlinePlayers())
			p.teleport(arena.spawn);
		
		TheAPI.createAndRegisterCommand("knockbackffa", "knockbackffa.command", new KBFFACmd(), "kbffa");
		
		//task na change areny
		arenaChangeTask=new Tasker() {
			public void run() {
				API.arena.moveAll(API.nextArena());
			}
		}.runRepeating(20*60*15, 20*60*15);
		new Tasker(){
			List<Position>remove=new ArrayList<>();
			public void run() {
				for(Entry<Position, BlockStateRemove> ll:KnockEvents.blocky.entrySet()){
					Position l=ll.getKey();
					BlockStateRemove r = ll.getValue();
					if(r.placeTime-System.currentTimeMillis()/1000<=0) {
						r.placeTime=System.currentTimeMillis()/1000+r.tickTime;
						if(r.i==0){
							++r.i;
							BlocksAPI.set(l,Material.YELLOW_TERRACOTTA);
							continue;
						}
						if(r.i==1){
							++r.i;
							BlocksAPI.set(l,Material.ORANGE_TERRACOTTA);
							continue;
						}
						if(r.i==2){
							++r.i;
							BlocksAPI.set(l,Material.PINK_TERRACOTTA);
							continue;
						}
						if(r.i==3){
							++r.i;
							BlocksAPI.set(l,Material.RED_TERRACOTTA);
							continue;
						}
						if(r.i==4){
							++r.i;
							BlocksAPI.set(l, Material.AIR);
							if(r.giveBack)
								TheAPI.giveItem(r.player, ItemCreatorAPI.create(Material.WHITE_TERRACOTTA,1,"&cBlocks"));
							remove.add(l);
						}
					}
				}
				for(Position s:remove)
					KnockEvents.blocky.remove(s);
				remove.clear();

				for(Entry<Position, BlockStateRemove> ll:KnockEvents.jumps.entrySet()){
					Position l=ll.getKey();
					BlockStateRemove r = ll.getValue();
					if(r.placeTime-System.currentTimeMillis()/1000<=0) {
						BlocksAPI.set(l, Material.AIR);
						if(r.giveBack)
		                    TheAPI.giveItem(r.player, ItemCreatorAPI.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE,1,"&eJumpPad"));
						remove.add(l);
					}
				}
				for(Position s:remove)
					KnockEvents.jumps.remove(s);
				remove.clear();
			}
		}.runRepeating(0,3);
		new PlaceholderRegister("kbffa","DevTec","1.0") {
			public String onRequest(Player player, String s) {
				if(s.equalsIgnoreCase("kills"))
				return TheAPI.getUser(player).getString("kbffa.kills");
				if(s.equalsIgnoreCase("deaths"))
				return TheAPI.getUser(player).getString("kbffa.deaths");
				return null;
			}
		}.register();
	}
	
	public void onDisable() {
		Scheduler.cancelTask(arenaChangeTask);
	}
}
