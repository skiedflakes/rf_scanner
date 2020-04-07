package com.wdysolutions.www.rf_scanner.ChangeNameTemp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.ChangeNameTemp.ChangeNameDialog.Change_temp_name_dialog;
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.MultiAction.TransferPen_main;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_adapter;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_branch;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_building;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_pen;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_pig;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Change_temp_name extends Fragment implements clickCallback {

    TextView tx_range;
    RecyclerView recyclerView;
    Change_temp_name_adapter change_temp_name_adapter;
    EditText txt_find;
    Spinner spinner_pen, spinner_branch, spinner_building;
    LinearLayout bg_branch, bg_building, bg_pen, layout_pig, layout_whole;
    ProgressBar loading_building, loading_pen;
    TextView txt_error_building, txt_error_pen, txt_empty;
    String company_code, company_id, category_id, user_id;
    ProgressBar loading_pigs, loading_whole;

    private void initMenu(final View view){
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
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
        Constant.power_level = "Max";
        tx_range.setText(Constant.power_level);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_temp_name, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        company_id = "135";

        layout_pig = view.findViewById(R.id.layout_pig);
        loading_pigs = view.findViewById(R.id.loading_pigs);
        tx_range = view.findViewById(R.id.tx_range);
        recyclerView = view.findViewById(R.id.recyclerView);
        txt_find = view.findViewById(R.id.txt_find);
        spinner_building = view.findViewById(R.id.spinner_building);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        loading_building = view.findViewById(R.id.loading_building);
        loading_pen = view.findViewById(R.id.loading_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        layout_whole = view.findViewById(R.id.layout_whole);
        loading_whole = view.findViewById(R.id.loading_whole);
        txt_empty = view.findViewById(R.id.txt_empty);

        txt_find.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                change_temp_name_adapter.getFilter().filter(editable);
            }
        });

        get_branch(company_id, company_code, "get_branch");
        initMenu(view);
        return view;
    }

    String selectedBranch="";
    ArrayList<Transfer_model_branch> transfer_model_branches = new ArrayList<>();
    public void get_branch(final String company_id, final String company_code, final String get_type){
        selectedBuilding = "";
        loading_whole.setVisibility(View.VISIBLE);
        layout_whole.setVisibility(View.GONE);
        String URL = "http://192.168.1.181/test_swine/pen_list.php";
        //String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    loading_whole.setVisibility(View.GONE);
                    layout_whole.setVisibility(View.VISIBLE);

                    transfer_model_branches.add(new Transfer_model_branch(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_branches.add(new Transfer_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_branches.size(); i++) {
                        lables.add(transfer_model_branches.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_branch click = transfer_model_branches.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedBranch = String.valueOf(click.getBranch_id());
                                get_building(company_id,company_code,"get_building",selectedBranch);
                                bg_branch.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBranch = "";
                                selectedBuilding = "";
                                selectedPen = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            spinner_pen.setAdapter(null);
                            spinner_building.setAdapter(null);
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
                    get_branch(company_id, company_code, "get_branch");
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

    String selectedBuilding="", selectedBuilding_name;
    ArrayList<Transfer_model_building> transfer_model_buildings;
    public void get_building(final String company_id, final String company_code, final String get_type,final String branch_id){
        selectedPen = "";
        bg_building.setBackgroundResource(R.drawable.bg_border_red);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        buildingLoading(true);
        String URL = "http://192.168.1.181/test_swine/pen_list.php";
        //String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    buildingLoading(false);

                    transfer_model_buildings = new ArrayList<>();
                    transfer_model_buildings.add(new Transfer_model_building("Please Select","0"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_buildings.add(new Transfer_model_building(cusObj.getString("building"),
                                cusObj.getString("building_id")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_buildings.size(); i++) {
                        lables.add(transfer_model_buildings.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_building click = transfer_model_buildings.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = click.getBuilding_id();
                                selectedBuilding_name = click.getBuilding();
                                get_pen(company_id,company_code,"get_pen",selectedBranch,selectedBuilding);
                                bg_building.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBuilding = "";
                                selectedPen = "";
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            spinner_pen.setAdapter(null);
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
                    loading_building.setVisibility(View.GONE);
                    txt_error_building.setText("Internet error, tap to refresh");
                    txt_error_building.setVisibility(View.VISIBLE);
                    txt_error_building.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_building(company_id,company_code,"get_building",selectedBranch);
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
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    String selectedPen="", selectedPen_name;
    ArrayList<Transfer_model_pen> transfer_model_pens;
    public void get_pen(final String company_id, final String company_code, final String get_type,final String branch_id,final String building_id){
        selectedPen = "";
        penLoading(true);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        String URL = "http://192.168.1.181/test_swine/pen_list.php";
        //String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    penLoading(false);

                    transfer_model_pens = new ArrayList<>();
                    transfer_model_pens.add(new Transfer_model_pen("0","Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_pen");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        transfer_model_pens.add(new Transfer_model_pen(cusObj.getString("pen_assignment_id"),
                                cusObj.getString("pen_name")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < transfer_model_pens.size(); i++) {
                        lables.add(transfer_model_pens.get(i).getPen_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Transfer_model_pen click = transfer_model_pens.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen = click.getPen_id();
                                selectedPen_name = click.getPen_name();
                                get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
                                bg_pen.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedPen = "";
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
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
                    loading_pen.setVisibility(View.GONE);
                    txt_error_pen.setVisibility(View.VISIBLE);
                    txt_error_pen.setText("Internet error, tap to refresh");
                    txt_error_pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            get_pen(company_id,company_code,"get_pen",selectedBranch,selectedBuilding);
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
                hashMap.put("building_id", building_id);
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    ArrayList<Change_temp_name_model> change_temp_name_models;
    public void get_pigs(final String company_id, final String company_code, final String get_type,final String branch_id,final String pen_code){
        loading_pigs.setVisibility(View.VISIBLE);
        layout_pig.setVisibility(View.GONE);
        txt_empty.setVisibility(View.GONE);
        String URL = "http://192.168.1.181/test_swine/pen_list.php";
        //String URL = getString(R.string.URL_online)+"transfer_pen/pen_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    //((ActivityMain)getActivity()).dialogBox(response);
                    loading_pigs.setVisibility(View.GONE);
                    change_temp_name_models = new ArrayList<>();

                    if(!response.equals("{\"response_swine\":[]}")){

                        layout_pig.setVisibility(View.VISIBLE);

                        JSONObject Object = new JSONObject(response);

                        JSONArray details = Object.getJSONArray("response_swine");
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);

                            change_temp_name_models.add(new Change_temp_name_model(r.getString("swine_id"),
                                    r.getString("swine_code")));
                        }

                    }else{
                        Toast.makeText(getActivity(), "No temp name", Toast.LENGTH_SHORT).show();
                        txt_empty.setVisibility(View.VISIBLE);
                        txt_empty.setText("No Temp name found");
                    }

                    change_temp_name_adapter = new Change_temp_name_adapter(getActivity(), change_temp_name_models, Change_temp_name.this);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(change_temp_name_adapter);

                }
                catch (JSONException e){}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    loading_pigs.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("get_type",get_type);
                hashMap.put("company_code", company_code);
                hashMap.put("pen_code", pen_code);
                hashMap.put("branch_id", branch_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
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

    @Override
    public void c_callback(String id, String swine_code) {
        openChangeNameDialog(id, swine_code);
    }

    private void openChangeNameDialog(String id, String swine_code){
        Bundle bundle = new Bundle();
        bundle.putString("swine_code", swine_code);
        bundle.putString("id", id);
        bundle.putString("company_id", company_id);
        bundle.putString("user_id", user_id);
        bundle.putString("category_id", category_id);
        DialogFragment fragment = new Change_temp_name_dialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog_");
        if (prev != null) {ft.remove(prev);}
        fragment.setArguments(bundle);
        fragment.show(ft, "dialog_");
        fragment.setCancelable(false);
    }
}
