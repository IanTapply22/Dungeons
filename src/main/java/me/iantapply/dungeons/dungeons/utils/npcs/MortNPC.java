package me.iantapply.dungeons.dungeons.utils.npcs;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class MortNPC {

    public static void spawnMortGateKepper(Location location) {
        /*
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        // Texture value and signature for mort
        String mortTextureValue = "eyJ0aW1lc3RhbXAiOjE1ODcwMTg2Nzk1NzYsInByb2ZpbGVJZCI6IjJkYzc3YWU3OTQ2MzQ4MDI5NDI4MGM4NDIyNzRiNTY3IiwicHJvZmlsZU5hbWUiOiJzYWR5MDYxMCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI1Njg5NWI5NjU5ODk2YWQ2NDdmNTg1OTkyMzhhZjUzMmQ0NmRiOWMxYjAzODliOGJiZWI3MDk5OWRhYjMzZCJ9fX0=";
        String mortTextureSignature = "ey9vkW8VXzne4hcYi93Qy8byhmeFjWO/jzoYMJ2R8Njg/daCGIqwp1JnWhnI3WWDZYfpqNF00UojtsaFH6RICa2+1jS+le82siwIFqH5/TufeVfjg8wn611PcbCMtlIbfTIP0jgDPCFTiXK+8gY4ES7fsFVy6Rs26xynPFABdZzPEMECW+gukpyz1hbc1gR57QRDfvKGe55e1Usqxh5v5q5B3uFV78WSJ8faZiItE2Re9HesqcR314Zst7On/jKOtmYfl7opCKQ/q7ySu2y55Tn0dRWWDQwZmHOuYuFE1hF9g1dtmNrPfGs7WuSbI5qZk9GfUJbSOe2naB6ZwF+C2WY77M9U8gDzlipGN7yEDDTBGvhbzhmXTHnEoRPxSBv2gpO6WEmfIrLvQzqWVS96rmwv/pMx62U3pxTJAcQRKBCzgT/EOh2lT886h6Gj71z43zCg+u3smvv0bjoe4IIUe8omurBXNLXXGY01vboXNXs2NpcVg1sX4Uk0NPhuR9Wh/S05bj6T4Tqke007g9lWFI8+gM8zRl4yLbafsQmk6ZmPO/6sF2oT+qfqgv2Tw1PH0nafoHYxGarjIEmVlNNS1mkFx8+CHmb36ntk/FKcFtX9zmuRXbMLWSP3XB9YjTeYZPtlOjzXdx5sXKrRJsJl/pfop8XWTjfI0HBvo30h2K0=";

        EntityPlayer mort = new EntityPlayer(server, world, GameProfile.createGameProfile("§e§lGATE KEEPER", mortTextureValue, mortTextureSignature), new PlayerInteractManager(world));

        // Datawatcher
        DataWatcher watcher = mort.getDataWatcher();
        watcher.watch(10, (byte) 127);

        // Set mort's location
        mort.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, mort));
            connection.sendPacket(new PacketPlayOutEntityMetadata(mort.getId(), watcher, true));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(mort));
            FloatingNametag.createFloatingName("§cMort", location);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, mort));
        }
         */

        String mortTextureValue = "eyJ0aW1lc3RhbXAiOjE1ODcwMTg2Nzk1NzYsInByb2ZpbGVJZCI6IjJkYzc3YWU3OTQ2MzQ4MDI5NDI4MGM4NDIyNzRiNTY3IiwicHJvZmlsZU5hbWUiOiJzYWR5MDYxMCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI1Njg5NWI5NjU5ODk2YWQ2NDdmNTg1OTkyMzhhZjUzMmQ0NmRiOWMxYjAzODliOGJiZWI3MDk5OWRhYjMzZCJ9fX0=";
        String mortTextureSignature = "ey9vkW8VXzne4hcYi93Qy8byhmeFjWO/jzoYMJ2R8Njg/daCGIqwp1JnWhnI3WWDZYfpqNF00UojtsaFH6RICa2+1jS+le82siwIFqH5/TufeVfjg8wn611PcbCMtlIbfTIP0jgDPCFTiXK+8gY4ES7fsFVy6Rs26xynPFABdZzPEMECW+gukpyz1hbc1gR57QRDfvKGe55e1Usqxh5v5q5B3uFV78WSJ8faZiItE2Re9HesqcR314Zst7On/jKOtmYfl7opCKQ/q7ySu2y55Tn0dRWWDQwZmHOuYuFE1hF9g1dtmNrPfGs7WuSbI5qZk9GfUJbSOe2naB6ZwF+C2WY77M9U8gDzlipGN7yEDDTBGvhbzhmXTHnEoRPxSBv2gpO6WEmfIrLvQzqWVS96rmwv/pMx62U3pxTJAcQRKBCzgT/EOh2lT886h6Gj71z43zCg+u3smvv0bjoe4IIUe8omurBXNLXXGY01vboXNXs2NpcVg1sX4Uk0NPhuR9Wh/S05bj6T4Tqke007g9lWFI8+gM8zRl4yLbafsQmk6ZmPO/6sF2oT+qfqgv2Tw1PH0nafoHYxGarjIEmVlNNS1mkFx8+CHmb36ntk/FKcFtX9zmuRXbMLWSP3XB9YjTeYZPtlOjzXdx5sXKrRJsJl/pfop8XWTjfI0HBvo30h2K0=";

        NPCRegistry registy = CitizensAPI.getNPCRegistry();
        NPC npc = registy.createNPC(EntityType.PLAYER, "§e§lGATE KEEPER");
        LookClose trait = npc.getOrAddTrait(LookClose.class);
        FloatingNametag.createFloatingName("§cMort", location);
        trait.setRealisticLooking(true);
        trait.setRange(3d);
        trait.setRandomLook(false);
        trait.lookClose(true);
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("Bob", mortTextureSignature, mortTextureValue);
        npc.spawn(location);
    }
}
