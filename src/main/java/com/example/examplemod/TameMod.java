package com.example.examplemod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.examplemod.init.ExampleEntityInit;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class TameMod
{
	public static final String MODID = "examplemod";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public TameMod() 
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ExampleEntityInit.ENTITY_TYPES.register(modEventBus);
	}
}
