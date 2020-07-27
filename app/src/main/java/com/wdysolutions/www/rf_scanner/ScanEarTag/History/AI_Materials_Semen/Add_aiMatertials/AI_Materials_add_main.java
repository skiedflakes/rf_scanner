package com.wdysolutions.www.rf_scanner.ScanEarTag.History.AI_Materials_Semen.Add_aiMatertials;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.AI_Materials_Semen.AI_Materials_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AI_Materials_add_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_date;
    Spinner spinner_materials;
    EditText input_qty;
    ProgressBar loading_save, progressBar;
    Button btn_save;
    LinearLayout layout_add;
    ArrayList<AI_Materials_add_model> diagnosis_models = new ArrayList<>();
    String selectedMaterials = "", swine_scanned_id = "", selectedDate = "", currentDate = "", user_id, category_id;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ai_materials_add_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        layout_add = view.findViewById(R.id.layout_add);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_materials = view.findViewById(R.id.spinner_materials);
        input_qty = view.findViewById(R.id.input_qty);
        btn_save = view.findViewById(R.id.btn_save);
        loading_save = view.findViewById(R.id.loading_save);
        progressBar = view.findViewById(R.id.progressBar);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(false);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedDate.equals("")){
                    Toast.makeText(getActivity(), "Please select date.", Toast.LENGTH_SHORT).show();
                }
                else if (selectedMaterials.equals("")){
                    Toast.makeText(getActivity(), "Please select AI Material.", Toast.LENGTH_SHORT).show();
                }
                else if (input_qty.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Please enter qty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveMaterials(company_id, company_code, input_qty.getText().toString());
                }
            }
        });

        getSpinner(company_id, company_code);
        return view;
    }

    public void getSpinner(final String company_id, final String company_code){
        String URL = getString(R.string.URL_online)+"scan_eartag/history/ai_materials_get_modal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_add.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    diagnosis_models.add(new AI_Materials_add_model(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("data");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        diagnosis_models.add(new AI_Materials_add_model(cusObj.getInt("product_id"),
                                cusObj.getString("product")));

                        currentDate = cusObj.getString("current_date");
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < diagnosis_models.size(); i++) {
                        lables.add(diagnosis_models.get(i).getProduct());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_materials.setAdapter(spinnerAdapter);
                    spinner_materials.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            AI_Materials_add_model click = diagnosis_models.get(position);
                            if (!click.getProduct().equals("Please Select")){
                                selectedMaterials = String.valueOf(click.getProduct_id());
                            } else {
                                selectedMaterials = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismiss();
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveMaterials(final String company_id, final String company_code, final String qty){
        loading_save.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/ai_materials_add_modal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                
                try {
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    if (response.equals("okay")){
                        FragmentManager fm = getFragmentManager();
                        AI_Materials_main fragment = (AI_Materials_main)fm.findFragmentByTag("fragmentRF");
                        fragment.getAIMatertialsDetails(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Successfully saved.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else if (response.equals("Insufficient")){
                        Toast.makeText(getActivity(), "Insufficient.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismiss();
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("company_code", company_code);
                hashMap.put("ai_material_added", selectedDate);
                hashMap.put("ai_material_id", selectedMaterials);
                hashMap.put("reference_number", "");
                hashMap.put("category_id", category_id);
                hashMap.put("user_id", user_id);
                hashMap.put("ai_qty", qty);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        String[] maxDate = currentDate.split(" ");

        Bundle bundle = new Bundle();
        bundle.putString("maxDate", maxDate[0]);
        bundle.putString("maxDate_minus", "");
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(String date) {
        selectedDate = date;
        btn_date.setText(selectedDate);
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        alertDialog.setView(input);
        input.setText(name);
        alertDialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
