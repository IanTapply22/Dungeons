package me.iantapply.dungeons.dungeons.utils.npcs;

import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class GameProfile {

    // Create a game profile
    public static com.mojang.authlib.GameProfile createGameProfile(String name, String textureValue, String textureSignature) {

        com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(UUID.randomUUID(), name);

        // Set skin from signature and value
        profile.getProperties().put("textures", new Property("textures", textureValue, textureSignature));

        return profile;
    }
}
