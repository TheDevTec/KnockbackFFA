package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.sortedmap.RankingAPI;
import me.devtec.theapi.utils.datakeeper.User;

public class KillStreaks {
	static Map<Player, Integer> killsteak = new HashMap<>();
	
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
	
	private static void notifyKillsteak(Player p, int sc) {
		if(sc%5==3)API.arena.addEnderpearl(p);
		else if(sc%5==5)p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, false, false));
	}
	
}
