package blue.beaming.soar;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StandingOnARaft implements ModInitializer {
    public static final Identifier STANDING_C2S_PAYLOAD_ID = id("c2s_payload");
    public static final Identifier STANDING_S2C_PAYLOAD_ID = id("s2c_payload");

    public static Identifier id(String path) {
        return Identifier.of("standing_on_a_raft", path);
    }

    public static PacketByteBuf ofS2C(int entityId, boolean standing) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeBoolean(standing);
        return buf;
    }

    public static PacketByteBuf ofC2S(boolean standing) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(standing);
        return buf;
    }

    @Override public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(STANDING_C2S_PAYLOAD_ID, (server, player, handler, packetByteBuf, sender) -> {
            boolean standing = packetByteBuf.readBoolean();
            server.execute(() -> {
                PacketByteBuf buf = ofS2C(player.getId(), standing);
                ((SoaRPlayer) player).soar$setStanding(standing);

                for (ServerPlayerEntity other : PlayerLookup.tracking(player)) {
                    ServerPlayNetworking.send(other, STANDING_S2C_PAYLOAD_ID, buf);
                }
            });
        });
    }
}
