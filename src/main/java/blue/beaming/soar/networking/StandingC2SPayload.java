package blue.beaming.soar.networking;

import blue.beaming.soar.StandingOnARaft;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StandingC2SPayload(boolean standing) implements CustomPayload {
    public static final Identifier IDENTIFIER = StandingOnARaft.id("c2s_payload");
    public static final Id<StandingC2SPayload> PAYLOAD_ID = new Id<>(IDENTIFIER);
    public static final PacketCodec<RegistryByteBuf, StandingC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, StandingC2SPayload::standing, StandingC2SPayload::new);

    @Override public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
