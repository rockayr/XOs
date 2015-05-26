package com.rockayr.xo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rockayr.xo.context.GameContext;

public class IPDialog extends DialogFragment {

    TextView description;
    EditText codigo;
    Button aceptar;
    Button cancelar;
    View progress;


    public IPDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    public IPDialogListener listener;
    public interface IPDialogListener{
        void init();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IPDialogListener){
            listener = (IPDialogListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verificar_transferencia, container, false);

        codigo = (EditText) view.findViewById(R.id.codigo);
        codigo.setText(GameContext.IP);
        aceptar = (Button) view.findViewById(R.id.btn_enviar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        cancelar = (Button) view.findViewById(R.id.btn_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void init() {
        GameContext.IP = codigo.getText().toString().trim();
        listener.init();
        dismiss();
    }


    public static IPDialog newInstance() {
        IPDialog dialog = new IPDialog();
        return dialog;
    }


}
