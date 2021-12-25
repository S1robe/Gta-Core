package me.Strobe.Core.Utils.Looting;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.GenUtils;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SerializableAs("Loot")
public class LootItem implements ConfigurationSerializable {
   private double weight = 0;
   private int min = 0;
   private int max = 0;
   private ItemStack item = ItemUtils.createItem(Material.AIR);
   private transient ItemStack displayItem = null;

   private LootItem(){}

   public static LootItem fillerItem(){return new LootItem(ItemUtils.createItem(Material.AIR), 0, 0, 0);}

   public LootItem(ItemStack itemRepresentation, int minAmt, int maxAmt, double weight) {
      this.item = itemRepresentation;
      this.min = minAmt;
      this.max = maxAmt;
      this.weight = weight;
      displayItem = ItemUtils.applyLore(item.clone(), "§aWeight: §7" + weight,
                                                                "§eMax Drop: §7" + max,
                                                                "§cMin Drop: §7" + min);
   }

   protected LootItem(Map<String, Object> serialized) {
      this.weight = (double) serialized.get("Weight");
      this.min = (int) serialized.get("Min");
      this.max = (int) serialized.get("Max");
      this.item = (ItemStack) serialized.get("ItemStack");
      this.displayItem = ItemUtils.applyLore(item.clone(), "§aWeight: §7" + weight, "§eMax Drop: §7" + max, "§cMin Drop: §7" + min);
   }

   public static LootItem deserialize(Map<String, Object> serialized) {
      double weight = (double) serialized.get("Weight");
      int min = (int) serialized.get("Min");
      int max = (int) serialized.get("Max");
      ItemStack item = (ItemStack) serialized.get("ItemStack");
      return new LootItem(item, min, max, weight);
   }

   public ItemStack getRandom() {
      ItemStack x = this.item.clone();
      x.setAmount(GenUtils.getRandInt(min, max));
      return x;
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> serialized = new HashMap<>();
      serialized.put("ItemStack", item);
      serialized.put("Min", min);
      serialized.put("Max", max);
      serialized.put("Weight", weight);
      return serialized;
   }

   @Override
   public boolean equals(Object o) {
      if(o == null)
         return false;
      if(o.getClass().equals(this.getClass())) {
         LootItem other = (LootItem) o;
         return other.weight == weight && other.min == min && other.max == max && other.displayItem.isSimilar(displayItem);
      }
      return false;
   }

   public LootItem clone() {
      LootItem clone;
      try {
         clone = (LootItem) super.clone();
      }
      catch(CloneNotSupportedException e) {
         e.printStackTrace();
         return new LootItem(item, min, max, weight);
      }
      clone.setItem(item);
      clone.setMax(max);
      clone.setMin(min);
      clone.setWeight(weight);
      return clone;
   }
}
