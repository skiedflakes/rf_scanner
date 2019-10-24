package com.wdysolutions.www.rf_scanner.WritterTag;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;


public class WritterTag_main extends Fragment {
    BroadcastReceiver epcReceiver;
    TextView et_tag;
    Button btn_write,btn_scan;
    String swine_id = "7";
    EditText et_write;
    int value = 0;
    int max = 6;
    int min = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.writtertag_main, container, false);
        et_write=view.findViewById(R.id.et_write);
        et_tag = view.findViewById(R.id.et_tag);
        btn_write = view.findViewById(R.id.btn_write);
        btn_scan = view.findViewById(R.id.btn_scan);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ((ActivityMain)getActivity()).read_tag();

            }
        });

        on_scan();

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // max = getCount(swine_id);
              // dialogBox(String.valueOf(max)+" "+String.valueOf(min));
               ///Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                swine_id = et_write.getText().toString();
                get_swine_id();

               // dialogBox(swine_id);
                ((ActivityMain)getActivity()).write_tag(et_tag.getText().toString(),swine_id,max,min);
            }
        });


        //load all swine id

        return view;
    }

    public void get_swine_id(){
        int max = 12-getCount(swine_id);

        for(int i=0;i<max;i++){

            swine_id = swine_id+" ";
        }
        swine_id = asciiToHex(swine_id);
    }


    public void on_scan(){

        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String tag = intent.getExtras().get("epc").toString();

                    if (!tag.equals("No transponders seen")){

                        Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
                        et_tag.setText(tag);
                      //  min = getCount(hexToASCII(tag));
                        dialogBox(tag);

                    } else {
                        Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));

    }



    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        EditText text = new EditText(getActivity());
        text.setText(name);
        alertDialog.setView(text);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static int getCount(String number) {
        int flag = 0;
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                flag++;
            }
        }
        return flag;
    }

    private static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    private static String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString().replaceAll(" ","");
    }


}
