package me.iantapply.dungeons.dungeons.utils.files;

import me.iantapply.dungeons.dungeons.Dungeons;
import lombok.SneakyThrows;
import me.iantapply.dungeons.developerkit.GKBase;

import java.io.File;

import static me.iantapply.dungeons.developerkit.GKBase.createFolder;

public class PluginFolder {

    @SneakyThrows
    public static void copyFiles() {
        GKBase.createFolder("plugins/Dungeons");
        GKBase.createFolder("plugins/Dungeons/samples");
        GKBase.createFolder("plugins/Dungeons/samples/specialTypes");

        String[] files = new String[]{"1x1", "2x1", "2x2", "3x1", "4x1", "L"};
        for(String s : files) {
            File file = new File("plugins/Dungeons/samples/" + s + ".schematic");
            if(!file.exists()) {
                //InputStream is = getResource("samples/" + s + ".schematic");
                //file.createNewFile();
                //FileUtils.copyInputStreamToFile(is, file);
                //saveFile(fileNormal, s + ".schematic", "samples/" + s + ".schematic");
                Dungeons.getMain().saveResource("samples/" + s + ".schematic", false);
            }
        }

        GKBase.createFolder("plugins/Dungeons/rooms/", files);
        GKBase.createFolder("plugins/Dungeons/rooms/specialTypes");

        String[] specials = new String[]{"Spawn", "Boss", "Fairy", "Puzzle", "MiniBoss", "Trap"};

        for(String s : specials) {
            File file = new File("plugins/Dungeons/samples/specialTypes/" + s + ".schematic");
            if(!file.exists()) {
                //InputStream is = getResource("samples/specialTypes/" + s + ".schematic");
                //file.createNewFile();
                //FileUtils.copyInputStreamToFile(is, file);
                //saveFile(fileSpecial, s + ".schematic", "samples/specialTypes/" + s + ".schematic");
                Dungeons.getMain().saveResource("samples/specialTypes/" + s + ".schematic", false);
            }
            GKBase.createFolder("plugins/Dungeons/rooms/specialTypes/" + s.toLowerCase());
        }


        File file = new File("plugins/Dungeons/data.yml");
        if(!file.exists()) {
            file.createNewFile();
        }

        File config = new File("plugins/Dungeons/config.yml");
        if (!config.exists()) {
            config.createNewFile();
        }
    }
}
