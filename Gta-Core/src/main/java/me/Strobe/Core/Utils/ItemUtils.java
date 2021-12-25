package me.Strobe.Core.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.Strobe.Core.Utils.StringUtils.color;
import static me.Strobe.Core.Utils.StringUtils.colorBulk;

public final class ItemUtils {

   private ItemUtils() {
   }

   public static ItemStack forArrow() {
      return createItem(Material.ARROW, 1, (byte) 0, "&e&lNext Page ->");
   }

   public static ItemStack backArrow() {
      return createItem(Material.ARROW, 1, (byte) 0, "&c&l<- Back");
   }

   public static ItemStack secretGlass() {
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte) 12, "&5???", "&7Secret...");
   }

   public static ItemStack blankFill() {
      return createItem(Material.STAINED_GLASS_PANE, 1, (byte) 7, " ");
   }


   //returns the skull of other people
   public static ItemStack getSkullOf(String otherOwners) {
      ItemStack memberSkull = createItem(Material.SKULL_ITEM, 1, 3, "&6" + Bukkit.getOfflinePlayer(UUID.fromString(otherOwners)).getPlayer().getDisplayName());
      SkullMeta memberMeta = (SkullMeta) memberSkull.getItemMeta();
      memberMeta.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(otherOwners)).getName());
      memberSkull.setItemMeta(memberMeta);
      return memberSkull;
   }

   public static ItemStack getSkullOf(UUID otherOwners) {
      ItemStack memberSkull = createItem(Material.SKULL_ITEM, 1, 3, "&6" + Bukkit.getOfflinePlayer(otherOwners).getPlayer().getDisplayName());
      SkullMeta memberMeta = (SkullMeta) memberSkull.getItemMeta();
      memberMeta.setOwner(Bukkit.getOfflinePlayer(otherOwners).getName());

      memberSkull.setItemMeta(memberMeta);
      return memberSkull;
   }

   // creates a new ItemStack of type 'material': must be of Material.*
   public static ItemStack createItem(Material material) {
      return new ItemStack(material);
   }

   // returns a quantity of itemstack 'm',
   // EX createItem( new ItemStack(Material.STONE), 3) would set the new itemstack's
   // amount to 3 and would return that reference to the called position.
   public static ItemStack createItem(ItemStack m, int amount) {
      m.setAmount(amount);
      ItemMeta meta = m.getItemMeta();
      meta.setDisplayName(color(m.getItemMeta().getDisplayName()));
      meta.setLore(colorBulk(m.getItemMeta().getLore()));
      m.setItemMeta(meta);
      return m;
   }

   // essentially does the same thing as abouve but creates a new itemstack in the process
   public static ItemStack createItem(Material material, int amount) {
      return new ItemStack(material, amount);
   }

   // Returns a new Named Itemstack.
   public static ItemStack createItem(Material material, String itemName) {
      ItemStack itemStack = new ItemStack(material);
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setDisplayName(color(itemName));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }

   // More specific way to spawn in items, uses the dataValue
   // Ex Redrose:4 <data value, also called damage value, is the type parameter specification
   public static ItemStack createItem(Material material, int amount, int dataValue) {
      return new ItemStack(material, amount, (short) dataValue);
   }

   // Returns new Itemstack that has custom material, name, and lore
   public static ItemStack createItem(Material material, String itemName, List<String> itemLore) {
      ItemStack itemStack = new ItemStack(material);
      ItemMeta itemMeta = itemStack.getItemMeta();
      return updateItemStack(itemName, itemLore, itemStack, itemMeta);
   }

   private static ItemStack updateItemStack(String itemName, List<String> itemLore, ItemStack itemStack, ItemMeta itemMeta) {
      itemMeta.setDisplayName(color(itemName));
      itemMeta.setLore(colorBulk(itemLore));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }

   // Returns new itemstack with material, amount, name, and lore custom
   public static ItemStack createItem(Material material, int amount, String itemName, List<String> itemLore) {
      ItemStack itemStack = new ItemStack(material, amount);
      ItemMeta itemMeta = itemStack.getItemMeta();
      return updateItemStack(itemName, itemLore, itemStack, itemMeta);
   }

   // Returns new itemstack with material, amount, data, and name custom
   public static ItemStack createItem(Material material, int amount, int dataValue, String itemName) {
      ItemStack itemStack = new ItemStack(material, amount, (short) dataValue);
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setDisplayName(color(itemName));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }

   // creates a new itemstack very specific allows for amount, type, data
   // enchanted, name and lore to be changed
   public static ItemStack createItem(Material m, int amount, byte data, boolean enchanted, String name, String... lore) {
      ItemStack item = new ItemStack(m, amount, data);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(color(name));
      if(enchanted) {
         meta.addEnchant(Enchantment.getByName("DURABILITY"), 1, true);
         meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      }
      meta.setLore(Arrays.asList(colorBulk(lore)));
      item.setItemMeta(meta);
      return item;
   }

   // Returns a new Itemstack with material, amount, data, item, and lore custom
   public static ItemStack createItem(Material material, int amount, int dataValue, String itemName, List<String> itemLore) {
      ItemStack itemStack = new ItemStack(material, amount, (short) dataValue);
      ItemMeta itemMeta = itemStack.getItemMeta();
      return updateItemStack(itemName, itemLore, itemStack, itemMeta);
   }

   public static ItemStack createItem(Material m, int amount, byte data, String name, String... lore) {
      ItemStack item = new ItemStack(m, amount, data);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(color(name));
      meta.setLore(Arrays.asList(colorBulk(lore)));
      item.setItemMeta(meta);
      return item;
   }

   public static ItemStack applyLore(ItemStack item, String... lore) {
      ItemMeta itemMeta = item.getItemMeta();
      List<String> itemLore = Arrays.asList(colorBulk(lore));
      itemMeta.setLore(itemLore);
      item.setItemMeta(itemMeta);
      return item;
   }

   public static ItemStack appendToLore(ItemStack itemStack, String... addedLines){
      ItemMeta itemMeta = itemStack.getItemMeta();
      List<String> itemLore = itemStack.getItemMeta().getLore();
      colorBulk(itemLore);
      itemLore.addAll(Arrays.asList(colorBulk(addedLines)));
      itemMeta.setLore(itemLore);
      itemStack.setItemMeta(itemMeta);
      return itemStack;
   }
   
   public static ItemStack changeLine(ItemStack itemStack, int line, String newLine){
      ItemMeta im = itemStack.getItemMeta();
      List<String> lore = im.getLore();
      if(line >= lore.size()){
         while(line >= lore.size()) lore.add("");
         lore.set(line, newLine);
      }
      else
         lore.set(line, newLine);
      im.setLore(lore);
      itemStack.setItemMeta(im);
      return itemStack;
   }

   public static ItemStack setDisplayName(ItemStack itemStack, String name){
      ItemMeta m = itemStack.getItemMeta();
      m.setDisplayName(color(name));
      itemStack.setItemMeta(m);
      return itemStack;
   }

   public static String getStrippedLoreLine(ItemStack itemStack, int line){
      if(itemStack.hasItemMeta()){
         List<String> lore = itemStack.getItemMeta().getLore();
         if(line < lore.size())
            return ChatColor.stripColor(lore.get(line));
      }
      return "";
   }

}


