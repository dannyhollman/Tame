package com.example.examplemod;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ExampleEntityInit {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ExampleMod.MODID);

	public static final String EXAMPLE_NAME = "test_entity";
	public static final RegistryObject<EntityType<ExampleEntity>> EXAMPLE_ENTITY = ENTITY_TYPES.register(EXAMPLE_NAME, () ->
			EntityType.Builder.<ExampleEntity>create(ExampleEntity::new, EntityClassification.CREATURE)
					.size(EntityType.CHICKEN.getWidth(), EntityType.CHICKEN.getHeight())
					.build(new ResourceLocation(ExampleMod.MODID, EXAMPLE_NAME).toString())
	);
}
