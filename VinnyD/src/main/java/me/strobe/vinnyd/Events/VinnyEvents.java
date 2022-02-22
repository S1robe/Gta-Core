package me.strobe.vinnyd.Events;

import me.Strobe.Core.Utils.Title;
import me.strobe.vinnyd.Utils.GUIS;
import me.strobe.vinnyd.Utils.StockItem;
import me.strobe.vinnyd.Utils.Upgrade;
import me.strobe.vinnyd.Utils.VinnyUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VinnyEvents implements Listener {

   public static final HashMap<Player, Integer> playersLookingAtVinny = new HashMap<>();
   public static final String VINNYD_META = "VINNYD";

   @EventHandler
   public void onNPCInteract(NPCRightClickEvent e){
      final Player p = e.getClicker();
      final NPC npc  = e.getNPC();
      if(npc.getEntity().hasMetadata(VINNYD_META))
         GUIS.openShop(p, VinnyUtils.getVinny());

   }

   @EventHandler
   public void onVinnyInteract(InventoryClickEvent e) {
      final Player p       = (Player) e.getWhoClicked();
      final ItemStack item = e.getCurrentItem();
      final Inventory inv  = e.getClickedInventory();
      if(inv == null) return;
      final String iTitle  = ChatColor.stripColor(inv.getTitle());
      if(item == null) return;
      final String itemTitle = item.hasItemMeta()?
                                       ChatColor.stripColor(item.getItemMeta().getDisplayName())
                                       : item.getType().name();
      if(iTitle.equalsIgnoreCase("Vinny's Upgrades")){
         //Handle Upgrading a weapon
         Upgrade u = VinnyUtils.getUpgradeByResult(item);
         assert u != null;
         if(VinnyUtils.getVinny().performUpgrade(u, p)){
            p.closeInventory();
            Title.sendTitle(p, 0, 5, 3, null, "&aItem Successfully Purchased!");
         }
         else{
            p.closeInventory();
            Title.sendTitle(p, 0, 5, 3, null, "&cYou lack the required items or currency!");
         }
      }
      else if(iTitle.equalsIgnoreCase("Vinny's Stock")){
         //Handle purchasing an item
         StockItem s = VinnyUtils.getStockItemByDisplay(item);
         assert s != null;
         if(VinnyUtils.getVinny().purchaseItem(s, p)) {
            p.closeInventory();
            Title.sendTitle(p, 0, 5, 3, null, "&aItem Successfully Purchased!");
         }
         else {
            p.closeInventory();
            Title.sendTitle(p, 0, 5, 3, null, "&7You need &a" + s.getOddCurrencyPrice() + "OC &7and &a$" + s.getMoneyPrice());
         }

      }
      else if (iTitle.equalsIgnoreCase("Vinny's Shop")){
         //Handle click on shop
         if(itemTitle.equals("Vinny's Stock")){
            p.closeInventory();
            GUIS.openShop(p, VinnyUtils.getVinny());
         }
         //handle click on upgrades,
         else if(itemTitle.equals("Vinny's Upgrades")){
            p.closeInventory();
            GUIS.openUpgrades(p, VinnyUtils.getVinny());
         }
      }
   }

   @EventHandler
   public void onVinnyGUIClose(InventoryCloseEvent e){
      if(e.getInventory().getTitle().equals("Vinny's Shop")){
         Player viewer = (Player) e.getPlayer();
         int x = playersLookingAtVinny.get(viewer);
         Bukkit.getScheduler().cancelTask(x);
         playersLookingAtVinny.remove(viewer);
      }
   }

}
