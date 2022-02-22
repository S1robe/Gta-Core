package me.strobe.vinnyd.Utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


@SerializableAs("StockItem")
public class StockItem implements ConfigurationSerializable {

   @Getter private final ItemStack actualItem;
   @Getter private final ItemStack representation;
   @Getter @Setter private double moneyPrice;
   @Getter @Setter private int oddCurrencyPrice;
   @Getter @Setter private double weightToBeChosenForSale;

   public StockItem(@NonNull ItemStack item, double moneyPrice, int oddCurrencyPrice, double weightToBeChosenForSale){
        this.actualItem = item;
        this.moneyPrice = moneyPrice;
        this.oddCurrencyPrice = oddCurrencyPrice;
        this.weightToBeChosenForSale = weightToBeChosenForSale;

        this.representation = ItemUtils.applyLore(item.clone(), "&aPrice: " + moneyPrice, "&eOdd Currency Required: " + oddCurrencyPrice);
   }

   public StockItem(Map<String, Object> dict){
      actualItem               = (ItemStack) dict.get("actualItem");
      representation           = (ItemStack) dict.get("representation");
      moneyPrice               = (double) dict.get("moneyPrice");
      oddCurrencyPrice         = (int) dict.get("oddCurrencyPrice");
      weightToBeChosenForSale  = (double) dict.get("weightToBeChosenForSale");
   }

   public static StockItem deserialize(Map<String, Object> dict){
      return new StockItem(dict);
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> x = new HashMap<>();
      x.put("actualItem", actualItem);
      x.put("representation", representation);
      x.put("moneyPrice", moneyPrice);
      x.put("oddCurrencyPrice", oddCurrencyPrice);
      x.put("weightToBeChosenForSale", weightToBeChosenForSale);
      return x;
   }




}
