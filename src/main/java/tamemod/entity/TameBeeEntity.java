package tamemod.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class TameBeeEntity extends TameableEntity implements IFlyingAnimal {
	private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.createKey(TameBeeEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(TameBeeEntity.class, DataSerializers.VARINT);
	private UUID lastHurtBy;
	private float rollAmount;
	private float rollAmountO;
	private int timeSinceSting;
	private int ticksWithoutNectarSinceExitingHive;
	private int stayOutOfHiveCountdown;
	private int numCropsGrownSincePollination;
	private int remainingCooldownBeforeLocatingNewHive = 0;
	private int remainingCooldownBeforeLocatingNewFlower = 0;
	@Nullable
	private BlockPos savedFlowerPos = null;
	@Nullable
	private BlockPos hivePos = null;
	private TameBeeEntity.PollinateGoal pollinateGoal;
	private TameBeeEntity.FindBeehiveGoal findBeehiveGoal;
	private FindFlowerGoal findFlowerGoal;
	private int underWaterTicks;

	public TameBeeEntity(EntityType<? extends TameableEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
		this.lookController = new TameBeeEntity.BeeLookController(this);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.COCOA, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
		this.setTamed(false);
	}
	
	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(DATA_FLAGS_ID, (byte)0);
		this.dataManager.register(ANGER_TIME, 0);
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

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new TameBeeEntity.StingGoal(this, (double)1.4F, true));
		this.goalSelector.addGoal(1, new TameBeeEntity.EnterBeehiveGoal());
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromTag(ItemTags.FLOWERS), false));
		this.pollinateGoal = new TameBeeEntity.PollinateGoal();
		this.goalSelector.addGoal(4, this.pollinateGoal);
		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
		this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		this.goalSelector.addGoal(5, new TameBeeEntity.UpdateBeehiveGoal());
		this.findBeehiveGoal = new TameBeeEntity.FindBeehiveGoal();
		this.goalSelector.addGoal(5, this.findBeehiveGoal);
		this.findFlowerGoal = new TameBeeEntity.FindFlowerGoal();
		this.goalSelector.addGoal(6, this.findFlowerGoal);
		this.goalSelector.addGoal(7, new TameBeeEntity.FindPollinationTargetGoal());
		this.goalSelector.addGoal(8, new TameBeeEntity.WanderGoal());
		this.goalSelector.addGoal(9, new SwimGoal(this));
		this.targetSelector.addGoal(1, (new TameBeeEntity.AngerGoal(this)).setCallsForHelp(new Class[0]));
		this.targetSelector.addGoal(2, new TameBeeEntity.AttackPlayerGoal(this));
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
	
	
	

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.hasHive()) {
			compound.put("HivePos", NBTUtil.writeBlockPos(this.getHivePos()));
		}

		if (this.hasFlower()) {
			compound.put("FlowerPos", NBTUtil.writeBlockPos(this.getFlowerPos()));
		}

		compound.putBoolean("HasNectar", this.hasNectar());
		compound.putBoolean("HasStung", this.hasStung());
		compound.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
		compound.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
		compound.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
		compound.putInt("Anger", this.getAnger());
		if (this.lastHurtBy != null) {
			compound.putString("HurtBy", this.lastHurtBy.toString());
		} else {
			compound.putString("HurtBy", "");
		}

	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		this.hivePos = null;
		if (compound.contains("HivePos")) {
			this.hivePos = NBTUtil.readBlockPos(compound.getCompound("HivePos"));
		}

		this.savedFlowerPos = null;
		if (compound.contains("FlowerPos")) {
			this.savedFlowerPos = NBTUtil.readBlockPos(compound.getCompound("FlowerPos"));
		}

		super.readAdditional(compound);
		this.setHasNectar(compound.getBoolean("HasNectar"));
		this.setHasStung(compound.getBoolean("HasStung"));
		this.setAnger(compound.getInt("Anger"));
		this.ticksWithoutNectarSinceExitingHive = compound.getInt("TicksSincePollination");
		this.stayOutOfHiveCountdown = compound.getInt("CannotEnterHiveTicks");
		this.numCropsGrownSincePollination = compound.getInt("CropsGrownSincePollination");
		String s = compound.getString("HurtBy");
		if (!s.isEmpty()) {
			this.lastHurtBy = UUID.fromString(s);
			PlayerEntity playerentity = this.world.getPlayerByUuid(this.lastHurtBy);
			this.setRevengeTarget(playerentity);
			if (playerentity != null) {
				this.attackingPlayer = playerentity;
				this.recentlyHit = this.getRevengeTimer();
			}
		}

	}

	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.func_226252_a_(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
		if (flag) {
			this.applyEnchantments(this, entityIn);
			if (entityIn instanceof LivingEntity) {
				((LivingEntity)entityIn).setBeeStingCount(((LivingEntity)entityIn).getBeeStingCount() + 1);
				int i = 0;
				if (this.world.getDifficulty() == Difficulty.NORMAL) {
					i = 10;
				} else if (this.world.getDifficulty() == Difficulty.HARD) {
					i = 18;
				}

				if (i > 0) {
					((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
				}
			}

			this.setHasStung(true);
			this.setAttackTarget((LivingEntity)null);
			this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
		}

		return flag;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void tick() {
		super.tick();
		if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.rand.nextFloat() < 0.05F) {
			for(int i = 0; i < this.rand.nextInt(2) + 1; ++i) {
				this.addParticle(this.world, this.getPosX() - (double)0.3F, this.getPosX() + (double)0.3F, this.getPosZ() - (double)0.3F, this.getPosZ() + (double)0.3F, this.getPosYHeight(0.5D), ParticleTypes.FALLING_NECTAR);
			}
		}

		this.updateBodyPitch();
	}

	private void addParticle(World worldIn, double p_226397_2_, double p_226397_4_, double p_226397_6_, double p_226397_8_, double posY, IParticleData particleData) {
		worldIn.addParticle(particleData, MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_2_, p_226397_4_), posY, MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_6_, p_226397_8_), 0.0D, 0.0D, 0.0D);
	}

	private void startMovingTo(BlockPos pos) {
		Vec3d vec3d = new Vec3d(pos);
		int i = 0;
		BlockPos blockpos = new BlockPos(this);
		int j = (int)vec3d.y - blockpos.getY();
		if (j > 2) {
			i = 4;
		} else if (j < -2) {
			i = -4;
		}

		int k = 6;
		int l = 8;
		int i1 = blockpos.manhattanDistance(pos);
		if (i1 < 15) {
			k = i1 / 2;
			l = i1 / 2;
		}

		Vec3d vec3d1 = RandomPositionGenerator.func_226344_b_(this, k, l, i, vec3d, (double)((float)Math.PI / 10F));
		if (vec3d1 != null) {
			this.navigator.setRangeMultiplier(0.5F);
			this.navigator.tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
		}
	}

	@Nullable
	public BlockPos getFlowerPos() {
		return this.savedFlowerPos;
	}

	public boolean hasFlower() {
		return this.savedFlowerPos != null;
	}

	public void setFlowerPos(BlockPos pos) {
		this.savedFlowerPos = pos;
	}

	private boolean failedPollinatingTooLong() {
		return this.ticksWithoutNectarSinceExitingHive > 3600;
	}

	private boolean canEnterHive() {
		if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isRunning() && !this.hasStung()) {
			boolean flag = this.failedPollinatingTooLong() || this.world.isRaining() || this.world.isNightTime() || this.hasNectar();
			return flag && !this.isHiveNearFire();
		} else {
			return false;
		}
	}

	public void setStayOutOfHiveCountdown(int p_226450_1_) {
		this.stayOutOfHiveCountdown = p_226450_1_;
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float p_226455_1_) {
		return MathHelper.lerp(p_226455_1_, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		if (this.isNearTarget()) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}

	}

	/**
	 * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
	 * change our actual active target (for example if we are currently busy attacking someone else)
	 */
	public void setRevengeTarget(@Nullable LivingEntity livingBase) {
		super.setRevengeTarget(livingBase);
		if (livingBase != null) {
			this.lastHurtBy = livingBase.getUniqueID();
		}

	}

	protected void updateAITasks() {
		boolean flag = this.hasStung();
		if (this.isInWaterOrBubbleColumn()) {
			++this.underWaterTicks;
		} else {
			this.underWaterTicks = 0;
		}

		if (this.underWaterTicks > 20) {
			this.attackEntityFrom(DamageSource.DROWN, 1.0F);
		}

		if (flag) {
			++this.timeSinceSting;
			if (this.timeSinceSting % 5 == 0 && this.rand.nextInt(MathHelper.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
				this.attackEntityFrom(DamageSource.GENERIC, this.getHealth());
			}
		}

		if (this.isAngry()) {
			int i = this.getAnger();
			this.setAnger(i - 1);
			LivingEntity livingentity = this.getAttackTarget();
			if (i == 0 && livingentity != null) {
				this.setBeeAttacker(livingentity);
			}
		}

		if (!this.hasNectar()) {
			++this.ticksWithoutNectarSinceExitingHive;
		}

	}

	public void resetTicksWithoutNectar() {
		this.ticksWithoutNectarSinceExitingHive = 0;
	}

	private boolean isHiveNearFire() {
		if (this.hivePos == null) {
			return false;
		} else {
			TileEntity tileentity = this.world.getTileEntity(this.hivePos);
			return tileentity instanceof BeehiveTileEntity && ((BeehiveTileEntity)tileentity).isNearFire();
		}
	}

	public boolean isAngry() {
		return this.getAnger() > 0;
	}

	private int getAnger() {
		return this.dataManager.get(ANGER_TIME);
	}

	private void setAnger(int angerTime) {
		this.dataManager.set(ANGER_TIME, angerTime);
	}

	private boolean doesHiveHaveSpace(BlockPos pos) {
		TileEntity tileentity = this.world.getTileEntity(pos);
		if (tileentity instanceof BeehiveTileEntity) {
			return !((BeehiveTileEntity)tileentity).isFullOfBees();
		} else {
			return false;
		}
	}

	public boolean hasHive() {
		return this.hivePos != null;
	}

	@Nullable
	public BlockPos getHivePos() {
		return this.hivePos;
	}

	/*
	protected void sendDebugPackets() {
		super.sendDebugPackets();
		DebugPacketSender.func_229749_a_(this);
	}
	*/

	private int getCropsGrownSincePollination() {
		return this.numCropsGrownSincePollination;
	}

	private void resetCropCounter() {
		this.numCropsGrownSincePollination = 0;
	}

	private void addCropCounter() {
		++this.numCropsGrownSincePollination;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void livingTick() {
		super.livingTick();
		if (!this.world.isRemote) {
			if (this.stayOutOfHiveCountdown > 0) {
				--this.stayOutOfHiveCountdown;
			}

			if (this.remainingCooldownBeforeLocatingNewHive > 0) {
				--this.remainingCooldownBeforeLocatingNewHive;
			}

			if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
				--this.remainingCooldownBeforeLocatingNewFlower;
			}

			boolean flag = this.isAngry() && !this.hasStung() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.0D;
			this.setNearTarget(flag);
			if (this.ticksExisted % 20 == 0 && !this.isHiveValid()) {
				this.hivePos = null;
			}
		}

	}

	private boolean isHiveValid() {
		if (!this.hasHive()) {
			return false;
		} else {
			TileEntity tileentity = this.world.getTileEntity(this.hivePos);
			return tileentity instanceof BeehiveTileEntity;
		}
	}

	public boolean hasNectar() {
		return this.getBeeFlag(8);
	}

	private void setHasNectar(boolean p_226447_1_) {
		if (p_226447_1_) {
			this.resetTicksWithoutNectar();
		}

		this.setBeeFlag(8, p_226447_1_);
	}

	public boolean hasStung() {
		return this.getBeeFlag(4);
	}

	private void setHasStung(boolean p_226449_1_) {
		this.setBeeFlag(4, p_226449_1_);
	}

	private boolean isNearTarget() {
		return this.getBeeFlag(2);
	}

	private void setNearTarget(boolean p_226452_1_) {
		this.setBeeFlag(2, p_226452_1_);
	}

	private boolean isTooFar(BlockPos pos) {
		return !this.isWithinDistance(pos, 48);
	}

	private void setBeeFlag(int flagId, boolean p_226404_2_) {
		if (p_226404_2_) {
			this.dataManager.set(DATA_FLAGS_ID, (byte)(this.dataManager.get(DATA_FLAGS_ID) | flagId));
		} else {
			this.dataManager.set(DATA_FLAGS_ID, (byte)(this.dataManager.get(DATA_FLAGS_ID) & ~flagId));
		}

	}

	private boolean getBeeFlag(int flagId) {
		return (this.dataManager.get(DATA_FLAGS_ID) & flagId) != 0;
	}

	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)0.6F);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
	}

	/**
	 * Returns new PathNavigateGround instance
	 */
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

			public void tick() {
				if (!TameBeeEntity.this.pollinateGoal.isRunning()) {
					super.tick();
				}
			}
		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem().isIn(ItemTags.FLOWERS);
	}

	private boolean isFlowers(BlockPos pos) {
		return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS);
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
	}

	protected SoundEvent getAmbientSound() {
		return null;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F;
	}

	public TameBeeEntity createChild(AgeableEntity ageable) {
		return null;
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return this.isChild() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	protected boolean makeFlySound() {
		return true;
	}

	public void onHoneyDelivered() {
		this.setHasNectar(false);
		this.resetCropCounter();
	}

	public boolean setBeeAttacker(Entity attacker) {
		this.setAnger(400 + this.rand.nextInt(400));
		if (attacker instanceof LivingEntity) {
			this.setRevengeTarget((LivingEntity)attacker);
		}

		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getTrueSource();
			if (!this.world.isRemote && entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canEntityBeSeen(entity) && !this.isAIDisabled()) {
				this.pollinateGoal.cancel();
				this.setBeeAttacker(entity);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	protected void handleFluidJump(Tag<Fluid> fluidTag) {
		this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
	}

	private boolean isWithinDistance(BlockPos pos, int distance) {
		return pos.withinDistance(new BlockPos(this), (double)distance);
	}

	class AngerGoal extends HurtByTargetGoal {
		AngerGoal(TameBeeEntity beeIn) {
			super(beeIn);
		}

		protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
			if (mobIn instanceof TameBeeEntity && this.goalOwner.canEntityBeSeen(targetIn) && ((TameBeeEntity)mobIn).setBeeAttacker(targetIn)) {
				mobIn.setAttackTarget(targetIn);
			}

		}
	}

	static class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
		AttackPlayerGoal(TameBeeEntity tameBeeEntity) {
			super(tameBeeEntity, PlayerEntity.class, true);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return this.canSting() && super.shouldExecute();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			boolean flag = this.canSting();
			if (flag && this.goalOwner.getAttackTarget() != null) {
				return super.shouldContinueExecuting();
			} else {
				this.target = null;
				return false;
			}
		}

		private boolean canSting() {
			TameBeeEntity beeentity = (TameBeeEntity)this.goalOwner;
			return beeentity.isAngry() && !beeentity.hasStung();
		}
	}

	class BeeLookController extends LookController {
		BeeLookController(MobEntity beeIn) {
			super(beeIn);
		}

		/**
		 * Updates look
		 */
		public void tick() {
			if (!TameBeeEntity.this.isAngry()) {
				super.tick();
			}
		}

		protected boolean func_220680_b() {
			return !TameBeeEntity.this.pollinateGoal.isRunning();
		}
	}

	class EnterBeehiveGoal extends TameBeeEntity.PassiveGoal {
		private EnterBeehiveGoal() {
		}

		public boolean canBeeStart() {
			if (TameBeeEntity.this.hasHive() && TameBeeEntity.this.canEnterHive() && TameBeeEntity.this.hivePos.withinDistance(TameBeeEntity.this.getPositionVec(), 2.0D)) {
				TileEntity tileentity = TameBeeEntity.this.world.getTileEntity(TameBeeEntity.this.hivePos);
				if (tileentity instanceof BeehiveTileEntity) {
					BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
					if (!beehivetileentity.isFullOfBees()) {
						return true;
					}

					TameBeeEntity.this.hivePos = null;
				}
			}

			return false;
		}

		public boolean canBeeContinue() {
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			TileEntity tileentity = TameBeeEntity.this.world.getTileEntity(TameBeeEntity.this.hivePos);
			if (tileentity instanceof BeehiveTileEntity) {
				BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
				beehivetileentity.tryEnterHive(TameBeeEntity.this, TameBeeEntity.this.hasNectar());
			}

		}
	}

	public class FindBeehiveGoal extends TameBeeEntity.PassiveGoal {
		private int ticks = TameBeeEntity.this.world.rand.nextInt(10);
		private List<BlockPos> possibleHives = Lists.newArrayList();
		@Nullable
		private Path path = null;

		FindBeehiveGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			return TameBeeEntity.this.hivePos != null && !TameBeeEntity.this.detachHome() && TameBeeEntity.this.canEnterHive() && !this.isCloseEnough(TameBeeEntity.this.hivePos) && TameBeeEntity.this.world.getBlockState(TameBeeEntity.this.hivePos).isIn(BlockTags.BEEHIVES);
		}

		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.ticks = 0;
			super.startExecuting();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			this.ticks = 0;
			TameBeeEntity.this.navigator.clearPath();
			TameBeeEntity.this.navigator.resetRangeMultiplier();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (TameBeeEntity.this.hivePos != null) {
				++this.ticks;
				if (this.ticks > 600) {
					this.makeChosenHivePossibleHive();
				} else if (!TameBeeEntity.this.navigator.func_226337_n_()) {
					if (!TameBeeEntity.this.isWithinDistance(TameBeeEntity.this.hivePos, 16)) {
						if (TameBeeEntity.this.isTooFar(TameBeeEntity.this.hivePos)) {
							this.reset();
						} else {
							TameBeeEntity.this.startMovingTo(TameBeeEntity.this.hivePos);
						}
					} else {
						boolean flag = this.startMovingToFar(TameBeeEntity.this.hivePos);
						if (!flag) {
							this.makeChosenHivePossibleHive();
						} else if (this.path != null && TameBeeEntity.this.navigator.getPath().isSamePath(this.path)) {
							this.reset();
						} else {
							this.path = TameBeeEntity.this.navigator.getPath();
						}

					}
				}
			}
		}

		private boolean startMovingToFar(BlockPos pos) {
			TameBeeEntity.this.navigator.setRangeMultiplier(10.0F);
			TameBeeEntity.this.navigator.tryMoveToXYZ((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0D);
			return TameBeeEntity.this.navigator.getPath() != null && TameBeeEntity.this.navigator.getPath().reachesTarget();
		}

		private boolean isPossibleHive(BlockPos pos) {
			return this.possibleHives.contains(pos);
		}

		private void addPossibleHives(BlockPos pos) {
			this.possibleHives.add(pos);

			while(this.possibleHives.size() > 3) {
				this.possibleHives.remove(0);
			}

		}

		private void clearPossibleHives() {
			this.possibleHives.clear();
		}

		private void makeChosenHivePossibleHive() {
			if (TameBeeEntity.this.hivePos != null) {
				this.addPossibleHives(TameBeeEntity.this.hivePos);
			}

			this.reset();
		}

		private void reset() {
			TameBeeEntity.this.hivePos = null;
			TameBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
		}

		private boolean isCloseEnough(BlockPos pos) {
			if (TameBeeEntity.this.isWithinDistance(pos, 2)) {
				return true;
			} else {
				Path path = TameBeeEntity.this.navigator.getPath();
				return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
			}
		}
	}

	public class FindFlowerGoal extends TameBeeEntity.PassiveGoal {
		private int ticks = TameBeeEntity.this.world.rand.nextInt(10);

		FindFlowerGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			return TameBeeEntity.this.savedFlowerPos != null && !TameBeeEntity.this.detachHome() && this.shouldMoveToFlower() && TameBeeEntity.this.isFlowers(TameBeeEntity.this.savedFlowerPos) && !TameBeeEntity.this.isWithinDistance(TameBeeEntity.this.savedFlowerPos, 2);
		}

		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.ticks = 0;
			super.startExecuting();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			this.ticks = 0;
			TameBeeEntity.this.navigator.clearPath();
			TameBeeEntity.this.navigator.resetRangeMultiplier();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (TameBeeEntity.this.savedFlowerPos != null) {
				++this.ticks;
				if (this.ticks > 600) {
					TameBeeEntity.this.savedFlowerPos = null;
				} else if (!TameBeeEntity.this.navigator.func_226337_n_()) {
					if (TameBeeEntity.this.isTooFar(TameBeeEntity.this.savedFlowerPos)) {
						TameBeeEntity.this.savedFlowerPos = null;
					} else {
						TameBeeEntity.this.startMovingTo(TameBeeEntity.this.savedFlowerPos);
					}
				}
			}
		}

		private boolean shouldMoveToFlower() {
			return TameBeeEntity.this.ticksWithoutNectarSinceExitingHive > 2400;
		}
	}

	class FindPollinationTargetGoal extends TameBeeEntity.PassiveGoal {
		private FindPollinationTargetGoal() {
		}

		public boolean canBeeStart() {
			if (TameBeeEntity.this.getCropsGrownSincePollination() >= 10) {
				return false;
			} else if (TameBeeEntity.this.rand.nextFloat() < 0.3F) {
				return false;
			} else {
				return TameBeeEntity.this.hasNectar() && TameBeeEntity.this.isHiveValid();
			}
		}

		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (TameBeeEntity.this.rand.nextInt(30) == 0) {
				for(int i = 1; i <= 2; ++i) {
					BlockPos blockpos = (new BlockPos(TameBeeEntity.this)).down(i);
					BlockState blockstate = TameBeeEntity.this.world.getBlockState(blockpos);
					Block block = blockstate.getBlock();
					boolean flag = false;
					IntegerProperty integerproperty = null;
					if (block.isIn(BlockTags.BEE_GROWABLES)) {
						if (block instanceof CropsBlock) {
							CropsBlock cropsblock = (CropsBlock)block;
							if (!cropsblock.isMaxAge(blockstate)) {
								flag = true;
								integerproperty = cropsblock.getAgeProperty();
							}
						} else if (block instanceof StemBlock) {
							int j = blockstate.get(StemBlock.AGE);
							if (j < 7) {
								flag = true;
								integerproperty = StemBlock.AGE;
							}
						} else if (block == Blocks.SWEET_BERRY_BUSH) {
							int k = blockstate.get(SweetBerryBushBlock.AGE);
							if (k < 3) {
								flag = true;
								integerproperty = SweetBerryBushBlock.AGE;
							}
						}

						if (flag) {
							TameBeeEntity.this.world.playEvent(2005, blockpos, 0);
							TameBeeEntity.this.world.setBlockState(blockpos, blockstate.with(integerproperty, Integer.valueOf(blockstate.get(integerproperty) + 1)));
							TameBeeEntity.this.addCropCounter();
						}
					}
				}

			}
		}
	}

	abstract class PassiveGoal extends Goal {
		private PassiveGoal() {
		}

		public abstract boolean canBeeStart();

		public abstract boolean canBeeContinue();

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return this.canBeeStart() && !TameBeeEntity.this.isAngry();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return this.canBeeContinue() && !TameBeeEntity.this.isAngry();
		}
	}

	class PollinateGoal extends TameBeeEntity.PassiveGoal {
		private final Predicate<BlockState> flowerPredicate = (p_226499_0_) -> {
			if (p_226499_0_.isIn(BlockTags.TALL_FLOWERS)) {
				if (p_226499_0_.getBlock() == Blocks.SUNFLOWER) {
					return p_226499_0_.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
				} else {
					return true;
				}
			} else {
				return p_226499_0_.isIn(BlockTags.SMALL_FLOWERS);
			}
		};
		private int pollinationTicks = 0;
		private int lastPollinationTick = 0;
		private boolean running;
		private Vec3d nextTarget;
		private int ticks = 0;

		PollinateGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			if (TameBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0) {
				return false;
			} else if (TameBeeEntity.this.hasNectar()) {
				return false;
			} else if (TameBeeEntity.this.world.isRaining()) {
				return false;
			} else if (TameBeeEntity.this.rand.nextFloat() < 0.7F) {
				return false;
			} else {
				Optional<BlockPos> optional = this.getFlower();
				if (optional.isPresent()) {
					TameBeeEntity.this.savedFlowerPos = optional.get();
					TameBeeEntity.this.navigator.tryMoveToXYZ((double)TameBeeEntity.this.savedFlowerPos.getX() + 0.5D, (double)TameBeeEntity.this.savedFlowerPos.getY() + 0.5D, (double)TameBeeEntity.this.savedFlowerPos.getZ() + 0.5D, (double)1.2F);
					return true;
				} else {
					return false;
				}
			}
		}

		public boolean canBeeContinue() {
			if (!this.running) {
				return false;
			} else if (!TameBeeEntity.this.hasFlower()) {
				return false;
			} else if (TameBeeEntity.this.world.isRaining()) {
				return false;
			} else if (this.completedPollination()) {
				return TameBeeEntity.this.rand.nextFloat() < 0.2F;
			} else if (TameBeeEntity.this.ticksExisted % 20 == 0 && !TameBeeEntity.this.isFlowers(TameBeeEntity.this.savedFlowerPos)) {
				TameBeeEntity.this.savedFlowerPos = null;
				return false;
			} else {
				return true;
			}
		}

		private boolean completedPollination() {
			return this.pollinationTicks > 400;
		}

		private boolean isRunning() {
			return this.running;
		}

		private void cancel() {
			this.running = false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			this.pollinationTicks = 0;
			this.ticks = 0;
			this.lastPollinationTick = 0;
			this.running = true;
			TameBeeEntity.this.resetTicksWithoutNectar();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask() {
			if (this.completedPollination()) {
				TameBeeEntity.this.setHasNectar(true);
			}

			this.running = false;
			TameBeeEntity.this.navigator.clearPath();
			TameBeeEntity.this.remainingCooldownBeforeLocatingNewFlower = 200;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			++this.ticks;
			if (this.ticks > 600) {
				TameBeeEntity.this.savedFlowerPos = null;
			} else {
				Vec3d vec3d = (new Vec3d(TameBeeEntity.this.savedFlowerPos)).add(0.5D, (double)0.6F, 0.5D);
				if (vec3d.distanceTo(TameBeeEntity.this.getPositionVec()) > 1.0D) {
					this.nextTarget = vec3d;
					this.moveToNextTarget();
				} else {
					if (this.nextTarget == null) {
						this.nextTarget = vec3d;
					}

					boolean flag = TameBeeEntity.this.getPositionVec().distanceTo(this.nextTarget) <= 0.1D;
					boolean flag1 = true;
					if (!flag && this.ticks > 600) {
						TameBeeEntity.this.savedFlowerPos = null;
					} else {
						if (flag) {
							boolean flag2 = TameBeeEntity.this.rand.nextInt(100) == 0;
							if (flag2) {
								this.nextTarget = new Vec3d(vec3d.getX() + (double)this.getRandomOffset(), vec3d.getY(), vec3d.getZ() + (double)this.getRandomOffset());
								TameBeeEntity.this.navigator.clearPath();
							} else {
								flag1 = false;
							}

							TameBeeEntity.this.getLookController().setLookPosition(vec3d.getX(), vec3d.getY(), vec3d.getZ());
						}

						if (flag1) {
							this.moveToNextTarget();
						}

						++this.pollinationTicks;
						if (TameBeeEntity.this.rand.nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
							this.lastPollinationTick = this.pollinationTicks;
							TameBeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
						}

					}
				}
			}
		}

		private void moveToNextTarget() {
			TameBeeEntity.this.getMoveHelper().setMoveTo(this.nextTarget.getX(), this.nextTarget.getY(), this.nextTarget.getZ(), (double)0.35F);
		}

		private float getRandomOffset() {
			return (TameBeeEntity.this.rand.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
		}

		private Optional<BlockPos> getFlower() {
			return this.findFlower(this.flowerPredicate, 5.0D);
		}

		private Optional<BlockPos> findFlower(Predicate<BlockState> p_226500_1_, double distance) {
			BlockPos blockpos = new BlockPos(TameBeeEntity.this);
			BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

			for(int i = 0; (double)i <= distance; i = i > 0 ? -i : 1 - i) {
				for(int j = 0; (double)j < distance; ++j) {
					for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							blockpos$mutable.setPos(blockpos).move(k, i - 1, l);
							if (blockpos.withinDistance(blockpos$mutable, distance) && p_226500_1_.test(TameBeeEntity.this.world.getBlockState(blockpos$mutable))) {
								return Optional.of(blockpos$mutable);
							}
						}
					}
				}
			}

			return Optional.empty();
		}
	}

	class StingGoal extends MeleeAttackGoal {
		StingGoal(CreatureEntity creatureIn, double speedIn, boolean useLongMemory) {
			super(creatureIn, speedIn, useLongMemory);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return super.shouldExecute() && TameBeeEntity.this.isAngry() && !TameBeeEntity.this.hasStung();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting() && TameBeeEntity.this.isAngry() && !TameBeeEntity.this.hasStung();
		}
	}

	class UpdateBeehiveGoal extends TameBeeEntity.PassiveGoal {
		private UpdateBeehiveGoal() {
		}

		public boolean canBeeStart() {
			return TameBeeEntity.this.remainingCooldownBeforeLocatingNewHive == 0 && !TameBeeEntity.this.hasHive() && TameBeeEntity.this.canEnterHive();
		}

		public boolean canBeeContinue() {
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			TameBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
			List<BlockPos> list = this.getNearbyFreeHives();
			if (!list.isEmpty()) {
				for(BlockPos blockpos : list) {
					if (!TameBeeEntity.this.findBeehiveGoal.isPossibleHive(blockpos)) {
						TameBeeEntity.this.hivePos = blockpos;
						return;
					}
				}

				TameBeeEntity.this.findBeehiveGoal.clearPossibleHives();
				TameBeeEntity.this.hivePos = list.get(0);
			}
		}

		private List<BlockPos> getNearbyFreeHives() {
			BlockPos blockpos = new BlockPos(TameBeeEntity.this);
			PointOfInterestManager pointofinterestmanager = ((ServerWorld)TameBeeEntity.this.world).getPointOfInterestManager();
			Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b((p_226486_0_) -> {
				return p_226486_0_ == PointOfInterestType.BEEHIVE || p_226486_0_ == PointOfInterestType.BEE_NEST;
			}, blockpos, 20, PointOfInterestManager.Status.ANY);
			return stream.map(PointOfInterest::getPos).filter((p_226487_1_) -> {
				return TameBeeEntity.this.doesHiveHaveSpace(p_226487_1_);
			}).sorted(Comparator.comparingDouble((p_226488_1_) -> {
				return p_226488_1_.distanceSq(blockpos);
			})).collect(Collectors.toList());
		}
	}

	class WanderGoal extends Goal {
		WanderGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return TameBeeEntity.this.navigator.noPath() && TameBeeEntity.this.rand.nextInt(10) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return TameBeeEntity.this.navigator.func_226337_n_();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Vec3d vec3d = this.getRandomLocation();
			if (vec3d != null) {
				TameBeeEntity.this.navigator.setPath(TameBeeEntity.this.navigator.getPathToPos(new BlockPos(vec3d), 1), 1.0D);
			}

		}

		@Nullable
		private Vec3d getRandomLocation() {
			Vec3d vec3d;
			if (TameBeeEntity.this.isHiveValid() && !TameBeeEntity.this.isWithinDistance(TameBeeEntity.this.hivePos, 40)) {
				Vec3d vec3d1 = new Vec3d(TameBeeEntity.this.hivePos);
				vec3d = vec3d1.subtract(TameBeeEntity.this.getPositionVec()).normalize();
			} else {
				vec3d = TameBeeEntity.this.getLook(0.0F);
			}

			int i = 8;
			Vec3d vec3d2 = RandomPositionGenerator.findAirTarget(TameBeeEntity.this, 8, 7, vec3d, ((float)Math.PI / 2F), 2, 1);
			return vec3d2 != null ? vec3d2 : RandomPositionGenerator.findGroundTarget(TameBeeEntity.this, 8, 4, -2, vec3d, (double)((float)Math.PI / 2F));
		}
	}
}
