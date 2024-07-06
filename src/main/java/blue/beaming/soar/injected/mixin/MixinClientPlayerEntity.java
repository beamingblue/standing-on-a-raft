package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.client.StandingOnARaftClient;
import blue.beaming.soar.networking.StandingC2SPayload;
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
        if (cir.getReturnValueZ()) ClientPlayNetworking.send(new StandingC2SPayload(StandingOnARaftClient.isStanding()));
    }
}
