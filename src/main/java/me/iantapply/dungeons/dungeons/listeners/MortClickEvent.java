package me.iantapply.dungeons.dungeons.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.iantapply.dungeons.developerkit.GKBase;
import me.iantapply.dungeons.developerkit.gui.GUIClickableItem;
import me.iantapply.dungeons.developerkit.item.GetSkullFromURL;
import me.iantapply.dungeons.developerkit.listener.GKListener;
import me.iantapply.dungeons.developerkit.utils.ItemBuilder;
import me.iantapply.dungeons.dungeons.commands.GenerateCMD;
import me.iantapply.dungeons.dungeons.generation.Preset;
import me.iantapply.dungeons.dungeons.utils.misc.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class MortClickEvent extends GKListener {

    @EventHandler
    public void onMortClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = (Entity) event.getRightClicked();

        if (entity.hasMetadata("NPC")) {
            if (entity.getCustomName().contains("GATE KEEPER")) {
                event.setCancelled(true);
                player.openInventory(mortGateKeeperMainMenu(true, true, true, true, true, true, true));
            }
        }
    }

    public Inventory mortGateKeeperMainMenu(boolean floor0Enabled, boolean floor2Enabled, boolean floor3Enabled, boolean floor4Enabled, boolean floor5Enabled, boolean floor6Enabled, boolean floor7Enabled) {
        Inventory inv = Bukkit.createInventory(null, 54, "Catacombs Gate");
        GKBase.fillEmptySpaces(inv);

        /**
         * These are all of the floors
         */

        // Entrance
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.ENTRANCE);
            }

            @Override
            public int getSlot() {
                return 11;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/5a2f67500a65f3ce79d34ec150de93df8f60ebe52e248f5e1cdb69b0726256f7"))
                        .setName(ChatColor.GREEN + "The Catacombs - Entrance")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bTiny", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cThe Watcher §a✔", ChatColor.DARK_GRAY + "Stalker", ChatColor.GRAY + "This strange creature is roaming the", ChatColor.GRAY + "Catacombs to add powerful adventurers to", ChatColor.GRAY + "its collection.", "", ChatColor.GRAY + "Requires: §bCombat Level XV", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 1
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
                e.getCurrentItem().setAmount(0);
                e.getCursor().setAmount(0);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR1);
            }

            @Override
            public int getSlot() {
                return 12;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/5720917cda0567442617f2721e88be9d2ffbb0b26a3f4c2fe21655814d4f4476"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor I")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bTiny", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cBonzo §a✔", ChatColor.DARK_GRAY + "New Necromancer", ChatColor.GRAY + "Involved in the dark arts due to his", ChatColor.GRAY + "parent's insistence. Originally worked as", ChatColor.GRAY + "a Circus clown.", "", ChatColor.GRAY + "Requires: §dCatacombs Level I", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 2
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR2);
            }

            @Override
            public int getSlot() {
                return 13;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/6b78e53cf0cbc83842a7a1a6977471378569f4bdc5e30404302c4aef3c6e933e"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor II")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bSmall", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cScarf §a✔", ChatColor.DARK_GRAY + "Apprentice Necromancer", ChatColor.GRAY + "First of his class. His teacher said he", ChatColor.GRAY + "will do \"great things\".", "", ChatColor.GRAY + "Requires: §dCatacombs Level III", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 3
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR3);
            }

            @Override
            public int getSlot() {
                return 14;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/3ce69d2ddcc81c9fc2e9948c92003eb0f7ebf0e7e952e801b7f2069dcee76d85"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor III")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bSmall", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cThe Professor §a✔", ChatColor.DARK_GRAY + "Professor", ChatColor.GRAY + "Despite his great technique, he failed", ChatColor.GRAY + "the Masters exam three times. Works from 8", ChatColor.GRAY + "to 5. Cares about his students.", "", ChatColor.GRAY + "Requires: §dCatacombs Level V", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 4
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR4);
            }

            @Override
            public int getSlot() {
                return 15;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/76965e3fd619de6b0a7ce1673072520a9360378e1cb8c19d4baf0c86769d3764"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor IV")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bSmall", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cThorn §a✔", ChatColor.DARK_GRAY + "Shaman Necromancer", ChatColor.GRAY + "Powerful Necromancer that specializes in", ChatColor.GRAY + "animals. Calls himself a vegetarian, go", ChatColor.GRAY + "figure.", "", ChatColor.GRAY + "Requires: §dCatacombs Level XI", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 5
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR5);
            }

            @Override
            public int getSlot() {
                return 21;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/33a0246d51350b770de27f1a1925b11cece57b3c9b209e2b6bf275d32d0154c3"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor V")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bMedium", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cLivid §a✔", ChatColor.DARK_GRAY + "Master Necromancer", ChatColor.GRAY + "Strongly believes he will become the", ChatColor.GRAY + "Lord one day. Subject of mockeries, even", ChatColor.GRAY + "from his disciplines.", "", ChatColor.GRAY + "Requires: §dCatacombs Level XIV", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 6
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR6);
            }

            @Override
            public int getSlot() {
                return 22;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/45079a72574b03ab8639c3da572cf5285faf89a85437489b847c09b8dada852e"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor VI")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bMedium", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cSadan §a✔", ChatColor.DARK_GRAY + "Necromancer Lord", ChatColor.GRAY + "Necromancy was always strong in his", ChatColor.GRAY + "family. Says he once beat a Wither in a", ChatColor.GRAY + "duel. Likes to brag.", "", ChatColor.GRAY + "Requires: §dCatacombs Level XIX", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        // Floor 7
        set(inv, new GUIClickableItem() {

            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);

                WorldCreator wc = new WorldCreator("test_" + UUID.randomUUID());
                wc.generator(new EmptyChunkGenerator());

                World world = wc.createWorld();
                world.setGameRuleValue("doMobSpawning", "false");

                GenerateCMD.doneRooms.put(world, new ArrayList<>());

                Player player = (Player) e.getWhoClicked();

                GenerateCMD.generateWorld(player, world, Preset.FLOOR7);
            }

            @Override
            public int getSlot() {
                return 23;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/6a40675d333adcca75b35b6ba29b4fd7e5eaeb998395295385b494128715dab3"))
                        .setName(ChatColor.GREEN + "The Catacombs - Floor VII")
                        .setLore(ChatColor.GRAY + "Dungeon Size: §bLarge", ChatColor.GRAY + "Party Size: §92-5", "", ChatColor.GRAY + "Mini-Boss: §cMaxor, Storm, Goldor, and Necron §a✔", ChatColor.DARK_GRAY + "The Wither Lords", ChatColor.GRAY + "Disciplines of the Wither King. Inherited", ChatColor.GRAY + "the Catacombs eons ago. Never defeated,", ChatColor.GRAY + "feared by anything living AND dead.", "", ChatColor.GRAY + "Requires: §dCatacombs Level XXIV", "", ChatColor.YELLOW + "Click to queue for dungeon!")
                        .build();
            }
        });

        /**
         * Other items in the menu such as party finder, etc
         */

        // Master Mode
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return 40;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(GetSkullFromURL.getSkull("https://textures.minecraft.net/texture/cb852ba1584da9e5714859995451e4b94748c4dd63ae4543c15f9f8aec65c8"))
                        .setName(ChatColor.RED + "Master Mode")
                        .setLore(ChatColor.DARK_GRAY + "§oLike normal Dungeons... but", ChatColor.DARK_GRAY + "§omore hardcore.", "", ChatColor.RED + "Must complete Catacombs Floor 7!")
                        .build();
            }
        });

        // Rules and tips
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return 53;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.REDSTONE_TORCH)
                        .setName(ChatColor.GREEN + "Dungeons Rules and Tips")
                        .setLore(ChatColor.GRAY + "Skyblock Dungeons has special", ChatColor.GRAY + "rules! Some things inside", ChatColor.GRAY + "Dungeons work differently than", ChatColor.GRAY + "they do in regular Skyblock.", ChatColor.GRAY + "This menu describes the list of", ChatColor.GRAY + "changes!", "", ChatColor.YELLOW + "Click to view!")
                        .build();
            }
        });

        // Catacombs profile
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return 50;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.OAK_SIGN)
                        .setName(ChatColor.GREEN + "Catacombs Profile")
                        .setLore(ChatColor.GRAY + "View your statistics, best", ChatColor.GRAY + "performances, and more for §cThe", ChatColor.RED + "Catacombs§7!", "", ChatColor.YELLOW + "Click to view!")
                        .build();
            }
        });

        // Exit GUI
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
            }

            @Override
            public int getSlot() {
                return 49;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.BARRIER)
                        .setName(ChatColor.RED + "Close")
                        .build();
            }
        });

        // Find a party
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return 48;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.REDSTONE_BLOCK)
                        .setName(ChatColor.GREEN + "Find A Party")
                        .setLore(ChatColor.GRAY + "Use the Party Finder to join a", ChatColor.GRAY + "party queued for the dungeon.", "", ChatColor.YELLOW + "Click to browse!")
                        .build();
            }
        });

        // Dungeon classes
        set(inv, new GUIClickableItem() {
            @Override
            public void run(InventoryClickEvent e) {
                e.setCancelled(true);
            }

            @Override
            public int getSlot() {
                return 47;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(XMaterial.NETHER_STAR)
                        .setName(ChatColor.GREEN + "Dungeon classes")
                        .setLore(ChatColor.GRAY + "View and select a dungeon class.", "", ChatColor.GREEN + "Currently Selected: §bMage")
                        .build();
            }
        });

        return inv;
    }
}
