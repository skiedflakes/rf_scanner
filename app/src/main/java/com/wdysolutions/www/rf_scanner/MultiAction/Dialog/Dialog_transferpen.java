package com.wdysolutions.www.rf_scanner.MultiAction.Dialog;

import android.app.Dialog;
import android.content.Context;
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


public class Dialog_transferpen extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_other, btn_within, btn_date, displayText_pen;
    LinearLayout location_layout, layout_;
    Spinner spinner_locations, spinner_building, spinner_pen;
    EditText editext_remarks;
    ProgressBar loading_save, progressBar, loading_building, loading_location, loading_pen;
    Button btn_save;
    ArrayList<Dialog_Locations_model> locations_models = new ArrayList<>();
    ArrayList<Dialog_Building_model> building_models = new ArrayList<>();
    ArrayList<Dialog_Pen_model> pen_models = new ArrayList<>();
    String selectedBuilding = "", selectedPen = "", selectedDate = "", selectedLocation = "", swine_scanned_id, currentDate="",
            company_code, company_id, currentLocation, user_id,branch_id,swine_id="",selectedBuilding_id="",selectedPen_id="",selectedPen_name="",selectedBuilding_name="";
    boolean isTransferWithin = true;

    ArrayList<String> message_list = new ArrayList<>();


    //temp data from current pen
    String temp_building_id , temp_building, temp_pen_id,temp_pen;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_pen_main, container, false);

        //shared preferences
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        //bundle
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        branch_id = getArguments().getString("branch_id");
        swine_id = getArguments().getString("swine_id");
        temp_building_id = getArguments().getString("selectedBuilding");
        temp_pen_id = getArguments().getString("selectedPen");
        temp_building = getArguments().getString("selectedBuilding_name");
        temp_pen = getArguments().getString("selectedPen_name");

        //views
        loading_building = view.findViewById(R.id.loading_building);
        loading_location = view.findViewById(R.id.loading_location);
        loading_pen = view.findViewById(R.id.loading_pen);
        displayText_pen = view.findViewById(R.id.displayText_pen);
        layout_ = view.findViewById(R.id.layout_);
        progressBar = view.findViewById(R.id.progressBar);
        btn_date = view.findViewById(R.id.btn_date);
        btn_within = view.findViewById(R.id.btn_within);
        btn_other = view.findViewById(R.id.btn_other);
        location_layout = view.findViewById(R.id.location_layout);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_locations = view.findViewById(R.id.spinner_locations);
        editext_remarks = view.findViewById(R.id.editext_remarks);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);

        //buttons
        btn_within.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWithin();
            }
        });
        btn_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOther();
            }
        });
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
                    // saveTransfer(swine_scanned_id, selectedPen, editext_remarks.getText().toString(), selectedDate, selectedLocation);

                    interfaceObj.senddata(selectedPen,editext_remarks.getText().toString(),selectedDate,selectedLocation,swine_id);
                    dismiss();

                }
            }
        });

        showWithin();
        return view;
    }

    //send back data to parent fragment
    public interface uploadDialogInterface
    {
        void senddata(String selectedPen, String remarks, String selectedDate, String selectedLocation, String swine_id);
    }

    uploadDialogInterface interfaceObj;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceObj= (uploadDialogInterface) getTargetFragment();
    }


    private void showWithin(){
        selectedPen_id = temp_pen_id;
        selectedPen_name = temp_pen;
        selectedBuilding_id = temp_building_id;
        selectedBuilding_name = temp_building;

        isTransferWithin = true;
        displayText_pen.setText("To Pen: ");
        location_layout.setVisibility(View.GONE);
        btn_within.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_other.setBackgroundResource(0);
        btn_within.setTextColor(getResources().getColor(R.color.white));
        btn_other.setTextColor(getResources().getColor(R.color.btn_blue_color2));
        getBuilding( "get_building", "", true);
        spinner_locations.setAdapter(null);
        spinner_building.setAdapter(null);
        spinner_pen.setAdapter(null);
        selectedDate = "";
        btn_date.setText("Please select date");
    }

    private void showOther(){
        selectedPen_id = "";
        selectedPen_name = "";
        selectedBuilding_id = "";
        selectedBuilding_name = "";

        isTransferWithin = false;
        displayText_pen.setText("Pen: ");
        location_layout.setVisibility(View.VISIBLE);
        btn_within.setBackgroundResource(0);
        btn_other.setBackgroundResource(R.drawable.btn_ripple_green);
        btn_within.setTextColor(getResources().getColor(R.color.btn_blue_color2));
        btn_other.setTextColor(getResources().getColor(R.color.white));
        getLocations("get_locations");
        spinner_locations.setAdapter(null);
        spinner_building.setAdapter(null);
        spinner_pen.setAdapter(null);
        selectedDate = "";
        btn_date.setText("Please select date");
    }

    private void locationLoading(boolean status){
        if (status){
            spinner_locations.setVisibility(View.GONE);
            loading_location.setVisibility(View.VISIBLE);
        } else {
            spinner_locations.setVisibility(View.VISIBLE);
            loading_location.setVisibility(View.GONE);
        }
    }

    private void buildingLoading(boolean status){
        if (status){
            spinner_building.setVisibility(View.GONE);
            loading_building.setVisibility(View.VISIBLE);
        } else {
            spinner_building.setVisibility(View.VISIBLE);
            loading_building.setVisibility(View.GONE);
        }
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

    public void getLocations(final String get_type) {
        locationLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    locationLoading(false);
                    locations_models.clear();
                    locations_models.add(new Dialog_Locations_model("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        locations_models.add(new Dialog_Locations_model(cusObj.getString("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < locations_models.size(); i++) {
                        lables.add(locations_models.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_locations.setAdapter(spinnerAdapter);
                    spinner_locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Dialog_Locations_model click = locations_models.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedLocation = String.valueOf(click.getBranch_id());
                                getBuilding("get_building_other_location", selectedLocation, false);
                            } else {
                                selectedLocation = "";
                            }
                            selectedBuilding = "";
                            spinner_building.setAdapter(null);
                            selectedPen = "";
                            spinner_pen.setAdapter(null);
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
                hashMap.put("branch_id", branch_id);
                hashMap.put("get_type", get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getBuilding(final String get_type, final String locations_other_branch_id, final boolean isWithin) {
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    buildingLoading(false);
                    layout_.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    building_models.clear();

                    if(selectedBuilding_id.equals("")){
                        building_models.add(0,new Dialog_Building_model("","Please Select"));
                    }else{
                        building_models.add(new Dialog_Building_model(selectedBuilding_id,selectedBuilding_name));
                    }

                    JSONObject Object = new JSONObject(response);


                    // get current date
                    final JSONArray response_date = Object.getJSONArray("response_date");
                    JSONObject date = (JSONObject) response_date.get(0);
                    currentDate = date.getString("current_date");
                    String[] setDate = currentDate.split(" ");
                    selectedDate = setDate[0];
                    btn_date.setText(selectedDate);


                    final JSONArray diag = Object.getJSONArray("response");
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);
                        if(!selectedBuilding_id.equals(cusObj.getString("building_id"))){

                            building_models.add(new Dialog_Building_model(cusObj.getString("building_id"),
                            cusObj.getString("building_name")));
                            currentLocation = cusObj.getString("current_location");

                        }
                    }

                    if(isWithin){
                        btn_within.setText("Transfer within location ("+currentLocation+")");
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
                            Dialog_Building_model click = building_models.get(position);
                            if (!click.getBuilding_name().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                getPen(company_code, company_id, swine_scanned_id, isWithin ? "get_pen" : "get_pen_other_locations", selectedBuilding);
                            } else {
                                selectedBuilding = "";
                            }
                            selectedPen = "";
                            spinner_pen.setAdapter(null);
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
                    layout_.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    buildingLoading(false);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", branch_id);
                hashMap.put("get_type", get_type);
                hashMap.put("locations_other_branch_id", locations_other_branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getPen(final String company_code, final String company_id, final String swine_id, final String get_type, final String building_id) {
        penLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_pen_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    penLoading(false);
                    pen_models.clear();
                    if(selectedPen_id.equals("")){
                        pen_models.add(0,new Dialog_Pen_model("","Please Select", ""));
                    }else{
                        pen_models.add(new Dialog_Pen_model(selectedPen_id,selectedPen_name, ""));
                    }

                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        if(!selectedPen_id.equals(cusObj.getString("pen_assignment_id"))){

                            pen_models.add(new Dialog_Pen_model(cusObj.getString("pen_assignment_id"),
                                    cusObj.getString("pen_name"),
                                    cusObj.getString("pen_type")));
                        }

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
                            Dialog_Pen_model click = pen_models.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen = String.valueOf(click.getPen_assignment_id());
                            } else {
                                selectedPen = "";
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
                hashMap.put("branch_id", branch_id);
                hashMap.put("get_type", get_type);
                hashMap.put("building_id", building_id);
                hashMap.put("locations_other_branch_id", selectedLocation);
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

    public void saveTransfer(final String swine_id, final String pen_id, final String remarks, final String transfer_date, final String selectedLocation) {
        btn_save.setVisibility(View.GONE);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"transfer_pen/"+ (isTransferWithin ? "pig_transfer_pen_add.php" : "pig_transfer_pen_add_other_location.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    if (response.equals("1")){
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Swine Transfer Successfully Saved!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("2")){
                        Toast.makeText(getActivity(), "Pen is Full!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("3")){
                        Toast.makeText(getActivity(), "Unable to transfer Non-weaner to Farrowing Pen", Toast.LENGTH_SHORT).show();
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
                hashMap.put("branch_id", branch_id);
                hashMap.put("userid", user_id);
                hashMap.put("pen_id", pen_id);
                hashMap.put("remarks", remarks);
                hashMap.put("breed_date", transfer_date);
                hashMap.put("locations_other_branch", selectedLocation);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
