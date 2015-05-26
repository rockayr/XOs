package com.rockayr.xo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockayr.xo.context.GameContext;
import com.rockayr.xo.service.GameService;
import com.rockayr.xo.state.O;
import com.rockayr.xo.state.Figura;
import com.rockayr.xo.state.X;

import java.util.HashMap;
import java.util.Map;


public class GameActivity extends ActionBarActivity implements View.OnClickListener, IPDialog.IPDialogListener {

    private static final long SPLASH_MILLIS = 200;

    HashMap<Integer,ImageView> boxes;

    TextView text;

    private Figura figura;

    public Figura getState() {
        return figura;
    }

    public void setState(Figura figura) {
        this.figura = figura;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        InitComponents();

        text = (TextView) findViewById(R.id.txt);
        registrar_receiver();

        IPDialog dialog = IPDialog.newInstance();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppThemeDialog);
        dialog.show(getSupportFragmentManager(),null);

    }

    private void InitComponents() {
        boxes = new HashMap<>();
        ImageView ivOne = (ImageView) findViewById(R.id.ivOne);
        boxes.put(1,ivOne);
        ImageView ivTwo = (ImageView) findViewById(R.id.ivTwo);
        boxes.put(2,ivTwo);
        ImageView ivThree = (ImageView) findViewById(R.id.ivThree);
        boxes.put(3,ivThree);
        ImageView ivFour = (ImageView) findViewById(R.id.ivFour);
        boxes.put(4,ivFour);
        ImageView ivFive = (ImageView) findViewById(R.id.ivFive);
        boxes.put(5,ivFive);
        ImageView ivSix = (ImageView) findViewById(R.id.ivSix);
        boxes.put(6,ivSix);
        ImageView ivSeven = (ImageView) findViewById(R.id.ivSeven);
        boxes.put(7,ivSeven);
        ImageView ivEight = (ImageView) findViewById(R.id.ivEight);
        boxes.put(8,ivEight);
        ImageView ivNine = (ImageView) findViewById(R.id.ivNine);
        boxes.put(9,ivNine);

        ivOne.setOnClickListener(this);
        ivTwo.setOnClickListener(this);
        ivThree.setOnClickListener(this);
        ivFour.setOnClickListener(this);
        ivFive.setOnClickListener(this);
        ivSix.setOnClickListener(this);
        ivSeven.setOnClickListener(this);
        ivEight.setOnClickListener(this);
        ivNine.setOnClickListener(this);
    }

    public void registrar_receiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GameService.ACTION_RESULT_OK);
        filter.addAction(GameService.ACTION_RESULT_CANCEL);
        filter.addAction(GameService.ACTION_RESPONSE);
        ConnectionReceiver rcv = new ConnectionReceiver();
        registerReceiver(rcv, filter);
    }

    @Override
    public void onClick(View v) {
        int box = -1;
        for(Map.Entry<Integer,ImageView> s :boxes.entrySet()){
            if(s.getValue().getId() == v.getId()){
                box = s.getKey();
                break;
            }
        }

        if(box==-1) return;

        Intent bcIntent = new Intent();
        bcIntent.setAction(GameService.ACTION_SEND_MESSAGE);
        bcIntent.putExtra(GameService.EXTRA_MSG, GameContext.player + "," + String.valueOf(box));
        sendBroadcast(bcIntent);
        if (GameContext.player == 2){
            setState(new X());
        }else{
            setState(new O());
        }

        markBox(box);
        enableImageView(false);
    }

    @Override
    public void init() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try{
                    Thread.sleep(SPLASH_MILLIS);
                    if(GameService.START_SERVICE==0){
                        Intent msgIntent = new Intent(GameActivity.this, GameService.class);
                        startService(msgIntent);
                    }
                }catch(Exception e){}
            }
        }, SPLASH_MILLIS);
    }

    class ConnectionReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GameService.ACTION_RESULT_OK)) {
                //Conexion Exitosa
                return;
            }
            if(intent.getAction().equals(GameService.ACTION_RESULT_CANCEL)) {
                text.setText("No se pudo conectar..");
                return;
            }
            if(intent.getAction().equals(GameService.ACTION_RESPONSE)) {
                String msj = intent.getExtras().getString(GameService.EXTRA_RESPONSE);
                switch (msj){
                    case GameContext.WAIT:
                        text.setText("Esperando al jugador 2...");
                        GameContext.player = 1;
                        //En espera
                        break;
                    case GameContext.START:
                        if(GameContext.player == -1){
                            GameContext.player = 2;
                        }
                        text.setText("Jugador "+String.valueOf(GameContext.player));
                        enableImageView(true);
                        break;
                    case GameContext.ESTA_MARCADA:
                        break;
                    case GameContext.GANADOR_1:
                        toast("Ha ganado el Jugador 1.");
                        clean();
                        break;
                    case GameContext.GANADOR_2:
                        toast("Ha ganado el Jugador 2.");
                        clean();
                        break;
                    case GameContext.EMPATE:
                        toast("El juego ha empatado.");
                        clean();
                        break;
                    default:

                        if (GameContext.player == 1){
                            setState(new X());
                        }else{
                            setState(new O());
                        }
                        markBox(Integer.parseInt(msj));
                        enableImageView(true);
                        break;
                }

                return;
            }
        }
    }

    public void toast(String msj){
        Toast.makeText(this,msj,Toast.LENGTH_LONG).show();
    }

    private void clean() {
        InitComponents();
        for(Map.Entry<Integer,ImageView> s :boxes.entrySet()){
            s.getValue().setImageDrawable(null);
            s.getValue().setEnabled(true);
        }
    }

    private void enableImageView(boolean b) {
        for(Map.Entry<Integer,ImageView> s :boxes.entrySet()){
            s.getValue().setEnabled(b);
        }
    }

    public void markBox(int box)
    {
        ImageView iv = boxes.get(box);
        iv.setImageResource(getState().recurso());
        boxes.remove(box);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent bcIntent = new Intent();
        bcIntent.setAction(GameService.ACTION_CLOSE);
        sendBroadcast(bcIntent);
    }
}
