package niketion.github.controlhacker.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import niketion.github.controlhacker.bukkit.Main;

public class FinishGUI {

	private Main main = Main.getInstance();
	
	private String guiTitle = main.format(main.getConfig().getString("finish-gui-title"));
	private Inventory finishGui;

	public FinishGUI() {
		finishGui = Bukkit.createInventory(null, 9, guiTitle);
	
		finishGui.setItem(1, itemStack(4, "first"));
		finishGui.setItem(4, itemStack(14, "second"));
		finishGui.setItem(7, itemStack(5, "third"));
		
	}
	
	private ItemStack itemStack(int data, String option) {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (short) data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        
        String configPath = "finish-" + option + "-option";
        itemMeta.setDisplayName(main.format(main.getConfig().getString(configPath + ".display-name")));
        itemMeta.setLore(main.colorLore(main.getConfig().getStringList(configPath + ".lore")));
        
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
	
	/**
	 * Return FinishGui's title
	 * @return
	 */
	public String getTitle() {
		return guiTitle;
	}
	
	/**
	 * Open the "Finish Gui" for the specified player
	 * @param player
	 */
	public void openGui(Player player) {
		player.openInventory(finishGui);
	}

}
