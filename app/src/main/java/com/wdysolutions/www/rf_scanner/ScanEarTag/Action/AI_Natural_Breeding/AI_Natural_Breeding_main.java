package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.AI_Natural_Breeding;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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


public class AI_Natural_Breeding_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    ProgressBar progressBar, loading_save, loading_pen;
    Button btn_save, btn_cancel;
    TextView btn_date;
    Spinner spinner_building, spinner_pen, spinner_boar1, spinner_boar2, spinner_boar3, spinner_technician;
    LinearLayout layout_;
    String selectedBuilding = "", selectedPen = "", selectedDate = "", selectedBoar1 = "", selectedBoar2 = "", currentDate="",
            selectedBoar3 = "", selectedTech = "", company_code, company_id, swine_scanned_id, pen_id, user_id, pen_name, pen_type, category_id;
    ArrayList<Building_model> building_models = new ArrayList<>();
    ArrayList<Technician_model> technician_models = new ArrayList<>();
    ArrayList<Boar_model> boar_models = new ArrayList<>();
    ArrayList<Pen_model> pen_models = new ArrayList<>();


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
        View view = inflater.inflate(R.layout.ai_natural_breeding_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        pen_id = getArguments().getString("pen_code");
        pen_name = getArguments().getString("pen_name");
        pen_type = getArguments().getString("pen_type");
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        btn_date = view.findViewById(R.id.btn_date);
        loading_pen = view.findViewById(R.id.loading_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_boar1 = view.findViewById(R.id.spinner_boar1);
        spinner_boar2 = view.findViewById(R.id.spinner_boar2);
        spinner_boar3 = view.findViewById(R.id.spinner_boar3);
        spinner_technician = view.findViewById(R.id.spinner_technician);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        layout_ = view.findViewById(R.id.layout_);
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
                    Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                } else if (selectedPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                } else if (selectedBoar1.equals("")){
                    Toast.makeText(getActivity(), "Please select boar 1", Toast.LENGTH_SHORT).show();
                } else if (selectedTech.equals("")){
                    Toast.makeText(getActivity(), "Please select technician", Toast.LENGTH_SHORT).show();
                } else {
                    saveAI(swine_scanned_id, pen_id);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getBuilding(swine_scanned_id);
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

    public void getBuilding(final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_AI_breeding_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    layout_.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    building_models.clear();
                    JSONObject Object = new JSONObject(response);


                    final JSONArray response_currentDate = Object.getJSONArray("response_currentDate");
                    JSONObject date = (JSONObject) response_currentDate.get(0);
                    currentDate = date.getString("current_date");
                    String[] maxDate = currentDate.split(" ");
                    selectedDate = maxDate[0];
                    btn_date.setText(selectedDate);

                    // Building Spinner ------------------------------------------------------------
                    building_models.add(new Building_model("0","Please Select"));
                    final JSONArray diag = Object.getJSONArray("response_building");
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
                                getPen(company_code, company_id, swine_scanned_id, selectedBuilding);
                            } else {
                                selectedBuilding = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Boar Spinner ----------------------------------------------------------------
                    final JSONArray jsonArray_boar = Object.getJSONArray("response_boar");
                    boar_models.add(new Boar_model("0","", "Please Select"));
                    for (int i = 0; i < jsonArray_boar.length(); i++) {
                        JSONObject cusObj_boar = (JSONObject) jsonArray_boar.get(i);

                        boar_models.add(new Boar_model(cusObj_boar.getString("boar_id"),
                                "",
                                cusObj_boar.getString("boar_name")));
                    }

                    // Populate Spinner
                    List<String> lables_boar = new ArrayList<>();
                    for (int i = 0; i < boar_models.size(); i++) {
                        lables_boar.add(boar_models.get(i).getBoar_name());
                    }

//                    ArrayAdapter<String> spinnerAdapter_boar = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, lables_boar) {
//                        // disabled click
//                        @Override
//                        public boolean isEnabled(int position) {
//                            if (boar_models.get(position).getDisable_info().equals("disabled")) {
//                                return false;
//                            }
//                            return true;
//                        }
//                        // Change color item
//                        @Override
//                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                            View mView = super.getDropDownView(position, convertView, parent);
//                            TextView mTextView = (TextView) mView;
//                            if (boar_models.get(position).getDisable_info().equals("disabled")) {
//                                mTextView.setTextColor(Color.GRAY);
//                            } else {
//                                mTextView.setTextColor(Color.BLACK);
//                            }
//                            return mView;
//                        }
//                    };

                    ArrayAdapter<String> spinnerAdapter_boar = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, lables_boar);

                    spinnerAdapter_boar.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_boar1.setAdapter(spinnerAdapter_boar);
                    spinner_boar1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Boar_model click = boar_models.get(position);
                            if (!click.getBoar_name().equals("Please Select")){
                                selectedBoar1 = String.valueOf(click.getBoar_id());
                            } else {
                                selectedBoar1 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    spinner_boar2.setAdapter(spinnerAdapter_boar);
                    spinner_boar2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Boar_model click = boar_models.get(position);
                            if (!click.getBoar_name().equals("Please Select")){
                                selectedBoar2 = String.valueOf(click.getBoar_id());
                            } else {
                                selectedBoar2 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    spinner_boar3.setAdapter(spinnerAdapter_boar);
                    spinner_boar3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Boar_model click = boar_models.get(position);
                            if (!click.getBoar_name().equals("Please Select")){
                                selectedBoar3 = String.valueOf(click.getBoar_id());
                            } else {
                                selectedBoar3 = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Technician Spinner ------------------------------------------------------------
                    technician_models.add(new Technician_model("0","Please Select"));
                    final JSONArray jsonArray_tech = Object.getJSONArray("response_tech");
                    for (int i = 0; i < jsonArray_tech.length(); i++) {
                        JSONObject cusObj_tech = (JSONObject) jsonArray_tech.get(i);

                        technician_models.add(new Technician_model(cusObj_tech.getString("tech_id"),
                                cusObj_tech.getString("tech_name")));
                    }

                    // Populate Spinner
                    List<String> lables_tech = new ArrayList<>();
                    for (int i = 0; i < technician_models.size(); i++) {
                        lables_tech.add(technician_models.get(i).getTech_name());
                    }
                    ArrayAdapter<String> spinnerAdapter_tech = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_tech);
                    spinnerAdapter_tech.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_technician.setAdapter(spinnerAdapter_tech);
                    spinner_technician.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Technician_model click = technician_models.get(position);
                            if (!click.getTech_name().equals("Please Select")){
                                selectedTech = String.valueOf(click.getTech_id());
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
                    layout_.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
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
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getPen(final String company_code, final String company_id, final String swine_id, final String building_id) {
        penLoading(true);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/get_all_pen.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    pen_models.clear();
                    penLoading(false);
                    JSONObject Object = new JSONObject(response);

                    // Find current pen and get the id (Default spinner)
                    final JSONArray diag = Object.getJSONArray("response");
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        if (pen_id.equals(cusObj.getString("pen_assignment_id"))){
                            pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                    cusObj.getString("pen_name"),
                                    ""));
                        }
                    }

                    // after the default spinner save all data
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                cusObj.getString("pen_name"),
                                ""));
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

                } catch (JSONException e) {}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
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
                hashMap.put("pen_id", pen_id);
                hashMap.put("building_id", building_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveAI(final String swine_id, final String pen_id) {
        btn_save.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_AI_breeding_add.php";
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
                        fragment.get_details(company_code, company_id, swine_id);
                        Toast.makeText(getActivity(), "AI/Natural Breeding Successfully Saved!", Toast.LENGTH_SHORT).show();
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
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("user_id", user_id);
                hashMap.put("breed_date", selectedDate);
                hashMap.put("AIBreeding_pen", selectedPen);
                hashMap.put("boar1", selectedBoar1);
                hashMap.put("boar2", selectedBoar2);
                hashMap.put("boar3", selectedBoar3);
                hashMap.put("from_pen", pen_id);
                hashMap.put("breeding_technician", selectedTech);
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
