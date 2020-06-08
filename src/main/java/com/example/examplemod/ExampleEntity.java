package com.example.examplemod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;

public class ExampleEntity extends ChickenEntity {

	public ExampleEntity(EntityType<? extends ChickenEntity> type, World worldIn) {
		super(type, worldIn);
	}

}
