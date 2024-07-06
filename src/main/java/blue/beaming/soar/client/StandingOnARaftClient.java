package blue.beaming.soar.client;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import blue.beaming.soar.networking.StandingC2SPayload;
import blue.beaming.soar.networking.StandingS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public class StandingOnARaftClient implements ClientModInitializer {
    private static final KeyBinding toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.standing_on_a_raft.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.standing_on_a_raft.keybinds"
    ));

    private static boolean standing = false;

    public static boolean isStanding() {
        return standing;
    }

    public static void setStanding(boolean standing) {
        StandingOnARaftClient.standing = standing;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ((SoaRPlayer) player).soar$setStanding(standing);
        ClientPlayNetworking.send(new StandingC2SPayload(standing));
    }

    public static void toggleStanding() {
        setStanding(!standing);
    }

    @Override public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggle.wasPressed()) {
                if (!((SoaRPlayer) client.player).soar$onRaft()) continue;
                toggleStanding();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(StandingS2CPayload.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;
                Entity entity = world.getEntityById(payload.id());
                if (!(entity instanceof SoaRPlayer player)) return;
                player.soar$setStanding(payload.standing());
            });
        });
    }
}
