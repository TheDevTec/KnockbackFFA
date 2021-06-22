package me.devtec.knockbackffa;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.placeholderapi.PlaceholderRegister;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;

public class Loader extends JavaPlugin {
	int arenaChangeTask,blocks,scc;
	
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
		
		scc=new Tasker() {
		    Vector sc = new Vector(0,1.8,0);
			public void run() {
				for(Player p : TheAPI.getOnlinePlayers())
		            if(p.getLocation().getBlock().getType().equals(Material.GOLD_PLATE)){
						p.setVelocity(sc);
		                return;
		            }
			}
		}.runRepeating(3, 2);
		
		TheAPI.createAndRegisterCommand("knockbackffa", "knockbackffa.command", new KBFFACmd(), "kbffa");
		
		//task na change areny
		arenaChangeTask=new Tasker() {
			public void run() {
				API.arena.moveAll(API.nextArena());
			}
		}.runRepeating(20*60*15, 20*60*15);
		blocks=new Tasker(){
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					Iterator<Entry<Location, BlockStateRemove>> e = new HashSet<>(KnockEvents.blocky.entrySet()).iterator();
					while(e.hasNext()) {
						Entry<Location,BlockStateRemove> ll = e.next();
						Location l=ll.getKey();
						BlockStateRemove r = ll.getValue();
						if(r.placeTime-System.currentTimeMillis()/1000<=0) {
							r.placeTime=System.currentTimeMillis()/1000+r.tickTime;
							if(r.i==0){
								++r.i;
								l.getBlock().setTypeIdAndData(159,(byte)4,true);
								continue;
							}
							if(r.i==1){
								++r.i;
								l.getBlock().setTypeIdAndData(159,(byte)1,true);
								continue;
							}
							if(r.i==2){
								++r.i;
								l.getBlock().setTypeIdAndData(159,(byte)6,true);
								continue;
							}
							if(r.i==3){
								++r.i;
								l.getBlock().setTypeIdAndData(159,(byte)14,true);
								continue;
							}
							if(r.i==4){
								++r.i;
								l.getBlock().setType(Material.AIR);
								if(r.giveBack)
									API.arena.addBlock(r.player);
								KnockEvents.blocky.remove(l);
							}
						}
					}
					e = new HashSet<>(KnockEvents.jumps.entrySet()).iterator();
					while(e.hasNext()) {
						Entry<Location,BlockStateRemove> ll = e.next();
						Location l=ll.getKey();
						BlockStateRemove r = ll.getValue();
						if(r.i!=0)return;
						if(r.placeTime-System.currentTimeMillis()/1000<=0) {
							++r.i;
							l.getBlock().setType(Material.AIR);
							if(r.giveBack)
								TheAPI.giveItem(r.player, ItemCreatorAPI.create(Material.GOLD_PLATE,1,"&e&lJumpPad"));
							KnockEvents.jumps.remove(l);
						}
					}
				}catch(Exception err) {
					err.printStackTrace();
				}
			}
		}.runRepeatingSync(0,3);
		new PlaceholderRegister("kbffa","DevTec","1.0") {
			public String onRequest(Player player, String s) {
				if(s.startsWith("top_")) {
					s=s.replace("top_", "");
					int slot = StringUtils.getInt(s);
					return KillStreaks.getTop(slot);
				}
				if(s.startsWith("score_")) {
					s=s.replace("score_", "");
					int slot = StringUtils.getInt(s);
					return KillStreaks.getValue(slot)+"";
				}
				if(player!=null) {
					if(s.equalsIgnoreCase("kills"))
					return TheAPI.getUser(player).getInt("kbffa.kills")+"";
					if(s.equalsIgnoreCase("deaths"))
					return TheAPI.getUser(player).getInt("kbffa.deaths")+"";
					if(s.equalsIgnoreCase("killsteak"))
					return KillStreaks.killsteak.getOrDefault(player,0)+"";
					if(s.equalsIgnoreCase("best_killsteak"))
					return KillStreaks.getTop(player)+"";
				}
				return null;
			}
		}.register();
	}
	
	public void onDisable() {
		Scheduler.cancelTask(arenaChangeTask);
		Scheduler.cancelTask(blocks);
		Scheduler.cancelTask(scc);
	}
}
