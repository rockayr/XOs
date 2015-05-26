package com.rockayr.xo.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.rockayr.xo.context.GameContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class GameService extends IntentService {

    private static final String ACTION_FOO = "com.rockayr.xo.service.action.FOO";
    private static final String ACTION_BAZ = "com.rockayr.xo.service.action.BAZ";

    private static final String EXTRA_PARAM1 = "com.rockayr.xo.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.rockayr.xo.service.extra.PARAM2";

    public static final String ACTION_CLOSE = "com.rockayr.xo.service.action.CLOSE";
    public static final String ACTION_SEND_MESSAGE = "com.rockayr.xo.service.action.SEND_MESSAGE";
    public static final String EXTRA_MSG= "com.rockayr.xo.service.extra.MSG";

    public static final String ACTION_RESULT_OK = "com.rockayr.xo.service.action.RESULT_OK";
    public static final String ACTION_RESULT_CANCEL = "com.rockayr.xo.service.action.RESULT_CANCEL";

    public static final String ACTION_RESPONSE = "com.rockayr.xo.service.action.RESULT_RESPONSE";
    public static final String EXTRA_RESPONSE= "com.rockayr.xo.service.extra.RESPONSE";

    public static int START_SERVICE = 0;

    private ClientSocket cliente;

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GameService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GameService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public GameService() {
        super("GameService");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                cliente= new ClientSocket();
                registrar_receiver();
                cliente.start();
            }
        };
        new Thread(runnable).start();
    }
    public void registrar_receiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SEND_MESSAGE);
        filter.addAction(ACTION_CLOSE);
        GameReceiver rcv = new GameReceiver();
        registerReceiver(rcv, filter);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    private void handleActionFoo(String param1, String param2) {

    }
    private void handleActionBaz(String param1, String param2) {

    }

    class ClientSocket extends Thread{
        Socket client;
        DataInputStream ois;
        DataOutputStream oos;
        public ClientSocket(){
            try{
                client= new Socket(GameContext.IP,GameContext.PORT);
                oos=new DataOutputStream(client.getOutputStream());
                oos.flush();
                ois=new DataInputStream(client.getInputStream());
                START_SERVICE = 1;
                conexion_exitosa();
            }catch(Exception e){
                //conexion_fallida();
                Log.e("Error connect()", "" + e);
            }

        }
        @Override
        public void run()
        {
            try{
                while(true){
                    GameListener listener= new GameListener(ois.readUTF());
                    listener.start();
                }
            }catch(Exception e){return;}
        }

        public void desconectar(){
            try{
                ois.close();
                oos.close();
                client.close();
            }catch(Exception e){}
        }

        public boolean reiniciar_conexion(){
            desconectar();
            try{
                client= new Socket(GameContext.IP,GameContext.PORT);
                oos=new DataOutputStream(client.getOutputStream());
                oos.flush();
                ois=new DataInputStream(client.getInputStream());
                return true;
            }catch(Exception e){return false;}
        }

        public void enviar(String m)
        {
            try{
                oos.writeUTF(m);
            }catch(Exception e)
            {
                Log.e("Error", "" + e);
            }
        }

        class GameListener extends Thread
        {
            Object msgX;
            String msg;
            public GameListener(Object o)
            {
                msgX=o;
            }

            @Override
            public void run()
            {
                if(msgX instanceof String){
                    msg=(String) msgX;
                    mostrar_mensaje(msg);
                }else{
                    //Error
                }
            }
        }
    }

    public void conexion_exitosa()
    {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_RESULT_OK);
        sendBroadcast(bcIntent);
    }

    public void mostrar_mensaje(String msj)
    {
        Log.e("Mensaje",msj);
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_RESPONSE);
        bcIntent.putExtra(GameService.EXTRA_RESPONSE, msj);
        sendBroadcast(bcIntent);

    }

    class GameReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_SEND_MESSAGE)) {
                String x= intent.getExtras().getString(EXTRA_MSG);
                cliente.enviar(x);
                return;
            }
            if(intent.getAction().equals(ACTION_CLOSE)) {
                cliente.desconectar();
            }
        }
    }

}
