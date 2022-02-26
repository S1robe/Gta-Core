package me.Strobe.Core.Utils;

import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.Strobe.Core.Utils.StringUtils.color;
import static me.Strobe.Core.Utils.StringUtils.repeat;

public final class GenUtils {

   private GenUtils() {}

   /**
    * generate a random number between 2 defined integers
    *
    * @param min minimum minimum number
    * @param max maximum random number
    *
    * @return random number
    */
   public static int getRandInt(int min, int max) {
      return (int) ((Math.random() * ((max - min) + 1)) + min);
   }

   public static double getRandDouble(double min, double max){
      return ((Math.random() * ((max - min) + 1)) + min);
   }

   // Fills all slots of a premade inventory.
   // Best to call first then overwrite slots. with custom items.
   public static void fill(Inventory inventory, ItemStack itemStack) {
      for(int i = 0; i < inventory.getSize(); i++) {
         inventory.setItem(i, itemStack);
      }
   }


   public static void setMiddle(Inventory inv, ItemStack filler, List<ItemStack> items){
      short loops = (short) (inv.getSize() / 9);
      for(int l = 0; l < loops; l++) {
         for(int i = 0, s = (9 * l) + 3; s < 9 * l + 7; s++, i++) {
            if(i >= items.size())
               inv.setItem(s, filler);
            else
               inv.setItem(s, items.get(i));
         }
      }
   }

   // overwrites the middle column of each row of a given inventory.
   public static void setMiddle(Inventory inventory, ItemStack itemStack) {
      for(int i = 4; i < inventory.getSize(); i = i + 9) {
         inventory.setItem(i, itemStack);
      }
   }

   public static void setLeft(Inventory inv, ItemStack filler, ArrayList<ItemStack> items) {
      short loops = (short) (inv.getSize() / 9);
      for(int l = 0; l < loops; l++) {
         for(int i = 0, s = 9 * l; s < 9 * l + 4; s++, i++) {
            if(i >= items.size())
               inv.setItem(s, filler);
            else
               inv.setItem(s, items.get(i));
         }
      }
   }

   public static void setRight(Inventory inv, ItemStack filler, ArrayList<ItemStack> items) {
      short loops = (short) (inv.getSize() / 9);
      for(int l = 0; l < loops; l++) {
         for(int i = 0, s = (9 * l) + 5; s < (9 * l) + 9; s++, i++) {
            if(i >= items.size())
               inv.setItem(s, filler);
            else
               inv.setItem(s, items.get(i));
         }
      }
   }

   /**
    * create a progress bar to show progress between 2 numbers
    *
    * @param current           number that will be used for percent
    * @param max               max amount that current can get to before 100%
    * @param totalBars         total amount of bars in the string
    * @param symbol            bar symbol
    * @param completedColor    completed color
    * @param notCompletedColor not completed color
    *
    * @return progress bar
    */
   public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {

      float percent = (float) current / max;

      int progressBars = (int) (totalBars * percent);

      int leftOver = (totalBars - progressBars);

      String sb = color( completedColor + repeat(symbol, Math.max(0, progressBars)) + notCompletedColor + repeat(symbol, Math.max(0, leftOver)));

      NumberFormat nf = NumberFormat.getNumberInstance();
      nf.setMaximumFractionDigits(0);
      String rounded = nf.format(percent * 100);
      return sb + color("&f&l " + rounded + "%");
   }

   /**
    * This method will construct from two potentially Different Collections a Mapping between the first arg and the second
    * This map is guaranteed to be of size (n*2) where n is either arg's size
    *
    * @apiNote          The sizes of both collections must be the same, null elements are not allowed.
    *
    * @param keys       The keys of the new map
    * @param values     The values of the new map
    * @param <K>        The key type parameter
    * @param <V>        The value type parameter
    * @return           A Map: Map<K, V>, a pairing of both key  and value collections into one map.
    *
    */
   public static <K, V> Map<K, V> mapFromTwoCollections(Collection<K> keys, Collection<V> values){
      Validate.isTrue(keys.size() == values.size());
      Validate.noNullElements(keys);
      Validate.noNullElements(values);
      Iterator<K> keyIterator = keys.iterator();
      Iterator<V> valIterator = values.iterator();
      return IntStream.range(0, keys.size()).boxed()
              .collect(Collectors.toMap($i -> keyIterator.next(), $i -> valIterator.next()));
   }

}
