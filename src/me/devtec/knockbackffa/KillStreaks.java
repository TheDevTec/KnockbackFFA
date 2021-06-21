package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.sortedmap.RankingAPI;

public class KillStreaks {
	static Map<Player, Integer> killsteak = new HashMap<>();
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
