package com.rockayr.xo.state;

import com.rockayr.xo.R;

/**
 * Created by Usuario on 24/05/2015.
 */
public class O implements Figura {
    @Override
    public String nombre() {
        return "Jugador 1";
    }

    @Override
    public int recurso() {
        return R.drawable.ic_panorama_fisheye_black_48dp;
    }
}
