package com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Med_Vac_locate_main extends DialogFragment {

    BroadcastReceiver epcReceiver;
    LinearLayout locate_eartag, layout_apply;

    ArrayList<Med_Vac_model_product> med_vac_model_products = new ArrayList<>();
    String branch_id, user_id, company_code, company_id, selectAction = "M", selectProduct = "",
            swine_id, swine_code, sched_id, pen_id, selectedDosage = "", selectedDisease = "";
    String[] current_date = {""};
    Spinner spinner;
    int position, count_action_not_select;
    ProgressBar loading_product, loading_save;
    TextView btn_med, btn_vacc, btn_date;
    dataDialog dataDialog;
    EditText dosage_edittext;
    ArrayAdapter<String> spinnerAdapter;
    Button btn_apply;

    int counter_swine_id=0;

    public interface dataDialog{
        void senddata(String swine_id, int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.med_vac_locate_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        branch_id = getArguments().getString("branch_id");
        swine_code = getArguments().getString("swine_code");
        swine_id = getArguments().getString("swine_id");
        pen_id = getArguments().getString("pen_id");
        position = getArguments().getInt("position");
        dataDialog = (dataDialog)getTargetFragment();

        locate_eartag = view.findViewById(R.id.locate_eartag);
        layout_apply = view.findViewById(R.id.layout_apply);

        TextView txt_locate = view.findViewById(R.id.txt_locate);
        txt_locate.setText("Locate Eartag "+swine_code);

        // for testing
        //initApply_layout(view);

        initReceiver(view);
        return view;
    }

    private void initReceiver(final View view){
        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try{
                        String tag = intent.getExtras().get("epc").toString();
                        counter_swine_id++;

                        if(counter_swine_id==1){

                            if (!tag.equals("No transponders seen")){

                                String eart_tag =  hexToASCII(tag);
                                String[] separated = eart_tag.split("-");

                                String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                                String str_digits = separated[0].replaceAll("\\D+","");

                                if(str_characters.equals("wdy")){

                                    if (separated[1].equals(swine_id)){

                                        initApply_layout(view);

                                        //String eart_tag =  hexToASCII(tag);
                                        //String[] separated = eart_tag.split("-");
                                        //getDetails(company_code, company_id,separated[1]);

                                    }else{
                                        counter_swine_id=0;
                                        Toast.makeText(context, "Invalid ear tag", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Invalid ear tag", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(context, "No ear tag found", Toast.LENGTH_SHORT).show();
                                counter_swine_id=0;
                            }
                        }
                    }catch (Exception e){}
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));
    }

    private void unregisterReceiver(){
        if (epcReceiver != null){
            getActivity().unregisterReceiver(epcReceiver);
        }
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



    // Apply layout --------------------------------------------------------------------------------

    private void initApply_layout(View view){
        layout_apply.setVisibility(View.VISIBLE);
        locate_eartag.setVisibility(View.GONE);

        CheckBox checkbox_swine = view.findViewById(R.id.checkbox_swine);
        checkbox_swine.setText(swine_code);
        checkbox_swine.setChecked(true);
        checkbox_swine.setClickable(false);

        dosage_edittext = view.findViewById(R.id.dosage_edittext);
        loading_save = view.findViewById(R.id.loading_save);
        btn_apply = view.findViewById(R.id.btn_apply);
        btn_date = view.findViewById(R.id.btn_date);
        btn_med = view.findViewById(R.id.btn_med);
        btn_vacc = view.findViewById(R.id.btn_vacc);
        loading_product = view.findViewById(R.id.loading_product);
        spinner = view.findViewById(R.id.spinner);

        btn_med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMed();
            }
        });

        btn_vacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectVac();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (med_vac_model_products.size() == 0){
                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                } else {
                    apply();
                }
            }
        });

        // default
        selectMed();
    }

    private void saveLoading(boolean status){
        if (status){
            btn_apply.setVisibility(View.GONE);
            loading_save.setVisibility(View.VISIBLE);
        } else {
            btn_apply.setVisibility(View.VISIBLE);
            loading_save.setVisibility(View.GONE);
        }
    }

    private void selectMed(){
        btn_med.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_med.setTextColor(getResources().getColor(R.color.white));
        btn_med.setEnabled(false);
        btn_vacc.setBackgroundResource(0);
        btn_vacc.setTextColor(Color.BLACK);
        btn_vacc.setEnabled(true);
        selectAction = "M";
        btn_date.setText("");
        btn_apply.setClickable(false);
        dosage_edittext.setEnabled(false);
        dosage_edittext.setText("");
        med_vac_model_products.clear();
        get_product("get_product");
    }

    private void selectVac(){
        btn_med.setBackgroundResource(0);
        btn_med.setTextColor(Color.BLACK);
        btn_med.setEnabled(true);
        btn_vacc.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_vacc.setTextColor(getResources().getColor(R.color.white));
        btn_vacc.setEnabled(false);
        selectAction = "V";
        btn_date.setText("");
        btn_apply.setClickable(false);
        dosage_edittext.setEnabled(false);
        dosage_edittext.setText("");
        med_vac_model_products.clear();
        get_product("get_product");
    }

    private void productLoading(boolean status){
        if (status){
            spinner.setVisibility(View.GONE);
            loading_product.setVisibility(View.VISIBLE);
        } else {
            spinner.setVisibility(View.VISIBLE);
            loading_product.setVisibility(View.GONE);
        }
    }

    private void get_product(final String get_type){
        productLoading(true);
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //dialogBox(response);
                    productLoading(false);
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_product");

                    if (response.equals("{\"response_product\":[]}")){
                        med_vac_model_products.add(new Med_Vac_model_product(0, "No schedule product","","", "",0));
                    }
                    else {
                        btn_apply.setClickable(true);
                        dosage_edittext.setEnabled(true);

                        // current date
                        JSONObject curretDate = (JSONObject) diag.get(0);
                        current_date = curretDate.getString("current_date").split(" ");
                        btn_date.setText(current_date[0]);

                        for (int i = 0; i < diag.length(); i++) {
                            JSONObject cusObj = (JSONObject) diag.get(i);

                            med_vac_model_products.add(new Med_Vac_model_product(cusObj.getInt("product_id"),
                                    cusObj.getString("product_name"),
                                    cusObj.getString("dosage"),
                                    cusObj.getString("disease_id"),
                                    cusObj.getString("med_vac_sched_id"),
                                    cusObj.getInt("count_not_selected_action")));
                        }
                    }

                    initSpinner(med_vac_model_products);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    productLoading(false);
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("get_type",get_type);
                hashMap.put("branch_id", branch_id);
                hashMap.put("action", selectAction);
                hashMap.put("swine_id", swine_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void apply(){
        saveLoading(true);
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_apply.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    //dialogBox(response);
                    saveLoading(false);
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_apply");
                    JSONObject cusObj = (JSONObject) diag.get(0);
                    String status = cusObj.getString("response_status");
                    String msg = cusObj.getString("response_msg");

                    if (status.equals("success")){
                        checkSpinner();
                    } else if (status.equals("insufficient")){

                    } else if (status.equals("error")){

                    }

                    dialogBox(msg);

                } catch (JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    saveLoading(false);
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("user_id", user_id);
                hashMap.put("date_applied", current_date[0]);
                hashMap.put("med_vac_sched_id", sched_id);
                hashMap.put("pen_code", pen_id);
                hashMap.put("product_id", selectProduct);
                hashMap.put("disease_id", selectedDisease);
                hashMap.put("dosage", dosage_edittext.getText().toString());
                hashMap.put("action", selectAction);
                hashMap.put("swine_id", swine_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void initSpinner(final ArrayList<Med_Vac_model_product> med_vac_model_products){

        List<String> lables = new ArrayList<>();
        for (int i = 0; i < med_vac_model_products.size(); i++) {
            lables.add(med_vac_model_products.get(i).getProduct_name());
        }

        spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Med_Vac_model_product click = med_vac_model_products.get(position);
                selectProduct = String.valueOf(click.getProduct_id());
                selectedDisease = click.getDisease_id();
                selectedDosage = click.getDosage();
                sched_id = click.getMed_vac_sched_id();
                count_action_not_select = click.getCount_not_selected_action();

                dosage_edittext.setText(selectedDosage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void checkSpinner(){
        // if product schedule is 1 left and "action not selected" is empty product schedule
        if (med_vac_model_products.size() == 1 && count_action_not_select == 0){
            dataDialog.senddata(swine_id, position);
            dismiss();
        }
        // else it will remove
        else {
            dosage_edittext.setText("");

            for (int i=0; i<med_vac_model_products.size(); i++){
                String getMed_vac_sched_id = String.valueOf(med_vac_model_products.get(i).getMed_vac_sched_id());

                if (sched_id.equals(getMed_vac_sched_id)){
                    med_vac_model_products.remove(i);
                    initSpinner(med_vac_model_products);
                    break;
                }
            }
        }
    }

    void dialogBox(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        unregisterReceiver();
    }

}
