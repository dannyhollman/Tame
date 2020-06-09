package com.example.examplemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.examplemod.init.ExampleEntityInit;
import com.example.examplemod.renderer.TameChickenRenderer;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventSubscriber {

	private static final Logger LOGGER = LogManager.getLogger(ExampleMod.MODID);

	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {

		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.EXAMPLE_ENTITY.get(), TameChickenRenderer::new);
		LOGGER.debug("Registered Entity Renderers");
		};
}