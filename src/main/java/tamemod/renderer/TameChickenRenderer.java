package tamemod.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamemod.entity.TameChickenEntity;
import tamemod.model.TameChickenModel;

//@OnlyIn(Dist.CLIENT)
public class TameChickenRenderer extends MobRenderer<TameChickenEntity, TameChickenModel<TameChickenEntity>>
{

	private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

	public TameChickenRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new TameChickenModel<>(), 0.3F);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getEntityTexture(TameChickenEntity entity) {
		return CHICKEN_TEXTURES;
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(TameChickenEntity livingBase, float partialTicks) {
		float f = MathHelper.lerp(partialTicks, livingBase.oFlap, livingBase.wingRotation);
		float f1 = MathHelper.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.destPos);
		return (MathHelper.sin(f) + 1.0F) * f1;
	}

	/*

	public static class RenderFactory implements IRenderFactory<ExampleEntity>
	{
		@Override
		public EntityRenderer<? super ExampleEntity> createRenderFor(EntityRendererManager manager) 
		{
			return new ExampleRenderer(manager);
		}
	}
	
	 */
}