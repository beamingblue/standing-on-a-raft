package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import blue.beaming.soar.networking.StandingS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class) public class MixinEntityTrackerEntry {
    @Shadow @Final private Entity entity;

    @Inject(method = "startTracking", at = @At("TAIL"))
    private void addStandingPayload(ServerPlayerEntity player, CallbackInfo ci) {
        if (!(this.entity instanceof SoaRPlayer soar)) return;
        ServerPlayNetworking.send(player, new StandingS2CPayload(this.entity.getId(), soar.soar$standing()));
    }
}
