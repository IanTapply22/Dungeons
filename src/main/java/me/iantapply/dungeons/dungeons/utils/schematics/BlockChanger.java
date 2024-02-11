package me.iantapply.dungeons.dungeons.utils.schematics;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.cryptomorin.xseries.ReflectionUtils;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author TheGaming999
 * @version 1.6
 * @apiNote 1.7 - 1.19 easy to use class to take advantage of different methods
 * that allow you to change blocks at rocket speeds
 * <p>
 * Made with the help of <a href=
 * "https://github.com/CryptoMorin/XSeries/blob/master/src/main/java/com/cryptomorin/xseries/ReflectionUtils.java">ReflectionUtils</a>
 * by <a href="https://github.com/CryptoMorin">CryptoMorin</a>
 * </p>
 * <p>
 * Uses the methods found
 * <a href="https://www.spigotmc.org/threads/395868/">here</a> by
 * <a href="https://www.spigotmc.org/members/220001/">NascentNova</a>
 * </p>
 * <p>
 * Async methods were made using
 * <a href="https://www.spigotmc.org/threads/409003/">How to handle
 * heavy splittable tasks</a> by
 * <a href="https://www.spigotmc.org/members/43809/">7smile7</a>
 * </p>
 */
public class BlockChanger extends GKListener {

    private static final Map<Material, Object> NMS_BLOCK_MATERIALS = new HashMap<>();
    private static final Map<World, Object> NMS_WORLDS = new HashMap<>();
    private static final Map<String, Object> NMS_WORLD_NAMES = new HashMap<>();
    private static final MethodHandle WORLD_GET_HANDLE;
    /**
     * <p>
     * Invoked parameters ->
     * <i>CraftItemStack.asNMSCopy({@literal<org.bukkit.inventory.ItemStack>})</i>
     */
    private static final MethodHandle NMS_ITEM_STACK_COPY;
    /**
     * <p>
     * Invoked parameters ->
     * <i>Block.asBlock({@literal<net.minecraft.world.item.Item>})</i>
     */
    private static final MethodHandle NMS_BLOCK_FROM_ITEM;
    /**
     * <p>
     * Invoked parameters ->
     * <i>{@literal<net.minecraft.world.item.ItemStack>}.getItem()</i>
     */
    private static final MethodHandle NMS_ITEM_STACK_TO_ITEM;
    /**
     * <p>
     * Changes block data / durability
     * </p>
     * <p>
     * Invoked parameters ->
     * <i>{@literal<net.minecraft.world.block.Block>}.fromLegacyData({@literal<int>});</i>
     * </p>
     */
    private static final MethodHandle BLOCK_DATA_FROM_LEGACY_DATA;
    /**
     * <p>
     * Invoked parameters ->
     * <i>{@literal<net.minecraft.world.level.block.Block>}.getBlockData()</i>
     */
    private static final MethodHandle ITEM_TO_BLOCK_DATA;
    private static final MethodHandle SET_TYPE_AND_DATA;
    private static final MethodHandle WORLD_GET_CHUNK;
    private static final MethodHandle CHUNK_GET_SECTIONS;
    private static final MethodHandle CHUNK_SECTION_SET_TYPE;
    /**
     * <p>
     * Behavior -> <i>{@literal<Chunk>}.getLevelHeightAccessor()</i>
     */
    private static final MethodHandle GET_LEVEL_HEIGHT_ACCESSOR;
    /**
     * <p>
     * Behavior -> <i>{@literal<Chunk>}.getSectionIndex()</i> or
     * <i>{@literal<LevelHeightAccessor>}.getSectionIndex()</i>
     */
    private static final MethodHandle GET_SECTION_INDEX;
    /**
     * <p>
     * Behavior -> <i>Chunk.getSections[{@literal<index>}] =
     * {@literal<ChunkSection>}</i>
     * </p>
     */
    private static final MethodHandle SET_SECTION_ELEMENT;
    private static final MethodHandle CHUNK_SECTION;
    private static final MethodHandle CHUNK_SET_TYPE;
    private static final MethodHandle BLOCK_NOTIFY;
    private static final MethodHandle CRAFT_BLOCK_GET_NMS_BLOCK;
    private static final MethodHandle NMS_BLOCK_GET_BLOCK_DATA;
    private static final MethodHandle WORLD_REMOVE_TILE_ENTITY;
    private static final MethodHandle WORLD_CAPTURED_TILE_ENTITIES;
    private static final MethodHandle IS_TILE_ENTITY;
    private static final MethodHandle GET_NMS_TILE_ENTITY;
    private static final MethodHandle GET_SNAPSHOT_NBT;
    private static final MethodHandle GET_SNAPSHOT;
    private static final BlockUpdater BLOCK_UPDATER;
    private static final BlockPositionConstructor BLOCK_POSITION_CONSTRUCTOR;
    private static final BlockDataRetriever BLOCK_DATA_GETTER;
    private static final TileEntityManager TILE_ENTITY_MANAGER;
    private static final String AVAILABLE_BLOCKS;
    private static final UncheckedSetters UNCHECKED_SETTERS;
    private final WorkloadRunnable WORKLOAD_RUNNABLE;
    private static final JavaPlugin PLUGIN;
    private static final Object AIR_BLOCK_DATA;

    static {

        Class<?> worldServer = ReflectionUtils.getNMSClass("server.level", "WorldServer");
        Class<?> world = ReflectionUtils.getNMSClass("world.level", "World");
        Class<?> craftWorld = ReflectionUtils.getCraftClass("CraftWorld");
        Class<?> craftBlock = ReflectionUtils.getCraftClass("block.CraftBlock");
        Class<?> blockPosition = ReflectionUtils.supports(8) ? ReflectionUtils.getNMSClass("core", "BlockPosition")
                : null;
        Class<?> mutableBlockPosition = ReflectionUtils.supports(8)
                ? ReflectionUtils.getNMSClass("core", "BlockPosition$MutableBlockPosition") : null;
        Class<?> blockData = ReflectionUtils.supports(8)
                ? ReflectionUtils.getNMSClass("world.level.block.state", "IBlockData") : null;
        Class<?> craftItemStack = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
        Class<?> worldItemStack = ReflectionUtils.getNMSClass("world.item", "ItemStack");
        Class<?> item = ReflectionUtils.getNMSClass("world.item", "Item");
        Class<?> block = ReflectionUtils.getNMSClass("world.level.block", "Block");
        Class<?> chunk = ReflectionUtils.getNMSClass("world.level.chunk", "Chunk");
        Class<?> chunkSection = ReflectionUtils.getNMSClass("world.level.chunk", "ChunkSection");
        Class<?> levelHeightAccessor = ReflectionUtils.supports(17)
                ? ReflectionUtils.getNMSClass("world.level.LevelHeightAccessor") : null;
        Class<?> blockDataReference = ReflectionUtils.supports(13) ? craftBlock : block;
        Class<?> craftBlockEntityState = ReflectionUtils.supports(12)
                ? ReflectionUtils.getCraftClass("block.CraftBlockEntityState")
                : ReflectionUtils.getCraftClass("block.CraftBlockState");
        Class<?> nbtTagCompound = ReflectionUtils.getNMSClass("nbt", "NBTTagCompound");

        Method getNMSBlockMethod = null;

        if (ReflectionUtils.VER <= 12) {
            try {
                getNMSBlockMethod = craftBlock.getDeclaredMethod("getNMSBlock");
                getNMSBlockMethod.setAccessible(true);
            } catch (NoSuchMethodException | SecurityException e2) {
                e2.printStackTrace();
            }
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Object airBlockData = null;
        try {
            airBlockData = lookup
                    .findStatic(block, ReflectionUtils.supports(18) ? "a" : "getByCombinedId",
                            MethodType.methodType(blockData, int.class))
                    .invoke(0);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        AIR_BLOCK_DATA = airBlockData;

        MethodHandle worldGetHandle = null;
        MethodHandle blockPositionXYZ = null;
        MethodHandle nmsItemStackCopy = null;
        MethodHandle blockFromItem = null;
        MethodHandle nmsItemStackToItem = null;
        MethodHandle itemToBlockData = null;
        MethodHandle setTypeAndData = null;
        MethodHandle worldGetChunk = null;
        MethodHandle chunkSetTypeM = null;
        MethodHandle blockNotify = null;
        MethodHandle chunkGetSections = null;
        MethodHandle chunkSectionSetType = null;
        MethodHandle getLevelHeightAccessor = null;
        MethodHandle getSectionIndex = null;
        MethodHandle setSectionElement = null;
        MethodHandle chunkSectionConstructor = null;
        MethodHandle blockDataFromLegacyData = null;
        MethodHandle mutableBlockPositionSet = null;
        MethodHandle mutableBlockPositionXYZ = null;
        MethodHandle craftBlockGetNMSBlock = null;
        MethodHandle nmsBlockGetBlockData = null;
        MethodHandle worldRemoveTileEntity = null;
        MethodHandle worldCapturedTileEntities = null;
        MethodHandle capturedTileEntitiesContainsKey = null;
        MethodHandle getNMSTileEntity = null;
        MethodHandle getSnapshot = null;
        MethodHandle getSnapshotNBT = null;

        // Method names
        String asBlock = ReflectionUtils.supports(18) || ReflectionUtils.VER < 8 ? "a" : "asBlock";
        String getBlockData = ReflectionUtils.supports(19) ? "m" : ReflectionUtils.supports(18) ? "n" : "getBlockData";
        String getItem = ReflectionUtils.supports(18) ? "c" : "getItem";
        String setType = ReflectionUtils.supports(18) ? "a" : "setTypeAndData";
        String getChunkAt = ReflectionUtils.supports(18) ? "d" : "getChunkAt";
        String chunkSetType = ReflectionUtils.supports(18) ? "a" : ReflectionUtils.VER < 8 ? "setTypeId"
                : ReflectionUtils.VER <= 12 ? "a" : "setType";
        String notify = ReflectionUtils.supports(18) ? "a" : "notify";
        String getSections = ReflectionUtils.supports(18) ? "d" : "getSections";
        String sectionSetType = ReflectionUtils.supports(18) ? "a" : ReflectionUtils.VER < 8 ? "setTypeId" : "setType";
        String setXYZ = ReflectionUtils.supports(13) ? "d" : "c";
        String getBlockData2 = ReflectionUtils.supports(13) ? "getNMS" : "getBlockData";
        String removeTileEntity = ReflectionUtils.supports(19) ? "n" : ReflectionUtils.supports(18) ? "m"
                : ReflectionUtils.supports(14) ? "removeTileEntity" : ReflectionUtils.supports(13) ? "n"
                : ReflectionUtils.supports(9) ? "s" : ReflectionUtils.supports(8) ? "t" : "p";

        MethodType notifyMethodType = ReflectionUtils.VER >= 14 ? MethodType.methodType(void.class, blockPosition,
                blockData, blockData, int.class)
                : ReflectionUtils.VER < 8 ? MethodType.methodType(void.class, int.class, int.class, int.class)
                : ReflectionUtils.VER == 8 ? MethodType.methodType(void.class, blockPosition)
                : MethodType.methodType(void.class, blockPosition, blockData, blockData, int.class);

        MethodType chunkSetTypeMethodType = ReflectionUtils.VER <= 12
                ? ReflectionUtils.VER >= 8 ? MethodType.methodType(blockData, blockPosition, blockData)
                : MethodType.methodType(boolean.class, int.class, int.class, int.class, block, int.class)
                : MethodType.methodType(blockData, blockPosition, blockData, boolean.class);

        MethodType chunkSectionSetTypeMethodType = ReflectionUtils.VER >= 14 ? MethodType.methodType(blockData,
                int.class, int.class, int.class, blockData)
                : ReflectionUtils.VER < 8 ? MethodType.methodType(void.class, int.class, int.class, int.class, block)
                : MethodType.methodType(void.class, int.class, int.class, int.class, blockData);

        MethodType chunkSectionConstructorMT = ReflectionUtils.supports(18) ? null
                : ReflectionUtils.supports(14) ? MethodType.methodType(void.class, int.class)
                : MethodType.methodType(void.class, int.class, boolean.class);

        MethodType removeTileEntityMethodType = ReflectionUtils.supports(8)
                ? MethodType.methodType(void.class, blockPosition)
                : MethodType.methodType(void.class, int.class, int.class, int.class);

        MethodType fromLegacyDataMethodType = ReflectionUtils.VER <= 12 ? MethodType.methodType(blockData, int.class)
                : null;

        BlockPositionConstructor blockPositionConstructor = null;

        try {
            worldGetHandle = lookup.findVirtual(craftWorld, "getHandle", MethodType.methodType(worldServer));
            worldGetChunk = lookup.findVirtual(worldServer, getChunkAt,
                    MethodType.methodType(chunk, int.class, int.class));
            nmsItemStackCopy = lookup.findStatic(craftItemStack, "asNMSCopy",
                    MethodType.methodType(worldItemStack, ItemStack.class));
            blockFromItem = lookup.findStatic(block, asBlock, MethodType.methodType(block, item));
            if (ReflectionUtils.supports(8)) {
                blockPositionXYZ = lookup.findConstructor(blockPosition,
                        MethodType.methodType(void.class, int.class, int.class, int.class));
                mutableBlockPositionXYZ = lookup.findConstructor(mutableBlockPosition,
                        MethodType.methodType(void.class, int.class, int.class, int.class));
                itemToBlockData = lookup.findVirtual(block, getBlockData, MethodType.methodType(blockData));
                setTypeAndData = lookup.findVirtual(worldServer, setType,
                        MethodType.methodType(boolean.class, blockPosition, blockData, int.class));
                mutableBlockPositionSet = lookup.findVirtual(mutableBlockPosition, setXYZ,
                        MethodType.methodType(mutableBlockPosition, int.class, int.class, int.class));
                blockPositionConstructor = new BlockPositionNormal(blockPositionXYZ, mutableBlockPositionXYZ,
                        mutableBlockPositionSet);
            } else {
                blockPositionXYZ = lookup.findConstructor(Location.class,
                        MethodType.methodType(void.class, World.class, double.class, double.class, double.class));
                mutableBlockPositionXYZ = lookup.findConstructor(Location.class,
                        MethodType.methodType(void.class, World.class, double.class, double.class, double.class));
                blockPositionConstructor = new BlockPositionAncient(blockPositionXYZ, mutableBlockPositionXYZ);
            }
            nmsItemStackToItem = lookup.findVirtual(worldItemStack, getItem, MethodType.methodType(item));
            blockDataFromLegacyData = ReflectionUtils.VER <= 12
                    ? lookup.findVirtual(block, "fromLegacyData", fromLegacyDataMethodType) : null;
            chunkSetTypeM = lookup.findVirtual(chunk, chunkSetType, chunkSetTypeMethodType);
            blockNotify = lookup.findVirtual(worldServer, notify, notifyMethodType);
            chunkGetSections = lookup.findVirtual(chunk, getSections,
                    MethodType.methodType(ReflectionUtils.toArrayClass(chunkSection)));
            chunkSectionSetType = lookup.findVirtual(chunkSection, sectionSetType, chunkSectionSetTypeMethodType);
            setSectionElement = MethodHandles.arrayElementSetter(ReflectionUtils.toArrayClass(chunkSection));
            chunkSectionConstructor = !ReflectionUtils.supports(18)
                    ? lookup.findConstructor(chunkSection, chunkSectionConstructorMT) : null;
            if (ReflectionUtils.supports(18)) {
                getLevelHeightAccessor = lookup.findVirtual(chunk, "z", MethodType.methodType(levelHeightAccessor));
                getSectionIndex = lookup.findVirtual(levelHeightAccessor, "e",
                        MethodType.methodType(int.class, int.class));
            } else if (ReflectionUtils.supports(17)) {
                getSectionIndex = lookup.findVirtual(chunk, "getSectionIndex",
                        MethodType.methodType(int.class, int.class));
            }
            craftBlockGetNMSBlock = ReflectionUtils.VER <= 12 ? lookup.unreflect(getNMSBlockMethod) : null;
            nmsBlockGetBlockData = lookup.findVirtual(blockDataReference, getBlockData2,
                    MethodType.methodType(blockData));
            worldRemoveTileEntity = lookup.findVirtual(world, removeTileEntity, removeTileEntityMethodType);
            worldCapturedTileEntities = ReflectionUtils.supports(8)
                    ? lookup.findGetter(world, "capturedTileEntities", Map.class) : null;
            capturedTileEntitiesContainsKey = ReflectionUtils.supports(8)
                    ? lookup.findVirtual(Map.class, "containsKey", MethodType.methodType(boolean.class, Object.class))
                    : null;
            Method getTileEntityMethod = craftBlockEntityState.getDeclaredMethod("getTileEntity");
            Method getSnapshotMethod = ReflectionUtils.supports(12)
                    ? craftBlockEntityState.getDeclaredMethod("getSnapshot") : null;
            if (getTileEntityMethod != null) getTileEntityMethod.setAccessible(true);
            if (getSnapshotMethod != null) getSnapshotMethod.setAccessible(true);
            getNMSTileEntity = lookup.unreflect(getTileEntityMethod);
            getSnapshot = ReflectionUtils.supports(12) ? lookup.unreflect(getSnapshotMethod) : null;
            getSnapshotNBT = ReflectionUtils.supports(12)
                    ? lookup.findVirtual(craftBlockEntityState, "getSnapshotNBT", MethodType.methodType(nbtTagCompound))
                    : null;
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        WORLD_GET_HANDLE = worldGetHandle;
        WORLD_GET_CHUNK = worldGetChunk;
        NMS_ITEM_STACK_COPY = nmsItemStackCopy;
        NMS_BLOCK_FROM_ITEM = blockFromItem;
        NMS_ITEM_STACK_TO_ITEM = nmsItemStackToItem;
        ITEM_TO_BLOCK_DATA = itemToBlockData;
        SET_TYPE_AND_DATA = setTypeAndData;
        CHUNK_SET_TYPE = chunkSetTypeM;
        BLOCK_NOTIFY = blockNotify;
        CHUNK_GET_SECTIONS = chunkGetSections;
        CHUNK_SECTION_SET_TYPE = chunkSectionSetType;
        GET_LEVEL_HEIGHT_ACCESSOR = getLevelHeightAccessor;
        GET_SECTION_INDEX = getSectionIndex;
        SET_SECTION_ELEMENT = setSectionElement;
        CHUNK_SECTION = chunkSectionConstructor;
        BLOCK_POSITION_CONSTRUCTOR = blockPositionConstructor;
        BLOCK_DATA_FROM_LEGACY_DATA = blockDataFromLegacyData;
        CRAFT_BLOCK_GET_NMS_BLOCK = craftBlockGetNMSBlock;
        NMS_BLOCK_GET_BLOCK_DATA = nmsBlockGetBlockData;
        WORLD_REMOVE_TILE_ENTITY = worldRemoveTileEntity;
        WORLD_CAPTURED_TILE_ENTITIES = worldCapturedTileEntities;
        IS_TILE_ENTITY = capturedTileEntitiesContainsKey;
        GET_NMS_TILE_ENTITY = getNMSTileEntity;
        GET_SNAPSHOT = getSnapshot;
        GET_SNAPSHOT_NBT = getSnapshotNBT;

        BLOCK_DATA_GETTER = ReflectionUtils.supports(13) ? new BlockDataGetter()
                : ReflectionUtils.supports(8) ? new BlockDataGetterLegacy() : new BlockDataGetterAncient();

        BLOCK_UPDATER = ReflectionUtils.supports(18) ? new BlockUpdaterLatest(BLOCK_NOTIFY, CHUNK_SET_TYPE,
                GET_SECTION_INDEX, GET_LEVEL_HEIGHT_ACCESSOR)
                : ReflectionUtils.supports(17) ? new BlockUpdater17(BLOCK_NOTIFY, CHUNK_SET_TYPE, GET_SECTION_INDEX,
                CHUNK_SECTION, SET_SECTION_ELEMENT)
                : ReflectionUtils.supports(13)
                ? new BlockUpdater13(BLOCK_NOTIFY, CHUNK_SET_TYPE, CHUNK_SECTION, SET_SECTION_ELEMENT)
                : ReflectionUtils.supports(9)
                ? new BlockUpdater9(BLOCK_NOTIFY, CHUNK_SET_TYPE, CHUNK_SECTION, SET_SECTION_ELEMENT)
                : ReflectionUtils.supports(8)
                ? new BlockUpdaterLegacy(BLOCK_NOTIFY, CHUNK_SET_TYPE, CHUNK_SECTION, SET_SECTION_ELEMENT)
                : new BlockUpdaterAncient(BLOCK_NOTIFY, CHUNK_SET_TYPE, CHUNK_SECTION, SET_SECTION_ELEMENT);

        TILE_ENTITY_MANAGER = ReflectionUtils.supports(8) ? new TileEntityManagerSupported()
                : new TileEntityManagerDummy();

        Arrays.stream(Material.values()).filter(Material::isBlock).forEach(BlockChanger::addNMSBlockData);

        NMS_BLOCK_MATERIALS.put(Material.AIR, AIR_BLOCK_DATA);

        AVAILABLE_BLOCKS = String.join(", ",
                NMS_BLOCK_MATERIALS.keySet()
                        .stream()
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()));

        UNCHECKED_SETTERS = new UncheckedSetters();

        PLUGIN = JavaPlugin.getProvidingPlugin(BlockChanger.class);
        Bukkit.getPluginManager().registerEvents(new BlockChanger(), PLUGIN);

        Bukkit.getWorlds().forEach(BlockChanger::addNMSWorld);
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        if(!BlockChanger.NMS_WORLDS.containsKey(e.getWorld())) {
            BlockChanger.addNMSWorld(e.getWorld());
        }
    }

    public BlockChanger() {
        WORKLOAD_RUNNABLE = new WorkloadRunnable();
        WORKLOAD_RUNNABLE.runTaskTimer(PLUGIN, 0, 1);
    }

    /**
     * Simply calls <b>static {}</b> so methods get cached, and ensures that the
     * first setBlock method call is as executed as fast as possible.
     * <p>
     * This already happens when calling a method for the first time.
     * </p>
     * <p>
     * Added for debugging purposes.
     * </p>
     */
    public static void cache() {
    }

    private static void addNMSBlockData(Material material) {
        ItemStack itemStack = new ItemStack(material);
        Object nmsData = getNMSBlockData(itemStack);
        if (nmsData != null) NMS_BLOCK_MATERIALS.put(material, nmsData);
    }

    private static void addNMSWorld(World world) {
        if (world == null) return;
        Object nmsWorld = getNMSWorld(world);
        if (nmsWorld != null) {
            NMS_WORLDS.put(world, nmsWorld);
            NMS_WORLD_NAMES.put(world.getName(), nmsWorld);
        }
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world    world where the block is located
     * @param x        x location point
     * @param y        y location point
     * @param z        z location point
     * @param material block material to apply on the created block
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     */
    public static void setBlock(World world, int x, int y, int z, Material material) {
        setBlock(world, x, y, z, material, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the block is located
     * @param x         x location point
     * @param y         y location point
     * @param z         z location point
     * @param itemStack ItemStack to apply on the created block
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     */
    public static void setBlock(World world, int x, int y, int z, ItemStack itemStack) {
        setBlock(world, x, y, z, itemStack, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world    world where the block is located
     * @param x        x location point
     * @param y        y location point
     * @param z        z location point
     * @param material block material to apply on the created block
     * @param physics  whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     */
    public static void setBlock(World world, int x, int y, int z, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(world);
        Object blockPosition = newBlockPosition(world, x, y, z);
        Object blockData = getBlockData(material);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the block is located
     * @param x         x location point
     * @param y         y location point
     * @param z         z location point
     * @param itemStack ItemStack to apply on the created block
     * @param physics   whether physics such as gravity should be applied or not
     */
    public static void setBlock(World world, int x, int y, int z, ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockPosition = newBlockPosition(world, x, y, z);
        Object blockData = getBlockData(itemStack);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world    world where the block is located
     * @param location location to put the block at
     * @param material block material to apply on the created block
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlock(World world, Location location, Material material) {
        setBlock(world, location, material, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the block is located
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the created block
     */
    public static void setBlock(World world, Location location, ItemStack itemStack) {
        setBlock(world, location, itemStack, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world    world where the block is located
     * @param location location to put the block at
     * @param material block material to apply on the created block
     * @param physics  whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlock(World world, Location location, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(world);
        Object blockPosition = newBlockPosition(world, location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the block is located
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the created block
     * @param physics   whether physics such as gravity should be applied or not
     */
    public static void setBlock(World world, Location location, ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockPosition = newBlockPosition(world, location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(itemStack);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param location location to put the block at
     * @param material block material to apply on the created block
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlock(Location location, Material material) {
        setBlock(location, material, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the created block
     */
    public static void setBlock(Location location, ItemStack itemStack) {
        setBlock(location, itemStack, true);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param location location to put the block at
     * @param material block material to apply on the created block
     * @param physics  whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlock(Location location, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Changes block type using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the created block
     * @param physics   whether physics such as gravity should be applied or not
     */
    public static void setBlock(Location location, ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(itemStack);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeAndData(nmsWorld, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * Asynchronously changes block type using native NMS world block type and data
     * setter {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     * <br>
     * <br>
     * Async within this context means:
     * <ul>
     * <li>There won't be any TPS loss no matter the amount of blocks being set</li>
     * <li>It can be safely executed inside an asynchronous task</li>
     * </ul>
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the created block
     * @param physics   whether physics such as gravity should be applied or not
     */
    public CompletableFuture<Void> setBlockAsynchronously(Location location, ItemStack itemStack,
                                                                 boolean physics) {
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newMutableBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(itemStack);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        BlockSetWorkload workload = new BlockSetWorkload(nmsWorld, blockPosition, blockData, location, physics);
        WORKLOAD_RUNNABLE.addWorkload(workload);
        WORKLOAD_RUNNABLE.whenComplete(() -> workloadFinishFuture.complete(null));
        return workloadFinishFuture;
    }

    /**
     * Mass changes block types using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the blocks are located at
     * @param locations locations to put the block at
     * @param material  block material to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlocks(World world, Collection<Location> locations, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        int applyPhysics = physics ? 3 : 2;
        locations.forEach(location -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            setBlockPosition(blockPosition, x, y, z);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeAndData(nmsWorld, blockPosition, blockData, applyPhysics);
        });
    }

    /**
     * Mass changes block types using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     *
     * @param world     world where the blocks are located at
     * @param locations locations to put the block at
     * @param itemStack ItemStack to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setBlocks(World world, Collection<Location> locations, ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        int applyPhysics = physics ? 3 : 2;
        locations.forEach(location -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            setBlockPosition(blockPosition, x, y, z);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeAndData(nmsWorld, blockPosition, blockData, applyPhysics);
        });
    }

    /**
     * Asynchronously changes block types using native NMS world block type and data
     * setter {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     * <br>
     * <br>
     * Async within this context means:
     * <ul>
     * <li>There won't be any TPS loss no matter the amount of blocks being set</li>
     * <li>It can be safely executed inside an asynchronous task</li>
     * </ul>
     *
     * @param world     world where the blocks are located at
     * @param locations locations to put the block at
     * @param itemStack ItemStack to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static CompletableFuture<Void> setBlocksAsynchronously(World world, Collection<Location> locations,
                                                                  ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1, 1);
        locations.forEach(location -> {
            BlockSetWorkload workload = new BlockSetWorkload(nmsWorld, blockPosition, blockData, location, physics);
            workloadRunnable.addWorkload(workload);
        });
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    /**
     * Asynchronously fills a cuboid from a corner to another with blocks retrieved
     * from the given ItemStack
     * using native NMS world block type and data setter
     * {@code nmsWorld.setTypeAndData(...)},
     * which surpasses bukkit's {@linkplain org.bukkit.block.Block#setType(Material)
     * Block.setType(Material)} speed.
     * <br>
     * <br>
     * Async within this context means:
     * <ul>
     * <li>There won't be any TPS loss no matter the amount of blocks being set</li>
     * <li>It can be safely executed inside an asynchronous task</li>
     * </ul>
     *
     * @param loc1      first corner
     * @param loc2      second corner
     * @param itemStack ItemStack to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static CompletableFuture<Void> setCuboidAsynchronously(Location loc1, Location loc2, ItemStack itemStack,
                                                                  boolean physics) {
        World world = loc1.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int baseX = x1;
        int baseY = y1;
        int baseZ = z1;
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int x3 = 0, y3 = 0, z3 = 0;
        Location location = new Location(world, baseX + x3, baseY + y3, baseZ + z3);
        int cuboidSize = sizeX * sizeY * sizeZ;
        Object blockPosition = newMutableBlockPosition(location);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1, 1);
        for (int i = 0; i < cuboidSize; i++) {
            BlockSetWorkload workload = new BlockSetWorkload(nmsWorld, blockPosition, blockData, location.clone(),
                    physics);
            if (++x3 >= sizeX) {
                x3 = 0;
                if (++y3 >= sizeY) {
                    y3 = 0;
                    ++z3;
                }
            }
            location.setX(baseX + x3);
            location.setY(baseY + y3);
            location.setZ(baseZ + z3);
            workloadRunnable.addWorkload(workload);
        }
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    /**
     * <p>
     * Changes block type using Chunk block setter, which in an NMS code, reads as
     * follows {@code nmsChunk.setType(...)} which surpasses
     * {@code nmsWorld.setTypeAndData(...)}
     * speed due to absence of light updates, the method that
     * {@link #setBlock(Location, Material)} uses. Then,
     * notifies the world of the updated blocks so they can be seen by the players.
     *
     * @param location location to put the block at
     * @param material material to apply on the block
     */
    public static void setChunkBlock(Location location, Material material) {
        setChunkBlock(location, material, false);
    }

    /**
     * <p>
     * Changes block type using Chunk block setter, which in an NMS code, reads as
     * follows {@code nmsChunk.setType(...)} which surpasses
     * {@code nmsWorld.setTypeAndData(...)}
     * speed due to absence of light updates, the method that
     * {@link #setBlock(Location, ItemStack)} uses. Then,
     * notifies the world of the updated blocks so they can be seen by the players.
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the block
     */
    public static void setChunkBlock(Location location, ItemStack itemStack) {
        setChunkBlock(location, itemStack, false);
    }

    /**
     * <p>
     * Changes block type using Chunk block setter, which in an NMS code, reads as
     * follows {@code nmsChunk.setType(...)} which surpasses
     * {@code nmsWorld.setTypeAndData(...)}
     * speed due to absence of light updates, the method that
     * {@link #setBlock(Location, ItemStack)} uses. Then,
     * notifies the world of the updated blocks so they can be seen by the players.
     *
     * @param location location to put the block at
     * @param material material to apply on the block
     * @param physics  whether physics should be applied or not
     */
    public static void setChunkBlock(Location location, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        Object chunk = getChunkAt(nmsWorld, location);
        removeIfTileEntity(nmsWorld, blockPosition);
        setType(chunk, blockPosition, blockData, physics);
        updateBlock(nmsWorld, blockPosition, blockData, physics);
    }

    /**
     * <p>
     * Changes block type using Chunk block setter, which in an NMS code, reads as
     * follows {@code nmsChunk.setType(...)} which surpasses
     * {@code nmsWorld.setTypeAndData(...)}
     * speed due to absence of light updates, the method that
     * {@link #setBlock(Location, ItemStack)} uses. Then,
     * notifies the world of the updated blocks so they can be seen by the players.
     *
     * @param location  location to put the block at
     * @param itemStack itemStack to apply on the block
     * @param physics   whether physics should be applied or not
     */
    public static void setChunkBlock(Location location, ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(itemStack);
        Object chunk = getChunkAt(nmsWorld, location);
        removeIfTileEntity(nmsWorld, blockPosition);
        setType(chunk, blockPosition, blockData, physics);
        updateBlock(nmsWorld, blockPosition, blockData, physics);
    }

    /**
     * As stated in {@link #setChunkBlock(Location, ItemStack, boolean)}:
     * <p>
     * Changes block type using Chunk block setter, which in an NMS code, reads as
     * follows {@code nmsChunk.setType(...)} which surpasses
     * {@code nmsWorld.setTypeAndData(...)}
     * speed due to absence of light updates, the method that
     * {@link #setBlock(Location, ItemStack)} uses. Then,
     * notifies the world of the updated blocks so they can be seen by the players.
     *
     * <p>
     * In addition to that, it makes sure that there is no TPS loss due to the
     * amount of blocks being changed.
     *
     * @param location  location to put the block at
     * @param itemStack itemStack to apply on the block
     * @param physics   whether physics should be applied or not
     */
    public CompletableFuture<Void> setChunkBlockAsynchronously(Location location, ItemStack itemStack,
                                                                      boolean physics) {
        Object nmsWorld = getWorld(location.getWorld());
        Object blockPosition = newMutableBlockPosition(location.getWorld(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        Object blockData = getBlockData(itemStack);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        ChunkSetWorkload workload = new ChunkSetWorkload(nmsWorld, blockPosition, blockData, location, physics);
        WORKLOAD_RUNNABLE.addWorkload(workload);
        WORKLOAD_RUNNABLE.whenComplete(() -> workloadFinishFuture.complete(null));
        return workloadFinishFuture;
    }

    /**
     * Mass change blocks at the given locations using Chunk block setter which
     * doesn't apply light updates but offers
     * better performance in comparison to setBlocks(...)
     *
     * @param world     world where the blocks are located at
     * @param locations locations to put the block at
     * @param itemStack ItemStack to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static void setChunkBlocks(World world, Collection<Location> locations, ItemStack itemStack,
                                      boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        locations.forEach(location -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object chunk = getChunkAt(nmsWorld, x, z);
            setBlockPosition(blockPosition, x, y, z);
            removeIfTileEntity(nmsWorld, blockPosition);
            setType(chunk, blockPosition, blockData, physics);
        });
    }

    /**
     * A thread safe version of
     * {@link #setChunkBlocks(World, Collection, ItemStack, boolean)}*
     * <p>
     * * Mass change blocks at the given locations using Chunk block setter which
     * doesn't apply light updates but offers
     * better performance in comparison to setBlocks(...).
     * <p>
     * With an eye on the server TPS, this method won't degrade the server
     * performance regardless of the
     * amount of blocks being changed in contrast to the regular one.
     *
     * @param world     world where the blocks are located at
     * @param locations locations to put the block at
     * @param itemStack ItemStack to apply on the created blocks
     * @param physics   whether physics such as gravity should be applied or not
     * @throws IllegalArgumentException if material is not perceived as a block
     *                                  material
     * @throws NullPointerException     if the specified material has no block data
     *                                  assigned to it
     */
    public static CompletableFuture<Void> setChunkBlocksAsynchronously(World world, Collection<Location> locations,
                                                                       ItemStack itemStack, boolean physics) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1, 1);
        locations.forEach(location -> {
            ChunkSetWorkload workload = new ChunkSetWorkload(nmsWorld, blockPosition, blockData, location, physics);
            workloadRunnable.addWorkload(workload);
        });
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    /**
     * Changes block type using the fastest method that can set blocks without the
     * need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param location location to put the block at
     * @param material material to apply on the block
     */
    public static Object setSectionBlock(Location location, Material material) {
        return setSectionBlock(location, material, false);
    }

    /**
     * Changes block type using the fastest method that can set blocks without the
     * need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param location location to put the block at
     * @param material material to apply on the block
     * @param physics  whether physics should be applied or not
     */
    public static Object setSectionBlock(Location location, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        World world = location.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Object nmsChunk = getChunkAt(nmsWorld, location);
        int j = x & 15;
        int k = y & 15;
        int l = z & 15;
        Object[] sections = getSections(nmsChunk);
        Object section = getSection(nmsChunk, sections, y);
        Object blockPosition = newBlockPosition(world, x, y, z);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeChunkSection(section, j, k, l, blockData);
        updateBlock(nmsWorld, blockPosition, blockData, physics);

        return nmsChunk;
    }

    /**
     * Changes block type using the fastest method that can set blocks without the
     * need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the block
     */
    public static Object setSectionBlock(Location location, ItemStack itemStack) {
        return setSectionBlock(location, itemStack, false);
    }

    /**
     * Changes block type using the fastest method that can set blocks without the
     * need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the block
     * @param physics   whether physics should be applied or not
     */
    public static Object setSectionBlock(Location location, ItemStack itemStack, boolean physics) {
        World world = location.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Object nmsChunk = getChunkAt(nmsWorld, location);
        int j = x & 15;
        int k = y & 15;
        int l = z & 15;
        Object[] sections = getSections(nmsChunk);
        Object section = getSection(nmsChunk, sections, y);
        Object blockPosition = newBlockPosition(world, x, y, z);
        removeIfTileEntity(nmsWorld, blockPosition);
        setTypeChunkSection(section, j, k, l, blockData);
        updateBlock(nmsWorld, blockPosition, blockData, physics);

        return nmsChunk;
    }

    /**
     * Changes block type using the fastest method that can set blocks without the
     * need to restart the server
     * {@code chunkSection.setType(...)} asynchronously
     *
     * @param location  location to put the block at
     * @param itemStack ItemStack to apply on the block
     * @param physics   whether physics should be applied or not
     */
    public CompletableFuture<Void> setSectionBlockAsynchronously(Location location, ItemStack itemStack,
                                                                        boolean physics) {
        World world = location.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<Void>();
        SectionSetWorkload workload = new SectionSetWorkload(nmsWorld, blockPosition, blockData, location, physics);
        WORKLOAD_RUNNABLE.addWorkload(workload);
        WORKLOAD_RUNNABLE.whenComplete(() -> workloadFinishFuture.complete(null));
        return workloadFinishFuture;
    }

    /**
     * Mass changes block types using the fastest method that can set blocks without
     * the need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param locations locations to put the blocks at
     * @param material  material to apply on the blocks
     * @param world     world where locations are taken from
     */
    public static void setSectionBlocks(World world, Collection<Location> locations, Material material) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        locations.forEach(location -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = getChunkAt(nmsWorld, location);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = getSections(nmsChunk);
            Object section = getSection(nmsChunk, sections, y);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeChunkSection(section, j, k, l, blockData);
            setBlockPosition(blockPosition, x, y, z);
            updateBlock(nmsWorld, blockPosition, blockData, false);
        });
    }

    /**
     * Mass changes block types using the fastest method that can set blocks without
     * the need to restart the server
     * {@code chunkSection.setType(...)}
     *
     * @param locations locations to put the blocks at
     * @param itemStack ItemStack to apply on the blocks
     * @param world     world where locations are taken from
     */
    public static void setSectionBlocks(World world, Collection<Location> locations, ItemStack itemStack) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        locations.forEach(location -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = getChunkAt(nmsWorld, location);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = getSections(nmsChunk);
            Object section = getSection(nmsChunk, sections, y);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeChunkSection(section, j, k, l, blockData);
            setBlockPosition(blockPosition, x, y, z);
            updateBlock(nmsWorld, blockPosition, blockData, false);
        });
    }

    /**
     * Mass changes block types using the fastest method that can set blocks without
     * the need to restart the server
     * {@code chunkSection.setType(...)} asynchronously
     *
     * @param locations locations to put the blocks at
     * @param itemStack ItemStack to apply on the blocks
     * @param world     world where locations are taken from
     */
    public static CompletableFuture<Void> setSectionBlocksAsynchronously(World world, Collection<Location> locations,
                                                                         ItemStack itemStack) {
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        Object blockPosition = newMutableBlockPosition(world, 0, 0, 0);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1, 1);
        locations.forEach(location -> {
            SectionSetWorkload workload = new SectionSetWorkload(nmsWorld, blockPosition, blockData, location, false);
            workloadRunnable.addWorkload(workload);
        });
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b>
     *
     * @param loc1     point 1
     * @param loc2     point 2
     * @param material material to apply on the blocks
     */
    public static void setSectionCuboid(Location loc1, Location loc2, Material material) {
        setSectionCuboid(loc1, loc2, material, false);
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b>
     *
     * @param loc1     point 1
     * @param loc2     point 2
     * @param material material to apply on the blocks
     * @param physics  whether to apply physics or not
     */
    public static void setSectionCuboid(Location loc1, Location loc2, Material material, boolean physics) {
        if (!material.isBlock()) throw new IllegalArgumentException("The specified material is not a placeable block!");
        World world = loc1.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(material);
        if (blockData == null)
            throw new NullPointerException("Unable to retrieve block data for the corresponding material.");
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int baseX = x1;
        int baseY = y1;
        int baseZ = z1;
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int x3 = 0, y3 = 0, z3 = 0;
        Location location = new Location(loc1.getWorld(), baseX + x3, baseY + y3, baseZ + z3);
        int cuboidSize = sizeX * sizeY * sizeZ;
        Object blockPosition = newMutableBlockPosition(location);
        for (int i = 0; i < cuboidSize; i++) {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = getChunkAt(nmsWorld, location);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = getSections(nmsChunk);
            Object section = getSection(nmsChunk, sections, y);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeChunkSection(section, j, k, l, blockData);
            setBlockPosition(blockPosition, x, y, z);
            updateBlock(nmsWorld, blockPosition, blockData, physics);
            if (++x3 >= sizeX) {
                x3 = 0;
                if (++y3 >= sizeY) {
                    y3 = 0;
                    ++z3;
                }
            }
            location.setX(baseX + x3);
            location.setY(baseY + y3);
            location.setZ(baseZ + z3);
        }
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b>
     *
     * @param loc1      point 1
     * @param loc2      point 2
     * @param itemStack ItemStack to apply on the blocks
     */
    public static void setSectionCuboid(Location loc1, Location loc2, ItemStack itemStack) {
        setSectionCuboid(loc1, loc2, itemStack, false);
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b>
     *
     * @param loc1      point 1
     * @param loc2      point 2
     * @param itemStack ItemStack to apply on the blocks
     */
    public static void setSectionCuboid(Location loc1, Location loc2, ItemStack itemStack, boolean physics) {
        World world = loc1.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int baseX = x1;
        int baseY = y1;
        int baseZ = z1;
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int x3 = 0, y3 = 0, z3 = 0;
        Location location = new Location(loc1.getWorld(), baseX + x3, baseY + y3, baseZ + z3);
        int cuboidSize = sizeX * sizeY * sizeZ;
        Object blockPosition = newMutableBlockPosition(location);
        for (int i = 0; i < cuboidSize; i++) {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = getChunkAt(nmsWorld, location);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = getSections(nmsChunk);
            Object section = getSection(nmsChunk, sections, y);
            removeIfTileEntity(nmsWorld, blockPosition);
            setTypeChunkSection(section, j, k, l, blockData);
            setBlockPosition(blockPosition, x, y, z);
            updateBlock(nmsWorld, blockPosition, blockData, physics);
            if (++x3 >= sizeX) {
                x3 = 0;
                if (++y3 >= sizeY) {
                    y3 = 0;
                    ++z3;
                }
            }
            location.setX(baseX + x3);
            location.setY(baseY + y3);
            location.setZ(baseZ + z3);
        }
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b> asynchronously
     *
     * @param loc1      point 1
     * @param loc2      point 2
     * @param itemStack ItemStack to apply on the blocks
     */
    public static CompletableFuture<Void> setSectionCuboidAsynchronously(Location loc1, Location loc2,
                                                                         ItemStack itemStack) {
        return setSectionCuboidAsynchronously(loc1, loc2, itemStack, false);
    }

    /**
     * but creates a cuboid from a location
     * to another as if using the vanilla command <b>/fill</b> asynchronously
     *
     * @param loc1      point 1
     * @param loc2      point 2
     * @param itemStack ItemStack to apply on the blocks
     */
    public static CompletableFuture<Void> setSectionCuboidAsynchronously(Location loc1, Location loc2,
                                                                         ItemStack itemStack, boolean physics) {
        World world = loc1.getWorld();
        Object nmsWorld = getWorld(world);
        Object blockData = getBlockData(itemStack);
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int baseX = x1;
        int baseY = y1;
        int baseZ = z1;
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int x3 = 0, y3 = 0, z3 = 0;
        Location location = new Location(loc1.getWorld(), baseX + x3, baseY + y3, baseZ + z3);
        int cuboidSize = sizeX * sizeY * sizeZ;
        Object blockPosition = newMutableBlockPosition(location);
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 1, 1);
        for (int i = 0; i < cuboidSize; i++) {
            SectionSetWorkload workload = new SectionSetWorkload(nmsWorld, blockPosition, blockData, location.clone(),
                    physics);
            if (++x3 >= sizeX) {
                x3 = 0;
                if (++y3 >= sizeY) {
                    y3 = 0;
                    ++z3;
                }
            }
            location.setX(baseX + x3);
            location.setY(baseY + y3);
            location.setZ(baseZ + z3);
            workloadRunnable.addWorkload(workload);
        }
        workloadRunnable.whenComplete(() -> {
            workloadFinishFuture.complete(null);
            workloadTask.cancel();
        });
        return workloadFinishFuture;
    }

    private static Object getSection(Object nmsChunk, Object[] sections, int y) {
        return BLOCK_UPDATER.getSection(nmsChunk, sections, y);
    }

    private static Object[] getSections(Object nmsChunk) {
        try {
            return (Object[]) CHUNK_GET_SECTIONS.invoke(nmsChunk);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setTypeChunkSection(Object chunkSection, int x, int y, int z, Object blockData) {
        try {
            CHUNK_SECTION_SET_TYPE.invoke(chunkSection, x, y, z, blockData);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void setTypeAndData(Object nmsWorld, Object blockPosition, Object blockData, int physics) {
        try {
            SET_TYPE_AND_DATA.invoke(nmsWorld, blockPosition, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        BLOCK_UPDATER.setType(chunk, blockPosition, blockData, physics);
    }

    private static Object getChunkAt(Object world, Location loc) {
        try {
            return WORLD_GET_CHUNK.invoke(world, loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getChunkAt(Object world, int x, int z) {
        try {
            return WORLD_GET_CHUNK.invoke(world, x >> 4, z >> 4);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getNMSWorld(@Nonnull World world) {
        try {
            return WORLD_GET_HANDLE.invoke(world);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static @Nullable
    Object getNMSBlockData(@Nullable ItemStack itemStack) {
        try {
            if (itemStack == null) return null;
            Object nmsItemStack = NMS_ITEM_STACK_COPY.invoke(itemStack);
            if (nmsItemStack == null) return null;
            Object nmsItem = NMS_ITEM_STACK_TO_ITEM.invoke(nmsItemStack);
            Object block = NMS_BLOCK_FROM_ITEM.invoke(nmsItem);
            if (ReflectionUtils.VER < 8) return block;
            return ITEM_TO_BLOCK_DATA.invoke(block);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isTileEntity(Object nmsWorld, Object blockPosition) {
        return TILE_ENTITY_MANAGER.isTileEntity(nmsWorld, blockPosition);
    }

    private static boolean removeIfTileEntity(Object nmsWorld, Object blockPosition) {
        if (!isTileEntity(nmsWorld, blockPosition)) return false;
        TILE_ENTITY_MANAGER.destroyTileEntity(nmsWorld, blockPosition);
        return true;
    }

    public static Object getTileEntity(Block block) {
        try {
            return GET_NMS_TILE_ENTITY.invoke(block.getState());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // 1.12+ only
    public static Object getSnapshotNBT(Block block) {
        try {
            return GET_SNAPSHOT_NBT.invoke(block.getState());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // 1.12+ only
    public static String debugSnapshotNBT(Block block) {
        try {
            return GET_SNAPSHOT_NBT.invoke(block.getState()).toString();
        } catch (Throwable e) {
            return "{" + block.getType() + "} is not a tile entity!";
        }
    }

    public static String debugTileEntity(Block block) {
        try {
            return GET_NMS_TILE_ENTITY.invoke(block.getState()).toString() + " (Tile Entity)";
        } catch (Throwable e) {
            return "{" + block.getType() + "} is not a tile entity!";
        }
    }

    // 1.12+ only
    public static Object getSnapshot(Block block) {
        try {
            return GET_SNAPSHOT.invoke(block.getState());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // 1.12+ only
    public static String debugStoredSnapshot(Block block) {
        try {
            return GET_SNAPSHOT.invoke(block.getState()).toString() + " (Tile Entity)";
        } catch (Throwable e) {
            return "{" + block.getType() + "} is not a tile entity!";
        }
    }

    /**
     * Refreshes a block so it appears to the players
     *
     * @param world         nms world {@link #getWorld(World)}
     * @param blockPosition nms block position
     *                      {@link #newBlockPosition(Object, Object, Object, Object)}
     * @param blockData     nms block data {@link #getBlockData(Material)}
     * @param physics       whether physics should be applied or not
     */
    public static void updateBlock(Object world, Object blockPosition, Object blockData, boolean physics) {
        BLOCK_UPDATER.update(world, blockPosition, blockData, physics ? 3 : 2);
    }

    /**
     * @param world (Bukkit world) can be null for versions 1.8+
     * @param x     point
     * @param y     point
     * @param z     point
     * @return constructs an unmodifiable block position
     */
    public static Object newBlockPosition(@Nullable Object world, Object x, Object y, Object z) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.newBlockPosition(world, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param world (Bukkit world) can be null for 1.8+
     * @param x     x pos
     * @param y     y pos
     * @param z     z pos
     * @return constructs a mutable block position that can be modified using
     * {@link #setBlockPosition(Object, Object, Object, Object)}
     */
    public static Object newMutableBlockPosition(@Nullable Object world, Object x, Object y, Object z) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.newMutableBlockPosition(world, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param location Location to get coordinates from
     * @return constructs a mutable block position that can be modified using
     * {@link #setBlockPosition(Object, Object, Object, Object)}
     */
    public static Object newMutableBlockPosition(Location location) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.newMutableBlockPosition(location.getWorld(), location.getBlockX(),
                    location.getBlockY(), location.getBlockZ());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param mutableBlockPosition MutableBlockPosition to modify
     * @param x                    new x pos
     * @param y                    new y pos
     * @param z                    new z pos
     * @return modified MutableBlockPosition (no need to set the variable to the
     * returned MutableBlockPosition)
     */
    public static Object setBlockPosition(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            return BLOCK_POSITION_CONSTRUCTOR.set(mutableBlockPosition, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param itemStack bukkit ItemStack
     * @return nms block data from bukkit item stack
     * @throws IllegalArgumentException if material is not a block
     */
    public static @Nonnull
    Object getBlockData(@Nonnull ItemStack itemStack) {
        Object blockData = BLOCK_DATA_GETTER.fromItemStack(itemStack);
        if (blockData == null) throw new IllegalArgumentException("Couldn't convert specified itemstack to block data");
        return blockData;
    }

    /**
     * @param material to get block data for
     * @return stored nms block data for the specified material
     */
    public static @Nullable
    Object getBlockData(@Nullable Material material) {
        return NMS_BLOCK_MATERIALS.get(material);
    }

    /**
     * This method should get block data even if block is not actually placed i.e
     * doesn't have location
     * <p>
     * Doesn't retrieve the tile entity as of now
     * </p>
     *
     * @param block bukkit block to cast to nms block data
     * @return nms block data from bukkit block
     */
    public static @Nonnull
    Object getBlockData(Block block) {
        Object blockData = BLOCK_DATA_GETTER.fromBlock(block);
        return blockData != null ? blockData : AIR_BLOCK_DATA;
    }

    /**
     * @return nms air block data
     */
    public static Object getAirBlockData() {
        return AIR_BLOCK_DATA;
    }

    /**
     * @param world to get nms world for
     * @return stored nms world for the specified world
     */
    public static Object getWorld(World world) {
        return NMS_WORLDS.get(world);
    }

    /**
     * @param worldName to get nms world for
     * @return stored nms world for the specified world name
     */
    public static Object getWorld(String worldName) {
        return NMS_WORLD_NAMES.get(worldName);
    }

    /**
     * @return all available block materials for the current version separated by
     * commas as follows:
     * <p>
     * <i>dirt, stone, glass, etc...</i>
     */
    public static String getAvailableBlockMaterials() {
        return AVAILABLE_BLOCKS;
    }

    /**
     * physics: 3 = yes, 2 = no
     *
     * @return methods that accept nms objects
     */
    public static UncheckedSetters getUncheckedSetters() {
        return UNCHECKED_SETTERS;
    }

    /**
     * @apiNote physics: 3 = yes, 2 = no
     */
    public static class UncheckedSetters {

        /**
         * @param nmsWorld      using {@link BlockChanger#getWorld(World)
         *                      getWorld(World)} or {@link BlockChanger#getWorld(String)
         *                      getWorld(String)}
         * @param blockPosition using
         *                      {@link BlockChanger#newMutableBlockPosition(Location)
         *                      newMutableBlockPosition(Location)}
         * @param nmsBlockData  {@link BlockChanger#getBlockData(ItemStack)
         *                      getBlockData(ItemStack)} or
         *                      {@link BlockChanger#getBlockData(Material)
         *                      getBlockData(Material)}
         * @param physics       3 = applies physics, 2 = doesn't
         *                      <p>
         *                      <i>blockPosition</i> can be further modified with new
         *                      coordinates using
         *                      {@link BlockChanger#setBlockPosition(Object, Object, Object, Object)}
         */
        public void setBlock(Object nmsWorld, Object blockPosition, Object nmsBlockData, int physics) {
            setTypeAndData(nmsWorld, blockPosition, nmsBlockData, physics);
        }

        /**
         * @param nmsWorld      using {@link BlockChanger#getWorld(World)
         *                      getWorld(World)} or {@link BlockChanger#getWorld(String)
         *                      getWorld(String)}
         * @param blockPosition using
         *                      {@link BlockChanger#newMutableBlockPosition(Location)
         *                      newMutableBlockPosition(Location)}
         * @param nmsBlockData  {@link BlockChanger#getBlockData(ItemStack)
         *                      getBlockData(ItemStack)} or
         *                      {@link BlockChanger#getBlockData(Material)
         *                      getBlockData(Material)}
         * @param x             x coordinate of the block
         * @param z             z coordinate of the block
         * @param physics       3 = applies physics, 2 = doesn't
         *                      <p>
         *                      <i>blockPosition</i> can be further modified with new
         *                      coordinates using
         *                      {@link BlockChanger#setBlockPosition(Object, Object, Object, Object)}
         */
        public void setChunkBlock(Object nmsWorld, Object blockPosition, Object nmsBlockData, int x, int z,
                                  boolean physics) {
            Object chunk = getChunkAt(nmsWorld, x, z);
            setType(chunk, blockPosition, nmsBlockData, physics);
            updateBlock(nmsWorld, blockPosition, nmsBlockData, physics);
        }

        /**
         * @param nmsWorld      using {@link BlockChanger#getWorld(World)
         *                      getWorld(World)} or {@link BlockChanger#getWorld(String)
         *                      getWorld(String)}
         * @param blockPosition using
         *                      {@link BlockChanger#newMutableBlockPosition(Location)
         *                      newMutableBlockPosition(Location)}
         * @param nmsBlockData  {@link BlockChanger#getBlockData(ItemStack)
         *                      getBlockData(ItemStack)} or
         *                      {@link BlockChanger#getBlockData(Material)
         *                      getBlockData(Material)}
         * @param x             x coordinate of the block
         * @param y             y coordinate of the block
         * @param z             z coordinate of the block
         * @param physics       3 = applies physics, 2 = doesn't
         *                      <p>
         *                      <i>blockPosition</i> can be further modified with new
         *                      coordinates using
         *                      {@link BlockChanger#setBlockPosition(Object, Object, Object, Object)}
         */
        public void setSectionBlock(Object nmsWorld, Object blockPosition, Object nmsBlockData, int x, int y, int z,
                                    boolean physics) {
            Object nmsChunk = getChunkAt(nmsWorld, x, z);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = getSections(nmsChunk);
            Object section = getSection(nmsChunk, sections, y);
            setTypeChunkSection(section, j, k, l, nmsBlockData);
            updateBlock(nmsWorld, blockPosition, nmsWorld, physics);
        }

    }

    private interface TileEntityManager {

        default Object getCapturedTileEntities(Object nmsWorld) {
            try {
                return WORLD_CAPTURED_TILE_ENTITIES.invoke(nmsWorld);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        default boolean isTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                return (boolean) IS_TILE_ENTITY.invoke(getCapturedTileEntities(nmsWorld), blockPosition);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }

        default void destroyTileEntity(Object nmsWorld, Object blockPosition) {
            try {
                WORLD_REMOVE_TILE_ENTITY.invoke(nmsWorld, blockPosition);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /*
         * Store bukkit block variable {
         * Block block = ...
         * }
         * Get block data (title entity data still exists in old the bukkit block
         * variable) {
         * Object blockData = BlockChanger.getBlockData(block);
         * }
         * Set block using BlockChanger within the method {
         * setType(...)
         * }
         * Check if block is a title entity {
         * isTitleEntity
         * }
         * Get tile entity that was stored in the bukkit block variable {
         * CraftBlockState craftBlockState = (CraftBlockState)block.getState();
         * // getState() creates a new block state with the location of that block
         * TileEntity nmsTileEntity = craftBlockState.getTileEntity();
         * }
         * Set tile entity using BlockChanger {
         * <Use nms method that applies tile entity on the block>
         * }
         */

    }

    public static void updateLight(ArrayList<Object> chunks) {
        for(Object chunk : chunks) {
            BLOCK_UPDATER.updateLighting(chunk);
        }
    }

    private static class TileEntityManagerSupported implements TileEntityManager {
    }

    private static class TileEntityManagerDummy implements TileEntityManager {

        @Override
        public Object getCapturedTileEntities(Object nmsWorld) {
            return null;
        }

        @Override
        public boolean isTileEntity(Object nmsWorld, Object blockPosition) {
            return false;
        }

        @Override
        public void destroyTileEntity(Object nmsWorld, Object blockPosition) {
        }

    }

    private interface BlockDataRetriever {

        default Object getNMSItem(ItemStack itemStack) throws Throwable {
            if (itemStack == null) throw new NullPointerException("ItemStack is null!");
            if (itemStack.getType() == Material.AIR) return null;
            Object nmsItemStack = NMS_ITEM_STACK_COPY.invoke(itemStack);
            if (nmsItemStack == null) throw new IllegalArgumentException("Failed to get NMS ItemStack!");
            return NMS_ITEM_STACK_TO_ITEM.invoke(nmsItemStack);
        }

        // 1.7-1.12 requires 2 methods to get block data
        default Object fromBlock(Block block) {
            try {
                Object nmsBlock = CRAFT_BLOCK_GET_NMS_BLOCK.invoke(block);
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(nmsBlock);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        Object fromItemStack(ItemStack itemStack);

    }

    // 1.13+ or 1.8+ without data support
    private static class BlockDataGetter implements BlockDataRetriever {

        @Override
        public Object fromItemStack(ItemStack itemStack) {
            try {
                Object block = NMS_BLOCK_FROM_ITEM.invoke(getNMSItem(itemStack));
                return ITEM_TO_BLOCK_DATA.invoke(block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        // 1.13+ one method to get block data (getNMS())
        @Override
        public Object fromBlock(Block block) {
            try {
                return NMS_BLOCK_GET_BLOCK_DATA.invoke(block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    // 1.8-1.12
    private static class BlockDataGetterLegacy implements BlockDataRetriever {

        @Override
        public Object fromItemStack(ItemStack itemStack) {
            try {
                Object nmsItem = getNMSItem(itemStack);
                if (nmsItem == null) return AIR_BLOCK_DATA;
                Object block = NMS_BLOCK_FROM_ITEM.invoke(nmsItem);
                short data = itemStack.getDurability();
                return data > 0 ? BLOCK_DATA_FROM_LEGACY_DATA.invoke(block, data) : ITEM_TO_BLOCK_DATA.invoke(block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    // 1.7
    private static class BlockDataGetterAncient implements BlockDataRetriever {

        @Override
        public Object fromItemStack(ItemStack itemStack) {
            try {
                Object nmsItem = getNMSItem(itemStack);
                if (nmsItem == null) return AIR_BLOCK_DATA;
                return NMS_BLOCK_FROM_ITEM.invoke(nmsItem);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static interface Workload {

        boolean compute();

    }

    private static class WorkloadRunnable extends BukkitRunnable {

        private static final double MAX_MILLIS_PER_TICK = 10.0;
        private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

        private final Deque<Workload> workloadDeque = new ArrayDeque<>();

        public void addWorkload(Workload workload) {
            this.workloadDeque.add(workload);
        }

        public void whenComplete(Runnable runnable) {
            WhenCompleteWorkload workload = new WhenCompleteWorkload(runnable);
            this.workloadDeque.add(workload);
        }

        @Override
        public void run() {
            long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

            Workload nextLoad;

            while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
                nextLoad.compute();
            }
        }

    }

    private static class BlockSetWorkload implements Workload {

        private Object nmsWorld;
        private Object blockPosition;
        private Object blockData;
        private Location location;
        private int physics;

        public BlockSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location,
                                boolean physics) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
            this.physics = physics ? 3 : 2;
        }

        @Override
        public boolean compute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(),
                    location.getBlockZ());
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setTypeAndData(nmsWorld, blockPosition, blockData, physics);
            return true;
        }

    }

    private static class ChunkSetWorkload implements Workload {

        private Object nmsWorld;
        private Object blockPosition;
        private Object blockData;
        private Location location;
        private boolean physics;

        public ChunkSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location,
                                boolean physics) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
            this.physics = physics;
        }

        @Override
        public boolean compute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(),
                    location.getBlockZ());
            Object chunk = BlockChanger.getChunkAt(nmsWorld, location.getBlockX(), location.getBlockZ());
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setType(chunk, blockPosition, blockData, physics);
            BlockChanger.updateBlock(nmsWorld, blockPosition, blockData, physics);
            return true;
        }

    }

    private static class SectionSetWorkload implements Workload {

        private Object nmsWorld;
        private Object blockPosition;
        private Object blockData;
        private Location location;
        private boolean physics;

        public SectionSetWorkload(Object nmsWorld, Object blockPosition, Object blockData, Location location,
                                  boolean physics) {
            this.nmsWorld = nmsWorld;
            this.blockPosition = blockPosition;
            this.blockData = blockData;
            this.location = location;
            this.physics = physics;
        }

        @Override
        public boolean compute() {
            BlockChanger.setBlockPosition(blockPosition, location.getBlockX(), location.getBlockY(),
                    location.getBlockZ());
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Object nmsChunk = BlockChanger.getChunkAt(nmsWorld, x, z);
            int j = x & 15;
            int k = y & 15;
            int l = z & 15;
            Object[] sections = BlockChanger.getSections(nmsChunk);
            Object section = BlockChanger.getSection(nmsChunk, sections, y);
            BlockChanger.removeIfTileEntity(nmsWorld, blockPosition);
            BlockChanger.setTypeChunkSection(section, j, k, l, blockData);
            BlockChanger.updateBlock(nmsWorld, blockPosition, blockData, physics);
            return true;
        }

    }

    private static class WhenCompleteWorkload implements Workload {

        private Runnable runnable;

        public WhenCompleteWorkload(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public boolean compute() {
            runnable.run();
            return false;
        }

    }

}

interface BlockPositionConstructor {

    Object newBlockPosition(Object world, Object x, Object y, Object z);

    Object newMutableBlockPosition(Object world, Object x, Object y, Object z);

    Object set(Object mutableBlockPosition, Object x, Object y, Object z);

}

interface BlockUpdater {

    void setType(Object chunk, Object blockPosition, Object blockData, boolean physics);

    void update(Object world, Object blockPosition, Object blockData, int physics);

    Object getSection(Object nmsChunk, Object[] sections, int y);

    int getSectionIndex(Object nmsChunk, int y);

    void updateLighting(Object nmsChunk);

}

class BlockPositionNormal implements BlockPositionConstructor {

    private MethodHandle blockPositionConstructor;
    private MethodHandle mutableBlockPositionConstructor;
    private MethodHandle mutableBlockPositionSet;

    public BlockPositionNormal(MethodHandle blockPositionXYZ, MethodHandle mutableBlockPositionXYZ,
                               MethodHandle mutableBlockPositionSet) {
        this.blockPositionConstructor = blockPositionXYZ;
        this.mutableBlockPositionConstructor = mutableBlockPositionXYZ;
        this.mutableBlockPositionSet = mutableBlockPositionSet;
    }

    @Override
    public Object newBlockPosition(Object world, Object x, Object y, Object z) {
        try {
            return blockPositionConstructor.invoke(x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newMutableBlockPosition(Object world, Object x, Object y, Object z) {
        try {
            return mutableBlockPositionConstructor.invoke(x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object set(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            return mutableBlockPositionSet.invoke(mutableBlockPosition, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}

class BlockPositionAncient implements BlockPositionConstructor {

    private MethodHandle blockPositionConstructor;
    private MethodHandle mutableBlockPositionConstructor;

    public BlockPositionAncient(MethodHandle blockPositionXYZ, MethodHandle mutableBlockPositionXYZ) {
        this.blockPositionConstructor = blockPositionXYZ;
        this.mutableBlockPositionConstructor = mutableBlockPositionXYZ;
    }

    @Override
    public Object newBlockPosition(Object world, Object x, Object y, Object z) {
        try {
            return blockPositionConstructor.invoke(world, x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object newMutableBlockPosition(Object world, Object x, Object y, Object z) {
        try {
            return mutableBlockPositionConstructor.invoke(x, y, z);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object set(Object mutableBlockPosition, Object x, Object y, Object z) {
        try {
            Location loc = (Location) mutableBlockPosition;
            loc.setX((double) x);
            loc.setY((double) y);
            loc.setZ((double) z);
            return loc;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}

class BlockUpdaterAncient implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle chunkSection;
    private MethodHandle setSectionElement;

    public BlockUpdaterAncient(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle chunkSection,
                               MethodHandle setSectionElement) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.chunkSection = chunkSection;
        this.setSectionElement = setSectionElement;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            Location loc = (Location) blockPosition;
            blockNotify.invoke(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        Object section = sections[getSectionIndex(nmsChunk, y)];
        if (section == null) {
            try {
                section = chunkSection.invoke(y >> 4 << 4, true);
                setSectionElement.invoke(sections, y >> 4, section);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return section;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        int i = y >> 4;
        return Math.min(i, 15);
    }

    @Override
    public void updateLighting(Object nmsChunk) {}

}

class BlockUpdaterLegacy implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle chunkSection;
    private MethodHandle setSectionElement;

    public BlockUpdaterLegacy(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle chunkSection,
                              MethodHandle setSectionElement) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.chunkSection = chunkSection;
        this.setSectionElement = setSectionElement;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            blockNotify.invoke(world, blockPosition);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        Object section = sections[getSectionIndex(nmsChunk, y)];
        if (section == null) {
            try {
                section = chunkSection.invoke(y >> 4 << 4, true);
                setSectionElement.invoke(sections, y >> 4, section);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return section;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        int i = y >> 4;
        return Math.min(i, 15);
    }

    @SneakyThrows
    public void updateLighting(Object nmsChunk) {
        Method initLighting = nmsChunk.getClass().getMethod("initLighting");
        initLighting.invoke(nmsChunk);
    }
}

class BlockUpdater9 implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle chunkSection;
    private MethodHandle setSectionElement;

    public BlockUpdater9(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle chunkSection,
                         MethodHandle setSectionElement) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.chunkSection = chunkSection;
        this.setSectionElement = setSectionElement;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            blockNotify.invoke(world, blockPosition, blockData, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        Object section = sections[getSectionIndex(nmsChunk, y)];
        if (section == null) {
            try {
                section = chunkSection.invoke(y >> 4 << 4, true);
                setSectionElement.invoke(sections, y >> 4, section);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return section;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        int i = y >> 4;
        return Math.min(i, 15);
    }

    @SneakyThrows
    public void updateLighting(Object nmsChunk) {
        Method initLighting = nmsChunk.getClass().getMethod("initLighting");
        initLighting.invoke(nmsChunk);
    }
}

class BlockUpdater13 implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle chunkSection;
    private MethodHandle setSectionElement;

    public BlockUpdater13(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle chunkSection,
                          MethodHandle setSectionElement) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.chunkSection = chunkSection;
        this.setSectionElement = setSectionElement;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            blockNotify.invoke(world, blockPosition, blockData, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        Object section = sections[getSectionIndex(nmsChunk, y)];
        if (section == null) {
            try {
                section = chunkSection.invoke(y >> 4 << 4);
                setSectionElement.invoke(sections, y >> 4, section);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return section;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        int i = y >> 4;
        return Math.min(i, 15);
    }

    @SneakyThrows
    public void updateLighting(Object nmsChunk) {
        Method initLighting = nmsChunk.getClass().getMethod("initLighting");
        initLighting.invoke(nmsChunk);
    }
}

class BlockUpdater17 implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle sectionIndexGetter;
    private MethodHandle chunkSection;
    private MethodHandle setSectionElement;

    public BlockUpdater17(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle sectionIndexGetter,
                          MethodHandle chunkSection, MethodHandle setSectionElement) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.sectionIndexGetter = sectionIndexGetter;
        this.chunkSection = chunkSection;
        this.setSectionElement = setSectionElement;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            blockNotify.invoke(world, blockPosition, blockData, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        Object section = sections[getSectionIndex(nmsChunk, y)];
        if (section == null) {
            try {
                section = chunkSection.invoke(y >> 4 << 4);
                setSectionElement.invoke(sections, y >> 4, section);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return section;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        int sectionIndex = -1;
        try {
            sectionIndex = (int) sectionIndexGetter.invoke(nmsChunk, y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sectionIndex <= 15 ? sectionIndex : 15;
    }

    public void updateLighting(Object nmsChunk) {}
}

class BlockUpdaterLatest implements BlockUpdater {

    private MethodHandle blockNotify;
    private MethodHandle chunkSetType;
    private MethodHandle sectionIndexGetter;
    private MethodHandle levelHeightAccessorGetter;

    public BlockUpdaterLatest(MethodHandle blockNotify, MethodHandle chunkSetType, MethodHandle sectionIndexGetter,
                              MethodHandle levelHeightAccessorGetter) {
        this.blockNotify = blockNotify;
        this.chunkSetType = chunkSetType;
        this.sectionIndexGetter = sectionIndexGetter;
        this.levelHeightAccessorGetter = levelHeightAccessorGetter;
    }

    @Override
    public void update(Object world, Object blockPosition, Object blockData, int physics) {
        try {
            blockNotify.invoke(world, blockPosition, blockData, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setType(Object chunk, Object blockPosition, Object blockData, boolean physics) {
        try {
            chunkSetType.invoke(chunk, blockPosition, blockData, physics);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getSection(Object nmsChunk, Object[] sections, int y) {
        return sections[getSectionIndex(nmsChunk, y)];
    }

    public Object getLevelHeightAccessor(Object nmsChunk) {
        try {
            return levelHeightAccessorGetter.invoke(nmsChunk);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getSectionIndex(Object nmsChunk, int y) {
        Object levelHeightAccessor = getLevelHeightAccessor(nmsChunk);
        try {
            return (int) sectionIndexGetter.invoke(levelHeightAccessor, y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateLighting(Object nmsChunk) {}
}