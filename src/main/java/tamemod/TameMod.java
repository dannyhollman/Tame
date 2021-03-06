package tamemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tamemod.init.ExampleEntityInit;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("tamemod")
public class TameMod
{
	public static final String MODID = "tamemod";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public TameMod() 
	{
		// Get Mod Event Bus
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		// Register entities
		ExampleEntityInit.ENTITY_TYPES.register(modEventBus);
		// Listen for OnLoadSpawns
		modEventBus.addListener(this::OnLoadSpawns);
	}
	// Register entity spawns
	private void OnLoadSpawns (FMLCommonSetupEvent event) {
		RegisterEntitySpawns.spawnMobs();
		TameMod.LOGGER.info("entity spawns loaded");
	}

}
