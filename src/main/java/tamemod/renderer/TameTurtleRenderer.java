package tamemod.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamemod.entity.TameTurtleEntity;
import tamemod.model.TameTurtleModel;

//@OnlyIn(Dist.CLIENT)
public class TameTurtleRenderer extends MobRenderer<TameTurtleEntity, TameTurtleModel<TameTurtleEntity>> {
   private static final ResourceLocation BIG_SEA_TURTLE = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public TameTurtleRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TameTurtleModel<>(0.0F), 0.7F);
   }

   public void render(TameTurtleEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      if (entityIn.isChild()) {
         this.shadowSize *= 0.5F;
      }

      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TameTurtleEntity entity) {
      return BIG_SEA_TURTLE;
   }
}