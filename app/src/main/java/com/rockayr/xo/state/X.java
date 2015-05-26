package com.rockayr.xo.state;

import com.rockayr.xo.R;

/**
 * Created by Usuario on 24/05/2015.
 */
public class X implements Figura {

    @Override
    public String nombre() {
        return "Jugador 2";
    }

    @Override
    public int recurso() {
        return R.drawable.ic_close_black_48dp;
    }
}
