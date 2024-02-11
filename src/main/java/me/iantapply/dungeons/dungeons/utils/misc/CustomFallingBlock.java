package me.iantapply.dungeons.dungeons.utils.misc;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomFallingBlock extends EntityFallingBlock {

    net.minecraft.server.v1_8_R3.World world;

    public CustomFallingBlock(World world, double x, double y, double z, Block block) {
        super(((CraftWorld) world).getHandle(),
                x, y, z,
                net.minecraft.server.v1_8_R3.Block.getByCombinedId(
                        block.getTypeId() + (block.getData() << 12)
                )
        );
        this.world = ((CraftWorld) world).getHandle();

        setPosition(x, y, z);
    }

    public void spawn() {
        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
}
