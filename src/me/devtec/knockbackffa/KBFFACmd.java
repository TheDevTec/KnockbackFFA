package me.devtec.knockbackffa;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.datakeeper.Data;

public class KBFFACmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length==0) {
			TheAPI.msg("&e/KBFFA Create <name>", s);
			TheAPI.msg("&e/KBFFA Delete <name>", s);
			TheAPI.msg("&e/KBFFA SetSpawn <name>", s);
			TheAPI.msg("&e/KBFFA Teleport <name>", s);
			TheAPI.msg("&e/KBFFA List", s);
			return true;
		}
		if(args[0].equalsIgnoreCase("create")) {
			if(args.length==1) {
				TheAPI.msg("&e/KBFFA Create <name>", s);
				return true;
			}
			if(API.arenas.containsKey(args[1])) {
				TheAPI.msg("&eArena "+args[1]+" already exists", s);
				return true;
			}
			API.arenas.put(args[1], new Arena(new Data("plugins/KnockbackFFA/Arenas/"+args[1]+".yml")));
			TheAPI.msg("&eCreated arena "+args[1], s);
			return true;
		}
		if(args[0].equalsIgnoreCase("delete")) {
			if(args.length==1) {
				TheAPI.msg("&e/KBFFA Delete <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1])) {
				TheAPI.msg("&eArena "+args[1]+" not exist", s);
				return true;
			}
			API.arenas.remove(args[1]);
			File f= new File("plugins/KnockbackFFA/Arenas/"+args[1]+".yml");
			if(f.exists())
				f.delete();
			TheAPI.msg("&eDeleted arena "+args[1], s);
			return true;
		}
		if(args[0].equalsIgnoreCase("setspawn")){

		}
		if(args[0].equalsIgnoreCase("teleport")){

		}
		if(args[0].equalsIgnoreCase("nextarena")){

		}
		return true;
	}

}
