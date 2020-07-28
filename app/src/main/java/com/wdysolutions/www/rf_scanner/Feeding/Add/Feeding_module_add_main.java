package com.wdysolutions.www.rf_scanner.Feeding.Add;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.wdysolutions.www.rf_scanner.Feeding.Add.DialogMultiSelectSpinner.Feeding_module_multiselect;
import com.wdysolutions.www.rf_scanner.Feeding.Feeding_model_branch;
import com.wdysolutions.www.rf_scanner.Feeding.resultListener;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Feeding_module_add_main extends DialogFragment implements DatePickerSelectionInterfaceCustom, Feeding_module_multiselect.checkboxSelected {

    String company_code, company_id, user_id, selectedBranch="", selectedBuilding="", selectPen="";
    ProgressBar loading_building, loading_pen, loading_eartag, loading_feedtype;
    Spinner spinner_branch, spinner_building, spinner_pen, spinner_feedtype;
    TextView btn_date, text_eartag;
    LinearLayout layout_, loading_,layout_farrowing;
    ArrayList<Feeding_model_branch> feeding_model_branches = new ArrayList<>();
    ArrayList<Feeding_model_building> feeding_model_buildings = new ArrayList<>();
    ArrayList<Feeding_model_pen> feeding_model_pens = new ArrayList<>();
    ArrayList<Feeding_model_eartag> feeding_model_eartags = new ArrayList<>();
    ArrayList<Feeding_model_product> feeding_model_products = new ArrayList<>();
    ArrayList<Feeding_model_selected> feeding_model_selecteds = new ArrayList<>();
    String selectedDate = "", pen_type="", selectedProduct="", isPiglet="0", current_date="", category_id;
    EditText editText_amount;
    RadioButton forSow, forPiglets;
    RelativeLayout btn_save;
    ProgressDialog loadingScan;
    resultListener pass;
    StringRequest stringRequest_add;


    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        Bundle bundle = new Bundle();
        bundle.putString("maxDate", current_date);
        bundle.putString("maxDate_minus", "");
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeding_module_add_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        pass = (resultListener)getTargetFragment();

        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        loading_ = view.findViewById(R.id.loading_);
        layout_ = view.findViewById(R.id.layout_);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        spinner_building = view.findViewById(R.id.spinner_building);
        loading_building = view.findViewById(R.id.loading_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        loading_pen = view.findViewById(R.id.loading_pen);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_feedtype = view.findViewById(R.id.spinner_feedtype);
        editText_amount = view.findViewById(R.id.editText_amount);
        loading_eartag = view.findViewById(R.id.loading_eartag);
        loading_feedtype = view.findViewById(R.id.loading_feedtype);
        layout_farrowing = view.findViewById(R.id.layout_farrowing);
        forPiglets = view.findViewById(R.id.forPiglets);
        forSow = view.findViewById(R.id.forSow);
        btn_save = view.findViewById(R.id.btn_save);
        text_eartag = view.findViewById(R.id.text_eartag);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(false);
            }
        });

        forPiglets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forPiglets.isChecked()){
                    forSow.setChecked(false);
                    isPiglet = "1";
                    getEartag("eartag");
                }
            }
        });

        forSow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forSow.isChecked()){
                    forPiglets.setChecked(false);
                    isPiglet = "0";
                    getEartag("eartag");
                }
            }
        });

        text_eartag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSelectEartag();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBranch.equals("")){
                    Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                } else if (selectedBuilding.equals("")){
                    Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_SHORT).show();
                } else if (selectedDate.equals("")){
                    Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
                } else if (selectPen.equals("")){
                    Toast.makeText(getActivity(), "Please select pen", Toast.LENGTH_SHORT).show();
                } else if (feeding_model_selecteds.size() == 0){
                    Toast.makeText(getActivity(), "Please select ear tag", Toast.LENGTH_SHORT).show();
                } else if (selectedProduct.equals("")){
                    Toast.makeText(getActivity(), "Please select product", Toast.LENGTH_SHORT).show();
                } else if (editText_amount.getText().toString().equals("") || editText_amount.getText().toString().equals("0")){
                    Toast.makeText(getActivity(), "Please enter amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialogBox_("Add feeding?");
                }
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        get_branch(company_id, company_code, "get_branch");
        return view;
    }

    private void openDialogSelectEartag(){
        Bundle bundle = new Bundle();
        bundle.putString("array", new Gson().toJson(feeding_model_eartags));
        DialogFragment selectedDialogFragment = new Feeding_module_multiselect();
        selectedDialogFragment.setTargetFragment(Feeding_module_add_main.this, 0);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog_select");
        if (prev != null) {ft.remove(prev);}
        ft.addToBackStack(null);
        selectedDialogFragment.setArguments(bundle);
        selectedDialogFragment.show(ft, "dialog_select");
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

    private void eartagLoading(boolean status){
        if (status){
            loading_eartag.setVisibility(View.VISIBLE);
            text_eartag.setVisibility(View.GONE);
        } else {
            loading_eartag.setVisibility(View.GONE);
            text_eartag.setVisibility(View.VISIBLE);
        }
    }

    private void get_branch(final String company_id, final String company_code, final String get_type){
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"feeding/feeding_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);
                    JSONObject Object = new JSONObject(response);

                    feeding_model_branches.add(new Feeding_model_branch(0,"Please Select"));
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        feeding_model_branches.add(new Feeding_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < feeding_model_branches.size(); i++) {
                        lables.add(feeding_model_branches.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Feeding_model_branch click = feeding_model_branches.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedBranch = String.valueOf(click.getBranch_id());
                                get_building("get_building");
                            } else {
                                selectedBranch = "";
                            }
                            resetSelectedEartag();

                            spinner_building.setAdapter(null);
                            spinner_pen.setAdapter(null);
                            isPiglet = "0";
                            layout_farrowing.setVisibility(View.GONE);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    JSONArray diag1 = Object.getJSONArray("response_date");
                    JSONObject j = (JSONObject)diag1.get(0);
                    current_date = j.getString("current_date");

                }
                catch (JSONException e) {}
                catch (Exception e){}
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
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void get_building(final String get_type){
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"feeding/feeding_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    buildingLoading(false);
                    JSONObject Object = new JSONObject(response);
                    feeding_model_buildings.clear();
                    feeding_model_buildings.add(new Feeding_model_building(0,"Please Select"));
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        feeding_model_buildings.add(new Feeding_model_building(cusObj.getInt("building_id"),
                                cusObj.getString("building")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < feeding_model_buildings.size(); i++) {
                        lables.add(feeding_model_buildings.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Feeding_model_building click = feeding_model_buildings.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                get_pen("get_pen");
                            } else {
                                selectedBuilding = "";
                            }
                            resetSelectedEartag();

                            spinner_pen.setAdapter(null);
                            isPiglet = "0";
                            layout_farrowing.setVisibility(View.GONE);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Product Master --------------------------------------------------------------
                    feeding_model_products.clear();
                    feeding_model_products.add(new Feeding_model_product("Please Select",""));
                    JSONArray diag1 = Object.getJSONArray("response_product");

                    for (int i = 0; i < diag1.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag1.get(i);

                        feeding_model_products.add(new Feeding_model_product(cusObj.getString("product"),
                                cusObj.getString("product_id")));
                    }

                    // Populate Spinner branch
                    List<String> lables1 = new ArrayList<>();
                    for (int i = 0; i < feeding_model_products.size(); i++) {
                        lables1.add(feeding_model_products.get(i).getProduct());
                    }

                    ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables1);

                    spinnerAdapter1.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_feedtype.setAdapter(spinnerAdapter1);
                    spinner_feedtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Feeding_model_product click = feeding_model_products.get(position);
                            if (!click.getProduct().equals("Please Select")){
                                selectedProduct = String.valueOf(click.getProduct_id());
                            } else {
                                selectedProduct = "";
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
                    buildingLoading(false);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("get_type",get_type);
                hashMap.put("branch_id", selectedBranch);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void get_pen(final String get_type){
        penLoading(true);
        String URL = getString(R.string.URL_online)+"feeding/feeding_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    penLoading(false);
                    JSONObject Object = new JSONObject(response);
                    feeding_model_pens.clear();
                    feeding_model_pens.add(new Feeding_model_pen(0,"Please Select", "", ""));
                    JSONArray diag = Object.getJSONArray("response_pen");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        feeding_model_pens.add(new Feeding_model_pen(cusObj.getInt("pen_assignment_id"),
                                cusObj.getString("pen_name"),
                                cusObj.getString("pen_type"),
                                cusObj.getString("max_heads")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < feeding_model_pens.size(); i++) {
                        lables.add(feeding_model_pens.get(i).getPen_name());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);

                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Feeding_model_pen click = feeding_model_pens.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectPen = String.valueOf(click.getPen_assignment_id());
                                pen_type = String.valueOf(click.getPen_type());

                                if (pen_type.equals("Farrowing")){
                                    layout_farrowing.setVisibility(View.VISIBLE);
                                } else {
                                    isPiglet = "0";
                                    layout_farrowing.setVisibility(View.GONE);
                                    getEartag("eartag");
                                }
                            } else {
                                selectPen = "";
                                layout_farrowing.setVisibility(View.GONE);
                            }
                            resetSelectedRadioButton();
                            resetSelectedEartag();
                            feeding_model_selecteds.clear();
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
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("building_id", selectedBuilding);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getEartag(final String get_type){
        eartagLoading(true);
        String URL = getString(R.string.URL_online)+"feeding/feeding_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    text_eartag.setEnabled(true);
                    eartagLoading(false);
                    JSONObject Object = new JSONObject(response);
                    feeding_model_eartags.clear();
                    JSONArray diag = Object.getJSONArray("response_swine");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        feeding_model_eartags.add(new Feeding_model_eartag(cusObj.getString("swine_id"),
                                cusObj.getString("swine_name"),
                                "0",
                                false));
                    }

                    if (feeding_model_eartags.size() == 0){
                        text_eartag.setText("No data found");
                        text_eartag.setClickable(false);
                    } else {
                        text_eartag.setText("Please select eartag");
                        text_eartag.setClickable(true);
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    eartagLoading(false);
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("pen_id", selectPen);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("isPiglet", isPiglet);
                hashMap.put("get_type", get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void feedAdd(){
        btn_save.setClickable(false);
        showLoading(loadingScan, "Saving...").show();
        String URL = getString(R.string.URL_online)+"feeding/feeding_add.php";
        stringRequest_add = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    showLoading(loadingScan, null).dismiss();
                    String res = "";
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject j = (JSONObject)jsonArray.get(i);
                        String status = j.getString("status");
                        String result = j.getString("result");

                        if (status.equals("success")){
                            res += "<font color='#50a44d'>"+result+"</font><br><br>";
                        } else {
                            res += "<font color='#ea4431'>"+result+"</font><br><br>";
                        }
                    }
                    dialogBox_msg(res);
                    pass.listener();
                    dismiss();

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_save.setClickable(true);
                    showLoading(loadingScan, null).dismiss();
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("pen_id", selectPen);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("product_id", selectedProduct);
                hashMap.put("building_id", selectedBuilding);
                hashMap.put("date_of_feeding", selectedDate);
                hashMap.put("isPiglet", isPiglet);
                hashMap.put("user_id", user_id);
                hashMap.put("category_id", category_id);
                hashMap.put("ms_swine_id", new Gson().toJson(feeding_model_selecteds));
                hashMap.put("ms_swine_id_count", String.valueOf(feeding_model_selecteds.size()));
                hashMap.put("amount", editText_amount.getText().toString());
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest_add);
        AppController.getInstance().addToRequestQueue(stringRequest_add);
    }

    void dialogBox_(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(Html.fromHtml(msg));
        alertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        feedAdd();
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    void dialogBox_msg(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(Html.fromHtml(msg));
        alertDialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onDateSelected(String date) {
        selectedDate = date;
        btn_date.setText(selectedDate);
    }

    private void resetSelectedEartag(){
        text_eartag.setText("");
        text_eartag.setEnabled(false);
    }

    private void resetSelectedRadioButton(){
        forPiglets.setChecked(false);
        forSow.setChecked(false);
    }

    @Override
    public void selected(ArrayList<Feeding_model_eartag> arraySelected) {
        feeding_model_eartags = arraySelected;

        int count = 0;
        String selected = "", selected_1="";
        feeding_model_selecteds.clear();
        for (int i=0; i<feeding_model_eartags.size(); i++){
            Feeding_model_eartag data = feeding_model_eartags.get(i);

            selected_1 = data.getSwine_name();

            if (data.getCheckStatus().equals("1")){
                selected += data.getSwine_name()+", ";
                count++;

                // save selected to new array
                feeding_model_selecteds.add(new Feeding_model_selected(data.getSwine_id()));
            }
        }

        if (feeding_model_eartags.size() != 0 && feeding_model_eartags.size() >= 2){
            if (feeding_model_eartags.size() == count){
                selected = "ALL";
            }
        } else if (feeding_model_eartags.size() == 1){
            selected = selected_1;
        }

        text_eartag.setText(selected);
    }
}
