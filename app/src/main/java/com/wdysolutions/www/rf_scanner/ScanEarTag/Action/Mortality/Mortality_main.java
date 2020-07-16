package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Mortality;

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
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Mortality_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_date;
    ProgressBar progressBar, loading_save;
    Spinner spinner_cause, spinner_caretaker;
    EditText editText_remarks;
    Button btn_save, btn_cancel;
    LinearLayout layout_add;
    ArrayList<Disease_model> disease_models = new ArrayList<>();
    ArrayList<Disease_model> caretaker_models = new ArrayList<>();
    String selectedDisease = "", swine_scanned_id, company_code, company_id, selectedDate = "", currentDate="",
            selectedCaretaker="", user_id, category_id;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mortality_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        spinner_caretaker = view.findViewById(R.id.spinner_caretaker);
        layout_add = view.findViewById(R.id.layout_add);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_cause = view.findViewById(R.id.spinner_cause);
        editText_remarks = view.findViewById(R.id.editText_remarks);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);
        btn_cancel = view.findViewById(R.id.btn_cancel);

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
                } else if (selectedDisease.equals("")){
                    Toast.makeText(getActivity(), "Please select disease.", Toast.LENGTH_SHORT).show();
                } else if (selectedCaretaker.equals("")){
                    Toast.makeText(getActivity(), "Please select caretaker", Toast.LENGTH_SHORT).show();
                } else {
                    saveMortality(company_code, company_id, swine_scanned_id);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDisease(company_code, company_id, swine_scanned_id);
        return view;
    }

    public void getDisease(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_mortality_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);
                    disease_models.add(new Disease_model("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        disease_models.add(new Disease_model(cusObj.getString("disease_id"),
                                cusObj.getString("cause")));

                        currentDate = cusObj.getString("current_date");
                    }

                    String[] maxDate = currentDate.split(" ");
                    selectedDate = maxDate[0];
                    btn_date.setText(selectedDate);

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < disease_models.size(); i++) {
                        lables.add(disease_models.get(i).getCause());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_cause.setAdapter(spinnerAdapter);
                    spinner_cause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Disease_model click = disease_models.get(position);
                            if (!click.getCause().equals("Please Select")){
                                selectedDisease = String.valueOf(click.getDisease_id());
                            } else {
                                selectedDisease = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Caretaker
                    final JSONArray diag_caretaker = Object.getJSONArray("data_caretaker");
                    caretaker_models.add(new Disease_model("","Please Select"));
                    for (int i = 0; i < diag_caretaker.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag_caretaker.get(i);

                        caretaker_models.add(new Disease_model(cusObj.getString("employee_id"),
                                cusObj.getString("name")));
                    }

                    // Populate Spinner
                    List<String> lables_caretaker = new ArrayList<>();
                    for (int i = 0; i < caretaker_models.size(); i++) {
                        lables_caretaker.add(caretaker_models.get(i).getCause());
                    }
                    ArrayAdapter<String> spinnerAdapter_caretaker = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_caretaker);
                    spinnerAdapter_caretaker.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_caretaker.setAdapter(spinnerAdapter_caretaker);
                    spinner_caretaker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Disease_model click = caretaker_models.get(position);
                            if (!click.getCause().equals("Please Select")){
                                selectedCaretaker = String.valueOf(click.getDisease_id());
                            } else {
                                selectedCaretaker = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });
                }
                catch (JSONException e){}
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
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveMortality(final String company_code, final String company_id, final String swine_id) {
        btn_save.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_mortality_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    btn_save.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    if (response.equals("1")){
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Mortality Successfully Saved!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("2")){
                        dialogBox("Unable to execute query. Found unweaned piglets. Please do adoption for piglets to continue.");
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_save.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("category_id", category_id);
                hashMap.put("user_id", user_id);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("caretaker_id", selectedCaretaker);
                hashMap.put("cause", selectedDisease);
                hashMap.put("reference_number", "");
                hashMap.put("remarks", editText_remarks.getText().toString());
                hashMap.put("mortality_date", selectedDate);
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
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
