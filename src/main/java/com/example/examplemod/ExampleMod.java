package com.example.examplemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.examplemod.init.ExampleEntityInit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class ExampleMod
{
	public static ExampleMod instance;
	public static final String MODID = "examplemod";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public ExampleMod() 
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ExampleEntityInit.ENTITY_TYPES.register(modEventBus);	
	}
	
	/*
    private void setup(final FMLCommonSetupEvent event) {

    	MinecraftForge.EVENT_BUS.register(new ForgeEventSubscriber());
        LOGGER.info("Setup method registered.");
    }
	*/
	
}
