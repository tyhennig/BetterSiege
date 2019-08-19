# BetterSiege
BetterSiege plugin for Spigot 1.14.4

This plugin utilizes GriefPrevention and CoreProtect to accomplish the following task:
  create the ability to siege without permanently destroying bases
  avoid completely impenatrable bases
  allow the theft of items
  avoid duplicating blocks
  
  
  
Use the /attack <player> command to iniate the siege
  this will start a GriefPrevention siege and a timer
  upon ending the siege the timer will end and run the CoreProtect /rollback command
  this will return all blocks broken by both players in the time of the siege
