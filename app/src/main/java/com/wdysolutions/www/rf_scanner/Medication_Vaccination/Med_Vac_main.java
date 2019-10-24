package com.wdysolutions.www.rf_scanner.Medication_Vaccination;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply.Med_Vac_locate_main;
import com.wdysolutions.www.rf_scanner.Medication_Vaccination.Dialog_apply.Med_Vac_model_product;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Med_Vac_main extends Fragment implements  Med_Vac_adapter.EventListener, Med_Vac_locate_main.dataDialog {

    LinearLayout layout_, loading_list, layout_error, loading_, empty_pen, bg_pen, bg_branch, bg_building;
    Spinner spinner_branch, spinner_building, spinner_pen;
    ProgressBar loading_building, loading_pen;
    RecyclerView rec_list_pigs;
    ArrayList<Med_Vac_model_branch> med_vac_model_branches = new ArrayList<>();
    ArrayList<Med_Vac_model_building> med_vac_model_buildings = new ArrayList<>();
    ArrayList<Med_Vac_model_pen> med_vac_model_pens = new ArrayList<>();
    ArrayList<Med_Vac_model_pig> med_vac_model_pigs = new ArrayList<>();
    ArrayList<Med_Vac_model_product> med_vac_model_products = new ArrayList<>();
    Med_Vac_adapter adapter_pig;
    String selectedBranch = "", selectedBuilding = "", selectPen = "", selectProduct ="", company_code, company_id, user_id;
    int count_scanned = 0;
    BroadcastReceiver epcReceiver;

    MenuItem max, min,low;
    TextView tx_range, txt_error_pen, txt_error_building;

    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        toolbar.inflateMenu(R.menu.menu_scan_eartag);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.max) {
                    Constant.power_level = "Max";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("max");
                    Toast.makeText(getActivity(), "Scan range set to Max", Toast.LENGTH_SHORT).show();
                }
                else if(menuItem.getItemId() == R.id.min) {
                    Constant.power_level = "Medium";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("med");
                    Toast.makeText(getActivity(), "Scan range set to Med", Toast.LENGTH_SHORT).show();
                }
                else if(menuItem.getItemId() == R.id.low) {
                    Constant.power_level = "Short";
                    tx_range.setText(Constant.power_level);
                    ((ActivityMain)getActivity()).setPower("short");
                    Toast.makeText(getActivity(), "Scan range set to Short", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // set default power level
        Constant.power_level = "Short";
        tx_range.setText(Constant.power_level);
        ((ActivityMain)getActivity()).setPower("short");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.med_vac_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        empty_pen = view.findViewById(R.id.empty_pen);
        layout_error = view.findViewById(R.id.layout_error);
        loading_ = view.findViewById(R.id.loading_);
        layout_ = view.findViewById(R.id.layout_);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        spinner_building = view.findViewById(R.id.spinner_building);
        loading_building = view.findViewById(R.id.loading_building);
        rec_list_pigs = view.findViewById(R.id.rec_list_pigs);
        loading_list = view.findViewById(R.id.loading_list);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        loading_pen = view.findViewById(R.id.loading_pen);
        tx_range = view.findViewById(R.id.tx_range);
        rec_list_pigs.addItemDecoration(new DividerItemDecoration(rec_list_pigs.getContext(), DividerItemDecoration.VERTICAL));

        get_branch(company_id,company_code,"get_branch");
        initMenu(view);
        return view;
    }

    private static String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString().replaceAll(" ","");
    }

    private void buildingLoading(boolean status){
        if (status){
            txt_error_building.setVisibility(View.GONE);
            spinner_building.setVisibility(View.GONE);
            loading_building.setVisibility(View.VISIBLE);
        } else {
            spinner_building.setVisibility(View.VISIBLE);
            loading_building.setVisibility(View.GONE);
        }
    }

    private void penLoading(boolean status){
        if (status){
            txt_error_pen.setVisibility(View.GONE);
            spinner_pen.setVisibility(View.GONE);
            loading_pen.setVisibility(View.VISIBLE);
        } else {
            spinner_pen.setVisibility(View.VISIBLE);
            loading_pen.setVisibility(View.GONE);
        }
    }

    private void listLoading(boolean status){
        if (status){
            loading_list.setVisibility(View.VISIBLE);
            rec_list_pigs.setVisibility(View.GONE);
        } else {
            loading_list.setVisibility(View.GONE);
            rec_list_pigs.setVisibility(View.VISIBLE);
        }
    }

//    private void get_action(){
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.action_array));
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_action.setAdapter(spinnerAdapter);
//        spinner_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                String selected = adapterView.getItemAtPosition(position).toString();
//                if (selected.equals("Please select")){
//                    selectedAction = "";
//                } else {
//                    if (selected.equals("Medication")){selectedAction = "M";}
//                    else if (selected.equals("Vaccination")){selectedAction = "V";}
//
//                    get_product("get_product");
//                }
//                spinner_product.setAdapter(null);
//                rec_list_pigs.setAdapter(null);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {}
//        });
//    }

    private void get_branch(final String company_id, final String company_code, final String get_type){
        layout_error.setVisibility(View.GONE);
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);
                    JSONObject Object = new JSONObject(response);

                    med_vac_model_branches.add(new Med_Vac_model_branch(0,"Please Select"));
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        med_vac_model_branches.add(new Med_Vac_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < med_vac_model_branches.size(); i++) {
                        lables.add(med_vac_model_branches.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Med_Vac_model_branch click = med_vac_model_branches.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedBranch = String.valueOf(click.getBranch_id());
                                get_building("get_building");
                                bg_branch.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBranch = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            rec_list_pigs.setAdapter(null);
                            spinner_building.setAdapter(null);
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
//                    loading_.setVisibility(View.GONE);
//                    layout_error.setVisibility(View.VISIBLE);
                    get_branch(company_id,company_code,"get_branch");
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
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    buildingLoading(false);
                    JSONObject Object = new JSONObject(response);
                    med_vac_model_buildings.clear();
                    med_vac_model_buildings.add(new Med_Vac_model_building(0,"Please Select"));
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        med_vac_model_buildings.add(new Med_Vac_model_building(cusObj.getInt("building_id"),
                                cusObj.getString("building")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < med_vac_model_buildings.size(); i++) {
                        lables.add(med_vac_model_buildings.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Med_Vac_model_building click = med_vac_model_buildings.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                get_pen("get_pen");
                                bg_building.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBuilding = "";
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            rec_list_pigs.setAdapter(null);
                            spinner_pen.setAdapter(null);
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
                    loading_building.setVisibility(View.GONE);
                    txt_error_building.setText("Internet error, tap to refresh");
                    txt_error_building.setVisibility(View.VISIBLE);
                    txt_error_building.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_building("get_building");
                        }
                    });
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
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    penLoading(false);
                    JSONObject Object = new JSONObject(response);
                    med_vac_model_pens.clear();
                    med_vac_model_pens.add(new Med_Vac_model_pen(0,"Please Select", "1"));
                    JSONArray diag = Object.getJSONArray("response_pen");

                    // add red first
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        if (cusObj.getString("status").equals("0")){
                            med_vac_model_pens.add(new Med_Vac_model_pen(cusObj.getInt("pen_assignment_id"),
                                    cusObj.getString("pen_name"), cusObj.getString("status")));
                        }
                    }

                    // then add not red
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        if (cusObj.getString("status").equals("1")){
                            med_vac_model_pens.add(new Med_Vac_model_pen(cusObj.getInt("pen_assignment_id"),
                                    cusObj.getString("pen_name"), cusObj.getString("status")));
                        }
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < med_vac_model_pens.size(); i++) {
                        lables.add(med_vac_model_pens.get(i).getPen_name());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, lables) {
                        // disabled click
                        @Override
                        public boolean isEnabled(int position) {
                            if (med_vac_model_pens.get(position).getRed_status().equals("1")) {
                                return false;
                            }
                            return true;
                        }

                        // Change color item
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View mView = super.getDropDownView(position, convertView, parent);
                            TextView mTextView = (TextView) mView;
                            if (med_vac_model_pens.get(position).getRed_status().equals("0")) {
                                mTextView.setTextColor(Color.RED);
                            } else if (med_vac_model_pens.get(position).getRed_status().equals("1")) {
                                mTextView.setTextColor(Color.GRAY);
                            }
                            return mView;
                        }
                    };


                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Med_Vac_model_pen click = med_vac_model_pens.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectPen = String.valueOf(click.getPen_assignment_id());
                                get_pigs("get_sched_pig");
                                bg_pen.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectPen = "";
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            rec_list_pigs.setAdapter(null);
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
                    loading_pen.setVisibility(View.GONE);
                    txt_error_pen.setVisibility(View.VISIBLE);
                    txt_error_pen.setText("Internet error, tap to refresh");
                    txt_error_pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_pen("get_pen");
                        }
                    });
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

    public void get_pigs(final String get_type){
        listLoading(true);
        empty_pen.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"medication_vaccination/med_vac_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    //((ActivityMain)getActivity()).dialogBox(response);
                    med_vac_model_pigs.clear();
                    if(!response.equals("{\"response_pig\":[]}")){
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response_pig");

                        // add red first
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);

                            if (r.getString("status").equals("0")){
                                med_vac_model_pigs.add(new Med_Vac_model_pig(r.getString("swine_id"),
                                        r.getString("swine_code"),
                                        r.getString("status")));
                            }
                        }

                        // then add not red
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);

                            if (r.getString("status").equals("1")){
                                med_vac_model_pigs.add(new Med_Vac_model_pig(r.getString("swine_id"),
                                        r.getString("swine_code"),
                                        r.getString("status")));
                            }
                        }

                        adapter_pig = new Med_Vac_adapter(getContext(), med_vac_model_pigs, Med_Vac_main.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        rec_list_pigs.setLayoutManager(layoutManager);
                        rec_list_pigs.setAdapter(adapter_pig);
                        rec_list_pigs.setNestedScrollingEnabled(false);
                        listLoading(false);

                    }else{
                        empty_pen.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    listLoading(false);
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("pen_id",selectPen);
                hashMap.put("product_id",selectProduct);
                hashMap.put("get_type",get_type);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onEvent(String swine_id, String swine_code, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("branch_id",selectedBranch);
        bundle.putString("pen_id",selectPen);
        bundle.putString("swine_id",swine_id);
        bundle.putString("swine_code",swine_code);
        bundle.putInt("position",position);
        Med_Vac_locate_main fragment = new Med_Vac_locate_main();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "dialog");
        fragment.setCancelable(true);
    }

    @Override
    public void senddata(String swine_id, int position) {

       try{
            JSONArray jsonArray = new JSONArray(new Gson().toJson(med_vac_model_pigs));
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject r = jsonArray.getJSONObject(i);
                String swine_id2 = r.getString("swine_id");
                String swine_code = r.getString("swine_code");

                if (swine_id2.equals(swine_id)){

                    med_vac_model_pigs.set(position, new Med_Vac_model_pig(swine_id2,
                            swine_code,
                            "1"));

                    adapter_pig.notifyItemChanged(position);
                }
            }
        } catch (JSONException e){}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((ActivityMain)getActivity()).setPower("max");
    }
}
