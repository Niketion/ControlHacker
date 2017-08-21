package niketion.github.controlhacker.bukkit;

public enum Permissions {
    ADMIN("controlhacker.admin"),
    CONTROL("controlhacker.control"),
    FINISH("controlhacker.finish"),
    PERMISSIONS_DENIED(Main.getInstance().format(Main.getInstance().getConfig().getString("permission-denied")));

    private String string;
    Permissions(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
