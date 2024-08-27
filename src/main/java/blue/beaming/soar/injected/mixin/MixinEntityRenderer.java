package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class) public class MixinEntityRenderer {
    @SuppressWarnings("UnreachableCode") @Inject(method = "renderLabelIfPresent",
                                                 at = @At(value = "INVOKE",
                                                          target = "Lnet/minecraft/client/util/math/MatrixStack;peek()Lnet/minecraft/client/util/math/MatrixStack$Entry;"))
    private void pushLabelUp(Entity entity, Text j1, MatrixStack stack, VertexConsumerProvider j2, int j3, CallbackInfo ci) {
        if ((EntityRenderer<?>) (Object) this instanceof PlayerEntityRenderer && entity instanceof SoaRPlayer player
                && player.soar$onMountAndStanding()) {
            stack.translate(0, player.soar$ridingSittingDifference() * (1 / -0.025f), 0); // Sorry, magic numbers are at it again 😟
        }
    }
}
