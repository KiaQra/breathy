package com.apps.philipps.source.abstracts;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.apps.philipps.source.interfaces.IBootable;

/**
 * Created by Jevgenij Huebert on 28.01.2017. Project Breathy
 */
public abstract class AbstractGameObject implements IBootable {
    /**
     * The Context.
     */
    protected Context context;

    @Override
    public void makeToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean end() {
        return true;
    }
}