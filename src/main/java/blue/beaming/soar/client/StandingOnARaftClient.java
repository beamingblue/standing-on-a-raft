package blue.beaming.soar.client;

import blue.beaming.soar.StandingOnARaft;
import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class StandingOnARaftClient implements ClientModInitializer {
    private static final KeyBinding toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.standing_on_a_raft.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.standing_on_a_raft.keybinds"
    ));

    public static final Logger LOG = LoggerFactory.getLogger("Standing on a Raft / Client");
    private static final Map<EntityType<?>, Boolean> standingByMountType = new Object2BooleanOpenHashMap<>();
    private static final File configurationFile = FabricLoader.getInstance()
                                                              .getConfigDir()
                                                              .resolve("standing_on_a_raft.json")
                                                              .toFile();
    private static Configuration configuration = Configuration.DEFAULT;

    public static boolean isStandingOn(EntityType<?> type) {
        return standingByMountType.computeIfAbsent(type, configuration::defaultSittingOn);
    }

    public static void setStanding(EntityType<?> type, boolean standing) {
        standingByMountType.put(type, standing);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ((SoaRPlayer) player).soar$setStanding(standing);
        ClientPlayNetworking.send(StandingOnARaft.STANDING_C2S_PAYLOAD_ID, StandingOnARaft.ofC2S(standing));
    }

    public static void toggleStanding(EntityType<?> type) {
        setStanding(type, !isStandingOn(type));
    }

    private static void readConfiguration() {
        try {
            if (!configurationFile.exists()) {
                saveConfiguration();
            } else {
                FileReader reader = new FileReader(configurationFile);
                configuration = Configuration.CODEC.parse(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                                                   .getOrThrow(false, msg -> {
                                                       throw new JsonParseException(msg);
                                                   });
                reader.close();
            }
        } catch (JsonParseException | IOException e) {
            LOG.warn("Cannot read configuration file - will be using default values", e);
        }
    }

    private static void saveConfiguration() {
        try (FileWriter writer = new FileWriter(configurationFile)) {
            writer.write(Configuration.CODEC.encodeStart(JsonOps.INSTANCE, configuration)
                                            .getOrThrow(false, msg -> {
                                                throw new JsonParseException(msg);
                                            })
                                            .toString());
        } catch (JsonParseException | IOException e) {
            LOG.warn("Cannot write to configuration file - won't be saving", e);
        }
    }

    @Override public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggle.wasPressed()) {
                if (!client.player.hasVehicle()) return;
                toggleStanding(client.player.getVehicle().getType());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(StandingOnARaft.STANDING_S2C_PAYLOAD_ID, (client, handler, packetByteBuf, sender) -> {
            int id = packetByteBuf.readInt();
            boolean standing = packetByteBuf.readBoolean();

            client.execute(() -> {
                ClientWorld world = client.world;
                if (world == null) return;
                Entity entity = world.getEntityById(id);
                if (entity instanceof SoaRPlayer player) player.soar$setStanding(standing);
            });
        });
        readConfiguration();
    }

    private record Configuration(boolean sittingByDefault, Map<Identifier, Boolean> sittingBehaviourByEntity) {
        public static final Configuration DEFAULT = new Configuration(false, Map.of(
                Registries.ENTITY_TYPE.getId(EntityType.BOAT), true,
                Registries.ENTITY_TYPE.getId(EntityType.CHEST_BOAT), true
        ));

        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("sitting_by_default").forGetter(Configuration::sittingByDefault),
                Codec.unboundedMap(
                        Identifier.CODEC,
                        Codec.BOOL
                ).fieldOf("sitting_behaviour_by_entity").forGetter(Configuration::sittingBehaviourByEntity)
        ).apply(instance, Configuration::new));

        public boolean defaultSittingOn(EntityType<?> type) {
            return Objects.requireNonNullElse(sittingBehaviourByEntity.get(Registries.ENTITY_TYPE.getId(type)), sittingByDefault);
        }
    }
}
