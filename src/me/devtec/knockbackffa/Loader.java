package me.devtec.knockbackffa;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.placeholderapi.PlaceholderRegister;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.nms.NMSAPI;

public class Loader extends JavaPlugin {
	int arenaChangeTask,blocks,scc;
	
	public static int nextArenaIn = 60*10;
	public static Arena next;
	public static Config c;

	public static int rewardAmount;
	PlaceholderRegister d;
	
	public void onEnable() {
		c = Config.loadConfig(this, "config.yml", "KnockbackFFA/config.yml");
		rewardAmount=c.getInt("rewards.required-kills");
		Bukkit.getPluginManager().registerEvents(new KnockEvents(), this);
		//load aren do mapy
		File fr = new File("plugins/KnockbackFFA/Arenas");
		if(!fr.exists())fr.mkdirs();
		for(File f : fr.listFiles())
			API.arenas.put(f.getName().substring(0, f.getName().length()-4).toLowerCase(), new Arena(new Data(f)));
		
		Arena arena = API.nextArena(); //load prvni areny
		API.arena=arena;
		next=API.nextArena();
		//teleport online hraci do areny
		if(arena!=null)
		for(Player p : TheAPI.getOnlinePlayers())
			arena.join(p);
		scc=new Tasker() {
			public void run() {
				for(Player p : TheAPI.getOnlinePlayers()) {
		            if(p.getLocation().getBlock().getType().equals(Material.GOLD_PLATE)){
						p.setVelocity(p.getLocation().getDirection().multiply(0.25).setY(1.8));
				    	p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1, 1);
		                return;
		            }
		            if(p.getLocation().getBlock().getType().equals(Material.IRON_PLATE)){
						p.setVelocity(p.getLocation().getDirection().multiply(4).setY(0.8));
				    	p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1, 1);
		                return;
		            }
				}
			}
		}.runRepeating(3, 2);
		
		TheAPI.createAndRegisterCommand("knockbackffa", null, new KBFFACmd(), "kbffa");
		
		//task na change areny
		arenaChangeTask=new Tasker() {
			public void run() {
				API.arena.a.getWorld().setTime(1000);
				if(--nextArenaIn==0) {
					for(Player p : TheAPI.getOnlinePlayers())
				    	p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 1);
					API.setArena(next);
					next=API.nextArena();
					for(Player p : TheAPI.getOnlinePlayers())
				    	p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 1);
				}else {
					switch(nextArenaIn) {
					case 1:
					case 2:
					case 3:
					case 10:
					case 15:
						for(Player p : TheAPI.getOnlinePlayers())
			    		p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
			        	TheAPI.bcMsg("&b▪&8| &bKBFFA &8› &7Arena se zmeni za &b"+nextArenaIn+"s");
			        	break;
					}
				}
			}
		}.runRepeating(20,20);
		blocks=new Tasker(){
			@SuppressWarnings("deprecation")
			public void run() {
				try {
					synchronized(KnockEvents.blocky) {
					Iterator<Entry<Position, BlockStateRemove>> e = new HashSet<>(KnockEvents.blocky.entrySet()).iterator();
					while(e.hasNext()) {
						Entry<Position,BlockStateRemove> ll = e.next();
						Position l=ll.getKey();
						BlockStateRemove r = ll.getValue();
						if(r.placeTime-System.currentTimeMillis()/1000<=0) {
							r.placeTime=System.currentTimeMillis()/1000+r.tickTime;
							if(r.i==0){
								if(l.getBukkitType()!=Material.HARD_CLAY) {
									NMSAPI.postToMainThread(()->l.getBlock().setType(Material.AIR));
									KnockEvents.blocky.remove(l);
									continue;
								}
								++r.i;
								l.setTypeAndUpdate(Material.getMaterial(159), 4);
								continue;
							}
							if(r.i==1){
								++r.i;
								l.setTypeAndUpdate(Material.getMaterial(159), 1);
								continue;
							}
							if(r.i==2){
								++r.i;
								l.setTypeAndUpdate(Material.getMaterial(159), 6);
								continue;
							}
							if(r.i==3){
								++r.i;
								l.setTypeAndUpdate(Material.getMaterial(159), 14);
								continue;
							}
							if(r.i==4){
								++r.i;
								NMSAPI.postToMainThread(()->ll.getValue().state.update(true,true));
								if(r.giveBack&&r.player.isOnline())
									API.arena.addBlock(r.player);
								r.giveBack=false;
								KnockEvents.blocky.remove(l);
							}
						}
					}
					}
					synchronized(KnockEvents.jumps){
						Iterator<Entry<Position, BlockStateRemove>> e = new HashSet<>(KnockEvents.jumps.entrySet()).iterator();
						while(e.hasNext()) {
							Entry<Position, BlockStateRemove> ll = e.next();
							Position l=ll.getKey();
							BlockStateRemove r = ll.getValue();
							if(r.i!=0)return;
							if(r.placeTime-System.currentTimeMillis()/1000<=0) {
								++r.i;
								NMSAPI.postToMainThread(()->ll.getValue().state.update(true,true));
								if(r.giveBack && r.player.isOnline()&&!r.player.getInventory().contains(Material.GOLD_PLATE))
									TheAPI.giveItem(r.player, ItemCreatorAPI.create(Material.GOLD_PLATE,1,"&e&lJumpPad"));
								r.giveBack=false;
								KnockEvents.jumps.remove(l);
							}
						}
					}
				}catch(Exception err) {
					err.printStackTrace();
				}
			}
		}.runRepeating(0,3);
		d=new PlaceholderRegister("kbffa","DevTec","1.0") {
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
				if(s.equalsIgnoreCase("next_map")) {
					return StringUtils.timeToString(nextArenaIn);
				}
				if(s.equalsIgnoreCase("map")) {
					return API.arena.name;
				}
				if(player!=null) {
					if(s.equalsIgnoreCase("kills"))
					return TheAPI.getUser(player).getInt("kbffa.kills")+"";
					if(s.equalsIgnoreCase("deaths"))
					return TheAPI.getUser(player).getInt("kbffa.deaths")+"";
					if(s.equalsIgnoreCase("killstreak"))
					return KillStreaks.killsteak.getOrDefault(player,0)+"";
					if(s.equalsIgnoreCase("best_killstreak"))
					return KillStreaks.getTop(player)+"";
				}
				return null;
			}
		};
		d.register();
	}
	
	public void onDisable() {
		for(BlockStateRemove e : KnockEvents.blocky.values()) {
			e.state.update(true,true);
		}
		KnockEvents.blocky.clear();
		for(BlockStateRemove e : KnockEvents.jumps.values()) {
			e.state.update(true,true);
		}
		KnockEvents.jumps.clear();
		d.doUnregister();
		Scheduler.cancelTask(arenaChangeTask);
		Scheduler.cancelTask(blocks);
		Scheduler.cancelTask(scc);
	}
}
