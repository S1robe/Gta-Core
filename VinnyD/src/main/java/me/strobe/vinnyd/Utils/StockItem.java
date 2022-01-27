package me.strobe.vinnyd.Utils;

import lombok.Getter;
import lombok.Setter;
import me.Strobe.Core.Utils.ItemUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


@SerializableAs("StockItem")
public class StockItem implements ConfigurationSerializable {

   @Getter private final ItemStack actualItem;
   @Getter private final ItemStack representation;
   @Getter @Setter private double moneyPrice;
   @Getter @Setter private int oddCurrencyPrice;
   @Getter @Setter private double weightToBeChosenForSale;

   public StockItem(ItemStack item, double moneyPrice, int oddCurrencyPrice, double weightToBeChosenForSale){
        this.actualItem = item;
        this.moneyPrice = moneyPrice;
        this.oddCurrencyPrice = oddCurrencyPrice;
        this.weightToBeChosenForSale = weightToBeChosenForSale;

        this.representation = ItemUtils.applyLore(item.clone(), "&aPrice: " + moneyPrice, "&eOdd Currency Required: " + oddCurrencyPrice);
   }

   @Override
   public Map<String, Object> serialize() {
      return null;
   }




}
