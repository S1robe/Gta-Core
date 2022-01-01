package me.strobe.bankrobberies.utils.Displays;

import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import me.strobe.bankrobberies.Main;
import me.strobe.bankrobberies.Robbery;
import me.strobe.bankrobberies.utils.RobItem;
import me.strobe.bankrobberies.utils.RobUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.Strobe.Core.Utils.ItemUtils.*;

public class GUIS {

   private static final Map<Player, Integer> pageViewing = new HashMap<>(); //page 1 is labeled as 1;

   public void NPCRobMenu(Player p, NPC npc) {
      Inventory robMenu = Bukkit.createInventory(null, 9, "Cashier");;
      ItemStack rob = ItemUtils.createItem(Material.GOLD_INGOT, 1, (byte)0,"&c&lRob Store",
                                           "&7Click to start robbing the store.",
                                           "&c&lWanted Level (&7+ 3&c&l)",
                                           "",
                                           "&c&lNOTE: &7When robbing the store, make sure",
                                           "&7you stay inside the store. Otherwise, robbing",
                                           "&7the store will be cancelled.");
      npc.getEntity().setMetadata("ID", new FixedMetadataValue(Main.getMain(), npc.getId()));
      robMenu.setItem(0, ItemUtils.blankFill());
      robMenu.setItem(1, ItemUtils.blankFill());
      robMenu.setItem(2, ItemUtils.blankFill());
      robMenu.setItem(4, ItemUtils.blankFill());
      robMenu.setItem(6, ItemUtils.blankFill());
      robMenu.setItem(7, ItemUtils.blankFill());
      robMenu.setItem(8, ItemUtils.blankFill());

      robMenu.setItem(4, rob);
      p.closeInventory();
      p.openInventory(robMenu);
   }

   public static void changePage(Player viewer, String regID, boolean forward) {
      Inventory inv = Bukkit.createInventory(viewer, 54, regID + " Rewards");
      GenUtils.fill(inv, blankFill());
      if(forward)
         pageViewing.put(viewer, pageViewing.get(viewer) + 1);
      else if(pageViewing.get(viewer) > 1)
         pageViewing.put(viewer, pageViewing.get(viewer) - 1);
      int loopamount = 49 * pageViewing.get(viewer);
      fillInvWithLoot(inv, loopamount, regID);
      finalizeInventory(viewer, inv);
   }

   private static void fillInvWithLoot(Inventory inv, int loopamount, String regID) {
      int slot = 0;
      List<RobItem> lootList = RobUtils.getLootListByRegID(regID);
      if(lootList.size() < loopamount)
         for(int i = loopamount - 49; i < lootList.size(); i++) {
            ItemStack item = lootList.get(i).getDisplayItem();
            inv.setItem(slot, item);
            slot++;
         }
      else
         for(int i = loopamount - 49; i < loopamount; i++) {
            ItemStack item = lootList.get(i).getDisplayItem();
            inv.setItem(slot, item);
            slot++;
         }
   }

   private static void finalizeInventory(Player viewer, Inventory inv) {
      inv.setItem(49, secretGlass());
      inv.setItem(50, secretGlass());
      inv.setItem(51, secretGlass());
      inv.setItem(52, backArrow());
      inv.setItem(53, forArrow());
      viewer.closeInventory();
      viewer.openInventory(inv);
   }

   public static void firstPage(Player viewer, String regID) {
      pageViewing.put(viewer, 1);
      Robbery r = RobUtils.getRobberyByRegID(regID);
      assert r != null;
      Inventory inv = Bukkit.createInventory(viewer, 54, r.getRegID() + " Rewards");
      GenUtils.fill(inv, blankFill());
      fillInvWithLoot(inv, 49, r.getRegID());
      finalizeInventory(viewer, inv);
   }




}
