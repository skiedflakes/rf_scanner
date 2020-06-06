package com.wdysolutions.www.rf_scanner.MultiAction.Transfer_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_Building_model;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_Locations_model;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_Pen_model;
import com.wdysolutions.www.rf_scanner.MultiAction.Dialog.Dialog_transferpen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Transfer_dialog extends DialogFragment implements DatePickerSelectionInterfaceCustom {
    TextView btn_other, btn_within, btn_date, displayText_pen;
    LinearLayout location_layout, layout_;
    Spinner spinner_locations, spinner_building, spinner_pen;
    EditText editext_remarks;
    ProgressBar loading_save, progressBar, loading_location, loading_building, loading_pen;
    Button btn_save;
    ArrayList<Dialog_Locations_model> locations_models = new ArrayList<>();
    ArrayList<Dialog_Building_model> building_models = new ArrayList<>();
    ArrayList<Dialog_Pen_model> pen_models = new ArrayList<>();
    String selectedBuilding = "", selectedPen = "", selectedDate = "", selectedLocation = "", swine_scanned_id,
            company_code, company_id, currentDate="", user_id,branch_id,swine_id="",selectedBuilding_id="",selectedPen_id="",selectedPen_name="",selectedBuilding_name="";
    boolean isTransferWithin = true;

    ArrayList<String> message_list = new ArrayList<>();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_dialog, container, false);

        //shared preferences
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        //bundle
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        branch_id = getArguments().getString("branch_id");
        swine_id = getArguments().getString("swine_id");
        selectedBuilding_id = getArguments().getString("selectedBuilding");
        selectedPen_id = getArguments().getString("selectedPen");
        selectedBuilding_name = getArguments().getString("selectedBuilding_name");
        selectedPen_name = getArguments().getString("selectedPen_name");

        //views
        displayText_pen = view.findViewById(R.id.displayText_pen);
        layout_ = view.findViewById(R.id.layout_);
        progressBar = view.findViewById(R.id.progressBar);
        btn_date = view.findViewById(R.id.btn_date);

        loading_building = view.findViewById(R.id.loading_building);
        loading_location = view.findViewById(R.id.loading_location);
        loading_pen = view.findViewById(R.id.loading_pen);
        location_layout = view.findViewById(R.id.location_layout);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_locations = view.findViewById(R.id.spinner_locations);
        editext_remarks = view.findViewById(R.id.editext_remarks);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);

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

        getLocations("get_locations");
        return view;
    }

    private void locationLoading(boolean status){
        if (status){
            layout_.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            layout_.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
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

    Dialog_transferpen.uploadDialogInterface interfaceObj;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceObj= (Dialog_transferpen.uploadDialogInterface) getTargetFragment();
    }


    public void getLocations(final String get_type) {
        locationLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    locationLoading(false);
                    locations_models.clear();
                    JSONObject Object = new JSONObject(response);

                    // get current date
                    final JSONArray response_date = Object.getJSONArray("response_date");
                    JSONObject date = (JSONObject) response_date.get(0);
                    currentDate = date.getString("current_date");
                    String[] setDate = currentDate.split(" ");
                    selectedDate = setDate[0];
                    btn_date.setText(selectedDate);


                    final JSONArray diag = Object.getJSONArray("response");
                    locations_models.add(new Dialog_Locations_model("0","Please Select"));
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
                                getBuilding("get_building_other_location", selectedLocation);
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
                hashMap.put("get_type", get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getBuilding(final String get_type, final String locations_other_branch_id) {
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    buildingLoading(false);
                    building_models.clear();
                    building_models.add(0,new Dialog_Building_model("0",
                            "Please Select"));

                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);
                        building_models.add(new Dialog_Building_model(cusObj.getString("building_id"),
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
                            Dialog_Building_model click = building_models.get(position);
                            if (!click.getBuilding_name().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                get_pen(locations_other_branch_id,selectedBuilding);
                            } else {
                                selectedBuilding = "";
                            }
//                            selectedPen = "";
//                            spinner_pen.setAdapter(null);
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
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("branch_id", selectedLocation);
                hashMap.put("get_type", get_type);
                hashMap.put("locations_other_branch_id", locations_other_branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_pen(final String locations_other_branch_id,final String building_id) {
        penLoading(true);
        String URL = getString(R.string.URL_online)+"transfer_pen/dialog_transfer_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    penLoading(false);
                    pen_models.clear();
                    pen_models.add(0,new Dialog_Pen_model("","Please Select", ""));

                    JSONObject Object = new JSONObject(response);
                    final JSONArray diag = Object.getJSONArray("response");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        pen_models.add(new Dialog_Pen_model(cusObj.getString("pen_assignment_id"),
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
                hashMap.put("get_type", "get_pen_other_locations");
                hashMap.put("locations_other_branch_id", locations_other_branch_id);
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("building_id", building_id);
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

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        EditText text = new EditText(getActivity());
        text.setText(name);
        alertDialog.setView(text);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
