package it.nik.controlhacker.files;

import it.nik.controlhacker.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileReport {

    private Main main = Main.getInstance();

    private static FileReport instance;

    private FileReport() {
        instance = this;
    }

    public static FileReport getInstance() {
        if (instance == null) {
            instance = new FileReport();
        }
        return instance;
    }

    private File file =
            new File(
                    "plugins/ControlHacker/reports.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            main.formatMessageError("I can not save the 'stats.yml'", e);
        }
    }

}
