package com.example.examplemod.renderer;

import com.example.examplemod.entity.TameCowEntity;
import com.example.examplemod.model.TameCowModel;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class TameCowRenderer extends MobRenderer<TameCowEntity, TameCowModel<TameCowEntity>> {
	   private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

	   public TameCowRenderer(EntityRendererManager renderManagerIn) {
	      super(renderManagerIn, new TameCowModel<>(), 0.7F);
	   }

	   /**
	    * Returns the location of an entity's texture.
	    */
	   public ResourceLocation getEntityTexture(TameCowEntity entity) {
	      return COW_TEXTURES;
	   }
	}