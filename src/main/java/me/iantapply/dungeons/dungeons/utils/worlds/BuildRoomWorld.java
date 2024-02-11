package me.iantapply.dungeons.dungeons.utils.worlds;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.dungeons.generation.SpecialType;
import me.iantapply.dungeons.dungeons.utils.rooms.RoomTypes;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public class BuildRoomWorld implements World{
    public static HashMap<World, BuildRoomWorld> worlds = new HashMap<>();

    public static BuildRoomWorld getWorld(World world) {
        return worlds.containsKey(world) ? worlds.get(world) : new BuildRoomWorld(world, RoomTypes._1x1, 31, 31);
    }

    public static void removeWorld(World world) {
        worlds.remove(world);
    }

    World world;

    public BuildRoomWorld(World world, RoomTypes roomType, int sizeX, int sizeZ) {
        this.world = world;
        this.roomType = roomType;
        setRoomSize(sizeX, sizeZ);
        worlds.put(world, this);
    }

    public BuildRoomWorld(World world, SpecialType roomType, int sizeX, int sizeZ) {
        this.world = world;
        this.specialType = roomType;
        setRoomSize(sizeX, sizeZ);
        worlds.put(world, this);
    }

    public String getTypeSave() {
        return roomType == null ?
                "specialTypes/" + specialType.getFileName().toLowerCase() :
                roomType.name().replace("_", "");

    }

    RoomTypes roomType = null;
    SpecialType specialType = null;
    @Getter int doorY = 5;
    @Getter int roomSizeX = 31;
    @Getter int roomSizeZ = 31;
    @Getter @Setter int spawnX = -Integer.MAX_VALUE;
    @Getter @Setter int spawnY = -Integer.MAX_VALUE;
    @Getter @Setter int spawnZ = -Integer.MAX_VALUE;

    public void setDoorY(int i) {
        this.doorY = i - 5;
    }

    private void setWalls(XMaterial material) {
        blocksFromTwoPoints(
                new Location(world, -1, 4, -1),
                new Location(world, getRoomSizeX() + 6, 255, -1)
        ).forEach(block -> block.setType(material.parseMaterial()));
        blocksFromTwoPoints(
                new Location(world, -1, 4, -1),
                new Location(world, -1, 255, getRoomSizeZ() + 6)
        ).forEach(block -> block.setType(material.parseMaterial()));
        blocksFromTwoPoints(
                new Location(world, getRoomSizeX() + 6, 4, getRoomSizeZ() + 6),
                new Location(world, -1, 255, getRoomSizeZ() + 6)
        ).forEach(block -> block.setType(material.parseMaterial()));
        blocksFromTwoPoints(
                new Location(world, getRoomSizeX() + 6, 4, getRoomSizeZ() + 6),
                new Location(world, getRoomSizeX() + 6, 255, -1)
        ).forEach(block -> block.setType(material.parseMaterial()));
    }

    public void setRoomSize(int roomSizeX, int roomSizeZ) {
        setWalls(XMaterial.AIR);
        this.roomSizeX = roomSizeX;
        this.roomSizeZ = roomSizeZ;
        setWalls(XMaterial.BARRIER);
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public Block getBlockAt(Location location) {
        return world.getBlockAt(location);
    }

    @Override @Deprecated
    public int getBlockTypeIdAt(int x, int y, int z) {
        return world.getBlockTypeIdAt(x, y, z);
    }

    @Override @Deprecated
    public int getBlockTypeIdAt(Location location) {
        return world.getBlockTypeIdAt(location);
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return world.getHighestBlockYAt(x, z);
    }

    @Override
    public int getHighestBlockYAt(Location location) {
        return world.getHighestBlockYAt(location);
    }

    @Override
    public Block getHighestBlockAt(int x, int z) {
        return world.getHighestBlockAt(x, z);
    }

    @Override
    public Block getHighestBlockAt(Location location) {
        return world.getHighestBlockAt(location);
    }

    @Override
    public Chunk getChunkAt(int x, int z) {
        return world.getChunkAt(x, z);
    }

    @Override
    public Chunk getChunkAt(Location location) {
        return world.getChunkAt(location);
    }

    @Override
    public Chunk getChunkAt(Block block) {
        return world.getChunkAt(block);
    }

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        return world.isChunkLoaded(chunk);
    }

    @Override
    public Chunk[] getLoadedChunks() {
        return world.getLoadedChunks();
    }

    @Override
    public void loadChunk(Chunk chunk) {
        world.loadChunk(chunk);
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return world.isChunkLoaded(x, z);
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        return world.isChunkInUse(x, z);
    }

    @Override
    public void loadChunk(int x, int z) {
        world.loadChunk(x, z);
    }

    @Override
    public boolean loadChunk(int x, int z, boolean generate) {
        return world.loadChunk(x, z, generate);
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return world.unloadChunk(chunk);
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return world.unloadChunk(x, z);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save) {
        return world.unloadChunk(x, z, save);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        return world.unloadChunk(x, z, save, safe);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        return world.unloadChunkRequest(x, z);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        return world.unloadChunkRequest(x, z, safe);
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        return world.regenerateChunk(x, z);
    }

    @Override @Deprecated
    public boolean refreshChunk(int x, int z) {
        return world.refreshChunk(x, z);
    }

    @Override
    public Item dropItem(Location location, ItemStack item) {
        return world.dropItem(location, item);
    }

    @Override
    public Item dropItemNaturally(Location location, ItemStack item) {
        return world.dropItemNaturally(location, item);
    }

    @Override
    public Arrow spawnArrow(Location location, Vector direction, float speed, float spread) {
        return world.spawnArrow(location, direction, speed, spread);
    }

    @Override
    public boolean generateTree(Location location, TreeType type) {
        return world.generateTree(location, type);
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        return world.generateTree(loc, type, delegate);
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType type) {
        return world.spawnEntity(loc, type);
    }

    @Override
    @Deprecated
    public LivingEntity spawnCreature(Location loc, EntityType type) {
        return world.spawnCreature(loc, type);
    }

    @Override
    @Deprecated
    public LivingEntity spawnCreature(Location loc, CreatureType type) {
        return world.spawnCreature(loc, type);
    }

    @Override
    public LightningStrike strikeLightning(Location loc) {
        return world.strikeLightning(loc);
    }

    @Override
    public LightningStrike strikeLightningEffect(Location loc) {
        return world.strikeLightningEffect(loc);
    }

    @Override
    public List<Entity> getEntities() {
        return world.getEntities();
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        return world.getLivingEntities();
    }

    @SafeVarargs @Override @Deprecated
    public final <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        return world.getEntitiesByClass(classes);
    }

    @Override
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls) {
        return world.getEntitiesByClass(cls);
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        return world.getEntitiesByClasses(classes);
    }

    @Override
    public List<Player> getPlayers() {
        return world.getPlayers();
    }

    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        return world.getNearbyEntities(location, x, y, z);
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public UUID getUID() {
        return world.getUID();
    }

    @Override
    public Location getSpawnLocation() {
        return world.getSpawnLocation();
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        return world.setSpawnLocation(x, y, z);
    }

    @Override
    public long getTime() {
        return world.getTime();
    }

    @Override
    public void setTime(long time) {
        world.setTime(time);
    }

    @Override
    public long getFullTime() {
        return world.getFullTime();
    }

    @Override
    public void setFullTime(long time) {
        world.setFullTime(time);
    }

    @Override
    public boolean hasStorm() {
        return world.hasStorm();
    }

    @Override
    public void setStorm(boolean hasStorm) {
        world.setStorm(hasStorm);
    }

    @Override
    public int getWeatherDuration() {
        return world.getWeatherDuration();
    }

    @Override
    public void setWeatherDuration(int duration) {
        world.setWeatherDuration(duration);
    }

    @Override
    public boolean isThundering() {
        return world.isThundering();
    }

    @Override
    public void setThundering(boolean thundering) {
        world.setThundering(thundering);
    }

    @Override
    public int getThunderDuration() {
        return world.getThunderDuration();
    }

    @Override
    public void setThunderDuration(int duration) {
        world.setThunderDuration(duration);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power) {
        return world.createExplosion(x, y, z, power);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        return world.createExplosion(x, y, z, power, setFire);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        return world.createExplosion(x, y, z, power, setFire, breakBlocks);
    }

    @Override
    public boolean createExplosion(Location loc, float power) {
        return world.createExplosion(loc, power);
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire) {
        return world.createExplosion(loc, power, setFire);
    }

    @Override
    public Environment getEnvironment() {
        return world.getEnvironment();
    }

    @Override
    public long getSeed() {
        return world.getSeed();
    }

    @Override
    public boolean getPVP() {
        return world.getPVP();
    }

    @Override
    public void setPVP(boolean pvp) {
        world.setPVP(pvp);
    }

    @Override
    public ChunkGenerator getGenerator() {
        return world.getGenerator();
    }

    @Override
    public void save() {
        world.save();
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        return world.getPopulators();
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        return world.spawn(location, clazz);
    }

    @Override @Deprecated
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
        return world.spawnFallingBlock(location, material, data);
    }

    @Override @Deprecated
    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
        return world.spawnFallingBlock(location, blockId, blockData);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        world.playEffect(location, effect, data);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data, int radius) {
        world.playEffect(location, effect, data, radius);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        world.playEffect(location, effect, data);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data, int radius) {
        world.playEffect(location, effect, data, radius);
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return world.getEmptyChunkSnapshot(x, z, includeBiome, includeBiomeTempRain);
    }

    @Override
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        world.setSpawnFlags(allowMonsters, allowAnimals);
    }

    @Override
    public boolean getAllowAnimals() {
        return world.getAllowAnimals();
    }

    @Override
    public boolean getAllowMonsters() {
        return world.getAllowMonsters();
    }

    @Override
    public Biome getBiome(int x, int z) {
        return world.getBiome(x, z);
    }

    @Override
    public void setBiome(int x, int z, Biome bio) {
        world.setBiome(x, z, bio);
    }

    @Override
    public double getTemperature(int x, int z) {
        return world.getTemperature(x, z);
    }

    @Override
    public double getHumidity(int x, int z) {
        return world.getHumidity(x, z);
    }

    @Override
    public int getMaxHeight() {
        return world.getMaxHeight();
    }

    @Override
    public int getSeaLevel() {
        return world.getSeaLevel();
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return world.getKeepSpawnInMemory();
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        world.setKeepSpawnInMemory(keepLoaded);
    }

    @Override
    public boolean isAutoSave() {
        return world.isAutoSave();
    }

    @Override
    public void setAutoSave(boolean value) {
        world.setAutoSave(value);
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        world.setDifficulty(difficulty);
    }

    @Override
    public Difficulty getDifficulty() {
        return world.getDifficulty();
    }

    @Override
    public File getWorldFolder() {
        return world.getWorldFolder();
    }

    @Override
    public WorldType getWorldType() {
        return world.getWorldType();
    }

    @Override
    public boolean canGenerateStructures() {
        return world.canGenerateStructures();
    }

    @Override
    public long getTicksPerAnimalSpawns() {
        return world.getTicksPerAnimalSpawns();
    }

    @Override
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        world.setTicksPerAnimalSpawns(ticksPerAnimalSpawns);
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        return world.getTicksPerMonsterSpawns();
    }

    @Override
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        world.setTicksPerMonsterSpawns(ticksPerMonsterSpawns);
    }

    @Override
    public int getMonsterSpawnLimit() {
        return world.getMonsterSpawnLimit();
    }

    @Override
    public void setMonsterSpawnLimit(int limit) {
        world.setMonsterSpawnLimit(limit);
    }

    @Override
    public int getAnimalSpawnLimit() {
        return world.getAnimalSpawnLimit();
    }

    @Override
    public void setAnimalSpawnLimit(int limit) {
        world.setAnimalSpawnLimit(limit);
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return world.getWaterAnimalSpawnLimit();
    }

    @Override
    public void setWaterAnimalSpawnLimit(int limit) {
        world.setWaterAnimalSpawnLimit(limit);
    }

    @Override
    public int getAmbientSpawnLimit() {
        return world.getAmbientSpawnLimit();
    }

    @Override
    public void setAmbientSpawnLimit(int limit) {
        world.setAmbientSpawnLimit(limit);
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        world.playSound(location, sound, volume, pitch);
    }

    @Override
    public String[] getGameRules() {
        return world.getGameRules();
    }

    @Override
    public String getGameRuleValue(String rule) {
        return world.getGameRuleValue(rule);
    }

    @Override
    public boolean setGameRuleValue(String rule, String value) {
        return world.setGameRuleValue(rule, value);
    }

    @Override
    public boolean isGameRule(String rule) {
        return world.isGameRule(rule);
    }

    @Override
    public Spigot spigot() {
        return world.spigot();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return world.getWorldBorder();
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        world.setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return world.getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return world.hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        world.removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        world.sendPluginMessage(source, channel, message);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return world.getListeningPluginChannels();
    }
}
