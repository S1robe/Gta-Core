package me.strobe.bankrobberies.utils;

import lombok.Getter;
import me.Strobe.Core.Utils.ItemUtils;
import me.Strobe.Core.Utils.Looting.LootItem;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("RobItem")
public class RobItem extends LootItem implements ConfigurationSerializable {

   @Getter
   private List<String> commands = new ArrayList<>();

   public RobItem(ItemStack itemRepresentation, int minAmt, int maxAmt, double weight, String cmds) {
      super(itemRepresentation, minAmt, maxAmt, weight);
      ItemStack x =getDisplayItem();
      if(cmds != null){
         String[] parsed = cmds.split("\\|");
         ItemUtils.appendToLore(x, "&6Commands:");
         for(String s : parsed) {
            ItemUtils.appendToLore(x, s);
            commands.add(s);
         }
      }
      setDisplayItem(x);
   }


   public RobItem(Map<String, Object> dict){
      super(dict);
      this.commands = (List<String>) dict.get("commands");
   }

   public static RobItem deserialize(Map<String, Object> dict){
      return new RobItem(dict);
   }

   public boolean isCommand(){
      return this.commands.isEmpty();
   }

   @Override
   public Map<String, Object> serialize(){
      Map<String, Object> x = super.serialize();
      x.put("commands", commands);
      return x;
   }
}
