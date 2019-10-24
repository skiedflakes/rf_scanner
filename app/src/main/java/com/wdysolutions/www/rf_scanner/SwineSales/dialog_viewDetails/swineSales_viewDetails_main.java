package com.wdysolutions.www.rf_scanner.SwineSales.dialog_viewDetails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply.Med_Vac_locate_main;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication.Medication_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.Dialog_Authenticate;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_adapter;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_add;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_main;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class swineSales_viewDetails_main extends DialogFragment implements Dialog_Authenticate.uploadDialogInterface {

    RecyclerView recyclerView;
    ArrayList<viewDetails_model> viewDetails_models = new ArrayList<>();
    TextView tv_total;
    ScrollView scrollView;
    ProgressBar loading;
    String delivery_number, branch_id, company_id, company_code;
    refreshData refreshData;


    public interface refreshData{
        void refresh();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swine_sales_viewdetails_main, container, false);

        String id = getArguments().getString("id");
        branch_id = getArguments().getString("branch_id");
        company_id = getArguments().getString("company_id");
        company_code = getArguments().getString("company_code");
        String discount = getArguments().getString("discount");
        delivery_number = getArguments().getString("delivery_number");
        String invoice = getArguments().getString("invoice");
        String date = getArguments().getString("date");
        String customer = getArguments().getString("customer");
        String remarks = getArguments().getString("remarks");
        String acount = getArguments().getString("tr_swine");
        String amount = getArguments().getString("tr_swine_a");
        String expense = getArguments().getString("tr_swine_e");
        String payment_type = getArguments().getString("payment_type");
        String status = getArguments().getString("status");
        refreshData = (refreshData)getTargetFragment();

        TextView tv_dr_num = view.findViewById(R.id.tv_dr_num);
        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_customer = view.findViewById(R.id.tv_customer);
        TextView tv_payment = view.findViewById(R.id.tv_payment);
        TextView tv_invoice = view.findViewById(R.id.tv_invoice);
        TextView tv_discount = view.findViewById(R.id.tv_discount);
        TextView tv_trucker = view.findViewById(R.id.tv_trucker);
        TextView tv_tracking_expense = view.findViewById(R.id.tv_tracking_expense);
        TextView tv_tracking_amount = view.findViewById(R.id.tv_tracking_amount);
        TextView tv_remarks = view.findViewById(R.id.tv_remarks);
        recyclerView = view.findViewById(R.id.recyclerView);
        tv_total = view.findViewById(R.id.tv_total);
        scrollView = view.findViewById(R.id.scrollView);
        loading = view.findViewById(R.id.loading);

        tv_customer.setText(customer);
        tv_dr_num.setText(delivery_number);
        tv_date.setText(date);
        tv_payment.setText(payment_type);
        tv_invoice.setText(invoice);
        tv_remarks.setText(remarks);
        tv_discount.setText(discount);
        tv_trucker.setText(acount);
        tv_tracking_expense.setText(expense);
        tv_tracking_amount.setText(amount);

        Button btn_cancel = view.findViewById(R.id.cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_authentication(branch_id);
            }
        });

        getDeliverDetails(delivery_number, company_code, company_id, branch_id);

        if(status.equals("finish")){

        }else{
            btn_cancel.setVisibility(View.GONE);
        }
        return view;
    }

    public void open_authentication(String branch_id){
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

    private void setLoading(boolean isLoadSuccess){
        if (isLoadSuccess){
            loading.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
    }

    public void getDeliverDetails(final String id, final String company_code, final String company_id, final String selectedBranch){
        setLoading(false);
        String URL = getString(R.string.URL_online)+"swine_sales/deliveryDetails_swine.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    //dialogBox(response);
                    setLoading(true);

                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("data");

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

                    viewDetails_adapter viewDetails_adapter = new viewDetails_adapter(getContext(), viewDetails_models);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(viewDetails_adapter);
                    recyclerView.setNestedScrollingEnabled(false);

                }
                catch (JSONException e){}
                catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    setLoading(true);
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("delivery_id", id);
                hashMap.put("branch", selectedBranch);
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void cancelDelivery(final String id, final String company_code, final String company_id, final String selectedBranch){
        String URL = getString(R.string.URL_online)+"swine_sales/cancelDelivery.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.equals("1")){
                        refreshData.refresh();
                        dismiss();
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.equals("2")){
                        Toast.makeText(getActivity(), "Unable to cancel. Delivery quantity is greater than your inventory quantity!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Query failed", Toast.LENGTH_SHORT).show();
                    }
                }
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
                hashMap.put("delivery_number", id);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void senddata_auth(String data) {
        if(data.equals("okay")){
            dialogBox_msg("Are you sure you want to cancel sales?");
        }
    }

    void dialogBox_msg(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        cancelDelivery(delivery_number, company_code, company_id, branch_id);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
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
