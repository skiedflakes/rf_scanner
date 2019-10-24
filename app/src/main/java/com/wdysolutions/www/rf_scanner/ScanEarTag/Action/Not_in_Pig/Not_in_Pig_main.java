package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Not_in_Pig;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen.Building_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen.Pen_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Not_in_Pig_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_date;
    Spinner spinner_building, spinner_pen;
    EditText edittext_remarks;
    ProgressBar loading_save, progressBar, loading_pen;
    Button btn_save;
    LinearLayout layout_add;
    ArrayList<Building_model> building_models = new ArrayList<>();
    ArrayList<Pen_model> pen_models = new ArrayList<>();
    String selectedBuilding = "", selectedPen = "", company_code, company_id, swine_scanned_id,
            swine_type, pen_id, selectedDate ="", user_id, currentDate="";


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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.not_in_pig_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        swine_type = getArguments().getString("swine_type");
        pen_id = getArguments().getString("pen_code");

        loading_pen = view.findViewById(R.id.loading_pen);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        edittext_remarks = view.findViewById(R.id.edittext_remarks);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);
        layout_add = view.findViewById(R.id.layout_add);

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
                    Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                } else if (selectedPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                } else {
                    saveNIP();
                }
            }
        });


        getSpinner("get_building", "");
        return view;
    }

    private void penLoading(boolean status){
        if (status){
            spinner_pen.setVisibility(View.GONE);
            loading_pen.setVisibility(View.VISIBLE);
        } else {
            spinner_pen.setVisibility(View.VISIBLE);
            loading_pen.setVisibility(View.GONE);
        }
    }

    private void getSpinner(final String get_type, final String building_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_nip_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    layout_add.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    if (get_type.equals("get_building")){

                        final JSONArray response_date = Object.getJSONArray("response_date");
                        JSONObject date = (JSONObject) response_date.get(0);
                        currentDate = date.getString("current_date");
                        String[] maxDate = currentDate.split(" ");
                        selectedDate = maxDate[0];
                        btn_date.setText(selectedDate);



                        building_models.add(new Building_model("0","Please Select"));
                        for (int i = 0; i < diag.length(); i++) {
                            JSONObject cusObj = (JSONObject) diag.get(i);

                            building_models.add(new Building_model(cusObj.getString("building_id"),
                                    cusObj.getString("building_name")));
                        }

                        // Populate Spinner
                        List<String> lables = new ArrayList<>();
                        for (int i = 0; i < building_models.size(); i++) {
                            lables.add(building_models.get(i).getBuilding_name());
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                        spinner_building.setAdapter(spinnerAdapter);
                        spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                Building_model click = building_models.get(position);
                                if (!click.getBuilding_name().equals("Please Select")){
                                    selectedBuilding = String.valueOf(click.getBuilding_id());
                                    getSpinner("get_pen", selectedBuilding);
                                    penLoading(true);
                                } else {
                                    selectedBuilding = "";
                                }
                                selectedPen = "";
                                spinner_pen.setAdapter(null);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                    }

                    else if (get_type.equals("get_pen")){

                        penLoading(false);
                        pen_models.clear();

                        // set default in spinner
                        for (int i = 0; i < diag.length(); i++) {
                            JSONObject cusObj = (JSONObject) diag.get(i);

                            if (pen_id.equals(cusObj.getString("pen_assignment_id"))){
                                pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"), cusObj.getString("pen_name"), ""));
                            }
                        }

                        // get all data
                        for (int i = 0; i < diag.length(); i++) {
                            JSONObject cusObj = (JSONObject) diag.get(i);

                            pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                    cusObj.getString("pen_name"), ""));
                        }

                        // Populate Spinner
                        List<String> lables = new ArrayList<>();
                        for (int i = 0; i < pen_models.size(); i++) {
                            lables.add(pen_models.get(i).getPen_name());
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                        spinner_pen.setAdapter(spinnerAdapter);
                        spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                Pen_model click = pen_models.get(position);
                                selectedPen = String.valueOf(click.getPen_assignment_id());
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    penLoading(false);
                    layout_add.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if (get_type.equals("get_building")){dismiss();}
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("swine_type", swine_type);
                hashMap.put("get_type", get_type);
                hashMap.put("building_id", building_id);
                hashMap.put("pen_id", pen_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void saveNIP() {
        btn_save.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_nip_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);

                    if (response.equals("1")){
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Successfully Saved entry for Not in Pig!", Toast.LENGTH_SHORT).show();
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
                    loading_save.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("pen_id", selectedPen);
                hashMap.put("user_id", user_id);
                hashMap.put("nip_date", selectedDate);
                hashMap.put("from_pen", pen_id);
                hashMap.put("remarks", edittext_remarks.getText().toString());
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
