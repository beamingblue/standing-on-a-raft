package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.client.StandingOnARaftClient;
import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class) public class MixinClientPlayerInteractionManager {
    @Inject(method = "createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)Lnet/minecraft/client/network/ClientPlayerEntity;",
            at = @At("RETURN"))
    private void setSoaRValue(ClientWorld j1, StatHandler j2, ClientRecipeBook j3, boolean j4, boolean j5,
                              CallbackInfoReturnable<ClientPlayerEntity> cir) {
        ((SoaRPlayer) cir.getReturnValue()).soar$setStanding(StandingOnARaftClient.isStanding());
    }
}
