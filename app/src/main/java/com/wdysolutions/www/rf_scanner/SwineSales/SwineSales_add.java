package com.wdysolutions.www.rf_scanner.SwineSales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RadioButton;
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
import com.wdysolutions.www.rf_scanner.Modal_fragment;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.viewDetails_adapter;
import com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails.viewDetails_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SwineSales_add extends Fragment implements DatePickerSelectionInterfaceCustom,
        Dialog_Authenticate.uploadDialogInterface, Modal_fragment.dialog_interface, SwineSales_add_adapter.buttonListener,Dialog_weight_price.weight_price{
    //initialize
    String branch_id;
    String add_edit;
    Button btn_save;
    String dr_header_id="";

    //session
    String company_code,company_id,user_id,category_id;

    //dr number
    TextView tv_dr_num, tv_total;
    String delivery_number = "";

    //date
    TextView tv_date;
    String max_date="",dateSelected="";

    //customer
    String customerSelected="";
    String customerSelected_name="";
    Spinner customer_spinner;
    ArrayList<Customers_model> customer_list_spinner = new ArrayList<>();

    //payment type
    String paymentSelected="";
    Spinner payment_spinner;
    String payment_type="";

    //invoice
    TextView tv_invoice;
    CheckBox cb_reciept;
    String invoice_number = "";

    //discount
    CheckBox cb_discount;
    EditText et_discount;
    String discount="";

    //trucking
    CheckBox cb_trucking;
    EditText et_trucking;
    String trucking_amount;
    String tr_status="No";

    //truckers
    String truckersSelected="";
    String truckersSelected_name="";
    Spinner truckers_spinner;
    ArrayList<Truckers_model> truckers_list_spinner = new ArrayList<>();

    //trucking expense
    String trucking_expenseSelected="";
    String trucking_expenseSelected_name="";
    Spinner trucking_expense_spinner;
    ArrayList<Trucking_expense_model> trucking_expense_list_spinner = new ArrayList<>();
    ArrayList<viewDetails_model> viewDetails_models = new ArrayList<>();

    //vat non vat
    RadioButton rb_vat,rb_non_vat;

    //remarks
    EditText et_remarks;
    String remarks="";
    RecyclerView recyclerView;

    //delivery
    Button btn_delivery;

    //addstatus
    String add_status="";
    Button btn_finish;
    ProgressDialog loadingScan;

    SwineSales_add_adapter swineSales_add_adapter;


    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swine_sales_add, container, false);

        //shared preferences
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        //      ~ initialize ids~
        //dr_num
        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        tv_dr_num = view.findViewById(R.id.tv_dr_num);
        recyclerView = view.findViewById(R.id.recyclerView);
        tv_total = view.findViewById(R.id.tv_total);

        //date
        tv_date = view.findViewById(R.id.tv_date);
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!max_date.equals("")){
                    openDatePicker(false,max_date);
                }
            }
        });

        //customer
        customer_spinner = view.findViewById(R.id.customer_spinner);

        //payment
        payment_spinner = view.findViewById(R.id.payment_spinner);

        //invoice
        tv_invoice = view.findViewById(R.id.tv_invoice);
        cb_reciept = view.findViewById(R.id.cb_reciept);
        cb_reciept.setClickable(false);

        //discount
        cb_discount = view.findViewById(R.id.cb_discount);
        et_discount = view.findViewById(R.id.tv_discount);
        et_discount.setEnabled(false);

        cb_discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic
                    open_authentication();

                }else{

                }

            }
        });

        //trucking
        cb_trucking = view.findViewById(R.id.cb_trucking);
        et_trucking = view.findViewById(R.id.et_trucking);
        et_trucking.setEnabled(false);
        cb_trucking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    et_trucking.setEnabled(true);
                    truckers_spinner.setEnabled(true);
                    trucking_expense_spinner.setEnabled(true);
                    tr_status ="Yes";
                }else{
                    et_trucking.setText("");
                    et_trucking.setEnabled(false);
                    tr_status ="No";
                }
            }
        });

        //trucker
        truckers_spinner = view.findViewById(R.id.trucker_spinner);
        truckers_spinner.setEnabled(false);

        //trucking expense
        trucking_expense_spinner = view.findViewById(R.id.trucking_expense_spinner);
        trucking_expense_spinner.setEnabled(false);

        //button add swine sales
        btn_save = view.findViewById(R.id.btn_save);


        //remarks
        et_remarks = view.findViewById(R.id.et_remarks);

        //delivery
        btn_delivery = view.findViewById(R.id.btn_delivery);

        //bundle
        Bundle bundle = getArguments();
        if(bundle != null){
            add_edit = bundle.getString("add_edit");
            branch_id = bundle.getString("branch_id");

            if(add_edit.equals("add")){
                init_add_edit(add_edit);

            }else{
                delivery_number = bundle.getString("delivery_number");
                invoice_number = bundle.getString("invoice");
                payment_type = bundle.getString("payment_type");
                dateSelected = bundle.getString("date");
                customerSelected_name = bundle.getString("customer");
                discount =  bundle.getString("discount");
                remarks = bundle.getString("remarks");
                truckersSelected_name = bundle.getString("tr_swine");
                trucking_amount = bundle.getString("tr_swine_a");
                trucking_expenseSelected_name = bundle.getString("tr_swine_e");
                dr_header_id = bundle.getString("id");

                if(payment_type.equals("CASH")){
                    paymentSelected = "C";
                }else if(payment_type.equals("CHARGE")){
                    paymentSelected = "C";
                }

                init_add_edit(add_edit);
            }

        }else{
            Toast.makeText(getContext(), "error loading", Toast.LENGTH_SHORT).show();
        }


        btn_finish = view.findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox_cofirmation(false);
            }
        });

        getBottomTable();
        return view;
    }



    public void getBottomTable(){
        showLoading(loadingScan, "loading...").show();
        String URL = getString(R.string.URL_online)+"swine_sales/deliveryDetails_swine.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //dialogBox(response);
                    //setLoading(true);
                    viewDetails_models.clear();
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("data");
                    showLoading(loadingScan, null).dismiss();
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        viewDetails_models.add(new viewDetails_model(jsonObject.getString("id"),
                                jsonObject.getString("packaging_id"),
                                jsonObject.getString("stock_id"),
                                jsonObject.getString("age"),
                                jsonObject.getString("total_feed_consumption"),
                                jsonObject.getString("weight"),
                                jsonObject.getString("price_kg"),
                                jsonObject.getString("sub_total"),
                                jsonObject.getString("price")));
                    }

                    JSONArray json = object.getJSONArray("data_total");
                    JSONObject jsObj = (JSONObject) json.get(0);
                    tv_total.setText("₱ "+jsObj.getString("total"));

                    swineSales_add_adapter = new SwineSales_add_adapter(getContext(), viewDetails_models, SwineSales_add.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(swineSales_add_adapter);
                    recyclerView.setNestedScrollingEnabled(false);

                }
                catch (JSONException e){}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).dismiss();
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("delivery_id", delivery_number);
                hashMap.put("branch", branch_id);
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void init_add_edit(String add_edit){
        if(add_edit.equals("add")){
            btn_delivery.setVisibility(View.GONE);
            get_details(company_id,company_code,"dr_num");
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogBox_cofirmation(true);
                }
            });

        }else{
            btn_delivery.setVisibility(View.VISIBLE);
            btn_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("branch_id",branch_id );
                    bundle.putString("dr_header_id",dr_header_id);
                    bundle.putString("dr_num",delivery_number);
                    bundle.putString("dr_date",dateSelected);
                    bundle.putString("add_status",add_status);
//                    bundle.putString("color",color);
                    Dialog_weight_price fragment = new Dialog_weight_price();
                    fragment.setTargetFragment(SwineSales_add.this, 0);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    fragment.setArguments(bundle);
                    fragment.show(ft, "UploadDialogFragment");
                    fragment.setCancelable(true);

                }
            });

            // update
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setMessage("Are you sure you wan't to update?");
                    alertDialog.setPositiveButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.setNegativeButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    update_dr();
                                }
                            });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });

            payment_spinner.setEnabled(false);
            customer_spinner.setEnabled(false);
            cb_reciept.setEnabled(false);
            et_trucking.setEnabled(false);
            cb_trucking.setEnabled(false);
            truckers_spinner.setEnabled(false);
            trucking_expense_spinner.setEnabled(false);

            tv_dr_num.setText(delivery_number);
            tv_invoice.setText(invoice_number);
            tv_date.setText(dateSelected);
            et_trucking.setText(trucking_amount);
            et_discount.setText(discount);
            et_remarks.setText(remarks);

            // Populate customer Spinner
            List<String> customer_lables = new ArrayList<>();
            customer_lables.add(customerSelected_name);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, customer_lables);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
            customer_spinner.setAdapter(spinnerAdapter);



            if(paymentSelected.equals("C")){
                payment_type="CASH";
            }else if(paymentSelected.equals("H")){
                payment_type="CHARGE";
            }else{

            }

            // Populate payment Spinner
            List<String> payment_lables = new ArrayList<>();
            payment_lables.add(payment_type);
            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, payment_lables);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
            payment_spinner.setAdapter(paymentAdapter);

            // Populate trucker Spinner
            List<String> trucker_lables = new ArrayList<>();
            trucker_lables.add(truckersSelected_name);
            ArrayAdapter<String> truckerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, trucker_lables);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
            truckers_spinner.setAdapter(truckerAdapter);

            // Populate trucker Spinner
            List<String> trucking_expense_lables = new ArrayList<>();
            trucking_expense_lables.add(trucking_expenseSelected_name);
            ArrayAdapter<String> trucking_expenseAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, trucking_expense_lables);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
            trucking_expense_spinner.setAdapter(trucking_expenseAdapter);

            btn_save.setText("Save Changes");
        }
    }

    public void open_authentication(){
        Bundle bundle = new Bundle();
        bundle.putString("branch_id",branch_id);
        Dialog_Authenticate fragment = new Dialog_Authenticate();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(true);
    }


    @Override
    public void onDateSelected(String date) {
        dateSelected = date;
        tv_date.setText(dateSelected);
    }

    public void openDatePicker(boolean isMinusDays21,String max_date) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        Bundle bundle = new Bundle();
        bundle.putString("maxDate",max_date);
        bundle.putString("maxDate_minus", "");
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
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

    public void set_modal(String tittle, String text, String color){
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle );
        bundle.putString("text",text);
        bundle.putString("color",color);
        Modal_fragment fragment = new Modal_fragment();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(true);
    }

    @Override
    public void senddata(int okay) {

    }


    //       ~ ~ php functions ~ ~

    //for add button
    public void generate_invoice(final String company_id, final String company_code,final String get_type){
        btn_save.setEnabled(false);
        String URL = getString(R.string.URL_online)+"swine_sales/generate_invoice_number.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("0")){
                    generate_invoice(company_id, company_code, "NRD");
                    cb_reciept.setChecked(false);
                } else {
                    tv_invoice.setText(response);
                    invoice_number = response;
                    btn_save.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                    btn_save.setEnabled(true);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("get_type", get_type);
                hashMap.put("pay_type", paymentSelected);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_details(final String company_id, final String company_code,final String get_type){
        customer_list_spinner.clear();
        String URL = getString(R.string.URL_online)+"swine_sales/add_swine_sales_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject Object = new JSONObject(response);

                    // json dr number and current date
                    String dr_num;
                    JSONArray dr = Object.getJSONArray("data");
                    JSONObject dr_obj = (JSONObject) dr.get(0);
                    dr_num = dr_obj.getString("dr_num");
                    delivery_number = dr_num;
                    tv_dr_num.setText(delivery_number);

                    max_date = dr_obj.getString("current_date");
                    dateSelected = max_date;
                    tv_date.setText(dateSelected);

                    //json customers
                    String customer,customer_id;
                    customer_list_spinner.add(new Customers_model("Please Select",""));
                    JSONArray customers = Object.getJSONArray("customers");

                    for (int i = 0; i < customers.length(); i++) {
                        JSONObject customer_obj = (JSONObject) customers.get(i);
                        customer_id = customer_obj.getString("customer_id");
                        customer = customer_obj.getString("customer");
                        customer_list_spinner.add(new Customers_model(customer,customer_id));
                    }

                    // Populate customer Spinner
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < customer_list_spinner.size(); i++) {
                        lables.add(customer_list_spinner.get(i).getCustomer());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    customer_spinner.setAdapter(spinnerAdapter);

                    customer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Customers_model click = customer_list_spinner.get(position);
                            if (!click.getCustomer().equals("Please Select")){
                                customerSelected = String.valueOf(click.getCustomer_id());
                                customerSelected_name = String.valueOf(click.getCustomer());
                            } else {

                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    // Populate payment type Spinner
                    final List<String> payment_array = new ArrayList<>();
                    payment_array.add("Please Select");
                    payment_array.add("Cash");
                    payment_array.add("Charge");

                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, payment_array);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    payment_spinner.setAdapter(paymentAdapter);

                    payment_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                            if ( !payment_array.get(position).equals("Please Select")){
                                if(payment_array.get(position).equals("Cash")){
                                    paymentSelected = "C";
                                    generate_invoice(company_id,company_code,"inv_num");

                                }else{
                                    paymentSelected = "H";
                                    generate_invoice(company_id,company_code,"inv_num");

                                }
                                cb_reciept.setClickable(true);
                                cb_reciept.setChecked(true);

                                cb_reciept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                    {
                                        if ( isChecked )
                                        {
                                            // perform logic
                                            generate_invoice(company_id,company_code,"inv_num");
                                        }else{
                                            generate_invoice(company_id,company_code,"NRD");
                                        }

                                    }
                                });

                            } else {
                                cb_reciept.setOnCheckedChangeListener(null);
                                cb_reciept.setClickable(false);
                                cb_reciept.setChecked(false);

                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });



                    //   json truckers spinner
                    String trucker,trucker_id;
                    truckers_list_spinner.add(new Truckers_model("","Please Select"));
                    JSONArray truckers = Object.getJSONArray("truckers");

                    for (int i = 0; i < truckers.length(); i++) {
                        JSONObject trucker_obj = (JSONObject) truckers.get(i);
                        trucker_id = trucker_obj.getString("trucker_id");
                        trucker = trucker_obj.getString("trucker");
                        truckers_list_spinner.add(new Truckers_model(trucker_id,trucker));
                    }

                    //populate truckers spinner
                    List<String> truckers_labels = new ArrayList<>();
                    for (int i = 0; i < truckers_list_spinner.size(); i++) {
                        truckers_labels.add(truckers_list_spinner.get(i).getTrucker());
                    }

                    ArrayAdapter<String> truckingAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, truckers_labels);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    truckers_spinner.setAdapter(truckingAdapter);

                    truckers_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Truckers_model click = truckers_list_spinner.get(position);
                            if (!click.getTrucker().equals("Please Select")){
                                truckersSelected = String.valueOf(click.getTrucker_id());
                                truckersSelected_name  = String.valueOf(click.getTrucker());
                            } else {

                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    //json trucking expense
                    String trucking_expense,trucking_expense_id;
                    trucking_expense_list_spinner.add(new Trucking_expense_model("Please Select","",""));
                    JSONArray trucking_expenses = Object.getJSONArray("trucking_expenses");

                    for (int i = 0; i < trucking_expenses.length(); i++) {
                        JSONObject trucking_expenses_obj = (JSONObject) trucking_expenses.get(i);
                        trucking_expense_id = trucking_expenses_obj.getString("Trucking_expense_id");
                        trucking_expense = trucking_expenses_obj.getString("Trucking_expense");

                        if(trucking_expense.equals("Main Accounts")||trucking_expense.equals("Sub Accounts")){
                            trucking_expense_list_spinner.add(new Trucking_expense_model(trucking_expense,trucking_expense_id,"1"));
                        }else{
                            trucking_expense_list_spinner.add(new Trucking_expense_model(trucking_expense,trucking_expense_id,"0"));
                        }



                    }
                    //populate trucking expense
                    List<String> trucking_expense_labels = new ArrayList<>();
                    for (int i = 0; i < trucking_expense_list_spinner.size(); i++) {
                        trucking_expense_labels.add(trucking_expense_list_spinner.get(i).getTrucking_expense());
                    }
                    // ArrayAdapter<String> trucking_expense_Adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, trucking_expense_labels);
                    ArrayAdapter<String> trucking_expense_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, trucking_expense_labels) {
                        // disabled click
                        @Override
                        public boolean isEnabled(int position) {
                            if (trucking_expense_list_spinner.get(position).getStatus().equals("1")) {
                                return false;
                            }
                            return true;
                        }


                    };

                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    trucking_expense_spinner.setAdapter(trucking_expense_Adapter);
                    trucking_expense_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {



                            Trucking_expense_model click = trucking_expense_list_spinner.get(position);
                            if (!click.getTrucking_expense().equals("Please Select")){

                                //   Toast.makeText(getActivity(), click.getTrucking_expense(), Toast.LENGTH_SHORT).show();
                                trucking_expenseSelected =  String.valueOf(click.getTrucking_expense_id());
                                trucking_expenseSelected_name =  String.valueOf(click.getTrucking_expense());
                            } else {

                            }

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                }
                catch (JSONException e) {}
                catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();


                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("get_type", get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void senddata_auth(String data) {
        if(data.equals("okay")){
            Toast.makeText(getActivity(), "User granted", Toast.LENGTH_SHORT).show();
            et_discount.setEnabled(true);
        }else{
            cb_discount.setChecked(false);
        }
    }

    public void save_dr(){
        showLoading(loadingScan, "Saving...").show();
        btn_save.setEnabled(false);
        //get text
        remarks = et_remarks.getText().toString();
        trucking_amount = et_trucking.getText().toString();
        discount = et_discount.getText().toString();

        if (delivery_number.equals("") || dateSelected.equals("") ||
                customerSelected.equals("") || paymentSelected.equals("") ||
                invoice_number.equals("")) {
            Toast.makeText(getActivity(), "Please fill up required fields", Toast.LENGTH_SHORT).show();
            btn_save.setEnabled(true);
            showLoading(loadingScan, null).dismiss();

        }else{

            if(tr_status.equals("No")){
                String URL = getString(R.string.URL_online)+"swine_sales/add_dr.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            showLoading(loadingScan, null).dismiss();
                            String status="";
                            JSONObject Object = new JSONObject(response);
                            JSONArray diag = Object.getJSONArray("data");
                            JSONObject cusObj = (JSONObject) diag.get(0);
                            status =  cusObj.getString("status");

                            if(status.equals("1")){
                                init_add_edit("edit");
                                add_status="yes";
                                dr_header_id = cusObj.getString("dr_header_id");
                                btn_save.setEnabled(true);

                                set_modal("System Message","Swine delivery added","green");
                            }else{
                                btn_save.setEnabled(true);
                                set_modal("System Message","Swine delivery insert failed","red");
                            }

                        }catch (Exception e){}

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                            btn_save.setEnabled(true);
                            showLoading(loadingScan, null).dismiss();
                        } catch (Exception e){}
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("company_id", company_id);
                        hashMap.put("company_code", company_code);
                        hashMap.put("branch_id", branch_id);
                        hashMap.put("user_id", user_id);

                        hashMap.put("delivery_number", delivery_number);
                        hashMap.put("dr_date", dateSelected);
                        hashMap.put("customer_id", customerSelected);
                        hashMap.put("pay_type", paymentSelected);
                        hashMap.put("remarks", remarks);
                        hashMap.put("invoice_number", invoice_number);
                        hashMap.put("trucking_amount", trucking_amount);
                        hashMap.put("trucking_account", truckersSelected);
                        hashMap.put("trucking_accountExpense",trucking_expenseSelected);
                        hashMap.put("tr_status",tr_status);
                        hashMap.put("a_discount",discount);
                        return hashMap;
                    }
                };
                AppController.getInstance().setVolleyDuration(stringRequest);
                AppController.getInstance().addToRequestQueue(stringRequest);
            }else if(tr_status.equals("Yes")){
                btn_save.setEnabled(true);

                if(truckersSelected.equals("")||trucking_amount.equals("")||trucking_expenseSelected.equals("")){
                    Toast.makeText(getActivity(), "Please fill up required fields", Toast.LENGTH_SHORT).show();
                }else{
                    String URL = getString(R.string.URL_online)+"swine_sales/add_dr.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try{
                                showLoading(loadingScan, null).dismiss();
                                String status="";
                                JSONObject Object = new JSONObject(response);
                                JSONArray diag = Object.getJSONArray("data");
                                JSONObject cusObj = (JSONObject) diag.get(0);
                                status =  cusObj.getString("status");

                                if(status.equals("1")){
                                    add_status="yes";
                                    init_add_edit("edit");
                                    dr_header_id = cusObj.getString("dr_header_id");
                                    btn_save.setEnabled(true);
                                    set_modal("System Message","Swine delivery added","green");
                                }else{
                                    btn_save.setEnabled(true);
                                    set_modal("System Message","Swine delivery insert failed","red");
                                }
                            }catch (Exception e){

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                btn_save.setEnabled(true);
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
                            hashMap.put("branch_id", branch_id);
                            hashMap.put("user_id", user_id);

                            hashMap.put("delivery_number", delivery_number);
                            hashMap.put("dr_date", dateSelected);
                            hashMap.put("customer_id", customerSelected);
                            hashMap.put("pay_type", paymentSelected);
                            hashMap.put("remarks", remarks);
                            hashMap.put("invoice_number", invoice_number);
                            hashMap.put("trucking_amount", trucking_amount);
                            hashMap.put("trucking_account", truckersSelected);
                            hashMap.put("trucking_accountExpense",trucking_expenseSelected);
                            hashMap.put("tr_status",tr_status);
                            hashMap.put("a_discount",discount);
                            return hashMap;
                        }
                    };
                    AppController.getInstance().setVolleyDuration(stringRequest);
                    AppController.getInstance().addToRequestQueue(stringRequest);
                }
            }
        }
    }

    public void update_dr(){
        discount = et_discount.getText().toString();
        remarks = et_remarks.getText().toString();
        btn_save.setEnabled(false);
        showLoading(loadingScan, "Updating...").show();
        String URL = getString(R.string.URL_online)+"swine_sales/update_dr.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    showLoading(loadingScan, null).dismiss();
                    if(response.equals("1")){
                        btn_save.setEnabled(true);
                        set_modal("System Message","Swine delivery updated","green");

                    }else{
                        btn_save.setEnabled(true);
                        set_modal("System Message","Swine delivery insert failed","red");
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).dismiss();
                    Toast.makeText(getActivity(), getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
                    btn_save.setEnabled(true);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("user_id", user_id);

                hashMap.put("pay_type", paymentSelected);
                hashMap.put("dr_date", dateSelected);
                hashMap.put("a_discount",discount);
                hashMap.put("remarks", remarks);
                hashMap.put("invoice_number", invoice_number);
                hashMap.put("dr_header_id", dr_header_id);

                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void delete_dr(final String id, final int position){
        showLoading(loadingScan, "Deleting...").show();
        String URL = getString(R.string.URL_online)+"swine_sales/deleteDR.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    showLoading(loadingScan, null).dismiss();
                    if (response.equals("1")){

                        for (int i=0; i<viewDetails_models.size(); i++){
                            viewDetails_model r = viewDetails_models.get(i);
                            if (r.getId().equals(id)){
                                viewDetails_models.remove(position);
                                swineSales_add_adapter.notifyDataSetChanged();
                            }
                        }

                        Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Query failed to delete", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading(loadingScan, null).dismiss();
                Toast.makeText(getActivity(), getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("id", id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    void dialogBox_cofirmation(final boolean isAdd){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(isAdd ? "Are you sure you wan't to add?" : "Are you sure you wan't to finish?");
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        //add_to_delivery();
                        if (isAdd){
                            save_dr();
                        } else {
                            finish_dr();
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void finish_dr(){
        showLoading(loadingScan, "loading...").show();
        btn_finish.setEnabled(false);
        String URL = getString(R.string.URL_online)+"swine_sales/finishDelivery.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //dialogBox(response);
                    btn_finish.setEnabled(true);
                    showLoading(loadingScan, null).dismiss();

                    if (response.equals("1")) {
                        Intent intent = new Intent(getContext(), SwineSales_scan.class);
                        intent.putExtra("branch_id", branch_id);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                        getFragmentManager().popBackStack();
                        Toast.makeText(getActivity(), "Successfully finished Delivery. ", Toast.LENGTH_SHORT).show();

                    }else if(response.equals("2")){
                        Toast.makeText(getActivity(), "No delivery details found", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(getActivity(), "Query failed", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading(loadingScan, null).dismiss();
                btn_finish.setEnabled(true);
                Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", branch_id);
                hashMap.put("company_id", company_id);
                hashMap.put("delivery_id", delivery_number);
                hashMap.put("pay_type", paymentSelected);
                hashMap.put("dr_date", dateSelected);
                hashMap.put("user_id", user_id);
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void passData(int position, String id) {
        delete_dr(id, position);
    }

    @Override
    public void senddata_weight_price(String ave_weight, String ave_price,String yes_no) {

        Bundle bundle = new Bundle();

        if(ave_weight.equals("")){
            bundle.putString("ave_weight","0");
        }else{
            bundle.putString("ave_weight",ave_weight);
        }
        if(ave_price.equals("")){
            bundle.putString("ave_price","0");
        }else{
            bundle.putString("ave_price",ave_price);
        }

        bundle.putString("yes_no",yes_no);
        bundle.putString("branch_id",branch_id);
        bundle.putString("dr_header_id",dr_header_id);
        bundle.putString("dr_date",dateSelected);
        bundle.putString("dr_num",delivery_number);

        Fragment fragment = new SwineSales_scan();
        fragment.setTargetFragment(SwineSales_add.this, 201);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,0);
        fragmentTransaction.add(R.id.container, fragment, "Main_menu").addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode==201){
                int refresh = data.getIntExtra("refresh", 0);

                if(refresh==1){
                    getBottomTable();
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        intent_frag();
    }

    public void intent_frag(){
        Intent intent = new Intent(getContext(), SwineSales_add.class);
        intent.putExtra("branch_id", branch_id);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        // getFragmentManager().popBackStack();
    }

}
