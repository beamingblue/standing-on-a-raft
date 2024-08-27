package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.StandingOnARaft;
import blue.beaming.soar.client.StandingOnARaftClient;
import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class) public class MixinClientPlayerEntity {
    @Inject(method = "startRiding", at = @At("RETURN"))
    private void sendPacket(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            boolean standing = StandingOnARaftClient.isStandingOn(entity.getType());
            ClientPlayNetworking.send(StandingOnARaft.STANDING_C2S_PAYLOAD_ID, StandingOnARaft.ofC2S(standing));
            ((SoaRPlayer) this).soar$setStanding(standing);
        }
    }
}
