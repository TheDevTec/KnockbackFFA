package me.devtec.knockbackffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.devtec.knockbackffa.events.ArenaDeathEvent;
import me.devtec.knockbackffa.events.ArenaJoinEvent;
import me.devtec.knockbackffa.events.ArenaLeaveEvent;
import me.devtec.knockbackffa.events.ArenaSwitchEvent;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.nms.NMSAPI;

public class Arena {
	static Random r = new Random();
	public static ItemStack arrow = hide(ItemCreatorAPI.create(Material.ARROW, 1, "&7&lSipy")),
			epearl=hide(ItemCreatorAPI.create(Material.ENDER_PEARL, 1, "&5&lEnder Perla")),
			block=hide(ItemCreatorAPI.create(Material.HARD_CLAY, 1, "&b&lB&f&llock"));
	
	public Location spawn;
	public Position a,b;
	public String name;
	public static ItemStack[] itemStacks =
            Arrays.asList(
            		unb(addEnchants(ItemCreatorAPI.create(Material.STICK, 1, "&c&lKnockback tycka"))),
                    unb(addEnchants2(ItemCreatorAPI.create(Material.BOW, 1, "&6&lLuk"))),
                    hide(ItemCreatorAPI.create(Material.HARD_CLAY, 64, "&b&lB&f&llock")),
                    ItemCreatorAPI.create(Material.GOLD_PLATE, 1, "&e&lJumpPad"),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    arrow,
                    epearl).toArray(new ItemStack[9]);

    public Arena(Data data) {
    	name=data.getString("name");
    	spawn=data.getAs("spawn", Location.class);
    	a=data.getAs("a", Position.class);
    	b=data.getAs("b", Position.class);
	}

	private static ItemStack unb(ItemStack addEnchants2) {
		ItemMeta a= addEnchants2.getItemMeta();
		a.spigot().setUnbreakable(true);
		a.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		addEnchants2.setItemMeta(a);
		return addEnchants2;
	}

	private static ItemStack hide(ItemStack addEnchants2) {
		addEnchants2.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 0);
		ItemMeta a= addEnchants2.getItemMeta();
		a.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		addEnchants2.setItemMeta(a);
		return addEnchants2;
	}

	private static ItemStack addEnchants(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
		return create;
	}

	private static ItemStack addEnchants2(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 4);
		return create;
	}
	
	public boolean isInRegion(Location loc) {
		double xMin = Math.min(a.getX(), b.getX());
		double yMin = Math.min(a.getY(), b.getY());
		double zMin = Math.min(a.getZ(), b.getZ());
		double xMax = Math.max(a.getX(), b.getX());
		double yMax = Math.max(a.getY(), b.getY());
		double zMax = Math.max(a.getZ(), b.getZ());
		return loc.getWorld().equals(a.getWorld()) && loc.getX() >= xMin && loc.getX() <= xMax
				&& loc.getY() >= yMin && loc.getY() <= yMax && loc.getZ() >= zMin
				&& loc.getZ() <= zMax;
	}
	
	public void join(Player dead) {
		ArenaJoinEvent d = new ArenaJoinEvent(dead);
    	TheAPI.callEvent(d);
		resetPlayer(dead);
    }
	
	public void resetPlayer(Player dead) {
    	try {
        	dead.setFireTicks(-20);
        	dead.getInventory().clear();
        	dead.setItemOnCursor(null);
    		dead.teleport(spawn);
        	new Tasker() {
				public void run() {
					int i = 0;
		        	for(ItemStack s : itemStacks)
		        		dead.getInventory().setItem(i++, s);
				}
			}.runLater(1);
    	}catch(Exception ner) {
    		ner.printStackTrace();
    	}
    }

    public Arena moveAll(Arena arena) {
        if (arena != null) {
    		ArenaSwitchEvent d = new ArenaSwitchEvent(this, arena);
        	TheAPI.callEvent(d);
        	arena=d.getTo();
        	if(arena==null)arena=d.getFrom();
        	TheAPI.bcMsg("&b▪&8| &bKBFFA &8› &7Arena zmenena na &b"+arena.name);
        	Loader.nextArenaIn=60*10;
        	Arena a = arena;
            NMSAPI.postToMainThread(() -> {
            	for (Player player : TheAPI.getOnlinePlayers())
                	a.resetPlayer(player);
            });
        }
        return arena;
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }
    
    List<Player> dead = new ArrayList<>();
    
    public void notifyLeave(Player dead) {
    	if(this.dead.contains(dead))return;
    	Player killer = KnockEvents.lastHit.remove(dead);
    	ArenaLeaveEvent d = new ArenaLeaveEvent(dead,killer);
    	TheAPI.callEvent(d);
    	KillStreaks.reset(dead);
    	List<Entity> f = KnockEvents.projectiles.remove(dead);
    	if(f!=null)
    	for(Entity e : f)e.remove();
    	if(killer!=null) {
    		killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1, 1);
    		Rewards.add(killer);
        	KillStreaks.addKillSteak(killer);
        	addArrow(killer);
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7"+generateRandomDeathMessageKiller(dead.getName()), killer);
        	User user = TheAPI.getUser(killer);
        	user.setAndSave("kbffa.kills", user.getInt("kbffa.kills")+1);
    	}
    	for(BlockStateRemove r : KnockEvents.blocky.values())
    		if(r.player.getName().equals(dead.getName()))r.giveBack=false;
    	for(BlockStateRemove r : KnockEvents.jumps.values())
    		if(r.player.getName().equals(dead.getName()))r.giveBack=false;
    	new Tasker() {
			public void run() {
				dead.playSound(dead.getLocation(), Sound.ANVIL_LAND, 1, 1);
			}
		}.runLater(1);
    	new Tasker() {
			public void run() {
				Arena.this.dead.remove(dead);
			}
		}.runLater(20);
    }
    
    public void notifyDeath(Player dead) {
    	if(this.dead.contains(dead))return;
		
    	Player killer = KnockEvents.lastHit.remove(dead);
    	ArenaDeathEvent d = new ArenaDeathEvent(dead,killer);
    	TheAPI.callEvent(d);
    	this.dead.add(dead);
    	KillStreaks.reset(dead);
    	List<Entity> f = KnockEvents.projectiles.remove(dead);
    	if(f!=null)
    	for(Entity e : f)e.remove();
    	resetPlayer(dead);
    	if(killer!=null) {
    		killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1, 1);
    		Rewards.add(killer);
        	KillStreaks.addKillSteak(killer);
        	addArrow(killer);
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7"+generateRandomDeathMessageKiller(dead.getName()), killer);
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7"+generateRandomDeathMessage(killer.getName()), dead);
        	User user = TheAPI.getUser(killer);
        	user.setAndSave("kbffa.kills", user.getInt("kbffa.kills")+1);
    	}else {
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7"+generateRandomDeathMessage(),dead); //Tomu říkám skok do neznáma!", dead);
    	}
    	User user = TheAPI.getUser(dead);
    	user.setAndSave("kbffa.deaths", user.getInt("kbffa.deaths")+1);
    	for(BlockStateRemove r : KnockEvents.blocky.values())
    		if(r.player.getName().equals(dead.getName()))r.giveBack=false;
    	for(BlockStateRemove r : KnockEvents.jumps.values())
    		if(r.player.getName().equals(dead.getName()))r.giveBack=false;
    	new Tasker() {
			public void run() {
				dead.playSound(dead.getLocation(), Sound.ANVIL_LAND, 1, 1);
			}
		}.runLater(1);
    	new Tasker() {
			public void run() {
				Arena.this.dead.remove(dead);
			}
		}.runLater(20);
    }
    
    private String generateRandomDeathMessage() {
		switch(r.nextInt(3)) {
		case 0:
			return "To musela byt velka vyska";
		case 1:
			return "Tomu rikam skok do neznama!";
		case 2:
			return "Skocil jsi do voidu";
		}
		return null;
	}
    private String generateRandomDeathMessage(String s) {
		switch(r.nextInt(4)) {
		case 0:
			return "Dostal jsi flakanec od hrace &b"+s;
		case 1:
			return "Byl jsi utlucen hracem &b"+s;
		case 2:
			return "Byl jsi zabit hracem &b"+s;
		case 3:
			return "Byl jsi rozdrcen hracem &b"+s;
		case 4:
			return "Byl jsi ustouchan hracem &b"+s+" &7k smrti";
		case 5:
			return "&b"+s+" &7te zabil svym mocnym klackem";
		}
		return null;
	}
    private String generateRandomDeathMessageKiller(String s) {
		switch(r.nextInt(5)) {
		case 0:
			return "Zabil jsi hrace &b"+s;
		case 1:
			return "Utloukl jsi hrace &b"+s;
		case 2:
			return "Ustouchal jsi hrace &b"+s+" &7k smrti";
		case 3:
			return "Rozdrtil jsi hrace &b"+s;
		case 4:
			return "Dal jsi flakanec hraci &b"+s;
		case 5:
			return "Zabil jsi &b"+s+" &7svym mocnym klackem";
		}
		return null;
	}
    
	public void addArrow(Player s) {
    	ItemStack st = arrow.clone();
    	if(s.getInventory().contains(st.getType())) {
    		s.getInventory().addItem(st);
    	}else {
    		ItemStack d = s.getInventory().getItem(7);
    		if(d==null||d.getType()==Material.AIR||d.getAmount()==0)s.getInventory().setItem(7, st);
    		else
        		s.getInventory().addItem(st);
    	}
    }
    
    public void addEnderpearl(Player s) {
    	ItemStack st = epearl.clone();
    	if(s.getInventory().contains(st.getType())) {
    		s.getInventory().addItem(st);
    	}else {
    		ItemStack d = s.getInventory().getItem(8);
    		if(d==null||d.getType()==Material.AIR||d.getAmount()==0)s.getInventory().setItem(8, st);
    		else
        		s.getInventory().addItem(st);
    	}
    }
    
    public void addBlock(Player s) {
    	ItemStack st = block.clone();
		if(count(s.getInventory().getContents(),st.getType())>=64)return;
    	if(s.getInventory().contains(st.getType())) {
    		s.getInventory().addItem(st);
    	}else {
    		ItemStack d = s.getInventory().getItem(3);
    		if(d==null||d.getType()==Material.AIR||d.getAmount()==0)s.getInventory().setItem(3, st);
    		else
        		s.getInventory().addItem(st);
    	}
    }

	private int count(ItemStack[] contents, Material type) {
		int c = 0;
		for(ItemStack st : contents)
			if(st!=null&&st.getType()==type)c+=st.getAmount();
		return c;
	}
}
