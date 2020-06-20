package tamemod.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamemod.entity.TameCowEntity;
import tamemod.model.TameCowModel;

//@OnlyIn(Dist.CLIENT)
public class TameCowRenderer extends MobRenderer<TameCowEntity, TameCowModel<TameCowEntity>> {
	   private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

	   public TameCowRenderer(EntityRendererManager renderManagerIn) {
	      super(renderManagerIn, new TameCowModel<>(), 0.7F);
	   }

	   /**
	    * Returns the location of an entity's texture.
	    */
	   public ResourceLocation getEntityTexture(TameCowEntity entity) {
	      return COW_TEXTURES;
	   }
	}