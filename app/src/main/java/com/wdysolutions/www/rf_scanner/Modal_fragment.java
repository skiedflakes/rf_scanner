package com.wdysolutions.www.rf_scanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Modal_fragment extends DialogFragment {

    Button btn_ok;
    TextView tv_text,tv_tittle;
    String tittle="",text="",color="";
    RelativeLayout rl_layout;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    int okay =0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_fragment, container, false);
        btn_ok = view.findViewById(R.id.btn_ok);
        tv_text = view.findViewById(R.id.tv_text);
        tv_tittle= view.findViewById(R.id.tv_tittle);
        rl_layout = view.findViewById(R.id.rl_layout);

        //set text
        text = getArguments().getString("text");
        color = getArguments().getString("color");
        tittle = getArguments().getString("tittle");
        tv_text.setText(text);
        tv_tittle.setText(tittle);
        if(color.equals("red")){
            rl_layout.setBackgroundResource(R.color.btn_red_color1);
            okay =2;
        }else{
            rl_layout.setBackgroundResource(R.color.btn_color1);
            okay =1;
        }

try {
    //button dismiss
    btn_ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            interfaceObj.senddata(okay);
            dismiss();
        }
    });
}catch (Exception e){}
        return view;
    }


    public interface dialog_interface {
        void senddata(int okay);
    }
    dialog_interface interfaceObj;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceObj= (dialog_interface) getTargetFragment();
    }


}
