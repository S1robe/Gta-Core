package me.Strobe.Core.Utils.Looting;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class WeightedRandomBag1 {

   public final List<Entry> entries = new ArrayList<>();
   public final List<LootItem> innerObjects = new ArrayList<>();
   private double accumulatedWeight;
   private Random rand = new Random();

   public boolean addEntry(LootItem item) {
      if(!innerObjects.contains(item)) {
         accumulatedWeight += item.getWeight();
         innerObjects.add(item);
         return entries.add(new Entry(item, accumulatedWeight));
      }
      return false;
   }

   public void removeEntry(ItemStack displayItem) {
      removeEntry(getItemByDisplay(displayItem));
   }

   public void removeEntry(LootItem item) {
      Entry e = getEntryByItem(item);
      int i = entries.indexOf(e);
      entries.remove(i);
      for(; i < entries.size(); i++)
         entries.get(i).accumulatedWeight -= item.getWeight();
      innerObjects.remove(item);
   }

   public LootItem getItemByDisplay(ItemStack displayItem) {
      for(LootItem o : innerObjects)
         if(o.getDisplayItem().equals(displayItem))
            return o;
      return null;
   }

   public LootItem getItemByRep(ItemStack item) {
      for(LootItem o : innerObjects)
         if(o.getItem().isSimilar(item))
            return o;
      return null;
   }

   public Entry getEntryByItem(LootItem item) {
      for(Entry entry : entries)
         if(entry.object == item)
            return entry;
      return null;
   }

   public LootItem getRandom() {
      double r = rand.nextDouble() * accumulatedWeight;
      for(Entry entry : entries) {
         if(entry.accumulatedWeight >= r) {
            return entry.object;
         }
      }
      return null;
   }

   public double getAccumulatedWeight(){
      return accumulatedWeight;
   }

   public void forEach(Consumer<? super Entry> consumer) throws NullPointerException {
      entries.forEach(consumer);
   }

   public void clear() {
      accumulatedWeight = 0;
      entries.clear();
      innerObjects.clear();
      rand = new Random();
   }

   public int size() {
      return entries.size();
   }

   public static class Entry {

      @Getter
      private final LootItem object;
      private double accumulatedWeight;

      Entry(LootItem item, double accumulatedWeight) {
         object = item;
         this.accumulatedWeight = accumulatedWeight;
      }
   }


}