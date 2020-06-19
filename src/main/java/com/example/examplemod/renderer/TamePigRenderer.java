package com.example.examplemod.renderer;

import com.example.examplemod.entity.TamePigEntity;
import com.example.examplemod.model.TamePigModel;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//@OnlyIn(Dist.CLIENT)
public class TamePigRenderer extends MobRenderer<TamePigEntity, TamePigModel<TamePigEntity>> {
   private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

   public TamePigRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TamePigModel<>(), 0.7F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TamePigEntity entity) {
      return PIG_TEXTURES;
   }
}