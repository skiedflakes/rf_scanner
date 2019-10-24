package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication.Add_Medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
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
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication.Medication_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination.Add_Vaccine.Diagnosis_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination.Add_Vaccine.Vaccine_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class addMedication_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    Spinner spinner_medicine, spinner_diagnosis;
    String selectedDiagnosis = "", selectedMedicine = "", selectedDate = "", currentDate="", checkedCounter, user_id;
    ArrayList<Vaccine_model> Vaccine_models = new ArrayList<>();
    ArrayList<Diagnosis_model> diagnosis_models = new ArrayList<>();
    TextView btn_date;
    EditText input_dosage;
    Button btn_save;
    LinearLayout layout_add;
    ProgressBar progressBar, loading_save;
    String selectView;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.medication_addmedication_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        final String swine_scanned_id = getArguments().getString("swine_scanned_id");
        final String array_piglets = getArguments().getString("array_piglets");
        selectView = getArguments().getString("selectView");
        checkedCounter = getArguments().getString("checkedCounter");

        loading_save = view.findViewById(R.id.loading_save);
        progressBar = view.findViewById(R.id.progressBar);
        layout_add = view.findViewById(R.id.layout_add);
        btn_save = view.findViewById(R.id.btn_save);
        input_dosage = view.findViewById(R.id.input_dosage);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_diagnosis = view.findViewById(R.id.spinner_diagnosis);
        spinner_medicine = view.findViewById(R.id.spinner_medicine);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(false); // +7 days
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate.equals("")){
                    Toast.makeText(getActivity(), "Please select date.", Toast.LENGTH_SHORT).show();
                } else if (selectedDiagnosis.equals("")){
                    Toast.makeText(getActivity(), "Please select diagnosis.", Toast.LENGTH_SHORT).show();
                } else if (selectedMedicine.equals("")){
                    Toast.makeText(getActivity(), "Please select medicine.", Toast.LENGTH_SHORT).show();
                } else if (input_dosage.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Please enter dosage.", Toast.LENGTH_SHORT).show();
                } else {
                    saveMedication(company_id, company_code, swine_scanned_id, array_piglets);
                }
            }
        });


        getMedication(company_id, company_code, swine_scanned_id, "1");
        getDiagnos(company_id, company_code, "2");
        return view;
    }

    private boolean isPiglets(){
        if (selectView.equals("piglet")){
            btn_save.setText("Save Entry");
            return true;
        } else {
            btn_save.setText("Save Medication"); // Dosage per Swine
            return false;
        }
    }

    private void isLoadCompleted(boolean isTrue){
        if (isTrue){
            progressBar.setVisibility(View.GONE);
            layout_add.setVisibility(View.VISIBLE);
        } else {
            dismiss();
            Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDiagnos(final String company_id, final String company_code, final String get_type){
        String URL = getString(R.string.URL_online)+"scan_eartag/history/medication_get.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    isLoadCompleted(true);
                    diagnosis_models.add(new Diagnosis_model(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        diagnosis_models.add(new Diagnosis_model(cusObj.getInt("disease_id"),
                                cusObj.getString("cause")));
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < diagnosis_models.size(); i++) {
                        lables.add(diagnosis_models.get(i).getName());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_diagnosis.setAdapter(spinnerAdapter);
                    spinner_diagnosis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Diagnosis_model click = diagnosis_models.get(position);
                            if (!click.getName().equals("Please Select")){
                                selectedDiagnosis = String.valueOf(click.getId());
                            } else {
                                selectedDiagnosis = "";
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
                    isLoadCompleted(false);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type", get_type);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getMedication(final String company_id, final String company_code, final String swine_id, final String get_type){
        String URL = getString(R.string.URL_online)+"scan_eartag/history/"+(isPiglets() ? "pig_piglets_medication.php" : "medication_get.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Vaccine_models.add(new Vaccine_model(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        Vaccine_models.add(new Vaccine_model(cusObj.getInt("product_id"),
                                cusObj.getString("product")));

                        currentDate = cusObj.getString("current_date");
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < Vaccine_models.size(); i++) {
                        lables.add(Vaccine_models.get(i).getName());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_medicine.setAdapter(spinnerAdapter);
                    spinner_medicine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Vaccine_model click = Vaccine_models.get(position);
                            if (!click.getName().equals("Please Select")){
                                selectedMedicine = String.valueOf(click.getId());
                            } else {
                                selectedMedicine = "";
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

                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("get_type", get_type);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveMedication(final String company_id, final String company_code, final String swine_id, final String array_piglets){
        loading_save.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/"+ (isPiglets() ? "pig_piglets_medication_add.php" : "medication_insert.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);

                    if (response.equals("okay")){
                        dismiss();

                        FragmentManager fm = getFragmentManager();

                        // refresh medication
                        Medication_main fragment = (Medication_main)fm.findFragmentByTag("fragmentRF");
                        fragment.getMedicationDetails(company_code, company_id, swine_id, "3");

                        // refresh swine card
                        RFscanner_main fragment_ = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment_.get_details(company_code, company_id, swine_id);

                        Toast.makeText(getActivity(), "Successfully save.", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("insufficient")){
                        Toast.makeText(getActivity(), "Insufficient", Toast.LENGTH_SHORT).show();
                    }
                    // piglets
                    else {
                        String success="";
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data_status");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        JSONArray details = jsonObject.getJSONArray("data");
                        for (int i=0; i<details.length();i++){
                            JSONObject r = details.getJSONObject(i);
                            success += r.getString("status")+"\n";
                        }

                        if (jsonObject1.getString("status").equals("complete")){
                            dismiss();
                            FragmentManager fm = getFragmentManager();
                            Medication_main fragment = (Medication_main)fm.findFragmentByTag("fragmentRF");
                            fragment.getMedicationDetails(company_code, company_id, swine_id, "3");

                            // refresh swine card
                            RFscanner_main fragment_ = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                            fragment_.get_details(company_code, company_id, swine_id);

                            Toast.makeText(getActivity(), "Successfully save.", Toast.LENGTH_SHORT).show();
                        }
                        else if (jsonObject1.getString("status").equals("insufficient")){
                            Toast.makeText(getActivity(), "Insufficient", Toast.LENGTH_SHORT).show();
                        }
                        else if (jsonObject1.getString("status").equals("failed")){
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                        dialogBox(success);
                    }

                } catch (JSONException e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("diagnosis", selectedDiagnosis);
                hashMap.put("swine_id", isPiglets() ? array_piglets : swine_id);
                hashMap.put("medication", selectedMedicine);
                hashMap.put("date_added", selectedDate);
                hashMap.put("check_counter", checkedCounter);
                hashMap.put("user_id", user_id);
                hashMap.put("dosage", input_dosage.getText().toString());
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
        bundle.putString("maxDate", plus7days(maxDate[0]));
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

    private String plus7days(String date){
        try {
            final Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, 7);
            String date_plus7 = sdf.format(c.getTime());
            return date_plus7;
        } catch (ParseException e){}
        return null;
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
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
