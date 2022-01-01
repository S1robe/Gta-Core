package me.Strobe.Core.Utils.Looting;

import lombok.Getter;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SerializableAs("LootBag")
public class WeightedRandomBag<T> {

   final List<Entry> entries = new ArrayList<>();
   List<T> innerObjects = new ArrayList<>();
   private double accumulatedWeight = 0;

   public WeightedRandomBag(){}

   public WeightedRandomBag(Map<String, Object> dict){
      //TOdo: doesnt like beign deseralized
   }

   public boolean addEntry(T item, double weight) {
      if(!innerObjects.contains(item)) {
         accumulatedWeight += weight;
         innerObjects.add(item);
         return entries.add(new Entry(item, weight, accumulatedWeight));
      }
      return false;
   }

   public boolean removeEntry(Entry e){
      int idx = entries.indexOf(e);
      if(idx != -1) {
         entries.remove(idx);
         innerObjects.remove(idx);
         double weight = e.weight;
         while(idx < entries.size())
            entries.get(idx++).accumulatedWeight -= weight;
         return true;
      }
      return false;
   }

   public boolean removeEntryByInner(T inner){
      int idx = innerObjects.indexOf(inner);
      if(idx != -1) {
         innerObjects.remove(idx);
         double weight = entries.get(idx).weight;
         entries.remove(idx);
         while(idx < entries.size())
            entries.get(idx++).accumulatedWeight -= weight;
         return true;
      }
      return false;
   }

   public Entry getEntryByInner(T inner){
      return entries.get(innerObjects.indexOf(inner));
   }

   public T getRandom() {
      double r = ThreadLocalRandom.current().nextDouble() * accumulatedWeight;
      for(Entry entry : entries)
         if(entry.accumulatedWeight >= r)
            return entry.object;
      return null;
   }

   public double getTotalWeight(){
      return accumulatedWeight;
   }

   public List<T> getInnerObjects() {return innerObjects;}

   public List<Entry> getEntries(){
      return entries;
   }

   public void clear() {
      accumulatedWeight = 0;
      entries.clear();
      innerObjects.clear();
   }

   public int size() {
      return entries.size();
   }

//   @Override
//   public Map<String, Object> serialize() {
//      //TOdo: doesnt like beign deseralized
//   }

  // @SerializableAs("LootEntry")
   public class Entry {
      @Getter
      private final T object;
      private double accumulatedWeight;
      private final double weight;

      Entry(T item, double initialWeight,  double accumulatedWeight) {
         object = item;
         this.accumulatedWeight = accumulatedWeight;
         weight = initialWeight;
      }
//
//      public Entry(Map<String, Object> dict){
//         //TOdo: doesnt like beign deseralized
//      }
//
//      @Override
//      public Map<String, Object> serialize() {
//         Map<String, Object> dict = new HashMap<>();
//         dict.put("obj", object);
//         dict.put("accumulatedWeight", accumulatedWeight);
//         dict.put("weight", weight);
//         return dict;
//      }


   }


}