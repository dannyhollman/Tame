package com.example.examplemod;

import com.example.examplemod.entity.TameBeeEntity;
import com.example.examplemod.entity.TameChickenEntity;
import com.example.examplemod.entity.TamePandaEntity;
import com.example.examplemod.entity.TameTurtleEntity;
import com.example.examplemod.init.ExampleEntityInit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void onEntityJoinWorld (EntityJoinWorldEvent event) {

        final Entity entity = event.getEntity();     
        final World world = event.getWorld();     
        final boolean loaded = world.getChunkProvider().isChunkLoaded(entity);


        if (entity instanceof ChickenEntity && loaded)
        {
        	ExampleMod.LOGGER.info("Chicken found");

        	entity.remove();

        	TameChickenEntity chicken = new TameChickenEntity(ExampleEntityInit.TAME_CHICKEN_ENTITY.get(), world);

        	chicken.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());

        	event.getWorld().addEntity(chicken);

        	ExampleMod.LOGGER.info("CHICKEN REPLACED! :D");
        }

        else if (entity instanceof PandaEntity && loaded)
        {
        	ExampleMod.LOGGER.info("Panda found");
        	
        	entity.remove();
        	
        	TamePandaEntity panda = new TamePandaEntity(ExampleEntityInit.TAME_PANDA_ENTITY.get(), world);
        	
        	panda.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(panda);
        	
        	ExampleMod.LOGGER.info("PANDA REPLACED! :D");
        }
        
        else if (entity instanceof BeeEntity && loaded)
        {
        	ExampleMod.LOGGER.info("Bee found");
        	
        	entity.remove();
        	
        	TameBeeEntity bee = new TameBeeEntity(ExampleEntityInit.TAME_BEE_ENTITY.get(), world);
        	
        	bee.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(bee);
        	
        	ExampleMod.LOGGER.info("BEE REPLACED! :D");
        }
        
        else if (entity instanceof TurtleEntity && loaded)
        {
        	ExampleMod.LOGGER.info("Turtle found");
        	
        	entity.remove();
        	
        	TameTurtleEntity turtle = new TameTurtleEntity(ExampleEntityInit.TAME_TURTLE_ENTITY.get(), world);
        	
        	turtle.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(turtle);
        	
        	ExampleMod.LOGGER.info("TURTLE REPLACED! :D");
        }
    }
}
