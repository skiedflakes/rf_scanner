package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Culling;

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
import android.widget.CheckBox;
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
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Mortality.Disease_model;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Culling_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    String company_code, company_id, swine_scanned_id, category_id, user_id;
    ArrayList<Customer_model> customer_models = new ArrayList<>();
    ArrayList<Payment_model> payment_models = new ArrayList<>();
    ArrayList<Disease_model> disease_models = new ArrayList<>();
    TextView text_delivery, btn_date, text_invoice;
    Button btn_save;
    EditText edittext_remarks, edittext_soldamount, edittext_weight;
    CheckBox checkbox_invoice;
    ProgressBar loading_save, progressBar;
    Spinner spinner_customer, spinner_payment, spinner_cause;
    LinearLayout layout_add;
    String selectedCustomer = "", selectedPayment = "", selectedInvoice = "", selectedCause = "", selectedDate = "",
    delivery_text = "", currentDate="";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.culling_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        text_delivery = view.findViewById(R.id.text_delivery);
        btn_date = view.findViewById(R.id.btn_date);
        spinner_customer = view.findViewById(R.id.spinner_customer);
        spinner_payment = view.findViewById(R.id.spinner_payment);
        text_invoice = view.findViewById(R.id.text_invoice);
        checkbox_invoice = view.findViewById(R.id.checkbox_invoice);
        edittext_remarks = view.findViewById(R.id.edittext_remarks);
        spinner_cause = view.findViewById(R.id.spinner_cause);
        edittext_soldamount = view.findViewById(R.id.edittext_soldamount);
        edittext_weight = view.findViewById(R.id.edittext_weight);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);
        layout_add = view.findViewById(R.id.layout_add);

        checkbox_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_invoice.isChecked()){
                    get_Invoice(company_code, company_id, swine_scanned_id, selectedPayment);
                } else {
                    getNRD(company_code, company_id, swine_scanned_id, selectedPayment);
                }
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
                } else if (selectedCustomer.equals("")){
                    Toast.makeText(getActivity(), "Please select customer.", Toast.LENGTH_SHORT).show();
                } else if (selectedPayment.equals("")){
                    Toast.makeText(getActivity(), "Please select payment.", Toast.LENGTH_SHORT).show();
                } else if (selectedInvoice.equals("")){
                    Toast.makeText(getActivity(), "Please select invoice no.", Toast.LENGTH_SHORT).show();
                } else {
                    String remarks = edittext_remarks.getText().toString();
                    String soldAmount = edittext_soldamount.getText().toString();
                    String weight = edittext_weight.getText().toString();

                    saveCulling(company_code, company_id, swine_scanned_id, selectedPayment, selectedCause, remarks,
                        selectedCustomer, soldAmount, weight, delivery_text, selectedDate, selectedInvoice);
                }
            }
        });

        fetch_details(company_code, company_id, swine_scanned_id);
        return view;
    }

    public void fetch_details(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_culling_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);

                    JSONObject Object = new JSONObject(response);

                    // Customer Spinner ------------------------------------------------------
                    customer_models.add(new Customer_model("0","Please Select"));
                    final JSONArray jsonArray_customer = Object.getJSONArray("response");
                    for (int i = 0; i < jsonArray_customer.length(); i++) {
                        JSONObject cusObj = (JSONObject) jsonArray_customer.get(i);

                        customer_models.add(new Customer_model(cusObj.getString("customer_id"),
                                cusObj.getString("customer")));
                    }
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < customer_models.size(); i++) {
                        lables.add(customer_models.get(i).getCustomer());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_customer.setAdapter(spinnerAdapter);
                    spinner_customer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Customer_model click = customer_models.get(position);
                            if (!click.getCustomer().equals("Please Select")){
                                selectedCustomer = String.valueOf(click.getCustomer_id());
                            } else {
                                selectedCustomer = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    // Cause Spinner ------------------------------------------------------
                    disease_models.add(new Disease_model("0","Please Select"));
                    final JSONArray jsonArray_cause = Object.getJSONArray("response_cause");
                    for (int i = 0; i < jsonArray_cause.length(); i++) {
                        JSONObject cusObj = (JSONObject) jsonArray_cause.get(i);

                        disease_models.add(new Disease_model(cusObj.getString("disease_id"),
                                cusObj.getString("cause")));
                    }
                    List<String> lables_cause = new ArrayList<>();
                    for (int i = 0; i < disease_models.size(); i++) {
                        lables_cause.add(disease_models.get(i).getCause());
                    }
                    ArrayAdapter<String> spinnerAdapter_cause = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_cause);
                    spinnerAdapter_cause.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_cause.setAdapter(spinnerAdapter_cause);
                    spinner_cause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Disease_model click = disease_models.get(position);
                            if (!click.getCause().equals("Please Select")){
                                selectedCause = String.valueOf(click.getDisease_id());
                            } else {
                                selectedCause = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    // Payment spinner ------------------------------------------------------
                    payment_models.add(new Payment_model("0","Please Select"));
                    payment_models.add(new Payment_model("C","Cash"));
                    payment_models.add(new Payment_model("H","Charge"));
                    List<String> lablesPayment = new ArrayList<>();
                    for (int i = 0; i < payment_models.size(); i++) {
                        lablesPayment.add(payment_models.get(i).getName());
                    }
                    ArrayAdapter<String> spinnerAdapter_payment = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lablesPayment);
                    spinnerAdapter_payment.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_payment.setAdapter(spinnerAdapter_payment);
                    spinner_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Payment_model click = payment_models.get(position);
                            if (!click.getName().equals("Please Select")){
                                selectedPayment = String.valueOf(click.getId());
                                get_Invoice(company_code, company_id, swine_scanned_id, selectedPayment);
                            } else {
                                selectedPayment = "";
                                text_invoice.setText("");
                                checkbox_invoice.setChecked(false);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    // Delivery number ------------------------------------------------------
                    final JSONArray delivery_response = Object.getJSONArray("delivery_response");
                    JSONObject jsonObject_deliveryNO = (JSONObject) delivery_response.get(0);
                    delivery_text = jsonObject_deliveryNO.getString("delivery_num");
                    currentDate  = jsonObject_deliveryNO.getString("current_date");
                    text_delivery.setText("   "+delivery_text);

                    String[] maxDate = currentDate.split(" ");
                    selectedDate = maxDate[0];
                    btn_date.setText(selectedDate);

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
                hashMap.put("swine_id", swine_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_Invoice(final String company_code, final String company_id, final String swine_id, final String pay_type) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_culling_gen_invoice.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("0")){
                    checkbox_invoice.setChecked(false);
                    getNRD(company_code, company_id, swine_id, pay_type);
                    Toast.makeText(getActivity(), "No available invoice", Toast.LENGTH_SHORT).show();
                } else {
                    selectedInvoice = response;
                    text_invoice.setText("   "+selectedInvoice);
                    checkbox_invoice.setChecked(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    selectedInvoice = "";
                    text_invoice.setText("");
                    checkbox_invoice.setChecked(false);
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
                hashMap.put("pay_type", pay_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getNRD(final String company_code, final String company_id, final String swine_id, final String pay_type) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_culling_gen_NRD.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    selectedInvoice = response;
                    text_invoice.setText("   "+selectedInvoice);
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
                hashMap.put("pay_type", pay_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void saveCulling(final String company_code, final String company_id, final String swine_id, final String pay_type,
                            final String cull_cause, final String cull_remarks, final String customer_id, final String cull_sold_amount, final String weight,
                            final String delivery_number, final String dr_date, final String invoice_number) {
        loading_save.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_culling_add2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    if (response.equals("0")){
                        Toast.makeText(getActivity(), "Culling Failed!", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("1")){
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Culling Successfully Saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("user_id", user_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("category_id", category_id);
                hashMap.put("cull_cause", cull_cause);
                hashMap.put("cull_remarks", cull_remarks);
                hashMap.put("customer_id", customer_id);
                hashMap.put("cull_sold_amount", cull_sold_amount);
                hashMap.put("weight", weight);
                hashMap.put("delivery_number", delivery_number);
                hashMap.put("dr_date", dr_date);
                hashMap.put("pay_type", pay_type);
                hashMap.put("reference_number", "");
                hashMap.put("invoice_number", invoice_number);
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
