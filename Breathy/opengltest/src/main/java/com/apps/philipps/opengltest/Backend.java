package com.apps.philipps.opengltest;

import android.content.Context;

import com.apps.philipps.source.OptionManager;
import com.apps.philipps.source.SaveData;

import java.util.ArrayList;

public class Backend {
    private static boolean init = false;
    private static int defaut_music_resource_id = R.raw.asmario;
    public static String gName;
    public static OptionManager<String, Boolean> options;
    private static String key;
    /**
     * The constant highscore.
     */
    public static int highscore;
    /**
     * Reinitialize <code>Backend</code> of the game.
     */
    public static void reinit(Context context, String name) {
        init = false;
        init(context, name);
    }

    /**
     * Initialize <code>Backend</code> of the game.
     */
    public static boolean init(Context context, String name) {
        gName = name;
        if (!init) {
            options = new OptionManager<>();
            options.add("White Car", false, 20);
            options.add("Police Car", false, 40);
            options.add("Ambulance Car", false, 30);
            init = true;
            loadGameOptions(context, gName);
            return true;
        }
        return false;
    }

    public static int getDefaut_music_resource_id() {
        return defaut_music_resource_id;
    }

    public static void setDefaut_music_resource_id(int defaut_music_resource_id) {
        Backend.defaut_music_resource_id = defaut_music_resource_id;
    }

    protected static void loadGameOptions(Context context, String keyword){
        keyword = keyword.replaceAll("[ ]*", "");
        key = keyword + "Options";
        SaveData<ArrayList> saveOptions =  new SaveData<>(context);
        ArrayList gameOptions = saveOptions.readObject(key);
        if (gameOptions != null)
            options.setOptions(gameOptions);
    }

    public static void saveGameOptions(Context context, String keyword){
        keyword = keyword.replaceAll("[ ]*", "");
        key = keyword + "Options";
        SaveData<ArrayList> saveOptions =  new SaveData<>(context);
        saveOptions.writeObject(key , options.getOptions());
    }
}