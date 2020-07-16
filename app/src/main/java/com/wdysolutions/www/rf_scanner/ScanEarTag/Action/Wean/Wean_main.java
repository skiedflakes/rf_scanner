package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen.Building_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen.Locations_model;
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


public class Wean_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_within_adopt, btn_other_adopt, btn_date_adopt, displayText_pen_adopt;
    Spinner spinner_locations_adopt, spinner_building_adopt, spinner_pen_adopt;
    ProgressBar loading_save_adopt, loading_pen_sow, loading_pen_piglet, loading_pen_adopt, loading_building_adopt, loading_location_adopt;
    Button btn_save_adopt;
    LinearLayout location_layout, remarks_layout;
    EditText editext_remarks;
    String selectedLocation="", currentLocation, currentdate="";
    String selectedDate_adopt = "", dateOf = "", selectedBuilding_adopt = "", selectedPen_adopt = "";
    boolean isTransferWithin = true;

    Button btn_adopt, btn_wean, btn_save;
    LinearLayout layout_, layout_wean_add, layout_spinner_piglets, layout_spinner_sow, layout_btn, layout_adopt;
    ProgressBar progressBar, loading_save;
    RecyclerView recyclerView, recyclerView_add;
    ArrayList<Wean_model> wean_models = new ArrayList<>();
    ArrayList<Wean_array_model> wean_array_models = new ArrayList<>();
    ArrayList<Wean_array_check_model> wean_array_check_models = new ArrayList<>();
    ArrayList<Wean_array_weight_model> wean_array_weight_models = new ArrayList<>();
    TextView btn_date;
    CheckBox checkbox_piglets, checkbox_sow;
    Spinner spinner_building, spinner_pen_piglets, spinner_building_sow, spinner_pen_sow;
    String selectedBuilding_piglets = "", selectedPen_piglets = "", selectedPen_sow = "", category_id,
            selectedBuilding_sow = "", selectedDate = "", user_id, swine_scanned_id, company_id, company_code, pen_code;
    int checkedCounter = 0, editTextEmptyCounter = 0, isSow = 0, isPiglet = 0;
    boolean isSowShow, isFirstOpen = true;

    ArrayList<Building_model> building_models = new ArrayList<>();
    ArrayList<Pen_model> pen_models = new ArrayList<>();
    ArrayList<Pen_model> pen_models_sow = new ArrayList<>();
    ArrayList<Locations_model> locations_models = new ArrayList<>();
    adapter_wean adapterPigletsWeight;
    StringRequest stringRequest_location, stringRequest_pen, stringRequest_building;


    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        String[] maxDate = currentdate.split(" ");

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
        if (dateOf.equals("wean")){
            selectedDate = date;
            btn_date.setText(selectedDate);
        }
        else if (dateOf.equals("adopt")){
            selectedDate_adopt = date;
            btn_date_adopt.setText(selectedDate_adopt);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wean_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        pen_code = getArguments().getString("pen_code");

        loading_location_adopt = view.findViewById(R.id.loading_location_adopt);
        loading_pen_adopt = view.findViewById(R.id.loading_pen_adopt);
        loading_building_adopt = view.findViewById(R.id.loading_building_adopt);
        loading_pen_sow = view.findViewById(R.id.loading_pen_sow);
        loading_pen_piglet = view.findViewById(R.id.loading_pen_piglet);
        editext_remarks = view.findViewById(R.id.editext_remarks);
        remarks_layout = view.findViewById(R.id.remarks_layout);
        location_layout = view.findViewById(R.id.location_layout);
        btn_within_adopt = view.findViewById(R.id.btn_within_adopt);
        btn_other_adopt = view.findViewById(R.id.btn_other_adopt);
        btn_date_adopt = view.findViewById(R.id.btn_date_adopt);
        spinner_locations_adopt = view.findViewById(R.id.spinner_locations_adopt);
        spinner_building_adopt = view.findViewById(R.id.spinner_building_adopt);
        displayText_pen_adopt = view.findViewById(R.id.displayText_pen_adopt);
        spinner_pen_adopt = view.findViewById(R.id.spinner_pen_adopt);
        loading_save_adopt = view.findViewById(R.id.loading_save_adopt);
        btn_save_adopt = view.findViewById(R.id.btn_save_adopt);
        layout_adopt = view.findViewById(R.id.layout_adopt);
        loading_save = view.findViewById(R.id.loading_save);
        layout_btn = view.findViewById(R.id.layout_btn);
        checkbox_sow = view.findViewById(R.id.checkbox_sow);
        spinner_pen_sow = view.findViewById(R.id.spinner_pen_sow);
        spinner_building_sow = view.findViewById(R.id.spinner_building_sow);
        layout_spinner_sow = view.findViewById(R.id.layout_spinner_sow);
        layout_spinner_piglets = view.findViewById(R.id.layout_spinner_piglets);
        recyclerView_add = view.findViewById(R.id.recyclerView_add);
        spinner_pen_piglets = view.findViewById(R.id.spinner_pen_piglets);
        spinner_building = view.findViewById(R.id.spinner_building);
        checkbox_piglets = view.findViewById(R.id.checkbox_piglets);
        btn_date = view.findViewById(R.id.btn_date);
        btn_save = view.findViewById(R.id.btn_save);
        layout_wean_add = view.findViewById(R.id.layout_wean_add);
        layout_ = view.findViewById(R.id.layout_);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        btn_wean = view.findViewById(R.id.btn_wean);
        btn_adopt = view.findViewById(R.id.btn_adopt);


        btn_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isCheckNone()){
                    Toast.makeText(getActivity(), "Please select piglets for weaning.", Toast.LENGTH_LONG).show();
                } else {
                    layout_wean_add.setVisibility(View.GONE);
                    layout_.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    showWithin();
                }
            }
        });

        btn_wean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (isCheckNone()){
                    Toast.makeText(getActivity(), "Please select piglets for weaning.", Toast.LENGTH_LONG).show();
                }
                else {
                    // if select all, show all
                    if (checkedCounter == wean_array_models.size()){
                        isSowShow = true;
                        layout_.setVisibility(View.GONE);
                        layout_wean_add.setVisibility(View.VISIBLE);
                    }
                    // else hide sow
                    else {
                        isSowShow = false;
                        checkbox_sow.setTextColor(getResources().getColor(R.color.text_grey));
                        checkbox_piglets.setTextColor(getResources().getColor(R.color.text_grey));
                        checkbox_sow.setEnabled(false);
                        checkbox_piglets.setEnabled(false);
                        layout_spinner_sow.setVisibility(View.GONE);
                        layout_.setVisibility(View.GONE);
                        layout_wean_add.setVisibility(View.VISIBLE);
                    }

                    adapter_wean_add adapter_wean_add = new adapter_wean_add(getContext(), wean_array_check_models);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView_add.setLayoutManager(layoutManager);
                    recyclerView_add.setAdapter(adapter_wean_add);
                    recyclerView_add.setNestedScrollingEnabled(false);
                }
            }
        });

        initAdopt();
        initWean();
        getWean(company_code, company_id, swine_scanned_id, pen_code);
        return view;
    }

    private boolean isCheckNone(){
        checkedCounter = 0;
        try {
            JSONArray jsonArray = new JSONArray(new Gson().toJson(wean_array_models));
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);

                if (r.getString("status").equals("checked")){
                    checkedCounter++;

                    // save selected piglets to new array
                    wean_array_check_models.add(new Wean_array_check_model(r.getString("id"),
                            r.getString("eartag"),
                            r.getString("gender"),
                            r.getString("dateofbirth")));

                    wean_array_weight_models.add(new Wean_array_weight_model(r.getString("eartag"), r.getString("id"), ""));
                }
            }
        } catch (JSONException e){}

        if (checkedCounter == 0){
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isWeightNone(){
        editTextEmptyCounter = 0;
        try {
            JSONArray jsonArray = new JSONArray(new Gson().toJson(wean_array_weight_models));
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);

                if (r.getString("wean_weight").equals("")){
                    editTextEmptyCounter++;
                }
            }
        } catch (JSONException e){}
        
        if (editTextEmptyCounter > 0){
            return true;
        } else {
            return false;
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Wean code below
     * ---------------------------------------------------------------------------------------------
     */

    private void penLoadingWean(boolean status, boolean isPiglet){
        if (status){
            if(isPiglet){
                spinner_pen_piglets.setVisibility(View.GONE);
                loading_pen_piglet.setVisibility(View.VISIBLE);
            } else {
                spinner_pen_sow.setVisibility(View.GONE);
                loading_pen_sow.setVisibility(View.VISIBLE);
            }
        }
        else {
            if(isPiglet){
                spinner_pen_piglets.setVisibility(View.VISIBLE);
                loading_pen_piglet.setVisibility(View.GONE);
            } else {
                spinner_pen_sow.setVisibility(View.VISIBLE);
                loading_pen_sow.setVisibility(View.GONE);
            }
        }
    }

    private void initWean(){

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOf = "wean";
                openDatePicker(false);
            }
        });

        checkbox_piglets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_piglets.isChecked()){
                    layout_spinner_piglets.setVisibility(View.GONE);
                    isPiglet = 1;
                } else {
                    layout_spinner_piglets.setVisibility(View.VISIBLE);
                    isPiglet = 0;
                }
            }
        });

        checkbox_sow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_sow.isChecked()){
                    layout_spinner_sow.setVisibility(View.GONE);
                    isSow = 1;
                } else {
                    layout_spinner_sow.setVisibility(View.VISIBLE);
                    isSow = 0;
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (selectedDate.equals("")){
                    Toast.makeText(getActivity(), "Please select date.", Toast.LENGTH_SHORT).show();
                }
                else if (isWeightNone()){
                    Toast.makeText(getActivity(), "Please enter weight of selected piglets.", Toast.LENGTH_SHORT).show();
                }
                else if (isSowShow){

                    if (selectedPen_sow.equals("") && isSow == 0){
                        Toast.makeText(getActivity(), "Please select pen for sow transfer.", Toast.LENGTH_SHORT).show();
                    }
                    else if (selectedPen_piglets.equals("") && isPiglet == 0){
                        Toast.makeText(getActivity(), "Please select pen for piglets transfer.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        checkWeaningStandard(company_code, company_id, swine_scanned_id, selectedDate,
                                pen_code, selectedPen_piglets, selectedPen_sow, new Gson().toJson(wean_array_weight_models));
                    }
                }
                else {
                    isSow = 1;
                    if (selectedPen_piglets.equals("")){
                        Toast.makeText(getActivity(), "Please select pen for piglets transfer.", Toast.LENGTH_SHORT).show();
                    } else {
                        checkWeaningStandard(company_code, company_id, swine_scanned_id, selectedDate,
                                pen_code, selectedPen_piglets, selectedPen_sow, new Gson().toJson(wean_array_weight_models));
                    }
                }
            }
        });
    }

    private void getPen(final String company_code, final String company_id, final String swine_id, final String building_id, final String pen_id, final String get_type) {
        if(get_type.equals("pen_for_wean")){penLoadingWean(true, true);}else{penLoadingWean(true, false);}
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_wean_input_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject Object = new JSONObject(response);

                    if (get_type.equals("pen_for_wean")){
                        penLoadingWean(false, true);
                        JSONArray jsonArray_ = Object.getJSONArray("for_wean_data");
                        pen_models.clear();
                        pen_models.add(new Pen_model("0","Please Select", ""));

                        for (int i = 0; i < jsonArray_.length(); i++) {
                            JSONObject cusObj = (JSONObject) jsonArray_.get(i);

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
                        spinner_pen_piglets.setAdapter(spinnerAdapter);
                        spinner_pen_piglets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                Pen_model click = pen_models.get(position);
                                if (!click.getPen_name().equals("Please Select")){
                                    selectedPen_piglets = String.valueOf(click.getPen_assignment_id());
                                } else {
                                    selectedPen_piglets = "";
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                    }

                    else {
                        penLoadingWean(false, false);
                        JSONArray jsonArray_ = Object.getJSONArray("for_sow_data");
                        pen_models_sow.clear();
                        pen_models_sow.add(new Pen_model("0","Please Select", ""));

                        for (int i = 0; i < jsonArray_.length(); i++) {
                            JSONObject cusObj = (JSONObject) jsonArray_.get(i);

                            pen_models_sow.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                    cusObj.getString("pen_name"),
                                    ""));
                        }

                        // Populate Spinner
                        List<String> lables = new ArrayList<>();
                        for (int i = 0; i < pen_models_sow.size(); i++) {
                            lables.add(pen_models_sow.get(i).getPen_name());
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                        spinner_pen_sow.setAdapter(spinnerAdapter);
                        spinner_pen_sow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                Pen_model click = pen_models_sow.get(position);
                                if (!click.getPen_name().equals("Please Select")){
                                    selectedPen_sow = String.valueOf(click.getPen_assignment_id());
                                } else {
                                    selectedPen_sow = "";
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {}
                        });
                    }

                } catch (JSONException e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
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
                hashMap.put("building_id", building_id);
                hashMap.put("get_type", get_type);
                hashMap.put("pen_id", pen_id);
                hashMap.put("rowCount", String.valueOf(checkedCounter));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getWean(final String company_code, final String company_id, final String swine_id, final String pen_code) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_wean_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    layout_.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    JSONObject Object = new JSONObject(response);
                    JSONArray details2 = Object.getJSONArray("data");
                    
                    if (!String.valueOf(details2).equals("[]")){
                        for (int i =0; i < details2.length(); i++){
                            JSONObject r = details2.getJSONObject(i);
                            wean_models.add(new Wean_model(r.getString("id"),
                                    r.getString("swine_code"),
                                    r.getString("gender"),
                                    r.getString("count"),
                                    r.getString("weight"),
                                    r.getString("swine_breed"),
                                    r.getString("genetic_line"),
                                    r.getString("classification"),
                                    r.getString("birthdate"),
                                    r.getString("no_days")));

                            wean_array_models.add(new Wean_array_model(r.getString("id"),
                                    "unchecked",
                                    r.getString("swine_code"),
                                    r.getString("gender"),
                                    r.getString("birthdate")));

                            currentdate = r.getString("current_date");
                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        adapterPigletsWeight = new adapter_wean(getContext(), wean_models);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapterPigletsWeight);
                        recyclerView.setNestedScrollingEnabled(false);
                    }


                    // Building spinner ---------------------------------------------------------------
                    building_models.add(new Building_model("0","Please Select"));
                    JSONArray jsonArray_building = Object.getJSONArray("building");
                    for (int i = 0; i < jsonArray_building.length(); i++) {
                        JSONObject cusObj = (JSONObject) jsonArray_building.get(i);

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

                    // Spinner for piglets
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Building_model click = building_models.get(position);
                            if (!click.getBuilding_name().equals("Please Select")){
                                selectedBuilding_piglets = String.valueOf(click.getBuilding_id());
                                getPen(company_code, company_id, swine_id, selectedBuilding_piglets, pen_code,"pen_for_wean");
                            } else {
                                selectedBuilding_piglets = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    // Spinner for sow
                    spinner_building_sow.setAdapter(spinnerAdapter);
                    spinner_building_sow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Building_model click = building_models.get(position);
                            if (!click.getBuilding_name().equals("Please Select")){
                                selectedBuilding_sow = String.valueOf(click.getBuilding_id());
                                getPen(company_code, company_id, swine_id, selectedBuilding_sow, pen_code, "pen_for_sow");
                            } else {
                                selectedBuilding_sow = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
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
                hashMap.put("pen_code", pen_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class adapter_wean extends RecyclerView.Adapter<adapter_wean.MyHolder>{
        ArrayList<Wean_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num;
        boolean isCheckALL = false;

        public adapter_wean(Context context, ArrayList<Wean_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wean_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getWeight = data.get(position).getWeight();
            final String getBirthdate = data.get(position).getBirthdate();
            final String getClassification = data.get(position).getClassification();
            final String getGender = data.get(position).getGender();
            final String getGenetic_line = data.get(position).getGenetic_line();
            final String getId = data.get(position).getId();
            final String getNo_days = data.get(position).getNo_days();
            final String getSwine_code = data.get(position).getSwine_code();
            final String getSwine_breed = data.get(position).getSwine_breed();
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
            holder.text_age_days.setText(getNo_days);
            holder.text_birthdate.setText(getBirthdate);
            holder.text_weight.setText(getWeight);
            holder.text_gender.setText(getGender);
            holder.text_genetic_breed.setText(getSwine_breed);
            holder.text_eartag.setText(getSwine_code);
            holder.text_genetic_line.setText(getGenetic_line);
            holder.text_progeny.setText(getClassification);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()){
                        holder.checkBox.setChecked(true);
                        wean_array_models.set(position, new Wean_array_model(data.get(position).getId(),
                                "checked",
                                data.get(position).getSwine_code(),
                                data.get(position).getGender(),
                                data.get(position).getBirthdate()));
                    } else {
                        holder.checkBox.setChecked(false);
                        wean_array_models.set(position, new Wean_array_model(data.get(position).getId(),
                                "unchecked",
                                data.get(position).getSwine_code(),
                                data.get(position).getGender(),
                                data.get(position).getBirthdate()));
                    }
                }
            });

//            if (isCheckALL){
//                holder.checkBox.setChecked(true);
//                wean_array_models.set(position, new Wean_array_model(data.get(position).getId(),
//                        "checked",
//                        data.get(position).getSwine_code(),
//                        data.get(position).getGender(),
//                        data.get(position).getBirthdate()));
//            } else {
//                holder.checkBox.setChecked(false);
//                wean_array_models.set(position, new Wean_array_model(data.get(position).getId(),
//                        "unchecked",
//                        data.get(position).getSwine_code(),
//                        data.get(position).getGender(),
//                        data.get(position).getBirthdate()));
//            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_count, text_eartag, text_gender, text_weight, text_genetic_breed,
                    text_progeny, text_birthdate, text_age_days, text_genetic_line;
            CheckBox checkBox;

            public MyHolder(View itemView) {
                super(itemView);
                //this.setIsRecyclable(false);
                text_count = itemView.findViewById(R.id.text_count);
                checkBox = itemView.findViewById(R.id.checkBox);
                text_eartag = itemView.findViewById(R.id.text_eartag);
                text_gender = itemView.findViewById(R.id.text_gender);
                text_weight = itemView.findViewById(R.id.text_weight);
                text_genetic_breed = itemView.findViewById(R.id.text_genetic_breed);
                text_progeny = itemView.findViewById(R.id.text_progeny);
                text_birthdate = itemView.findViewById(R.id.text_birthdate);
                text_age_days = itemView.findViewById(R.id.text_age_days);
                text_genetic_line = itemView.findViewById(R.id.text_genetic_line);

//                checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (checkBox.isChecked()){
//                            wean_array_models.set(getAdapterPosition(), new Wean_array_model(data.get(getAdapterPosition()).getId(),
//                                    "checked",
//                                    data.get(getAdapterPosition()).getSwine_code(),
//                                    data.get(getAdapterPosition()).getGender(),
//                                    data.get(getAdapterPosition()).getBirthdate()));
//                        } else {
//                            wean_array_models.set(getAdapterPosition(), new Wean_array_model(data.get(getAdapterPosition()).getId(),
//                                    "unchecked",
//                                    data.get(getAdapterPosition()).getSwine_code(),
//                                    data.get(getAdapterPosition()).getGender(),
//                                    data.get(getAdapterPosition()).getBirthdate()));
//                        }
//                    }
//                });
            }
        }

//        public void checkAll(){
//            isCheckALL = true;
//            notifyDataSetChanged();
//        }
    }

    private class adapter_wean_add extends RecyclerView.Adapter<adapter_wean_add.MyHolder>{
        ArrayList<Wean_array_check_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num;

        public adapter_wean_add(Context context, ArrayList<Wean_array_check_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wean_add_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getDateofbirth = data.get(position).getDateofbirth();
            final String getEartag = data.get(position).getEartag();
            final String getGender = data.get(position).getGender();
            final String getId = data.get(position).getId();
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
            holder.text_dateofbirth.setText(getDateofbirth);
            holder.text_eartag.setText(getEartag);
            holder.text_gender.setText(getGender);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_count, text_eartag, text_gender, text_dateofbirth;
            EditText editText_weight;

            public MyHolder(View itemView) {
                super(itemView);
                text_count = itemView.findViewById(R.id.text_count);
                text_eartag = itemView.findViewById(R.id.text_eartag);
                text_gender = itemView.findViewById(R.id.text_gender);
                text_dateofbirth = itemView.findViewById(R.id.text_dateofbirth);
                editText_weight = itemView.findViewById(R.id.editText_weight);

                editText_weight.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        wean_array_weight_models.set(getAdapterPosition(), new Wean_array_weight_model(data.get(getAdapterPosition()).getEartag(),
                                data.get(getAdapterPosition()).getId(),
                                editable.toString()));
                    }
                });
            }
        }
    }

    private void checkWeaningStandard(final String company_code, final String company_id, final String swine_id, final String weaning_date,
                                      final String from_pen, final String pen_for_wean, final String pen_for_sow, final String all_data) {
        btn_save.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_checkweaningStandards.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (Integer.valueOf(response) > 0){
                        saveWean(company_code, company_id, swine_id, weaning_date,
                                from_pen, pen_for_wean, pen_for_sow, all_data);
                    } else {
                        Toast.makeText(getActivity(), "Cannot Proceed. Please set weaning weight standards in MASTER DATA >> Farm Standards >> Weaning Weight", Toast.LENGTH_SHORT).show();
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
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void saveWean(final String company_code, final String company_id, final String swine_id, final String weaning_date,
                          final String from_pen, final String pen_for_wean, final String pen_for_sow, final String all_data) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_wean_add2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);

                    // old
//                    if (response.equals("1")){
//                        dismiss();
//                        FragmentManager fm = getFragmentManager();
//                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
//                        fragment.get_details(company_code, company_id, swine_id);
//                        Toast.makeText(getActivity(), "Successfully wean!", Toast.LENGTH_SHORT).show();
//                    } else if (response.equals("0")){
//                        Toast.makeText(getActivity(), "Failed wean!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
//                    }

                    String success="";
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray details = jsonObject.getJSONArray("array_status");
                    for (int i=0; i<details.length();i++){
                        JSONObject r = details.getJSONObject(i);
                        success += r.getString("status")+"\n";
                    }

                    dialogBox(success, swine_id);

                    JSONArray jsonArray = jsonObject.getJSONArray("data_status");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    if (jsonObject1.getString("status").equals("complete")){

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
                hashMap.put("weaning_date", weaning_date);
                hashMap.put("from_pen", from_pen);
                hashMap.put("pen_code_for_piglets", pen_for_wean);
                hashMap.put("pen_for_sow", pen_for_sow);
                hashMap.put("all_data", all_data);
                hashMap.put("category_id", category_id);
                hashMap.put("reference_number", "");
                hashMap.put("weaned_piglets", String.valueOf(checkedCounter));
                hashMap.put("isSowStay", String.valueOf(isSow));
                hashMap.put("isPigletStay", String.valueOf(isPiglet));
                hashMap.put("user_id", user_id);
                hashMap.put("number_of_farrow", String.valueOf(wean_array_models.size()));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    void dialogBox(String name, final String swine_id){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        reloadSwineCard(swine_id);
                        Toast.makeText(getActivity(), "Successfully save.", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void reloadSwineCard(String swine_id){
        dismiss();
        FragmentManager fm = getFragmentManager();
        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
        fragment.get_details(company_code, company_id, swine_id);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Adopt code below
     * ---------------------------------------------------------------------------------------------
     */

    private void locationLoading(boolean status){
        if (status){
            spinner_locations_adopt.setVisibility(View.GONE);
            loading_location_adopt.setVisibility(View.VISIBLE);
        } else {
            spinner_locations_adopt.setVisibility(View.VISIBLE);
            loading_location_adopt.setVisibility(View.GONE);
        }
    }

    private void buildingLoading(boolean status){
        if (status){
            spinner_building_adopt.setVisibility(View.GONE);
            loading_building_adopt.setVisibility(View.VISIBLE);
        } else {
            spinner_building_adopt.setVisibility(View.VISIBLE);
            loading_building_adopt.setVisibility(View.GONE);
        }
    }

    private void penLoading(boolean status){
        if (status){
            spinner_pen_adopt.setVisibility(View.GONE);
            loading_pen_adopt.setVisibility(View.VISIBLE);
        } else {
            spinner_pen_adopt.setVisibility(View.VISIBLE);
            loading_pen_adopt.setVisibility(View.GONE);
        }
    }

    private void initAdopt(){
        btn_date_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOf = "adopt";
                openDatePicker(false);
            }
        });

        btn_other_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOther();
            }
        });

        btn_within_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithin();
            }
        });

        btn_save_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate_adopt.equals("")){
                    Toast.makeText(getActivity(), "Please select date.", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding_adopt.equals("")){
                    Toast.makeText(getActivity(), "Please select building.", Toast.LENGTH_SHORT).show();
                } else if (selectedPen_adopt.equals("")){
                    Toast.makeText(getActivity(), "Please select pen.", Toast.LENGTH_SHORT).show();
                } else {
                    saveTransfer(swine_scanned_id, selectedPen_adopt, editext_remarks.getText().toString(), selectedDate_adopt, selectedLocation);
                }
            }
        });
    }

    private void showWithin(){
        isTransferWithin = true;
        remarks_layout.setVisibility(View.GONE);
        displayText_pen_adopt.setText("To Pen: ");
        locationLoading(false);
        location_layout.setVisibility(View.GONE);
        btn_within_adopt.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_other_adopt.setBackgroundResource(0);
        btn_within_adopt.setTextColor(getResources().getColor(R.color.white));
        btn_other_adopt.setTextColor(getResources().getColor(R.color.btn_blue_color2));
        getBuilding(swine_scanned_id, "get_building", "", true);
        if (!isFirstOpen){stringRequest_location.cancel();}
        spinner_locations_adopt.setAdapter(null);
        spinner_building_adopt.setAdapter(null);
        spinner_pen_adopt.setAdapter(null);
        selectedDate_adopt = "";
        btn_date_adopt.setText("Please select date");
        btn_within_adopt.setEnabled(false);
        btn_other_adopt.setEnabled(true);
    }

    private void showOther(){
        isTransferWithin = false;
        remarks_layout.setVisibility(View.VISIBLE);
        displayText_pen_adopt.setText("Pen: ");
        buildingLoading(false);
        location_layout.setVisibility(View.VISIBLE);
        btn_within_adopt.setBackgroundResource(0);
        btn_other_adopt.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_within_adopt.setTextColor(getResources().getColor(R.color.btn_blue_color2));
        btn_other_adopt.setTextColor(getResources().getColor(R.color.white));
        getLocations(swine_scanned_id, "get_locations");
        if (!isFirstOpen){stringRequest_building.cancel();}
        spinner_locations_adopt.setAdapter(null);
        spinner_building_adopt.setAdapter(null);
        spinner_pen_adopt.setAdapter(null);
        selectedDate_adopt = "";
        btn_date_adopt.setText("Please select date");
        btn_within_adopt.setEnabled(true);
        btn_other_adopt.setEnabled(false);
    }

    public void getLocations(final String swine_id, final String get_type) {
        locationLoading(true);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_adopt_transfer_pen_details.php";
        stringRequest_location = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    locationLoading(false);
                    locations_models.clear();
                    locations_models.add(new Locations_model("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        locations_models.add(new Locations_model(cusObj.getString("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < locations_models.size(); i++) {
                        lables.add(locations_models.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_locations_adopt.setAdapter(spinnerAdapter);
                    spinner_locations_adopt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Locations_model click = locations_models.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedLocation = String.valueOf(click.getBranch_id());
                                getBuilding(swine_scanned_id, "get_building_other_location", selectedLocation, false);
                            } else {
                                selectedLocation = "";
                            }
                            selectedBuilding_adopt = "";
                            spinner_building_adopt.setAdapter(null);
                            selectedPen_adopt = "";
                            spinner_pen_adopt.setAdapter(null);
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
                    locationLoading(false);
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
                hashMap.put("get_type", get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest_location);
        AppController.getInstance().addToRequestQueue(stringRequest_location);
    }

    public void getBuilding(final String swine_id, final String get_type, final String locations_other_branch_id, final boolean isWithin) {
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_adopt_transfer_pen_details.php";
        stringRequest_building = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    isFirstOpen = false;
                    buildingLoading(false);
                    layout_adopt.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    building_models.clear();
                    building_models.add(new Building_model("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        building_models.add(new Building_model(cusObj.getString("building_id"),
                                cusObj.getString("building_name")));

                        currentLocation = cusObj.getString("current_location");
                    }

                    if(isWithin){
                        btn_within_adopt.setText("Transfer within location ("+currentLocation+")");
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < building_models.size(); i++) {
                        lables.add(building_models.get(i).getBuilding_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building_adopt.setAdapter(spinnerAdapter);
                    spinner_building_adopt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Building_model click = building_models.get(position);
                            if (!click.getBuilding_name().equals("Please Select")){
                                selectedBuilding_adopt = String.valueOf(click.getBuilding_id());
                                getPen_adopt(company_code, company_id, swine_scanned_id, isWithin ? "get_pen" : "get_pen_other_locations", selectedBuilding_adopt);
                            } else {
                                selectedBuilding_adopt = "";
                            }
                            selectedPen_adopt = "";
                            spinner_pen_adopt.setAdapter(null);
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
                    buildingLoading(false);
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
                hashMap.put("get_type", get_type);
                hashMap.put("locations_other_branch_id", locations_other_branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest_building);
        AppController.getInstance().addToRequestQueue(stringRequest_building);
    }

    public void getPen_adopt(final String company_code, final String company_id, final String swine_id, final String get_type, final String building_id) {
        penLoading(true);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_adopt_transfer_pen_details.php";
        stringRequest_pen = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    penLoading(false);
                    pen_models.clear();
                    pen_models.add(new Pen_model("0","Please Select", ""));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        pen_models.add(new Pen_model(cusObj.getString("pen_assignment_id"),
                                cusObj.getString("pen_name"),
                                cusObj.getString("pen_type")));
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < pen_models.size(); i++) {
                        lables.add(pen_models.get(i).getPen_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen_adopt.setAdapter(spinnerAdapter);
                    spinner_pen_adopt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Pen_model click = pen_models.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen_adopt = String.valueOf(click.getPen_assignment_id());
                            } else {
                                selectedPen_adopt = "";
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
                hashMap.put("get_type", get_type);
                hashMap.put("pen_id", pen_code);
                hashMap.put("building_id", building_id);
                hashMap.put("locations_other_branch_id", selectedLocation);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest_pen);
        AppController.getInstance().addToRequestQueue(stringRequest_pen);
    }

    public void saveTransfer(final String swine_id, final String pen_id, final String remarks, final String transfer_date, final String selectedLocation) {
        btn_save_adopt.setVisibility(View.GONE);
        loading_save_adopt.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/"+ (isTransferWithin ? "pig_adopt_transfer_pen_add.php" : "pig_adopt_transfer_pen_add_other_loc.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    btn_save_adopt.setVisibility(View.VISIBLE);
                    loading_save_adopt.setVisibility(View.GONE);

                    StringBuilder s = new StringBuilder();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject r = (JSONObject) jsonArray.get(i);

                        s.append(r.getString("ear_tag")+"\n");
                    }
                    dialogBox_success(s.toString());

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_save_adopt.setVisibility(View.VISIBLE);
                    loading_save_adopt.setVisibility(View.GONE);
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
                hashMap.put("pen_id", pen_id);
                hashMap.put("remarks", remarks);
                hashMap.put("reference_number", "");
                hashMap.put("category_id", category_id);
                hashMap.put("transfer_date", transfer_date);
                hashMap.put("all_data", new Gson().toJson(wean_array_check_models));
                hashMap.put("locations_other_branch", selectedLocation);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    void dialogBox_success(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_scanned_id);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


}
