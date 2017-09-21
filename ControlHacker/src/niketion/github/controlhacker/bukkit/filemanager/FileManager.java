package niketion.github.controlhacker.bukkit.filemanager;

import niketion.github.controlhacker.bukkit.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class FileManager {
    private File configFile;
    private FileConfiguration config;

    public FileManager(String nameConfig, String directoryName) {
        configFile = new File(Main.getInstance().getDataFolder()+"/"+directoryName+"/"+nameConfig+".yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Set to config and save file
     * @param path - Path
     * @param value - Value
     */
    public void set(String path, Object value) {
        config.set(path, value);
        try{
            config.save(configFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get configuration file
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Add to config and save file
     * @param path - Path
     * @param value - Value
     */
    public void add(String path, String value) {
        List<String> configList = getConfig().getStringList(path);
        configList.add(value);
        config.set(path, configList);
        try{
            config.save(configFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Remove to config and save file
     * @param path - Path
     * @param value - Value
     */
    public void remove(String path, String value) {
        List<String> configList = getConfig().getStringList(path);
        configList.remove(value);
        config.set(path, configList);
        try{
            config.save(configFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get config file
     * @return File
     */
    public File getConfigFile() {
        return configFile;
    }
}
