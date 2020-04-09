package com.wdysolutions.www.rf_scanner.ChangeNameTemp.ChangeNameDialog;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_branch;
import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Change_temp_name_dialog_main extends DialogFragment {

    Button btn_change_name, btn_cancel, btn_close, btn_create;
    EditText edit_text;
    BroadcastReceiver epcReceiver;
    String selected_swine;
    ProgressBar scan_load;
    LinearLayout layout_button, layout_1, layout_2, loading_, layout_edit, layout_range;
    TextView txt_success_msg, txt_success_title;
    ImageView img_check;
    View view;
    Spinner spinner_range;
    Change_name_dialog_callback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.change_temp_name_dialog_main, container, false);

        callback = (Change_name_dialog_callback) getTargetFragment();

        selected_swine = getArguments().getString("id");
        final String swine_code = getArguments().getString("swine_code");
        final String user_id = getArguments().getString("user_id");
        final String company_id = getArguments().getString("company_id");
        final String category_id = getArguments().getString("category_id");
        final String company_code = getArguments().getString("company_code");

        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_change_name = view.findViewById(R.id.btn_change_name);
        edit_text = view.findViewById(R.id.edit_text);
        loading_ = view.findViewById(R.id.loading_);
        layout_button = view.findViewById(R.id.layout_button);
        layout_1 = view.findViewById(R.id.layout_1);
        layout_2 = view.findViewById(R.id.layout_2);
        layout_edit = view.findViewById(R.id.layout_edit);

        edit_text.setText(swine_code);

        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_swine_code = edit_text.getText().toString();

                if (new_swine_code.replace(" ", "").equals("")){
                    Toast.makeText(getActivity(), "Please enter swine name", Toast.LENGTH_SHORT).show();
                } else if (new_swine_code.equals(swine_code)){
                    Toast.makeText(getActivity(), "Swine name still the same", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialogBox(company_id, new_swine_code, swine_code, category_id, user_id, company_code);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    public void dialogBox(final String company_id, final String new_swine_code, final String swine_code, final String category_id, final String user_id, final String company_code){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(Html.fromHtml("Are you sure you wan't to rename <font color='#de4e35'>"+swine_code+"</font> to <font color='#de4e35'>"+new_swine_code+"</font>?"));
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        updateName(company_id, selected_swine, new_swine_code, swine_code, category_id, user_id, company_code);
                        //showLayout_2(new_swine_code, swine_code);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showLayout_2(String old_, String new_){

        scan_load = view.findViewById(R.id.scan_load);
        txt_success_msg = view.findViewById(R.id.txt_success_msg);
        txt_success_title = view.findViewById(R.id.txt_success_title);
        btn_close = view.findViewById(R.id.btn_close);
        btn_create = view.findViewById(R.id.btn_create);
        img_check = view.findViewById(R.id.img_check);
        layout_range = view.findViewById(R.id.layout_range);
        spinner_range = view.findViewById(R.id.spinner_range);

        layout_1.setVisibility(View.GONE);
        layout_2.setVisibility(View.VISIBLE);
        txt_success_msg.setText(Html.fromHtml("Updated Ear Tag From <font color='#de4e35'>"+old_+"</font> to <font color='#de4e35'>"+new_+"</font>.<br><br>Would you like to write this to ear tag? <br><br>press create to write."));

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_close.setText("Cancel");
                btn_create.setVisibility(View.GONE);
                txt_success_title.setText("Write ear tag");
                txt_success_msg.setText("You scan now");
                scan_load.setVisibility(View.VISIBLE);
                init_epc();
                initSpinner();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.dialogCallback();
            }
        });
    }

    private void initSpinner(){
        final List<String> lables = new ArrayList<>();
        lables.add("Short");
        lables.add("Medium");
        lables.add("Max");

        layout_range.setVisibility(View.VISIBLE);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner_range.setAdapter(spinnerAdapter);
        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String click = lables.get(position);
                if (click.equals("Short")){
                    ((ActivityMain)getActivity()).setPower("short");
                    Toast.makeText(getActivity(), "Scan range set to Short", Toast.LENGTH_SHORT).show();
                } else if (click.equals("Medium")){
                    ((ActivityMain)getActivity()).setPower("med");
                    Toast.makeText(getActivity(), "Scan range set to Medium", Toast.LENGTH_SHORT).show();
                } else {
                    ((ActivityMain)getActivity()).setPower("max");
                    Toast.makeText(getActivity(), "Scan range set to Max", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void updateName(final String company_id, final String id, final String swine_code, final String old_swine_code, final String category_id, final String user_id, final String company_code){
        layout_edit.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"change_temp_name/pig_updateSwineCode.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_.setVisibility(View.GONE);
                    layout_edit.setVisibility(View.VISIBLE);

                    if (response.equals("not available")){
                        Toast.makeText(getActivity(), "Ear Tag : "+swine_code+" is not available.", Toast.LENGTH_SHORT).show(); // red
                    } else if (response.equals("duplicate")){
                        Toast.makeText(getActivity(), "Duplicate name", Toast.LENGTH_SHORT).show(); // blue
                    } else if (response.equals("success update")){

                        showLayout_2(old_swine_code, swine_code);

                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    loading_.setVisibility(View.GONE);
                    layout_edit.setVisibility(View.VISIBLE);
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
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        //AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    int max = 8;
    int min = 2;
    String target_tag ="";
    public void init_epc(){
        if(epcReceiver == null){

            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String tag = intent.getExtras().get("epc").toString();
                    String write =intent.getExtras().get("epc").toString();

                    if (!tag.equals("No transponders seen")) {

                        // updateSingleItem(hexToASCII(tag));

                        if (tag.equals(String.valueOf(max))) {
                            //set_modal("System message", "Write success", "green");
                            scan_load.setVisibility(View.GONE);
                            img_check.setVisibility(View.VISIBLE);
                            txt_success_msg.setText("Successfully written");
                            btn_close.setText("Close");

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
