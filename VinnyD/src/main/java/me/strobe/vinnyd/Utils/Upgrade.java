package me.strobe.vinnyd.Utils;

import lombok.Getter;
import lombok.Setter;
import me.strobe.vinnyd.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Upgrade")
public class Upgrade implements ConfigurationSerializable {

   @Getter @Setter private ItemStack resultItem;
   @Getter @Setter private double moneyPrice;
   @Getter @Setter private int oddCurrencyPrice;
   @Getter @Setter private List<ItemStack> requiredItems; // one of these should be the base item.

   //if cs not exist deny.
   //Need to pull item_name + t(n)
   // if exist, then pull requirements
   // if not, dont allow
   // if has take items, return new item
   public Upgrade(String weaponToUpgradeTo, double moneyPrice, int oddCurrencyPrice, ItemStack... requiredItems){
      this.resultItem = Main.getMain().getCsUtil().generateWeapon(weaponToUpgradeTo);
      if(this.resultItem == null) throw new IllegalArgumentException("Weapon not found: " + weaponToUpgradeTo);
      this.moneyPrice = moneyPrice;
      this.oddCurrencyPrice = oddCurrencyPrice;
      this.requiredItems = Arrays.asList(requiredItems);
   }

   public Upgrade(Map<String, Object> dict){
      resultItem        = (ItemStack) dict.get("resultItem");
      if(this.resultItem == null) throw new IllegalArgumentException("Resulting Weapon is null after serialziation.");
      if(Main.getMain().getCsUtil().getWeaponTitle(resultItem) == null) throw new IllegalArgumentException("Resulting Weapon is not in crackshot's weapons folder!");
      moneyPrice        = (double) dict.get("moneyPrice");
      oddCurrencyPrice  = (int) dict.get("oddCurrencyPrice");
      requiredItems     = (List<ItemStack>) dict.get("requiredItems");
   }

   public static Upgrade deserialize(Map<String, Object> dict){
      return new Upgrade(dict);
   }


   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> x = new HashMap<>();
      x.put("resultItem", resultItem);
      x.put("moneyPrice", moneyPrice);
      x.put("oddCurrencyPrice", oddCurrencyPrice);
      x.put("requiredItems", requiredItems);
      return x;
   }



}
