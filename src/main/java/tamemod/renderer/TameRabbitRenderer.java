package tamemod.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamemod.entity.TameRabbitEntity;
import tamemod.model.TameRabbitModel;

//@OnlyIn(Dist.CLIENT)
public class TameRabbitRenderer extends MobRenderer<TameRabbitEntity, TameRabbitModel<TameRabbitEntity>> {
   private static final ResourceLocation BROWN = new ResourceLocation("textures/entity/rabbit/brown.png");
   private static final ResourceLocation WHITE = new ResourceLocation("textures/entity/rabbit/white.png");
   private static final ResourceLocation BLACK = new ResourceLocation("textures/entity/rabbit/black.png");
   private static final ResourceLocation GOLD = new ResourceLocation("textures/entity/rabbit/gold.png");
   private static final ResourceLocation SALT = new ResourceLocation("textures/entity/rabbit/salt.png");
   private static final ResourceLocation WHITE_SPLOTCHED = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   private static final ResourceLocation TOAST = new ResourceLocation("textures/entity/rabbit/toast.png");
   private static final ResourceLocation CAERBANNOG = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public TameRabbitRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new TameRabbitModel<>(), 0.3F);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(TameRabbitEntity entity) {
      String s = TextFormatting.getTextWithoutFormattingCodes(entity.getName().getString());
      if (s != null && "Toast".equals(s)) {
         return TOAST;
      } else {
         switch(entity.getRabbitType()) {
         case 0:
         default:
            return BROWN;
         case 1:
            return WHITE;
         case 2:
            return BLACK;
         case 3:
            return WHITE_SPLOTCHED;
         case 4:
            return GOLD;
         case 5:
            return SALT;
         case 99:
            return CAERBANNOG;
         }
      }
   }
}