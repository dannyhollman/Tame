package tamemod;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tamemod.init.ExampleEntityInit;
import tamemod.renderer.TameBeeRenderer;
import tamemod.renderer.TameChickenRenderer;
import tamemod.renderer.TameCowRenderer;
import tamemod.renderer.TameFoxRenderer;
import tamemod.renderer.TamePandaRenderer;
import tamemod.renderer.TamePigRenderer;
import tamemod.renderer.TameRabbitRenderer;
import tamemod.renderer.TameTurtleRenderer;
import net.minecraftforge.api.distmarker.Dist;


@EventBusSubscriber(modid = TameMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventSubscriber {

	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
		// Register the entity renderers
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_CHICKEN_ENTITY.get(), TameChickenRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_PANDA_ENTITY.get(),  TamePandaRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_BEE_ENTITY.get(), TameBeeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_TURTLE_ENTITY.get(),  TameTurtleRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_COW_ENTITY.get(),  TameCowRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_FOX_ENTITY.get(),  TameFoxRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_PIG_ENTITY.get(),  TamePigRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ExampleEntityInit.TAME_RABBIT_ENTITY.get(),  TameRabbitRenderer::new);
		
		TameMod.LOGGER.info("Registered Entity Renderers");
		
		/*
		DeferredWorkQueue.runLater(() -> {
			
		});
		*/	
	}
}
