package me.strobe.vinnyd.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.Strobe.Core.Utils.ItemUtils.createItem;


public class Items {

   public static ItemStack lockedSlot() {
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte)14, false, "&c&lItem Slot",
                              "",
                              "&c&lLOCKED: &7Upgrade your rank",
                              "&7to unlock more item slots.");
   }

   public static ItemStack metaItemSlot() {
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte)4, true, "&e&lMeta Item Slot",
                              "",
                              "&7Every Saturday, new META items",
                              "&7are available in the Mystery Man.");
   }

   public static ItemStack fillerItem() {
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte)15, false, " ");
   }






}
