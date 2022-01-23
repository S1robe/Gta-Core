package me.Strobe.Core.Utils.Entity;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.World;

public class CustomEnderman extends EntityEnderman {
   public CustomEnderman(World world) {
      super(world);
   }

   @Override
   protected boolean n() {
      double d0 = this.locX + (this.random.nextDouble() - 0.5D) * 64.0D;
      double d1 = this.locY + (double)(this.random.nextInt(64) - 32);
      double d2 = this.locZ + (this.random.nextDouble() - 0.5D) * 64.0D;
      return this.k(d0, d1, d2);
   }

   @Override
   protected boolean b(Entity entity) {
      Vec3D vec3d = new Vec3D(this.locX - entity.locX, this.getBoundingBox().b + (double)(this.length / 2.0F) - entity.locY + (double)entity.getHeadHeight(), this.locZ - entity.locZ);
      vec3d = vec3d.a();
      double d0 = 16.0D;
      double d1 = this.locX + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.a * d0;
      double d2 = this.locY + (double)(this.random.nextInt(16) - 8) - vec3d.b * d0;
      double d3 = this.locZ + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.c * d0;
      return this.k(d1, d2, d3);
   }

//   @Override
//   public boolean damageEntity(DamageSource damagesource, float f) {
//      EntityDamageByEntityEvent damaged = new EntityDamageByEntityEvent(damagesource.getEntity(), this, EntityDamageEvent.DamageCause.CUSTOM, (double) f);
//      this.world.getServer().getPluginManager().callEvent(damaged);
//   }
}
