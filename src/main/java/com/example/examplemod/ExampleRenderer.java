package com.example.examplemod;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class ExampleRenderer extends LivingRenderer<ExampleEntity, ExampleModel>
{
	public ExampleRenderer(EntityRendererManager manager) 
	{
		super(manager, new ExampleModel(), 0f);
	}

	public static class RenderFactory implements IRenderFactory<ExampleEntity>
	{
		@Override
		public EntityRenderer<? super ExampleEntity> createRenderFor(EntityRendererManager manager) 
		{
			return new ExampleRenderer(manager);
		}
	}

	@Override
	public ResourceLocation getEntityTexture(ExampleEntity entity) {
		return null;
	}
}