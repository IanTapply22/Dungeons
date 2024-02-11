package me.iantapply.dungeons.developerkit;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.iantapply.dungeons.developerkit.gui.GUIClickableItem;
import me.iantapply.dungeons.developerkit.gui.GUIListener;
import me.iantapply.dungeons.developerkit.item.RightClickListener;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GKBase {

    @Getter private static JavaPlugin plugin;

    static {
        plugin = JavaPlugin.getProvidingPlugin(GKBase.class);
        RightClickListener.startup();
        GUIListener.startup();
    }

    public static void println(Object... variables) {
        print(variables);
        println();
    }

    // File Stuff

    public static void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void createFolder(String path, String[] paths) {
        for(String s : paths) {
            File file = new File(path + s);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    // GUI Stuff

    public static void set(Inventory inv, GUIClickableItem item) {
        inv.setItem(item.getSlot(), item.getFinishedItem());
    }

    public static void setBorder(Inventory inv) {
        int size = inv.getSize();
        if (size < 27) return;

        for (int i = 0; i < 9; i++) {
            set(inv, GUIClickableItem.getBorderItem(i));
        }

        for(int i = 0; i < Math.ceil(size / 10.0); i++) {
            set(inv, GUIClickableItem.getBorderItem(8 + (9 * i)));
            if(9 + (9 * i) > size - 1) continue;
            set(inv, GUIClickableItem.getBorderItem(9 + (9 * i)));
        }

        for(int i = size - 9; i < size; i++) {
            set(inv, GUIClickableItem.getBorderItem(i));
        }
    }

    public static void fillEmptySpaces(Inventory inv) {

        for(int slot = 0; slot < inv.getSize(); slot++){
            set(inv, GUIClickableItem.getBorderItem(slot));
        }
    }

    public static void addItem(ItemStack is, Inventory inv) {
        if (inv.firstEmpty() != -1) {
            inv.addItem(is);
        }
    }

    public static void fill(Inventory inv, ItemStack is) {
        for(int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, is);
        }
    }

    // Mob Stuff

    public boolean isMobType(Entity e, EntityType... types) {
        return Arrays.stream(types).anyMatch(type -> type == e.getType());
    }

    // Block Stuff

    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {

        List<Location> circleBlocks = new ArrayList<Location>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

                        Location l = new Location(centerBlock.getWorld(), x, centerBlock.getY(), z);

                        if (!circleBlocks.contains(l)) circleBlocks.add(l);
                    }

                }
            }
        }

        return circleBlocks;
    }

    // Color Stuff

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static Color getColorByRGB(String string) {
        return Color.fromRGB(
                Integer.parseInt(string.split(",")[0]),
                Integer.parseInt(string.split(",")[1]),
                Integer.parseInt(string.split(",")[2])
        );
    }

    public static Color getColorByString(String string) {
        switch (string.toLowerCase()) {
            case "aqua":
                return Color.AQUA;
            case "black":
                return Color.BLACK;
            case "blue":
                return Color.BLUE;
            case "fuchsia":
                return Color.FUCHSIA;
            case "gray":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "lime":
                return Color.LIME;
            case "maroon":
                return Color.MAROON;
            case "navy":
                return Color.NAVY;
            case "olive":
                return Color.OLIVE;
            case "orange":
                return Color.ORANGE;
            case "purple":
                return Color.PURPLE;
            case "red":
                return Color.RED;
            case "silver":
                return Color.SILVER;
            case "teal":
                return Color.TEAL;
            case "white":
                return Color.WHITE;
            case "yellow":
                return Color.YELLOW;
            default:
                break;
        }
        return null;
    }

    // Block Stuff

    public static Location getBlockLocationCentered(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        Location returnLoc = new Location(loc.getWorld(), x, y, z);
        returnLoc.add(x >= 0 ? 0.5 : -0.5, 0.0, z >= 0 ? 0.5 : -0.5);
        return returnLoc;
    }

    // Data Stuff

    public static boolean blockMatches(Block b1, Block b2) {
        if(XMaterial.supports(13)) {
            return b1.getType() == b2.getType();
        } else {
            return b1.getType() == b2.getType() && b1.getData() == b2.getData();
        }
    }

    public static boolean blockMatches(MaterialData m1, MaterialData m2) {
        if(XMaterial.supports(13)) {
            return m1.getItemType() == m2.getItemType();
        } else {
            return m1.getItemType() == m2.getItemType() && m1.getData() == m2.getData();
        }
    }

    public static boolean blockMatches(MaterialData m1, Block b2) {
        if(XMaterial.supports(13)) {
            return m1.getItemType() == b2.getType();
        } else {
            return m1.getItemType() == b2.getType() && m1.getData() == b2.getData();
        }
    }

    public static void setData(Block b, byte by) {
        try {
            Method setData = b.getClass().getMethod("setData", byte.class);
            setData.invoke(b, by);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
    }

    // Object Stuff

    public static boolean checkEquals(Object comparisonObj, Object... otherObj) {
        for (Object o : otherObj) {
            if(comparisonObj == o) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            double d = (double) obj;
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    // List Stuff

    public static <T> T getRandom(T[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static <T> T getRandom(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    @SafeVarargs
    public static <T> T[] array(T... array) {
        return array;
    }

    public static <T> T[] recursiveArray(int size, T array) {
        @SuppressWarnings("unchecked")
        T[] t = (T[]) Array.newInstance(array.getClass(), size);
        for(int i = 0; i < size; i++) {
            t[i] = array;
        }
        return t;
    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    public static <T> ArrayList<T[]> split(T[] array, int size) {
        if(array.length == 0) return new ArrayList<>();

        Class<?> clazz =  array[0].getClass();

        int splitSize = (int) Math.ceil(array.length / (double) size);
        ArrayList<T[]> returnArray = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T[] split = (T[]) Array.newInstance(clazz, splitSize);
            for(int j = 0; j < splitSize; j++) {
                if(i * splitSize + j < array.length) {
                    split[j] = array[i * splitSize + j];
                }
            }

            returnArray.add(split);
        }
        return returnArray;
    }

    public static <T> ArrayList<T> fromArray(T[] array) {
        return new ArrayList<T>(Arrays.asList(array));
    }

    public static <T> List<T> shuffle(List<T> list) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = list.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T t = list.get(index);
            list.set(index, list.get(i));
            list.set(i, t);
        }
        return list;
    }

    public static <T> T getOrDefault(List<T> list, int index, T def) {
        if (index < 0 || index >= list.size())
            return def;
        return list.get(index);
    }

    // Math Stuff

    public static double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }

    // Formatting Stuff

    private static final NumberFormat COMMA_FORMAT = NumberFormat.getInstance();

    static {
        COMMA_FORMAT.setGroupingUsed(true);
    }

    public static String commaify(int i) {
        return COMMA_FORMAT.format(i);
    }

    public static String space(String thing) {
        return thing.replaceAll("(.)([A-Z])", "$1 $2");
    }

    // Roman Numeral Stuff

    public static String toRomanNumeral(int num) {
        StringBuilder sb = new StringBuilder();
        int times;
        String[] romans = new String[]{"I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M"};
        int[] ints = new int[]{1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000};
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    public static int romanToInteger(String roman) {
        Map<Character, Integer> numbersMap = new HashMap<>();
        numbersMap.put('I', 1);
        numbersMap.put('V', 5);
        numbersMap.put('X', 10);
        numbersMap.put('L', 50);
        numbersMap.put('C', 100);
        numbersMap.put('D', 500);
        numbersMap.put('M', 1000);

        int result = 0;

        for (int i = 0; i < roman.length(); i++) {
            char ch = roman.charAt(i);      // Current Roman Character

            //Case 1
            if (i > 0 && numbersMap.get(ch) > numbersMap.get(roman.charAt(i - 1))) {
                result += numbersMap.get(ch) - 2 * numbersMap.get(roman.charAt(i - 1));
            }

            // Case 2: just add the corresponding number to result.
            else
                result += numbersMap.get(ch);
        }

        return result;
    }

    // Text Stuff

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    // Internal Stuff

    /**
     * For arrays, use printArray() instead. This function causes a warning
     * because the new print(Object...) and println(Object...) functions can't
     * be reliably bound by the compiler.
     */
    public static void println(Object what) {
        if (what == null) {
            System.out.println("null");
        } else if (what.getClass().isArray()) {
            printArray(what);
        } else {
            System.out.println(what);
            System.out.flush();
        }
    }

    public static void println() {
        System.out.println();
    }

    public static void print(Object... variables) {
        StringBuilder sb = new StringBuilder();
        for (Object o : variables) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            if (o == null) {
                sb.append("null");
            } else {
                sb.append(o);
            }
        }
        System.out.print(sb);
    }

    public static void printArray(Object what) {
        if (what == null) {
            // special case since this does fuggly things on > 1.1
            System.out.println("null");

        } else {
            String name = what.getClass().getName();
            if (name.charAt(0) == '[') {
                switch (name.charAt(1)) {
                    case 'L':
                        // print a 1D array of objects as individual elements
                        Object[] poo = (Object[]) what;
                        for (int i = 0; i < poo.length; i++) {
                            if (poo[i] instanceof String) {
                                System.out.println("[" + i + "] \"" + poo[i] + "\"");
                            } else {
                                System.out.println("[" + i + "] " + poo[i]);
                            }
                        }
                        break;

                    case 'Z':  // boolean
                        boolean[] zz = (boolean[]) what;
                        for (int i = 0; i < zz.length; i++) {
                            System.out.println("[" + i + "] " + zz[i]);
                        }
                        break;

                    case 'B':  // byte
                        byte[] bb = (byte[]) what;
                        for (int i = 0; i < bb.length; i++) {
                            System.out.println("[" + i + "] " + bb[i]);
                        }
                        break;

                    case 'C':  // char
                        char[] cc = (char[]) what;
                        for (int i = 0; i < cc.length; i++) {
                            System.out.println("[" + i + "] '" + cc[i] + "'");
                        }
                        break;

                    case 'I':  // int
                        int[] ii = (int[]) what;
                        for (int i = 0; i < ii.length; i++) {
                            System.out.println("[" + i + "] " + ii[i]);
                        }
                        break;

                    case 'J':  // int
                        long[] jj = (long[]) what;
                        for (int i = 0; i < jj.length; i++) {
                            System.out.println("[" + i + "] " + jj[i]);
                        }
                        break;

                    case 'F':  // float
                        float[] ff = (float[]) what;
                        for (int i = 0; i < ff.length; i++) {
                            System.out.println("[" + i + "] " + ff[i]);
                        }
                        break;

                    case 'D':  // double
                        double[] dd = (double[]) what;
                        for (int i = 0; i < dd.length; i++) {
                            System.out.println("[" + i + "] " + dd[i]);
                        }
                        break;

                    default:
                        System.out.println(what);
                }
            } else {  // not an array
                System.out.println(what);
            }
        }
        System.out.flush();
    }

    static Random internalRandom;

    /**
     *
     */
    public static float random(float high) {
        // avoid an infinite loop when 0 or NaN are passed in
        if (high == 0 || high != high) {
            return 0;
        }

        if (internalRandom == null) {
            internalRandom = new Random();
        }

        // for some reason (rounding error?) Math.random() * 3
        // can sometimes return '3' (once in ~30 million tries)
        // so a check was added to avoid the inclusion of 'howbig'
        float value = 0;
        do {
            value = internalRandom.nextFloat() * high;
        } while (value == high);
        return value;
    }

    // Item Stuff

    public static ItemStack setTexture(ItemStack item, String texture) {
        SkullMeta hm = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field field = hm.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(hm, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item.setItemMeta(hm);
        return item;
    }

    public static ItemStack getSkull(String url) {
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

        if (url == null || url.isEmpty())
            return skull;

        ItemMeta skullMeta = skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    /*public static ItemStack IDtoSkull(ItemStack head, String id) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(new String(gk.skyblock.utils.base64.Base64.decodeBase64(id))).getAsJsonObject();
        String skinUrl = o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = gk.skyblock.utils.base64.Base64.encodeBase64(("{textures:{SKIN:{url:\"" + skinUrl + "\"}}}").getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

     */

    public static boolean isNegative(double number) {
        return number < 0;
    }
}
