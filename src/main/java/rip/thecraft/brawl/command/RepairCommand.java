package rip.thecraft.brawl.command;

/**
 * Created by Flatfile on 10/21/2021.
 */
public class RepairCommand {

//    @Command(names = {"repair"})
//    public static void execute(Player player){
//        PlayerData data = Brawl.getInstance().getPlayerDataHandler().getPlayerData(player);
//
//        if(data.isDuelArena()){
//            player.sendMessage(ChatColor.RED + "You cannot use that here.");
//            return;
//        }
//
//        Game game = Brawl.getInstance().getGameHandler().getActiveGame();
//        if(game != null){
//            if(game.containsPlayer(player)){
//                player.sendMessage(ChatColor.RED + "You cannot use this command while in an event.");
//                return;
//            }
//        }
//
//        if(data.getSelectedKit() == null){
//            player.sendMessage(ChatColor.RED + "You need to have a kit equipped to use this command.");
//            return;
//        }
//
//        if(EconUtil.canAfford(data, 200)){
//            if(player.getInventory().firstEmpty() == -1){
//                player.sendMessage(ChatColor.RED + "Your inventory is full.");
//                return;
//            }
//
//            Cooldown refill = data.getCooldown("REPAIR");
//            if (refill != null && !refill.hasExpired()) {
//                player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + refill.getTimeLeft() + ChatColor.RED + " before using /repair again.");
//                return;
//            }
//            data.addCooldown("REPAIR", TimeUnit.MINUTES.toMillis(3));
//
//
//            ItemStack item = data.getRefillType().getItem();
//            if (item.getType() != Material.AIR) {
//                while (player.getInventory().firstEmpty() != -1) {
//                    player.getInventory().addItem(item);
//                }
//            }
//
//            player.updateInventory();
//            player.sendMessage(ChatColor.YELLOW + "You have purchased a repair for " + ChatColor.LIGHT_PURPLE + "50 credits" + ChatColor.YELLOW + ".");
//            EconUtil.withdraw(data, 200);
//        }else{
//            player.sendMessage(ChatColor.RED + "You don't have enough credits to purchase a refill.");
//        }
//    }

}
