package com.example.examplemod;

import com.example.examplemod.init.ExampleEntityInit;
import com.example.examplemod.renderer.TameBeeRenderer;
import com.example.examplemod.renderer.TameChickenRenderer;
import com.example.examplemod.renderer.TamePandaRenderer;
import com.example.examplemod.renderer.TameTurtleRenderer;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;


@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventSubscriber {

	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {

		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_CHICKEN_ENTITY.get(), TameChickenRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_PANDA_ENTITY.get(),  TamePandaRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_BEE_ENTITY.get(), TameBeeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_TURTLE_ENTITY.get(),  TameTurtleRenderer::new);

		ExampleMod.LOGGER.info("Registered Entity Renderers");
		
		/*
		DeferredWorkQueue.runLater(() -> {

		});
		*/
	}
}