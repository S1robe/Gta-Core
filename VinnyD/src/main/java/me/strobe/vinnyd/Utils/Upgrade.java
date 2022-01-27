package me.strobe.vinnyd.Utils;

import lombok.Getter;
import lombok.Setter;
import me.strobe.vinnyd.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@SerializableAs("Upgrade")
public class Upgrade implements ConfigurationSerializable {

   @Getter @Setter private ItemStack resultItem;
   @Getter @Setter private double moneyPrice;
   @Getter @Setter private int oddCurrencyPrice;
   @Getter @Setter private ItemStack[] requiredItems; // one of these should be the base item.

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
      this.requiredItems = requiredItems;
   }

   @Override
   public Map<String, Object> serialize() {
      return null;
   }



}
