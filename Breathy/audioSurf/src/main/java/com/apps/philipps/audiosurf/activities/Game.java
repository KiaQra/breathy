package com.apps.philipps.audiosurf.activities;

import android.app.Activity;
import android.os.Bundle;

import com.apps.philipps.audiosurf.R;

/**
 * Game Activity
 */
public class Game extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_game);
    }
    //TODO: Hier wird das Spiel ausgeführt. OpenGL.
}