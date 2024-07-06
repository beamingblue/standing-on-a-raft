package blue.beaming.soar.networking;

import blue.beaming.soar.StandingOnARaft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StandingS2CPayload(int id, boolean standing) implements CustomPayload {
    public static final Identifier IDENTIFIER = StandingOnARaft.id("s2c_payload");
    public static final Id<StandingS2CPayload> PAYLOAD_ID = new Id<>(IDENTIFIER);
    public static final PacketCodec<RegistryByteBuf, StandingS2CPayload>
            CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, StandingS2CPayload::id, PacketCodecs.BOOL, StandingS2CPayload::standing, StandingS2CPayload::new);

    @Override public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
