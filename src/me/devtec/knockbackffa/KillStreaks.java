package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.particlesapi.Particle;
import me.devtec.theapi.particlesapi.ParticleAPI;
import me.devtec.theapi.particlesapi.ParticleData;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.sortedmap.RankingAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.User;

public class KillStreaks {
	static Map<Player, Integer> killsteak = new HashMap<>();
	static Player top;
	static RankingAPI<Player, Integer> e;
	
	static {
		Map<String, Integer> f = new HashMap<>();
		for(UUID s : TheAPI.getUsers()) {
			User d = new User(s);
			if(d.getInt("kbffa.killsteak")>0)
				f.put(d.getName(),d.getInt("kbffa.killsteak"));
		}
		sc=new RankingAPI<>(f);
		f.clear();
		new Tasker() {
			int runs = 0;
			public void run() {
				for(UUID s : TheAPI.getUsers()) {
					if(++runs==50) {
						runs=0;
						try {
							Thread.sleep(500);
						} catch (Exception e) {
						}
					}
					User d = new User(s);
					if(d.getInt("kbffa.killsteak")>0)
						f.put(d.getName(),d.getInt("kbffa.killsteak"));
				}
				sc.setMap(f);
				f.clear();
			}
		}.runRepeating(20*60, 20*60);
		new Tasker() {
			public void run() {
				try {
					if(!killsteak.isEmpty())
						if(top!=null && top.isOnline() && killsteak.get(top)!=0) {
							List<Player> ps = TheAPI.getPlayers();
							ps.remove(top);
							if(!ps.isEmpty())
							ParticleAPI.spawnParticle(ps, topKiller, new Position(top.getLocation().add(0,2.5,0)));
						}
				}catch(Exception er) {
					
				}
			}
		}.runRepeating(20, 5);
		new Tasker() {
			public void run() {
				if(killsteak.isEmpty()) {
					top=null;
				}else {
					e = new RankingAPI<>(killsteak);
					top=e.getObject(1);
				}
			}
		}.runRepeating(20, 20);
	}
	
	static RankingAPI<String, Integer> sc; 
	public static void addKillSteak(Player p) {
		int sc = killsteak.getOrDefault(p, 0)+1;
		notifyKillsteak(p, sc);
		killsteak.put(p, sc);
		if(getTop(p)<sc)
			TheAPI.getUser(p).setAndSave("kbffa.killsteak", sc);
	}

	public static void reset(Player p) {
		killsteak.remove(p);
		if(top!=null && top.equals(p)) {
			e.getMap().remove(p);
			top=e.getObject(1);
		}
	}
	
	public static int getTop(Player p) {
		return TheAPI.getUser(p).getInt("kbffa.killsteak");
	}
	
	public static String getTop(int i) {
		return sc.getObject(i);
	}
	
	public static int getValue(int i) {
		return sc.getValue(sc.getObject(i));
	}
	private static Particle walkspeed = new Particle("VILLAGER_HAPPY"),
			topKiller = new Particle("BLOCK_CRACK", new ParticleData.BlockOptions(Material.REDSTONE_BLOCK,(byte)0));
	
	private static void notifyKillsteak(Player p, int sc) {
		if(sc%6==3) {
    		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			API.arena.addEnderpearl(p);
		}
		else if(sc%6==5) {
    		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
    		new Tasker() {
				public void run() {
					if(!p.isOnline()) {
						cancel();
						return;
					}
					List<Player> ps = TheAPI.getPlayers();
					ps.remove(p);
					if(!ps.isEmpty())
					ParticleAPI.spawnParticle(ps, walkspeed, new Position(p.getLocation().add(0,2.5+(top!=null && top.equals(p) && killsteak.get(top)!=0?1:0),0)));
				}
			}.runRepeatingTimes(0, 5, 240);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false));
		}
	}
	
}
