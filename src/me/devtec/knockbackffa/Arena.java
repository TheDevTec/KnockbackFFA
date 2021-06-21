package me.devtec.knockbackffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.User;

public class Arena {
	static Random r = new Random();
	public static ItemStack arrow = hide(ItemCreatorAPI.create(Material.ARROW, 1, "&7&lSipy")),
			epearl=hide(ItemCreatorAPI.create(Material.ENDER_PEARL, 1, "&5&lEnder Perla")),
			block=hide(ItemCreatorAPI.create(Material.HARD_CLAY, 64, "&b&lB&f&llock"));
	
	public Location spawn;
	public Position a,b;
	public ItemStack[] itemStacks =
            Arrays.asList(
            		unb(addEnchants(ItemCreatorAPI.create(Material.STICK, 1, "&c&lKnockback tycka"))),
                    unb(addEnchants2(ItemCreatorAPI.create(Material.BOW, 1, "&6&lLuk"))),
                    block,
                    ItemCreatorAPI.create(Material.GOLD_PLATE, 1, "&e&lJumpPad"),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    arrow,
                    epearl).toArray(new ItemStack[0]);

    public Arena(Data data) {
    	spawn=data.getAs("spawn", Location.class);
    	a=data.getAs("a", Position.class);
    	b=data.getAs("b", Position.class);
	}

	private ItemStack unb(ItemStack addEnchants2) {
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

	private ItemStack addEnchants(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
		return create;
	}

	private ItemStack addEnchants2(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		return create;
	}
	
	public boolean isInRegion(Location loc) {
		return BlocksAPI.isInside(new Position(loc), a, b);
	}
	
	public void join(Player dead) {
    	dead.teleport(spawn);
    	dead.setHealth(20);
    	dead.setFireTicks(-20);
    	dead.getInventory().clear();
    	dead.getInventory().setContents(itemStacks);
    	dead.updateInventory();
    }

    public void moveAll(Arena arena) {
        if (arena != null)
            for (Player player : TheAPI.getOnlinePlayers())
                arena.join(player);
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }
    
    List<Player> dead = new ArrayList<>();
    
    public void notifyDeath(Player dead) {
    	if(this.dead.contains(dead))return;
    	this.dead.add(dead);
    	KillStreaks.reset(dead);
    	List<Entity> f = KnockEvents.projectiles.remove(dead);
    	if(f!=null)
    	for(Entity e : f)e.remove();
    	dead.teleport(spawn);
    	dead.setHealth(20);
    	dead.setFireTicks(-20);
    	dead.getInventory().clear();
    	dead.getInventory().setContents(itemStacks);
    	dead.updateInventory();
    	Player killer = KnockEvents.lastHit.remove(dead);
    	if(killer!=null) {
        	KillStreaks.addKillSteak(killer);
    		killer.getInventory().addItem(arrow);
    		killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 3, false, false));
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
    		if(r.player.equals(dead))r.giveBack=false;
    	for(BlockStateRemove r : KnockEvents.jumps.values())
    		if(r.player.equals(dead))r.giveBack=false;
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
    	if(s.getInventory().contains(arrow)) {
    		s.getInventory().addItem(arrow);
    	}else {
    		ItemStack d = s.getInventory().getItem(7);
    		if(d==null||d.getType()==Material.AIR)s.getInventory().setItem(7, arrow);
    		else
        		s.getInventory().addItem(arrow);
    	}
    }
    
    public void addEnderpearl(Player s) {
    	if(s.getInventory().contains(epearl)) {
    		s.getInventory().addItem(epearl);
    	}else {
    		ItemStack d = s.getInventory().getItem(8);
    		if(d==null||d.getType()==Material.AIR)s.getInventory().setItem(8, epearl);
    		else
        		s.getInventory().addItem(epearl);
    	}
    }
    
    public void addBlock(Player s) {
    	if(s.getInventory().contains(block)) {
    		s.getInventory().addItem(block);
    	}else {
    		ItemStack d = s.getInventory().getItem(3);
    		if(d==null||d.getType()==Material.AIR)s.getInventory().setItem(3, block);
    		else
        		s.getInventory().addItem(block);
    	}
    }
}
