package it.nik.controlhacker.files;

import it.nik.controlhacker.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileLocation {

    private Main main = Main.getInstance();

    private static FileLocation instance;

    private FileLocation() {
        instance = this;
    }

    public static FileLocation getInstance() {
        if (instance == null) {
            instance = new FileLocation();
        }
        return instance;
    }

    private File locationFile =
            new File(
                    "plugins/ControlHacker/location.yml");
    private FileConfiguration locationConfig = YamlConfiguration.loadConfiguration(locationFile);

    public File getLocationFile() {
        return locationFile;
    }

    public FileConfiguration getLocationConfig() {
        return locationConfig;
    }

    public void saveConfig() {
        try {
            getLocationConfig().save(getLocationFile());
        } catch (IOException e) {
            main.formatMessageError("I can not save the 'location.yml'", e);
        }
    }

}
