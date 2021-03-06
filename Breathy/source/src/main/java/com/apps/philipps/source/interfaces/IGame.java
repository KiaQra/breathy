package com.apps.philipps.source.interfaces;

import android.content.Context;
import android.support.annotation.RawRes;

/**
 * Created by Jevgenij Huebert on 27.01.2017. Project Breathy
 */
public interface IGame {
    /**
     * Start the game.
     *
     * @return true if the game successfully started
     */
    boolean startGame();

    /**
     * Start the options.
     *
     * @return true if the options successfully started
     */
    boolean startOptions();

    /**
     * Start the preview.
     *
     * @return true if the preview successfully started
     */
    @RawRes Integer getPreview();

    /**
     * Returns <code>true</code> if this game was bought.
     *
     * @return true if this game was bought
     */
    boolean isBought();

    void init(Context context, boolean bought);

    /**
     * Buy this game.
     *
     * @return true if player had enough amount of coins
     */
    boolean buy(Context context);

    /**
     * Name of the game.
     *
     * @return name of the game
     */
    String getName();
}
