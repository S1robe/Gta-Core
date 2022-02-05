package me.strobe.vinnyd.Events;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
      if(npc.getEntity().hasMetadata(VINNYD_META)){

      }
   }

   @EventHandler
   public void onVinnyInteract(InventoryClickEvent e) {
      final Player p       = (Player) e.getWhoClicked();
      final ItemStack item = e.getCurrentItem();
      final Inventory inv  = e.getClickedInventory();
      if(inv == null) return;
      final String iTitle  = ChatColor.stripColor(inv.getTitle());
      if(item == null) return;
      if(iTitle.equalsIgnoreCase("Vinny's Stock")){
         //Handle purchasing an item

         //Handle Upgrading a weapon

         //Handle attachments
      }
   }

}
