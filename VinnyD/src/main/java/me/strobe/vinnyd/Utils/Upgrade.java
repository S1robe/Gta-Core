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

   /**
    * This represents the upgrade from some items to a CS weapon.
    * @apiNote The parameter {@param weaponToUpgradeTo} must exist with in CrackShot's internal files otherwise this
    *   Object will not be made.
    *
    * @param weaponToUpgradeTo   The weapon will recieve after upgrading
    * @param moneyPrice          The fiscal price of this upgrade
    * @param oddCurrencyPrice    The Odd Currency price of this upgrade.
    * @param requiredItems       The complete list of items that this upgrade will require.
    *
    * @apiNote The {@param requiredItems} size should be no more than 36, the size of a standard player inventory.
    */
   public Upgrade(String weaponToUpgradeTo, double moneyPrice, int oddCurrencyPrice, ItemStack... requiredItems){
      this.resultItem = Main.getMain().getCsUtil().generateWeapon(weaponToUpgradeTo);
      if(this.resultItem == null) throw new IllegalArgumentException("Weapon not found: " + weaponToUpgradeTo);

      this.moneyPrice = moneyPrice;
      this.oddCurrencyPrice = oddCurrencyPrice;
      this.requiredItems = Arrays.asList(requiredItems);
   }

   public Upgrade(Map<String, Object> x){
      String weaponToUpgradeTo = (String) x.get("weaponToUpgradeTo");

      this.resultItem          = Main.getMain().getCsUtil().generateWeapon(weaponToUpgradeTo);
      if(this.resultItem == null) throw new IllegalArgumentException("Weapon not found: " + weaponToUpgradeTo);

      this.moneyPrice          = (double) x.get("moneyPrice");
      this.oddCurrencyPrice    = (int) x.get("oddCurrency");
      this.requiredItems       = (List<ItemStack>) x.get("requiredItems");
   }

   public static Upgrade deserialize(Map<String, Object> x){
      return new Upgrade(x);
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> x = new HashMap<>();
      x.put("weaponToUpgradeTo", Main.getMain().getCsUtil().getWeaponTitle(resultItem));
      x.put("moneyPrice", moneyPrice);
      x.put("oddCurrency", oddCurrencyPrice);
      x.put("requiredItems", requiredItems);
      return x;
   }
}
