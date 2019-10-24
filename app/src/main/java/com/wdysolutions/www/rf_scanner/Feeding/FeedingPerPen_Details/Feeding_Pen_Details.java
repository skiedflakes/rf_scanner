package com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Feeding.resultListener;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Feeding_Pen_Details extends DialogFragment {

    String company_code, company_id, user_id, getPen_name, getBuilding_name, getId, selectedBranch, category_id;
    RecyclerView recyclerView;
    TextView txt_pen, txt_product;
    LinearLayout layout_main;
    ProgressBar progressBar3;
    Button btn_delete;
    resultListener refreshLayout;
    ProgressDialog loadingScan;
    CheckBox checkBox;

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeding_pen_details, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        refreshLayout = (resultListener) getTargetFragment();

        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        final String getProduct_name = getArguments().getString("getProduct_name");
        getPen_name = getArguments().getString("getPen_name");
        getBuilding_name = getArguments().getString("getBuilding_name");
        selectedBranch = getArguments().getString("selectedBranch");
        getId = getArguments().getString("getId");

        progressBar3 = view.findViewById(R.id.progressBar3);
        layout_main = view.findViewById(R.id.layout_main);
        txt_pen = view.findViewById(R.id.txt_pen);
        txt_product = view.findViewById(R.id.txt_product);
        recyclerView = view.findViewById(R.id.recyclerView);
        btn_delete = view.findViewById(R.id.btn_delete);
        checkBox = view.findViewById(R.id.checkBox);

        txt_pen.setText(getBuilding_name+" ("+getPen_name+")");
        txt_product.setText(getProduct_name);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    checkAllBoxToggle("1");
                } else {
                    checkAllBoxToggle("0");
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfCheckboxSelected()){
                    dialogBox_permission();
                } else {
                    Toast.makeText(getActivity(), "Please select to delete.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getActivity().registerReceiver(refresh, new IntentFilter("refresh_data"));
        getFeedingPenDetails();
        return view;
    }

    BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLayout.listener();
            dismiss();
        }
    };

    Feeding_Pen_adapter feeding_pen_adapter;
    ArrayList<Feeding_Pen_model> feeding_pen_models;
    private void getFeedingPenDetails(){
        progressBar3.setVisibility(View.VISIBLE);
        layout_main.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"feeding/feeding_per_pen.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar3.setVisibility(View.GONE);
                    layout_main.setVisibility(View.VISIBLE);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    feeding_pen_models = new ArrayList<>();
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);

                        feeding_pen_models.add(new Feeding_Pen_model(jsonObject1.getString("feeding_id"),
                                jsonObject1.getString("ear_tag"),
                                jsonObject1.getString("quantity"),
                                jsonObject1.getString("btn_status"),
                                jsonObject1.getString("swine_id"),
                                "0"));
                    }

                    feeding_pen_adapter = new Feeding_Pen_adapter(getActivity(), feeding_pen_models);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(feeding_pen_adapter);
                    recyclerView.setNestedScrollingEnabled(false);

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
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("id", getId);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void checkAllBoxToggle(String checkbox){
        for (int i=0; i<feeding_pen_models.size(); i++){
            Feeding_Pen_model model = feeding_pen_models.get(i);
            feeding_pen_models.set(i, new Feeding_Pen_model(model.getFeeding_id(),
                    model.getEar_tag(),
                    model.getQuantity(),
                    model.getBtn_status(),
                    model.getSwine_id(),
                    checkbox));
        }
        feeding_pen_adapter.notifyDataSetChanged();
    }

    private boolean checkIfCheckboxSelected(){
        for (int i = 0; i<feeding_pen_models.size(); i++){
            if (feeding_pen_models.get(i).getCheck_status().equals("1")){
                return true;
            }
        }
        return false;
    }

    private void deleteFeeding(){
        showLoading(loadingScan, "Deleting").show();
        String URL = getString(R.string.URL_online)+"feeding/feeding_delete.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    showLoading(loadingScan, "").dismiss();
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("response");

                    String msg_status = "";
                    int countSuccess = 0;
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        if (status.equals("success")){
                            msg_status += "<font color='#50a44d'>"+msg+"</font><br>";
                            countSuccess++;
                        } else {
                            msg_status += "<font color='#ca2429'>"+msg+"</font><br>";
                        }
                    }

                    dialogBox_msg(msg_status);

                    if (countSuccess > 0){
                        dismiss();
                    }

                }
                catch (JSONException e){}
                catch (Exception e){}
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
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("category_id", category_id);
                hashMap.put("user_id", user_id);
                hashMap.put("aray_", new Gson().toJson(feeding_pen_models));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void dialogBox_permission(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        deleteFeeding();
                    }
                });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void dialogBox_msg(String msg){
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

}
