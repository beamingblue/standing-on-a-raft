package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class) public class MixinLivingEntityRenderer {
    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
              at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/EntityModel;riding:Z", opcode = Opcodes.PUTFIELD))
    private void dontSetSittingIfNotAllowed(EntityModel model, boolean value, @Local(argsOnly = true) LivingEntity entity,
                                            @Local(argsOnly = true) MatrixStack stack) {
        if (entity instanceof SoaRPlayer player && player.soar$onMountAndStanding()) {
            model.riding = false;
            stack.translate(0, player.soar$ridingSittingDifference(), 0);
        } else {
            model.riding = value;
        }
    }
}
