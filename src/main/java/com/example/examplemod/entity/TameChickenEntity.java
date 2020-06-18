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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TameChickenEntity extends TameableEntity {

	private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
	public float wingRotation;
	public float destPos;
	public float oFlapSpeed;
	public float oFlap;
	public float wingRotDelta = 1.0F;
	public int timeUntilNextEgg = this.rand.nextInt(6000) + 6000;

	public TameChickenEntity(EntityType<? extends TameChickenEntity> type, World worldIn) {
		super(type, worldIn);
		this.setTamed(false);
	}


	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, false, TEMPTATION_ITEMS));
		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return this.isChild() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
	}

	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	public void livingTick() {
		super.livingTick();
		this.oFlap = this.wingRotation;
		this.oFlapSpeed = this.destPos;
		this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
		this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
		if (!this.onGround && this.wingRotDelta < 1.0F) {
			this.wingRotDelta = 1.0F;
		}

		this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
		Vec3d vec3d = this.getMotion();
		if (!this.onGround && vec3d.y < 0.0D) {
			this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
		}

		this.wingRotation += this.wingRotDelta * 2.0F;
		if (!this.world.isRemote && this.isAlive() && !this.isChild() && --this.timeUntilNextEgg <= 0) {
			this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.entityDropItem(Items.EGG);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_CHICKEN_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_CHICKEN_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CHICKEN_DEATH;
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
	}

	public TameChickenEntity createChild(AgeableEntity ageable) {
		return null;
	}

	public boolean isBreedingItem(ItemStack stack) {
		return TEMPTATION_ITEMS.test(stack);
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("EggLayTime")) {
			this.timeUntilNextEgg = compound.getInt("EggLayTime");
		}

	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("EggLayTime", this.timeUntilNextEgg);
	}

	public boolean canDespawn(double distanceToClosestPlayer) {
		return true;
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

	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		if (itemstack.getItem() instanceof SpawnEggItem) {
			return super.processInteract(player, hand);
		} else if (this.world.isRemote) {
			return this.isOwner(player);
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
}
