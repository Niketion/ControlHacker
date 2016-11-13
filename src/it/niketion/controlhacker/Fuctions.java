package it.niketion.controlhacker;

import java.util.ArrayList;

public class Fuctions {

    //
    //
    //

    private static Fuctions instance;

    private Fuctions() {
        instance = this;
    }

    public static Fuctions getInstance() {
        if (instance == null) {
            instance = new Fuctions();
        }
        return instance;
    }

    //
    //
    //

    private static ArrayList<String> playerControl = new ArrayList<>();

    public boolean hasPlayerControl(String string) {
        return playerControl.contains(string);
    }

    public boolean addPlayerControl(String string) {
        return playerControl.add(string);
    }

    public boolean removePlayerControl(String string) {
        return playerControl.remove(string);
    }
}
