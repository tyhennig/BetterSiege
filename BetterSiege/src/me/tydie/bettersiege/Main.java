package me.tydie.bettersiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.SiegeData;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	
	//the required onEnable method for JavaPlugins
	@Override
	public void onEnable() {
		System.out.println("BetterSiege initializing...");
		Bukkit.getPluginManager().registerEvents(new Events(), this);
	}
	
	//declaring variables for time calculation
	private long start;
	private long finish;
	private long difference;
	public boolean taskStarted = false;
	public String attacker;
	public String defender;
	
	//hashmaps for keeping track of player uuid's and the times
	protected HashMap<UUID, Long> startTimeMap = new HashMap<UUID, Long>();
	protected HashMap<UUID, Long> finishTimeMap = new HashMap<UUID, Long>();
	
	//arraylist for keeping track of players in sieges
	protected ArrayList<UUID> inSiege = new ArrayList<UUID>();

	//on command method when /attack is run
	 public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		 
		 //creates reference to the player who sent the command 
		 Player player = (Player) sender;
		 attacker = player.getName();
		 defender = args[0];
		 
		 //starting code for our attack command
		 if (cmd.getName().equalsIgnoreCase("attack")){
			 //System.out.println("***Detected attack command***");
			 if (args.length > 1) {
				 return false;
			 }			 
			 
			 //setting the start to whatever time the attack command took place
			 start = System.currentTimeMillis()/1000;
			 
			 //performs the /siege command for the player to start the GriefPrevention siege mode
			 player.performCommand("siege " + args[0]);
			 
			 
			 //adds player to the start time hashmap
			 startTimeMap.put(player.getUniqueId(), start);
			 
			 //System.out.println("***" + startTimeMap.toString() + "***");

			 //creating reference to our siege checkup task 
			 //System.out.print("***running scheduler...***");
			 runnable();
			 
			 return true;
		 }
		 
	return true;
			
	 }
	 
	
	 
	 public void runnable() {
		 
		 new BukkitRunnable() {

			 @Override
			 public void run() {
				 //System.out.println("***Starting run method***");
				
				
				 
				 
				 //if the start map is empty then no one is in a siege so cancel the runnable
				 if (startTimeMap.isEmpty()) {
					 //System.out.println("***map is empty, canceling runnable task***");
					 taskStarted = false;
					 
					 cancel();
					 return;
				 } else {
					 
					 //for each player in the start map check if they are in a siege still
					 //System.out.println("***entering for loop***");
					 for (UUID uuid : startTimeMap.keySet()) {
						 //System.out.println("***Entered for loop for each uuid***");
						 SiegeData siegeData = GriefPrevention.instance.dataStore.getPlayerData(uuid).siegeData;
						 
						 //if they are not in a siege there are two possibilities
						 if (siegeData == null) {
							 //System.out.println("***Siege data is null***");
							 
							 //player was previously in a siege so they must have won or lost a siege, run restoreClaim
							 if (inSiege.contains(uuid)) {
								 //System.out.println("***was in siege restoring the claim***");
								 restoreClaim(uuid);
								 
								 //player was not previously in a siege so removing them from all hashmaps
							 } else {
								 //System.out.println("***not in siege so running removeSiege***");
								 removeSiege(uuid);
							 }	 
							 
							 //else they are still in a siege so keep running the runnable method
							 //add player to inSiege so the next iteration of the runnable knows they were previously in a siege;
						 } else {
							 //System.out.println("***First run through, adding uuid to inSiege***");
							 //if inSiege already has the player, continue running, else add to inSiege
							 if (inSiege.contains(uuid)) {
								 
							 } else {
							 inSiege.add(uuid);
							 }
						 }
						 
					 }
				 } 
			 }
		 }.runTaskTimer(this, 0, 20*10);
	 }
	 
	 
	
	 //remove siege method wipes player from hashmaps
	 public void removeSiege(UUID uuid){
		 //System.out.println("***starting removeSiege method***");
		 
		 startTimeMap.remove(uuid);
		 finishTimeMap.remove(uuid);
	  	 inSiege.remove(uuid);
	 }
	 
	 //restore claim utilizes CoreProtect's rollback feature
	 //console rollsback using the attacker and defender and time difference
	 public void restoreClaim(UUID uuid) {
		 
		 //System.out.println("***Starting restoreClaim method***");
		 
		 //reference to the Siege Data of that siege instance
		 finish = System.currentTimeMillis()/1000;
		 start = startTimeMap.get(uuid);
		 difference = finish - start;
		 
		 //System.out.println("***Difference calculated to be " + difference);
		 
		 //creates reference to the specific attacker and defender of that siege
		 
		
		 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "co rollback user:" + attacker + " time:" + difference + "s"
				 	+ " action:block");
		 
		 //System.out.println("***Rolling back attacker***");

		 
		 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rollback u:" + defender + " t:" + difference + "s"
				 	+ " a:block");
		 //System.out.println("***Rolling back defender***");
		 
		 removeSiege(uuid);
	 }
	
	@Override
	public void onDisable() {
		System.out.println("BetterSiege closing...");
	}

}
