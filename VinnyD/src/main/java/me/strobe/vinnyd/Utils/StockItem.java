package me.strobe.vinnyd.Utils;

import lombok.Getter;
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


    /**
     * This is represents an item that Vinny may sell for a specific combination of optional oddCurrency and in game money
     * The item provided should match identically what the player will receive.
     *
     * @param item                      The Item the player will recieve upon purchase
     * @param moneyPrice                The fiscal currency cost of this item (Optional) set to 0 if not needed
     * @param oddCurrencyPrice          The Odd currency price of this item. (Otional) set to 0 if not needed
     * @param weightToBeChosenForSale   The weight of this item relative to every other potential item: The bigger the
     *                                  number the more frequent.
     */
   public StockItem(ItemStack item, double moneyPrice, int oddCurrencyPrice, double weightToBeChosenForSale){
        this.actualItem = item;
        this.moneyPrice = moneyPrice;
        this.oddCurrencyPrice = oddCurrencyPrice;
        this.weightToBeChosenForSale = weightToBeChosenForSale;

        this.representation = ItemUtils.applyLore(item.clone(), "&aPrice: " + moneyPrice, "&eOdd Currency Required: " + oddCurrencyPrice);
   }

   public StockItem(Map<String, Object> x){
       actualItem              = (ItemStack) x.get("actualItem");
       moneyPrice              = (double) x.get("moneyPrice");
       oddCurrencyPrice        = (int) x.get("oddCurrencyPrice");
       weightToBeChosenForSale = (double) x.get("weightToBeChosenForSale");

       representation = ItemUtils.applyLore(actualItem.clone(), "&aPrice: " + moneyPrice, "&eOdd Currency Required: " + oddCurrencyPrice);
   }

   public static StockItem deserialize(Map<String, Object> x){
       return new StockItem(x);
   }


   @Override
   public Map<String, Object> serialize() {
        Map<String, Object> x = new HashMap<>();
        x.put("actualItem", actualItem);
        x.put("moneyPrice", moneyPrice);
        x.put("oddCurrencyPrice", oddCurrencyPrice);
        x.put("weightToBeChosenForSale", weightToBeChosenForSale);
        return x;
   }




}
