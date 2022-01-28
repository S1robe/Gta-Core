//package me.Strobe.Core.Utils.Looting;
//
//import lombok.Getter;
//import org.bukkit.configuration.serialization.ConfigurationSerializable;
//import org.bukkit.configuration.serialization.SerializableAs;
//
//import java.util.*;
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * An Implementaiton of a Loot Table, supports full modification.
// */
//@SerializableAs("LootBag")
//public class WeightedRandomBag2 implements ConfigurationSerializable {
//
//   /**
//    * The list of entries to be drawn from, these should not be modified
//    */
//   private final List<Entry<? extends ConfigurationSerializable>> entries = new ArrayList<>();
//   /**
//    * The inner references to each entry, used to find the exact entry within the table
//    */
//   @Getter private final List<ConfigurationSerializable> innerObjects = new ArrayList<>();
//   /**
//    * The total accumulated weight, used to draw from the pool
//    */
//   @Getter private double accumulatedWeight = 0;
//
//   /**
//    * Default constructor for empty initialzation
//    */
//   public WeightedRandomBag2(){}
//
//   /**
//    * This constructor is to be used during deseralization, but can also be used if you
//    * are certain that the object provided by the map and 'T' are the same type object.
//    *
//    * @param dict The entries maplist to be constructed
//    */
//   public WeightedRandomBag2(Map<String, Object> dict) {
//      ((List<?>) dict.get("entries")).forEach(map -> {
//         Entry<? extends ConfigurationSerializable> x = (Entry<? extends ConfigurationSerializable>) map;
//         entries.add(x);
//         innerObjects.add(x.object);
//      });
//      accumulatedWeight = (double) dict.get("accumulatedWeight");
//   }
//
//   public static WeightedRandomBag2 deserialize(Map<String, Object> dict){
//      return new WeightedRandomBag2(dict);
//   }
//
//   /**
//    * Adds an entry to the loot table
//    *
//    * @apiNote No two objects can be the same.
//    * @apiNote You may add at most one null value to the table, although this seems counterintuitive
//    *
//    * @param item    The object we are adding to the table
//    * @param weight  The weight of the object in the table
//    * @return        If the object was successfully added to the table
//    */
//   public boolean addEntry(ConfigurationSerializable item, double weight) {
//      if(!innerObjects.contains(item)) {
//         accumulatedWeight += weight;
//         innerObjects.add(item);
//         return entries.add(new Entry(item, weight, accumulatedWeight));
//      }
//      return false;
//   }
//
//   /**
//    * Removes an entry from this loot table
//    *
//    * @param e    The entry to be removed from the table
//    * @return     If the entry was removed or not (also specifies if
//    *                the table held the item previously
//    */
//   public boolean removeEntry(Entry e){
//      int idx = entries.indexOf(e);
//      if(idx != -1) {
//         entries.remove(idx);
//         innerObjects.remove(idx);
//         double weight = e.weight;
//         while(idx < entries.size())
//            entries.get(idx++).accumulatedWeight -= weight;
//         return true;
//      }
//      return false;
//   }
//
//   /**
//    * This method is used when you do not have access or an easy way to identify an entry
//    * It will compare the inner objects with x.equals(inner), using this implementation it is
//    * wise to override the Object#equals() method to facillitate this process otherwise you
//    * may get false positives.
//    *
//    * @param inner   A copy of or psuedo item that is close enough for x.equals(inner) to be true.
//    * @return        If the inner object was found and removed.
//    */
//   public boolean removeEntryByInner(ConfigurationSerializable inner){
//      int idx = innerObjects.indexOf(inner);
//      if(idx != -1) {
//         innerObjects.remove(idx);
//         double weight = entries.get(idx).weight;
//         entries.remove(idx);
//         while(idx < entries.size())
//            entries.get(idx++).accumulatedWeight -= weight;
//         return true;
//      }
//      return false;
//   }
//
//   /**
//    * This method is used when you do not have access or an easy way to identify an entry
//    * It will compare the inner objects with x.equals(inner), using this implementation it is
//    * wise to override the Object#equals() method to facillitate this process otherwise you
//    * may get false positives.
//    *
//    * @param inner A copy of or psuedo item that is close enough for x.equals(inner) to be true.
//    * @return      The direct entry reference from the loottable,
//    */
//   public Entry<? extends ConfigurationSerializable> getEntryByInner(ConfigurationSerializable inner){
//      return entries.get(innerObjects.indexOf(inner));
//   }
//
//   /**
//    * Used to return a random value from the loot table, loot "drops" are determined on a weighted system as implied by
//    * the name of this class. This is most similar to how a raffel works.
//    *
//    * @return  An object stored in this table.
//    */
//   public Object getRandom() {
//      double r = ThreadLocalRandom.current().nextDouble() * accumulatedWeight;
//      for(Entry<? extends ConfigurationSerializable> entry : entries)
//         if(entry.accumulatedWeight >= r)
//            return entry.object;
//      return null;
//   }
//
//   /**
//    * Similar to WeightedRandomBag2#getRandom, this will return a number of items from the bag on a weighted system
//    * Items pulled are not guaranteed to be unique.
//    *
//    * @param amt  The amount of items to draw from the pool.
//    * @return     The list of items drawn from the pool
//    */
//   public List<? extends ConfigurationSerializable> getRandomAmt(int amt){
//      List<? extends ConfigurationSerializable> hand = new ArrayList<>();
//      while(hand.size() < amt)
//         hand.add(getRandom());
//      return hand;
//   }
//
//   /**
//    * Similar to WeightedRandomBag2#getRandomAmt, this will also return a number of items from the bag on a weighted
//    * system. However, this guarantees uniqueness between items so that no two items fulfill the predicate : x.equals(y)
//    *
//    * @param amt The amount of unique items to draw from the bag
//    * @return     The list of unique items.
//    */
//   public List<? extends ConfigurationSerializable> getRandomAmtUnique(int amt){
//      Set<? extends ConfigurationSerializable> hand = new HashSet<>();
//      while(hand.size() < amt)
//         hand.add(getRandom());
//      return new ArrayList<>(hand);
//   }
//
//   /**
//    * Clears the current entries, and accumualated weight of this bag.
//    */
//   public void clear() {
//      accumulatedWeight = 0;
//      entries.clear();
//      innerObjects.clear();
//   }
//
//   /**
//    * @return The current size of the bag, also known as the number of entries present in the table.
//    */
//   public int size() { return entries.size();}
//
//   @Override
//   public Map<String, Object> serialize() {
//      Map<String, Object> x = new HashMap<>();
//      x.put("entries", entries);
//      x.put("accumulatedWeight", accumulatedWeight);
//      return x;
//   }
//
//   @SerializableAs("LootEntry")
//   public static class Entry<T extends ConfigurationSerializable> implements ConfigurationSerializable{
//      @Getter private final T object;
//      @Getter private double accumulatedWeight;
//      @Getter private final double weight;
//
//      private Entry(T item, double initialWeight,  double accumulatedWeight) {
//         this.object            = item;
//         this.accumulatedWeight = accumulatedWeight;
//         this.weight            = initialWeight;
//      }
//
//      public Entry(Map<String, Object> dict){
//         this.object            = (T) dict.get("obj");
//         this.accumulatedWeight = (double) dict.get("accumulatedWeight");
//         this.weight            = (double) dict.get("weight");
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
//   }
//
//
//}