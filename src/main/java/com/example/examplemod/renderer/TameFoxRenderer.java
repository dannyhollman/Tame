package com.example.examplemod.renderer;

import com.example.examplemod.entity.TameFoxEntity;
import com.example.examplemod.model.TameFoxModel;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//@OnlyIn(Dist.CLIENT)
public class TameFoxRenderer extends MobRenderer<TameFoxEntity, TameFoxModel<TameFoxEntity>> {
   private static final ResourceLocation FOX = new ResourceLocation("textures/entity/fox/fox.png");
   private static final ResourceLocation SLEEPING_FOX = new ResourceLocation("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX = new ResourceLocation("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SLEEPING_SNOW_FOX = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

   public TameFoxRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TameFoxModel<>(), 0.4F);
   }

   protected void applyRotations(TameFoxEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      if (entityLiving.func_213480_dY() || entityLiving.isStuck()) {
         float f = -MathHelper.lerp(partialTicks, entityLiving.prevRotationPitch, entityLiving.rotationPitch);
         matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f));
      }

   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TameFoxEntity entity) {
      if (entity.getVariantType() == TameFoxEntity.Type.RED) {
         return entity.isSleeping() ? SLEEPING_FOX : FOX;
      } else {
         return entity.isSleeping() ? SLEEPING_SNOW_FOX : SNOW_FOX;
      }
   }
}