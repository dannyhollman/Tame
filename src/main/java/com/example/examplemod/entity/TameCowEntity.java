package com.example.examplemod.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TameCowEntity extends TameableEntity {
	
	   public TameCowEntity(EntityType<? extends TameCowEntity> type, World worldIn) {
	      super(type, worldIn);
	      this.setTamed(false);
	   }
	   
		public void setTamed(boolean tamed) {
			super.setTamed(tamed);
			if (tamed) {
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
				this.setHealth(20.0F);
			} else {
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
			}
		}

	   protected void registerGoals() {
	      this.goalSelector.addGoal(0, new SwimGoal(this));
	      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
	      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
	      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromItems(Items.WHEAT), false));
	      this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
	      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
	      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
	      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
	      this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
	   }

	   protected void registerAttributes() {
	      super.registerAttributes();
	      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
	      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
	   }

	   protected SoundEvent getAmbientSound() {
	      return SoundEvents.ENTITY_COW_AMBIENT;
	   }

	   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
	      return SoundEvents.ENTITY_COW_HURT;
	   }

	   protected SoundEvent getDeathSound() {
	      return SoundEvents.ENTITY_COW_DEATH;
	   }

	   protected void playStepSound(BlockPos pos, BlockState blockIn) {
	      this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
	   }

	   /**
	    * Returns the volume for the sounds this mob makes.
	    */
	   protected float getSoundVolume() {
	      return 0.4F;
	   }

	   public boolean processInteract(PlayerEntity player, Hand hand) {
	      ItemStack itemstack = player.getHeldItem(hand);
	      if (itemstack.getItem() == Items.BUCKET && !player.abilities.isCreativeMode && !this.isChild()) {
	         player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
	         itemstack.shrink(1);
	         if (itemstack.isEmpty()) {
	            player.setHeldItem(hand, new ItemStack(Items.MILK_BUCKET));
	         } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET))) {
	            player.dropItem(new ItemStack(Items.MILK_BUCKET), false);
	         }

	         return true;
	      } else {
	    	  if (!this.isTamed()) {
	    		  if (!player.abilities.isCreativeMode) {
	    			  itemstack.shrink(1);
	    		  }

	    		  if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
	    			  System.out.println("Tamed! :D");
	    			  this.setTamedBy(player);
	    			  this.navigator.clearPath();
	    			  this.world.setEntityState(this, (byte)7);
	    		  } else {
	    			  this.world.setEntityState(this, (byte)6);
	    		  }

	    		  return true;
	    	  }
	    	  return super.processInteract(player, hand);
	      }
	   }

	   public TameCowEntity createChild(AgeableEntity ageable) {
	      return null;
	   }

	   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
	      return this.isChild() ? sizeIn.height * 0.95F : 1.3F;
	   }
	}