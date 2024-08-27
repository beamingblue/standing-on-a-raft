package blue.beaming.soar;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import blue.beaming.soar.networking.StandingC2SPayload;
import blue.beaming.soar.networking.StandingS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StandingOnARaft implements ModInitializer {
    public static Identifier id(String path) {
        return Identifier.of("standing_on_a_raft", path);
    }

    @Override public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(StandingS2CPayload.PAYLOAD_ID, StandingS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StandingC2SPayload.PAYLOAD_ID, StandingC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StandingC2SPayload.PAYLOAD_ID, (payload, context) -> {
            ((SoaRPlayer) context.player()).soar$setStanding(payload.standing());
            for (ServerPlayerEntity player : PlayerLookup.tracking(context.player())) {
                ServerPlayNetworking.send(player, new StandingS2CPayload(context.player().getId(), payload.standing()));
            }
        });
    }
}
