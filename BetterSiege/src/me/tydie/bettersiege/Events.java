package me.tydie.bettersiege;


import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.SiegeData;

public class Events implements Listener {
	
	
	//block break event to stop blocks from dropping during a siege.
	 @EventHandler
	 public void onPlayerBreakBlock(BlockBreakEvent e) { 
		 Player p = e.getPlayer();
		 UUID playerid = p.getUniqueId();
		 //System.out.println("Working!");
		 
		 //System.out.print(GriefPrevention.instance.dataStore.getPlayerData(playerid).playerID);
		 
		 try {
		 SiegeData siegeData = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId()).siegeData;
		 
		 
		 if (siegeData == null) {
			 //System.out.println("***Siege data is null dropping items***");
			 e.setDropItems(true);
			 
		 } 
		 else {
			 System.out.println("***Siege data is NOT null NOT dropping items***");
			 e.setDropItems(false);
		 }
		 
		 } catch(Exception except) {
			 System.out.print("***error in block break***");
		 }
		 
	 }
	 
	 @SuppressWarnings("deprecation")
	@EventHandler
	 public void blockInventoryInteract(PlayerInteractEvent event) {
		 Player player = event.getPlayer();
		 Block clickedBlock = event.getClickedBlock();
		 UUID uuid = player.getUniqueId();
		 
		 if(event.getAction() == Action.LEFT_CLICK_AIR) return;
		 if(event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		 
		 if(GriefPrevention.instance.dataStore.getPlayerData(uuid).siegeData != null) {
			 if(clickedBlock instanceof BlockInventoryHolder) {
				 event.setCancelled(false);
			 }
			 
		 }
	 }
		 

}

