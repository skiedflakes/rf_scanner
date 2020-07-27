package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addBreedingFailed;

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
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Farrowing_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Farrowing_addBreedingFailed_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    int getId;
    String getBreeding_id, getBreeding_date, getBreeding_date_minus21, getStatus;
    TextView btn_date_breeding, btn_date_breedingFaield;
    Button btn_save;
    Spinner spinner_boar1, spinner_boar2, spinner_boar3, spinner_technician;
    ProgressBar loading_save, progressBar;
    ArrayList<Farrowing_addBreedFailed_model> Farrowing_addBreedFailed_models = new ArrayList<>();
    ArrayList<Farrowing_addTechnician_model> Farrowing_addTechnician_models = new ArrayList<>();
    String selectedBoar1, selectedBoar2, selectedBoar3, selectedTech, dateBreeding = "", dateBreedingFailed = "", selectedDate,
            category_id;
    LinearLayout layout_add;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farrowing_add_breeding_failed_main, container, false);
        final SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        final String user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        final String swine_scanned_id = getArguments().getString("swine_scanned_id");
        getBreeding_date = getArguments().getString("getBreeding_date");
        getBreeding_id = getArguments().getString("getBreeding_id");
        getBreeding_date_minus21 = getArguments().getString("getBreeding_date_minus21");
        getStatus = getArguments().getString("getStatus");
        getId = getArguments().getInt("getId");

        layout_add = view.findViewById(R.id.layout_add);
        btn_date_breeding = view.findViewById(R.id.btn_date_breeding);
        btn_date_breedingFaield = view.findViewById(R.id.btn_date_breedingFaield);
        spinner_boar1 = view.findViewById(R.id.spinner_boar1);
        spinner_boar2 = view.findViewById(R.id.spinner_boar2);
        spinner_boar3 = view.findViewById(R.id.spinner_boar3);
        spinner_technician = view.findViewById(R.id.spinner_technician);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);

        btn_date_breeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "breeding";
                openDatePicker(true);
            }
        });

        btn_date_breedingFaield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDate = "breedingFailed";
                openDatePicker(false);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dateBreeding.equals("")){
                    Toast.makeText(getActivity(), "Please select breeding date.", Toast.LENGTH_SHORT).show();
                } else if (dateBreedingFailed.equals("")){
                    Toast.makeText(getActivity(), "Please select breeding failed date.", Toast.LENGTH_SHORT).show();
                } else if (selectedBoar1.equals("")){
                    Toast.makeText(getActivity(), "Please select Boar 1 used.", Toast.LENGTH_SHORT).show();
                } else if (selectedTech.equals("")){
                    Toast.makeText(getActivity(), "Please select breeding technician.", Toast.LENGTH_SHORT).show();
                } else {
                    saveBreedingFailed(company_id, company_code, swine_scanned_id, user_id);
                }
            }
        });

        getBoarSpinner(company_id, company_code, swine_scanned_id);
        return view;
    }

    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        Bundle bundle = new Bundle();
        bundle.putString("maxDate", getBreeding_date);
        bundle.putString("maxDate_minus", getBreeding_date_minus21);
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(String date) {
        if (selectedDate.equals("breeding")){
            dateBreeding = date;
            btn_date_breeding.setText(dateBreeding);
        }
        else if (selectedDate.equals("breedingFailed")){
            dateBreedingFailed = date;
            btn_date_breedingFaield.setText(dateBreedingFailed);
        }
    }

    public void getBoarSpinner(final String company_id, final String company_code, final String swine_id){
        progressBar.setVisibility(View.VISIBLE);
        layout_add.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_farrowing_add_breding_failed_get.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);
                    Farrowing_addBreedFailed_models.add(new Farrowing_addBreedFailed_model(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("fetch_boar_data");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        Farrowing_addBreedFailed_models.add(new Farrowing_addBreedFailed_model(cusObj.getInt("swine_id"),
                                cusObj.getString("swine_code")));
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < Farrowing_addBreedFailed_models.size(); i++) {
                        lables.add(Farrowing_addBreedFailed_models.get(i).getSwine_code());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_boar1.setAdapter(spinnerAdapter);
                    spinner_boar1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Farrowing_addBreedFailed_model click = Farrowing_addBreedFailed_models.get(position);
                            if (!click.getSwine_code().equals("Please Select")){
                                selectedBoar1 = String.valueOf(click.getSwine_id());
                            } else {
                                selectedBoar1 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    spinner_boar2.setAdapter(spinnerAdapter);
                    spinner_boar2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Farrowing_addBreedFailed_model click = Farrowing_addBreedFailed_models.get(position);
                            if (!click.getSwine_code().equals("Please Select")){
                                selectedBoar2 = String.valueOf(click.getSwine_id());
                            } else {
                                selectedBoar2 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    spinner_boar3.setAdapter(spinnerAdapter);
                    spinner_boar3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Farrowing_addBreedFailed_model click = Farrowing_addBreedFailed_models.get(position);
                            if (!click.getSwine_code().equals("Please Select")){
                                selectedBoar3 = String.valueOf(click.getSwine_id());
                            } else {
                                selectedBoar3 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // ----------------------------------------------- Technician
                    Farrowing_addTechnician_models.add(new Farrowing_addTechnician_model(0,"Please Select"));
                    final JSONArray technicianID_data = Object.getJSONArray("fetch_technicianID_data");
                    for (int i = 0; i < technicianID_data.length(); i++) {
                        JSONObject cusObj = (JSONObject) technicianID_data.get(i);

                        Farrowing_addTechnician_models.add(new Farrowing_addTechnician_model(cusObj.getInt("Technician_id"),
                                cusObj.getString("Technician")));
                    }

                    // Populate Spinner
                    List<String> lables2 = new ArrayList<>();
                    for (int i = 0; i < Farrowing_addTechnician_models.size(); i++) {
                        lables2.add(Farrowing_addTechnician_models.get(i).getTechnician());
                    }
                    ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables2);
                    spinnerAdapter2.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_technician.setAdapter(spinnerAdapter2);
                    spinner_technician.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Farrowing_addTechnician_model click = Farrowing_addTechnician_models.get(position);
                            if (!click.getTechnician().equals("Please Select")){
                                selectedTech = String.valueOf(click.getTechnician_id());
                            } else {
                                selectedTech = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                } catch (JSONException e) {}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismiss();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveBreedingFailed(final String company_id, final String company_code, final String swine_id, final String user_id){
        progressBar.setVisibility(View.VISIBLE);
        layout_add.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_farrowing_add_breding_failed_add2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);
                    if(response.equals("okay")){
                        FragmentManager fm = getFragmentManager();
                        Farrowing_main fragment = (Farrowing_main)fm.findFragmentByTag("fragmentRF");
                        fragment.getFarrowingDetails(company_code, company_id, swine_id);
                        Toast.makeText(getActivity(), "Successfully saved.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dismiss();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("company_code", company_code);
                hashMap.put("user_id", user_id);
                hashMap.put("breeding_date", getBreeding_date);
                hashMap.put("breeding_date_encoded", dateBreeding);
                hashMap.put("breeding_date_failed", dateBreedingFailed);
                hashMap.put("boar1", selectedBoar1);
                hashMap.put("boar2", selectedBoar2);
                hashMap.put("boar3", selectedBoar3);
                hashMap.put("breeding_technician", selectedTech);
                hashMap.put("success_breeding_id", getBreeding_id);
                hashMap.put("farr_status", getStatus);
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
