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
		if(!s.hasPermission("kbffa.admin"))return true;
		if(args.length==0) {
			TheAPI.msg("&e/KBFFA Create <name>", s);
			TheAPI.msg("&e/KBFFA Delete <name>", s);
			TheAPI.msg("&e/KBFFA SetSpawn <name>", s);
			TheAPI.msg("&e/KBFFA Teleport <name>", s);
			TheAPI.msg("&e/KBFFA Pos1 <name>", s);
			TheAPI.msg("&e/KBFFA Pos2 <name>", s);
			TheAPI.msg("&e/KBFFA NextArena [name]", s);
			TheAPI.msg("&e/KBFFA List", s);
			return true;
		}
		if(args[0].equalsIgnoreCase("create")) {
			if(args.length==1) {
				TheAPI.msg("&e/KBFFA Create <name>", s);
				return true;
			}
			if(API.arenas.containsKey(args[1].toLowerCase())) {
				TheAPI.msg("&eArena "+args[1]+" already exists", s);
				return true;
			}
			Data d =new Data("plugins/KnockbackFFA/Arenas/"+args[1]+".yml");
			d.set("name", args[1]);
			d.save(DataType.YAML);
			API.arenas.put(args[1].toLowerCase(), new Arena(d));
			TheAPI.msg("&eCreated arena "+args[1], s);
			return true;
		}
		if(args[0].equalsIgnoreCase("delete")) {
			if(args.length==1) {
				TheAPI.msg("&e/KBFFA Delete <name>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1].toLowerCase())) {
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			API.arenas.remove(args[1].toLowerCase());
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
			if(!API.arenas.containsKey(args[1].toLowerCase())){
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1].toLowerCase()).spawn=l;
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
			if(!API.arenas.containsKey(args[1].toLowerCase())){
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1].toLowerCase()).a=new Position(l);
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
			if(!API.arenas.containsKey(args[1].toLowerCase())){
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			Location l = ((Player)s).getLocation();
			API.arenas.get(args[1].toLowerCase()).b=new Position(l);
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
			if(!API.arenas.containsKey(args[1].toLowerCase())){
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			((Player)s).teleport(API.arenas.get(args[1].toLowerCase()).spawn);
			TheAPI.msg("&eTeleported to arena &b"+args[1],s);
			return true;
		}
		if(args[0].equalsIgnoreCase("name")){
			if(!(s instanceof Player))return true;
			if(args.length<=2){
				TheAPI.msg("&e/KBFFA Name <arena> <value...>", s);
				return true;
			}
			if(!API.arenas.containsKey(args[1].toLowerCase())){
				TheAPI.msg("&eArena "+args[1]+" not exist", s);return true;}
			API.arenas.get(args[1].toLowerCase()).name=StringUtils.buildString(2,args);
			TheAPI.msg("&eSet displayname of arena &b"+args[1]+" &7to &b"+API.arenas.get(args[1].toLowerCase()).name,s);
			return true;
		}
		if(args[0].equalsIgnoreCase("nextarena")){
			if(args.length==1) {
				Loader.nextArenaIn=3;
				Loader.next=API.nextArena();
				TheAPI.msg("&eArena will be changed in 3s",s);
				return true;
			}
			if(!API.arenas.containsKey(args[1].toLowerCase())) {
				TheAPI.msg("&eArena "+args[1]+" doesn't exist", s);return true;}
			Loader.nextArenaIn=3;
			Loader.next=API.arenas.get(args[1].toLowerCase());
			TheAPI.msg("&eArena will be changed in 3s to "+Loader.next.name,s);
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
