package me.devtec.knockbackffa;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.utils.datakeeper.User;

public class Rewards { //quests?
	public static void add(Player player) {
		User s = TheAPI.getUser(player);
		int rew = s.getInt("kbffa.rewards")+1;
		if(rew>=Loader.rewardAmount) {
			s.remove("kbffa.rewards");
			s.save();
			giveRewards(player);
		}else {
			s.setAndSave("kbffa.rewards", rew);
		}
	}
	
	public static void reset(Player player) {
		User s = TheAPI.getUser(player);
		s.remove("kbffa.rewards");
		s.save();
	}
	
	public static void giveRewards(Player player) {
		player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
		for(String s : Loader.c.getStringList("rewards.commands")) {
			TheAPI.sudoConsole(PlaceholderAPI.setPlaceholders(player, s.replace("%player%", player.getName())));
		}
		for(String s : Loader.c.getStringList("rewards.messages")) {
			TheAPI.msg(PlaceholderAPI.setPlaceholders(player, s),player);
		}
	}
}
