package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class) public class MixinCamera {
    @ModifyReturnValue(method = "getPos", at = @At("RETURN"))
    private Vec3d addStandingOffset(Vec3d original) {
        Entity entity = MinecraftClient.getInstance().cameraEntity;
        if (!(entity instanceof SoaRPlayer player) || !player.soar$onMountAndStanding()) return original;
        return original.add(0, player.soar$ridingSittingDifference(), 0);
    }
}
