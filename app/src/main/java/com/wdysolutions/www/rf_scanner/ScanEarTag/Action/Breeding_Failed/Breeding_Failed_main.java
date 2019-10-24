package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Breeding_Failed;

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


public class Breeding_Failed_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    Button btn_save;
    Spinner spinner_building, spinner_pen;
    ProgressBar loading_save, progressBar, loading_pen;
    TextView btn_date;
    LinearLayout layout_add;
    ArrayList<Building_model> building_models = new ArrayList<>();
    ArrayList<Pen_model> pen_models = new ArrayList<>();
    String selectedBuilding = "", selectedPen = "", selectedDate = "", currentDate="";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.breeding_failed_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        final String swine_scanned_id = getArguments().getString("swine_scanned_id");
        final String pen_code = getArguments().getString("pen_code");
        final String swine_type = getArguments().getString("swine_type");

        loading_pen = view.findViewById(R.id.loading_pen);
        layout_add = view.findViewById(R.id.layout_add);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
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
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building.", Toast.LENGTH_SHORT).show();
                } else if (selectedPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen.", Toast.LENGTH_SHORT).show();
                } else {
                    saveBreedingFailed(company_code, company_id, swine_scanned_id, swine_type, pen_code, user_id);
                }
            }
        });

        getSpinner(company_code, company_id, swine_scanned_id, swine_type, pen_code, "","get_building");
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

    public void getSpinner(final String company_code, final String company_id, final String swine_id, final String swine_type, final String pen_code, final String building_id, final String get_type) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_breeding_failed_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);
                    JSONObject Object = new JSONObject(response);

                    // Spinner building
                    if (get_type.equals("get_building")){

                        final JSONArray response_date = Object.getJSONArray("response_date");
                        JSONObject date = (JSONObject) response_date.get(0);
                        currentDate = date.getString("current_date");

                        String[] maxDate = currentDate.split(" ");
                        selectedDate = maxDate[0];
                        btn_date.setText(selectedDate);


                        final JSONArray diag = Object.getJSONArray("response_building");
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
                                    getSpinner(company_code, company_id, swine_id, swine_type, pen_code, selectedBuilding, "get_pen");
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

                    // Spinner pen
                    else if (get_type.equals("get_pen")){
                        penLoading(false);

                        pen_models.add(new Pen_model("0","Please Select", ""));
                        final JSONArray diag = Object.getJSONArray("response_pen");

                        for (int i = 0; i < diag.length(); i++) {
                            JSONObject cusObj = (JSONObject) diag.get(i);

                            pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                    cusObj.getString("pen_name"), ""));
                        }

                        // Populate Spinner
                        List<String> lables_pen = new ArrayList<>();
                        for (int i = 0; i < pen_models.size(); i++) {
                            lables_pen.add(pen_models.get(i).getPen_name());
                        }
                        ArrayAdapter<String> spinnerAdapter_pen = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_pen);
                        spinnerAdapter_pen.setDropDownViewResource(R.layout.custom_spinner);
                        spinner_pen.setAdapter(spinnerAdapter_pen);
                        spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                Pen_model click = pen_models.get(position);
                                if (!click.getPen_name().equals("Please Select")){
                                    selectedPen = String.valueOf(click.getPen_assignment_id());
                                } else {
                                    selectedPen = "";
                                }
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
                    dismiss();
                    penLoading(false);
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
                hashMap.put("pen_id", pen_code);
                hashMap.put("get_type", get_type);
                hashMap.put("swine_type", swine_type);
                hashMap.put("building_id", building_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveBreedingFailed(final String company_code, final String company_id, final String swine_id, final String swine_type, final String pen_id, final String user_id) {
        btn_save.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_breeding_failed_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    if (response.equals("1")){
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_id);
                        Toast.makeText(getActivity(), "Successfully Saved!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Failed to Save!", Toast.LENGTH_SHORT).show();
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
                hashMap.put("swine_id", swine_id);
                hashMap.put("breeding_failed_pen", selectedPen);
                hashMap.put("from_pen", pen_id);
                hashMap.put("swine_type", swine_type);
                hashMap.put("breeding_failed_date", selectedDate);
                hashMap.put("user_id", user_id);
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

}
