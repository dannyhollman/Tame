package com.example.examplemod;

import com.example.examplemod.entity.TameBeeEntity;
import com.example.examplemod.entity.TameChickenEntity;
import com.example.examplemod.entity.TameCowEntity;
import com.example.examplemod.entity.TameFoxEntity;
import com.example.examplemod.entity.TamePandaEntity;
import com.example.examplemod.entity.TamePigEntity;
import com.example.examplemod.entity.TameTurtleEntity;
import com.example.examplemod.init.ExampleEntityInit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = TameMod.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void onEntityJoinWorld (EntityJoinWorldEvent event) {

        final Entity entity = event.getEntity();     
        final World world = event.getWorld();     
        final boolean loaded = world.getChunkProvider().isChunkLoaded(entity);


        if (entity instanceof ChickenEntity && loaded)
        {
        	TameMod.LOGGER.info("Chicken found");

        	entity.remove();

        	TameChickenEntity chicken = new TameChickenEntity(ExampleEntityInit.TAME_CHICKEN_ENTITY.get(), world);

        	chicken.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());

        	event.getWorld().addEntity(chicken);

        	TameMod.LOGGER.info("CHICKEN REPLACED! :D");
        }

        else if (entity instanceof PandaEntity && loaded)
        {
        	TameMod.LOGGER.info("Panda found");
        	
        	entity.remove();
        	
        	TamePandaEntity panda = new TamePandaEntity(ExampleEntityInit.TAME_PANDA_ENTITY.get(), world);
        	
        	panda.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(panda);
        	
        	TameMod.LOGGER.info("PANDA REPLACED! :D");
        }
        
        else if (entity instanceof BeeEntity && loaded)
        {
        	TameMod.LOGGER.info("Bee found");
        	
        	entity.remove();
        	
        	TameBeeEntity bee = new TameBeeEntity(ExampleEntityInit.TAME_BEE_ENTITY.get(), world);
        	
        	bee.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(bee);
        	
        	TameMod.LOGGER.info("BEE REPLACED! :D");
        }
        
        else if (entity instanceof TurtleEntity && loaded)
        {
        	TameMod.LOGGER.info("Turtle found");
        	
        	entity.remove();
        	
        	TameTurtleEntity turtle = new TameTurtleEntity(ExampleEntityInit.TAME_TURTLE_ENTITY.get(), world);
        	
        	turtle.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(turtle);
        	
        	TameMod.LOGGER.info("TURTLE REPLACED! :D");
        }
        
        else if (entity instanceof CowEntity && loaded)
        {
        	TameMod.LOGGER.info("Cow found");
        	
        	entity.remove();
        	
        	TameCowEntity cow = new TameCowEntity(ExampleEntityInit.TAME_COW_ENTITY.get(), world);
        	
        	cow.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(cow);
        	
        	TameMod.LOGGER.info("COW REPLACED! :D");
        }
        
        else if (entity instanceof FoxEntity && loaded)
        {
        	TameMod.LOGGER.info("Fox found");
        	
        	entity.remove();
        	
        	TameFoxEntity fox = new TameFoxEntity(ExampleEntityInit.TAME_FOX_ENTITY.get(), world);
        	
        	fox.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(fox);
        	
        	TameMod.LOGGER.info("FOX REPLACED! :D");
        }
        
        else if (entity instanceof PigEntity && loaded)
        {
        	TameMod.LOGGER.info("Pig found");
        	
        	entity.remove();
        	
        	TamePigEntity pig = new TamePigEntity(ExampleEntityInit.TAME_PIG_ENTITY.get(), world);
        	
        	pig.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
        	
        	event.getWorld().addEntity(pig);
        	
        	TameMod.LOGGER.info("PIG REPLACED! :D");
        }
    }
}
