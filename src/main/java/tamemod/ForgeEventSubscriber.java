package tamemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
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

		if (entity instanceof ChickenEntity || 
				entity instanceof BeeEntity || 
				entity instanceof CowEntity || 
				entity instanceof PigEntity ||
				entity instanceof FoxEntity ||
				entity instanceof PandaEntity ||
				entity instanceof RabbitEntity)
		{
			if (!world.isRemote)
			{
				entity.remove();
				event.setCanceled(true);
			}
		}
	}
}
