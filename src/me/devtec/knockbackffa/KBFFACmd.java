package me.devtec.knockbackffa;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;

public class KBFFACmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length==0) {
			TheAPI.msg("&e/KBFFA Create <name>", s);
			TheAPI.msg("&e/KBFFA Delete <name>", s);
			TheAPI.msg("&e/KBFFA SetSpawn <name>", s);
			TheAPI.msg("&e/KBFFA Teleport <name>", s);
			TheAPI.msg("&e/KBFFA Pos1 <name>", s);
			TheAPI.msg("&e/KBFFA Pos2 <name>", s);
			TheAPI.msg("&e/KBFFA NextArena", s);
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
			if(!(s instanceof Player))return true;
			if(args.length==1){
				TheAPI.msg("&e/KBFFA SetSpawn <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1])){
				TheAPI.msg("&eArena "+args[1]+" not exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1]).spawn=l;
			Data d = new Data("plugins/KnockbackFFA/Arenas/"+args[1]+".yml");
			d.set("spawn",l);
			d.save(DataType.YAML);
			TheAPI.msg("&eSpawn set for arena &b"+args[1]+" &eat &9"+ StringUtils.fixedFormatDouble(l.getX())+" "+StringUtils.fixedFormatDouble(l.getY())+" "+StringUtils.fixedFormatDouble(l.getZ()), s);
			return true;
		}
		if(args[0].equalsIgnoreCase("pos1")){
			if(!(s instanceof Player))return true;
			if(args.length==1){
				TheAPI.msg("&e/KBFFA Pos1 <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1])){
				TheAPI.msg("&eArena "+args[1]+" not exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1]).a=new Position(l);
			Data d = new Data("plugins/KnockbackFFA/Arenas/"+args[1]+".yml");
			d.set("a",new Position(l));
			d.save(DataType.YAML);
			TheAPI.msg("&eRegion position #1 set for arena &b"+args[1]+" &eat &9"+ StringUtils.fixedFormatDouble(l.getX())+" "+StringUtils.fixedFormatDouble(l.getY())+" "+StringUtils.fixedFormatDouble(l.getZ()), s);
			return true;
		}
		if(args[0].equalsIgnoreCase("pos2")){
			if(!(s instanceof Player))return true;
			if(args.length==1){
				TheAPI.msg("&e/KBFFA Pos2 <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1])){
				TheAPI.msg("&eArena "+args[1]+" not exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1]).b=new Position(l);
			Data d = new Data("plugins/KnockbackFFA/Arenas/"+args[1]+".yml");
			d.set("b",new Position(l));
			d.save(DataType.YAML);
			TheAPI.msg("&eRegion position #2 set for arena &b"+args[1]+" &eat &9"+ StringUtils.fixedFormatDouble(l.getX())+" "+StringUtils.fixedFormatDouble(l.getY())+" "+StringUtils.fixedFormatDouble(l.getZ()), s);
			return true;
		}
		if(args[0].equalsIgnoreCase("teleport")){
			if(!(s instanceof Player))return true;
			if(args.length==1){
				TheAPI.msg("&e/KBFFA Teleport <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1])){
				TheAPI.msg("&eArena "+args[1]+" not exist", s);return true;}
			((Player)s).teleport(API.arenas.get(args[1]).spawn);
			TheAPI.msg("&eTeleported to arena &b"+args[1],s);
			return true;
		}
		if(args[0].equalsIgnoreCase("nextarena")){
			API.arena.moveAll(API.nextArena());
			return true;
		}
		if(args[0].equalsIgnoreCase("list")){
			TheAPI.msg("&eList of arenas:", s);
			for(String ds : API.arenas.keySet())
				TheAPI.msg("&6- "+ds, s);
			return true;
		}
		return true;
	}

}
