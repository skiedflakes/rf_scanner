package com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Feeding_Pen_adapter;
import com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Feeding_Pen_model;
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


public class Feeding_Pen_Update_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    String feeding_id, company_code, company_id, category_id, user_id;
    ProgressBar progressBar4, update_loading;
    EditText  editText_amount;
    Spinner spinner_feed_type;
    Button btn_close, btn_submit;
    LinearLayout layout_main, layout_btn;
    ProgressDialog loadingScan;
    TextView txt_date;

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeding_pen_update_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        feeding_id = getArguments().getString("getFeeding_id");

        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        progressBar4 = view.findViewById(R.id.progressBar4);
        txt_date = view.findViewById(R.id.txt_date);
        spinner_feed_type = view.findViewById(R.id.spinner_feed_type);
        editText_amount = view.findViewById(R.id.editText_amount);
        btn_close = view.findViewById(R.id.btn_close);
        btn_submit = view.findViewById(R.id.btn_submit);
        layout_main = view.findViewById(R.id.layout_main);
        update_loading = view.findViewById(R.id.update_loading);
        layout_btn = view.findViewById(R.id.layout_btn);

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(false);
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFeeding();
            }
        });

        getFeedingPenDetails();
        return view;
    }

    String selectedProduct, quantity, swine_id, feeding_date, product_id, current_date;
    ArrayList<Feeding_Pen_Update_model> feeding_pen_update_models;
    private void getFeedingPenDetails(){
        progressBar4.setVisibility(View.VISIBLE);
        layout_main.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"feeding/feeding_per_pen_view_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar4.setVisibility(View.GONE);
                    layout_main.setVisibility(View.VISIBLE);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject jsonObject1 = (JSONObject)jsonArray.get(0);
                    quantity = jsonObject1.getString("quantity");
                    swine_id = jsonObject1.getString("swine_id");
                    feeding_date = jsonObject1.getString("feeding_date");
                    product_id = jsonObject1.getString("product_id");
                    current_date = jsonObject1.getString("current_date");

                    txt_date.setText(feeding_date);

                    editText_amount.setText(quantity);
                    editText_amount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            quantity = s.toString();
                        }
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    });

                    feeding_pen_update_models = new ArrayList<>();
                    JSONArray jsonArray_spinner = jsonObject.getJSONArray("data_spinner");
                    for (int i=0; i<jsonArray_spinner.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray_spinner.get(i);

                        if (product_id.equals(jsonObject2.getString("product_id"))){
                            feeding_pen_update_models.add(new Feeding_Pen_Update_model(jsonObject2.getString("product"),
                                    jsonObject2.getString("product_id")));
                            break;
                        }
                    }

                    for (int i=0; i<jsonArray_spinner.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray_spinner.get(i);

                        if (!product_id.equals(jsonObject2.getString("product_id"))){
                            feeding_pen_update_models.add(new Feeding_Pen_Update_model(jsonObject2.getString("product"),
                                    jsonObject2.getString("product_id")));
                        }
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < feeding_pen_update_models.size(); i++) {
                        lables.add(feeding_pen_update_models.get(i).getProduct());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_feed_type.setAdapter(spinnerAdapter);
                    spinner_feed_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Feeding_Pen_Update_model click = feeding_pen_update_models.get(position);
                            selectedProduct = String.valueOf(click.getProduct_id());
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("feeding_id", feeding_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void updateFeeding(){
        showLoading(loadingScan, "Saving").show();
        String URL = getString(R.string.URL_online)+"feeding/feeding_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //((ActivityMain)getActivity()).dialogBox(response);
                    showLoading(loadingScan, "").dismiss();

                    if (response.equals("1")){
                        refresh();
                        dismiss();
                        Toast.makeText(getActivity(), "Successfully update", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("2")){
                        Toast.makeText(getActivity(), "Insufficient quantity. Please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed query", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, "").dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("category_id", category_id);
                hashMap.put("update_feeding_id", feeding_id);
                hashMap.put("update_swine_id", swine_id);
                hashMap.put("update_date_of_feeding", feeding_date);
                hashMap.put("update_product_id", selectedProduct);
                hashMap.put("update_amount", quantity);
                hashMap.put("user_id", user_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void refresh(){
        Intent i = new Intent("refresh_data");
        i.putExtra("refresh", "data");
        getActivity().sendBroadcast(i);
    }

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

    @Override
    public void onDateSelected(String date) {
        feeding_date = date;
        txt_date.setText(feeding_date);
    }
}
