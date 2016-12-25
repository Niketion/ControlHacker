CONTROLHACKER
===================
Control **cheaters** in a **minecraft** server.

## API
Version 1.0 (Beta)
```java
import it.nik.controlhacker.Fuctions;
import org.bukkit.entity.Player;

public class Test {
	private Fuctions fuctions = Fuctions.getInstance();

	private static Test instance;
    private Test() { instance = this; }
    public static Test getInstance() { if (instance == null) { instance = new Test(); } return instance; }

	public void add(Player player) {
		fuctions.addToControl(player);
	}
	public void remove(Player player) {
		fuctions.removeToControl(player);
	}
	public void is(Player player) {
		fuctions.isOnControl(player);
	}
	public void end(Player player, CommandSender sender) {
		fuctions.finishControl(player, sender);
	}
}
```

