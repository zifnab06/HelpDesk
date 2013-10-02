package at.junction.HelpDesk;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.inventory.ItemStack;

import org.bukkit.ChatColor;
import org.bukkit.World;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.entity.Player;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

import org.bukkit.potion.PotionEffect;

import net.milkbowl.vault.permission.Permission;

public class HelpDesk extends JavaPlugin {
	HelpDeskListener listener = new HelpDeskListener(this);

	public List<String> vanished;
	public List<String> staffMode;
	public String staffGroup;
	public static Permission permission = null;

	@Override
	public void onEnable() {
		File conf = new File(getDataFolder(), "config.yml");
		if (!conf.exists()){
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		vanished = getConfig().getStringList("vanished");
		staffMode = getConfig().getStringList("staffMode");
		staffGroup = getConfig().getString("staffGroup");
		permission = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
	}

	@Override
	public void onDisable() {
		getConfig().set("vanished", vanished);
		getConfig().set("staffMode", staffMode);
		permission = null;
	}

	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

		//If sender is console, quit
		if (!(sender instanceof Player)) return false;

		Player player = (Player) sender;
		String commandName = command.getName();
		if (commandName.equalsIgnoreCase("mode")) {
			if (permission.playerHas(player, Permissions.STAFFMODE)){
				if (staffMode.contains(player.getName())){//leave staff mode
					staffMode.remove(player.getName());
					//Disable Flight
						player.setAllowFlight(false || player.getGameMode() == org.bukkit.GameMode.CREATIVE);
					//Unvanish
						unvanish(player);
					//Change Group
						for (World w : getServer().getWorlds()){
							permission.playerRemoveGroup(w, player.getName(), staffGroup);
						}
					//Restore Player Data
						clearInventory(player);
						loadInventory(player);
						try {
						} catch (Exception e){
							player.sendMessage("Your inventory was lost, PLEASE CONTACT A TECH IMMEDIATELY");
						}
					//TODO: Reset Inventory
						//Audio Cue
						player.playEffect(player.getLocation(), org.bukkit.Effect.EXTINGUISH, 0);
					return true;
				} else {//enter staff mode
					staffMode.add(player.getName());
					getLogger().info(player.getName() + " has entered staffMode at coordinates " + player.getLocation().toString());
					//Enable Flight
						player.setAllowFlight(true);
					//Change Group
						for (World w : getServer().getWorlds()){
							permission.playerAddGroup(w, player.getName(), staffGroup);
						}
						for (String s : permission.getPlayerGroups(player)) player.sendMessage(s);
					//Save Old Inventory
						saveInventory(player);
						clearInventory(player);
						player.getInventory().setHelmet(new ItemStack(org.bukkit.Material.GLASS));
					//Play noise as "YAY YOU MADE IT"
						player.playEffect(player.getLocation(), org.bukkit.Effect.BLAZE_SHOOT, 0);
					return true;
				}
			} else { //show list of current staff
				//get group listing of staff from PEX
				return true;
			}
		} else if (commandName.equalsIgnoreCase("vanish")) {
			if (args.length == 1 && args[0].equalsIgnoreCase("list") && permission.playerHas(player, Permissions.VANISHLIST)){
				//show vanished players
				return true;
			} else {//vanish if perms
				if (permission.playerHas(player, Permissions.VANISH)){
					if (vanished.contains(player.getName())) {
						player.sendMessage(ChatColor.RED + "You are already vanished!");
						return true;
					} else {
						vanished.add(player.getName());
						for (Player p : getServer().getOnlinePlayers()) {
							p.hidePlayer(player);
						}
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
					return true;
				}
			}
		} else if (commandName.equalsIgnoreCase("unvanish")) {
			if (permission.playerHas(player, Permissions.VANISH)) {
				if (!vanished.contains(player.getName())){
					player.sendMessage(ChatColor.RED + "You are not vanished!");
				} else {
					unvanish(player);
				}
			}
			return true;
		}
		return false;
	}
	public String[] getStaffList() {
		return new String[1];
	}
	private void unvanish(Player player){
		if (!vanished.contains(player.getName())) {
			return;
		} else {
			vanished.remove(player.getName());
			for (Player p : getServer().getOnlinePlayers()) {
				p.showPlayer(player);
			}
		}
	}
	private void clearInventory(Player player){
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}
	private void saveInventory(Player player){
	}
	private void loadInventory(Player player){
	}
}

