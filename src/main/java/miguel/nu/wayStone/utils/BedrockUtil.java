package miguel.nu.wayStone.utils;

import java.util.UUID;

import org.geysermc.floodgate.api.FloodgateApi;

public final class BedrockUtil {

    private BedrockUtil() {}

    public static boolean isBedrockPlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
