package me.Strobe.Core.Utils.Entity;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public enum CustomEntityType {

   ENDERMAN("Enderman", 58, EntityType.ENDERMAN, EntityEnderman.class, CustomEnderman.class);

   private final String name;
   private final int id;
   private final EntityType entityType;
   private final Class<? extends EntityInsentient> nmsClass;
   private final Class<? extends EntityInsentient> customClass;

   CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
      this.name = name;
      this.id = id;
      this.entityType = entityType;
      this.nmsClass = nmsClass;
      this.customClass = customClass;
   }

   public String getName(){
      return this.name;
   }

   public int getID(){
      return this.id;
   }

   public EntityType getEntityType(){
      return this.entityType;
   }

   public Class<? extends EntityInsentient> getNMSClass(){
      return this.nmsClass;
   }

   public Class<? extends EntityInsentient> getCustomClass(){
      return this.customClass;
   }

   public static void registerEntities(){
      for (CustomEntityType entity : values()){
         try{
            //Unregister normal enderman
            Field c = EntityTypes.class.getDeclaredField("c");
            c.setAccessible(true);
            Map<String, Class<? extends Entity>> registeredEntities = (Map<String, Class<? extends Entity>>) c.get("c");
            registeredEntities.remove(entity.getName());

            //Unregister normal enderman
            Field e = EntityTypes.class.getDeclaredField("e");
            e.setAccessible(true);
            Map<Integer, Class<? extends Entity>> registeredEntitiesID = (Map<Integer, Class<? extends Entity>>) e.get("e");
            registeredEntitiesID.remove(entity.getID());

            //register new enderman
            Method a = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            a.setAccessible(true);
            a.invoke(null, entity.getCustomClass(), entity.getName(), entity.getID());

         }catch (Exception e){
            e.printStackTrace();
         }
      }
      for (BiomeBase biomeBase : BiomeBase.getBiomes()){
         if (biomeBase == null){
            break;
         }

         for (String field : new String[]{"at", "au", "av", "aw"}){
            try{
               Field list = BiomeBase.class.getDeclaredField(field);
               list.setAccessible(true);
               @SuppressWarnings("unchecked")
               List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list.get(biomeBase);

               for (BiomeBase.BiomeMeta meta : mobList){
                  for (CustomEntityType entity : values()){
                     if (entity.getNMSClass().equals(meta.b)){
                        meta.b = entity.getCustomClass();
                     }
                  }
               }
            }catch (Exception e){
               e.printStackTrace();
            }
         }
      }
   }

}
