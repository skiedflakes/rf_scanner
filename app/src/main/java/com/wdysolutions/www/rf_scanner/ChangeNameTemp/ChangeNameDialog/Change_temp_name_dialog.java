package com.wdysolutions.www.rf_scanner.ChangeNameTemp.ChangeNameDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.ChangeNameTemp.Change_temp_name;
import com.wdysolutions.www.rf_scanner.ChangeNameTemp.Change_temp_name_adapter;
import com.wdysolutions.www.rf_scanner.ChangeNameTemp.Change_temp_name_model;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Change_temp_name_dialog extends DialogFragment implements Modal_fragment.dialog_interface {

    Button btn_change_name, btn_cancel;
    EditText edit_text;
    BroadcastReceiver epcReceiver;
    String selected_swine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_temp_name_dialog, container, false);

        selected_swine = getArguments().getString("id");
        final String swine_code = getArguments().getString("swine_code");
        final String user_id = getArguments().getString("user_id");
        final String company_id = getArguments().getString("company_id");
        final String category_id = getArguments().getString("category_id");

        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_change_name = view.findViewById(R.id.btn_change_name);
        edit_text = view.findViewById(R.id.edit_text);

        edit_text.setText(swine_code);

        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_swine_code = edit_text.getText().toString();

                if (new_swine_code.replace(" ", "").equals("")){
                    Toast.makeText(getActivity(), "Please enter swine name", Toast.LENGTH_SHORT).show();
                } else {
                    updateName(company_id, selected_swine, new_swine_code, swine_code, category_id, user_id);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        init_epc();
        return view;
    }

    public void updateName(final String company_id, final String id, final String swine_code, final String old_swine_code, final String category_id, final String user_id){
        String URL = "http://192.168.1.181/test_swine/pig_updateSwineCode.php";
        //String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.equals("not available")){
                        Toast.makeText(getActivity(), "Ear Tag : "+swine_code+" is not available.", Toast.LENGTH_SHORT).show(); // red
                    } else if (response.equals("duplicate")){
                        Toast.makeText(getActivity(), "Duplicate name", Toast.LENGTH_SHORT).show(); // blue
                    } else if (response.equals("success update")){
                        Toast.makeText(getActivity(), "Updated Ear Tag From "+old_swine_code+" to "+swine_code+".", Toast.LENGTH_SHORT).show(); // green
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", id);
                hashMap.put("swine_code", swine_code);
                hashMap.put("category_id", category_id);
                hashMap.put("user_id", user_id);
                return hashMap;
            }
        };
        //AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void set_modal(String tittle, String text, String color){
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("text",text);
        bundle.putString("color",color);
        Modal_fragment fragment = new Modal_fragment();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(false);
        isModalOpen = true;
    }

    int max = 8;
    int min = 2;
    String target_tag ="";
    boolean isVolleyLoad = false;
    boolean isModalOpen = false;
    public void init_epc(){
        if(epcReceiver == null){

            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String tag = intent.getExtras().get("epc").toString();
                    String write =intent.getExtras().get("epc").toString();

                    if (!isModalOpen){
                        if (!isVolleyLoad) {

                            if (!tag.equals("No transponders seen")) {

                                // updateSingleItem(hexToASCII(tag));

                                if (tag.equals(String.valueOf(max))) {

                                    isVolleyLoad = true;
                                    set_modal("System message", "Write success", "green");

                                }else {

                                    try {
                                        String eart_tag = hexToASCII(tag);

                                        String[] separated = eart_tag.split("-");
                                        String newstr = separated[0].replaceAll("[^A-Za-z]+", "");
                                        if (newstr.equals("wdy")) {
                                            target_tag = tag;
                                            ((ActivityMain) getActivity()).write_tag(target_tag, set_new_tag(), max, min);

                                            // update_counter(target_tag, String.valueOf(selected_swine), String.valueOf(selected_count + 1));

                                        } else {

                                            Toast.makeText(context, "invalid tag", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e){}
                                }
                            } else {
                                Toast.makeText(context, "No eartag seen", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, "Please wait... tag is writting", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please finish action", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (epcReceiver != null) { getActivity().unregisterReceiver(epcReceiver); }
    }

    @Override
    public void senddata(int okay) {
        Toast.makeText(getActivity(), String.valueOf(okay), Toast.LENGTH_SHORT).show();

        isVolleyLoad = false;
        isModalOpen = false;
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

    private static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    String new_tag;
    public String set_new_tag(){
        new_tag = "wdy-"+String.valueOf(selected_swine);
        int max2 = 12-getCount(new_tag);

        for(int i=0;i<max2;i++){
            new_tag = new_tag+" ";
        }
        new_tag = asciiToHex(new_tag);
        return new_tag;
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
}
