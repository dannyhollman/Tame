package com.example.examplemod.init;

import com.example.examplemod.TameMod;
import com.example.examplemod.entity.TameBeeEntity;
import com.example.examplemod.entity.TameChickenEntity;
import com.example.examplemod.entity.TameCowEntity;
import com.example.examplemod.entity.TamePandaEntity;
import com.example.examplemod.entity.TamePigEntity;
import com.example.examplemod.entity.TameTurtleEntity;
import com.example.examplemod.entity.TameFoxEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ExampleEntityInit {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, TameMod.MODID);

	public static final String TAME_CHICKEN_NAME = "tame_chicken";
	public static final RegistryObject<EntityType<TameChickenEntity>> TAME_CHICKEN_ENTITY = ENTITY_TYPES.register(TAME_CHICKEN_NAME, () ->
			EntityType.Builder.<TameChickenEntity>create(TameChickenEntity::new, EntityClassification.CREATURE)
					.size(EntityType.CHICKEN.getWidth(), EntityType.CHICKEN.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_CHICKEN_NAME).toString())
	);
	
	
	public static final String TAME_PANDA_NAME = "tame_panda";
	public static final RegistryObject<EntityType<TamePandaEntity>> TAME_PANDA_ENTITY = ENTITY_TYPES.register(TAME_PANDA_NAME, () ->
			EntityType.Builder.<TamePandaEntity>create(TamePandaEntity::new, EntityClassification.CREATURE)
					.size(EntityType.PANDA.getWidth(), EntityType.PANDA.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_PANDA_NAME).toString())
	);


	public static final String TAME_BEE_NAME = "tame_bee";
	public static final RegistryObject<EntityType<TameBeeEntity>> TAME_BEE_ENTITY = ENTITY_TYPES.register(TAME_BEE_NAME, () ->
			EntityType.Builder.<TameBeeEntity>create(TameBeeEntity::new, EntityClassification.CREATURE)
					.size(EntityType.BEE.getWidth(), EntityType.BEE.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_BEE_NAME).toString())
	);

	public static final String TAME_TURTLE_NAME = "tame_turtle";
	public static final RegistryObject<EntityType<TameTurtleEntity>> TAME_TURTLE_ENTITY = ENTITY_TYPES.register(TAME_TURTLE_NAME, () ->
			EntityType.Builder.<TameTurtleEntity>create(TameTurtleEntity::new, EntityClassification.CREATURE)
					.size(EntityType.TURTLE.getWidth(), EntityType.TURTLE.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_TURTLE_NAME).toString())
	);
	
	public static final String TAME_COW_NAME = "tame_cow";
	public static final RegistryObject<EntityType<TameCowEntity>> TAME_COW_ENTITY = ENTITY_TYPES.register(TAME_COW_NAME, () ->
			EntityType.Builder.<TameCowEntity>create(TameCowEntity::new, EntityClassification.CREATURE)
					.size(EntityType.COW.getWidth(), EntityType.COW.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_COW_NAME).toString())
	);
	
	public static final String TAME_FOX_NAME = "tame_fox";
	public static final RegistryObject<EntityType<TameFoxEntity>> TAME_FOX_ENTITY = ENTITY_TYPES.register(TAME_FOX_NAME, () ->
			EntityType.Builder.<TameFoxEntity>create(TameFoxEntity::new, EntityClassification.CREATURE)
					.size(EntityType.FOX.getWidth(), EntityType.FOX.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_FOX_NAME).toString())
	);
	
	public static final String TAME_PIG_NAME = "tame_pig";
	public static final RegistryObject<EntityType<TamePigEntity>> TAME_PIG_ENTITY = ENTITY_TYPES.register(TAME_PIG_NAME, () ->
			EntityType.Builder.<TamePigEntity>create(TamePigEntity::new, EntityClassification.CREATURE)
					.size(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
					.build(new ResourceLocation(TameMod.MODID, TAME_FOX_NAME).toString())
	);
}
