package com.wdysolutions.www.rf_scanner.WritterTag;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Modal_fragment;
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


public class WritterTag_main2 extends Fragment  implements WritterTag_adapter.EventListener, Modal_fragment.dialog_interface{

    Spinner spinner_pen,spinner_branch,spinner_building;
    String company_code, company_id, swine_scanned_id,selectedBranch,selectedBuilding,selectedPen,user_id;
    SessionPreferences sessionPreferences;
    ArrayList<WritterTag_model_branch> writter_pen_model_list_branch = new ArrayList<>();
    ArrayList<WritterTag_model_building> writter_pen_model_list_building = new ArrayList<>();
    ArrayList<WritterTag_model_pen> writter_pen_model_list_pen = new ArrayList<>();
    ArrayList<WritterTag_model_pig> writter_pen_model_list_pig = new ArrayList<>();

    WritterTag_adapter adapter_pig;
    RecyclerView rec_pigs;
    // Button btn_scan;
    BroadcastReceiver epcReceiver;
    LinearLayout layout_, loading_, layout_error, loading_list, bg_pen, bg_branch, bg_building, bg_scan_status;
    // test
    EditText et_test;
    ProgressBar loading_pen, loading_building;
    TextView tv_scaned_total,tv_nip;
    int count_scanned=0;
    int count_total_pigs=0;
    int count_nip;

    //write function
    String target_tag="",new_tag;

    int max = 8;
    int min = 2;
    int selected_swine=0;
    int selected_count=0;
    Boolean write_read = true;
    boolean isVolleyLoad = false;

    Button check_tag;
    MenuItem menu_max, menu_min,low;
    TextView tx_range, txt_error_building, txt_error_pen, txt_error;


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
        Constant.power_level = "Short";
        tx_range.setText(Constant.power_level);
        ((ActivityMain)getActivity()).setPower("short");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.writter_tag_main2, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);

        txt_error = view.findViewById(R.id.txt_error);
        bg_scan_status = view.findViewById(R.id.bg_scan_status);
        txt_error_pen = view.findViewById(R.id.txt_error_pen);
        txt_error_building = view.findViewById(R.id.txt_error_building);
        bg_pen = view.findViewById(R.id.bg_pen);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_building = view.findViewById(R.id.bg_building);
        layout_ = view.findViewById(R.id.layout_);
        loading_ = view.findViewById(R.id.loading_);
        layout_error = view.findViewById(R.id.layout_error);
        loading_list = view.findViewById(R.id.loading_list);
        rec_pigs = view.findViewById(R.id.rec_list_pigs);
        rec_pigs.addItemDecoration(new DividerItemDecoration(rec_pigs.getContext(), DividerItemDecoration.VERTICAL));
        tv_nip = view.findViewById(R.id.tv_nip);
        loading_pen = view.findViewById(R.id.loading_pen);
        loading_building = view.findViewById(R.id.loading_building);
        check_tag = view.findViewById(R.id.check_tag);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        spinner_pen = view.findViewById(R.id.spinner_pen);
        spinner_building = view.findViewById(R.id.spinner_building);
        tx_range = view.findViewById(R.id.tx_range);

        check_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isModalOpen=true;

                try {
                    if (epcReceiver != null) {
                        getActivity().unregisterReceiver(epcReceiver);
                    }
                }catch (Exception e){}

                Bundle bundle = new Bundle();
                bundle.putString("swine_id", "callback");
                bundle.putString("power_level", Constant.power_level);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RFscanner_main frag = new RFscanner_main();
                //This is required to communicate between two framents. Similar to startActivityForResult
                frag.setTargetFragment(WritterTag_main2.this, 199);
                frag.setArguments(bundle);
                fragmentTransaction.add(R.id.container, frag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        layout_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_branch(company_id,company_code,"get_branch");
            }
        });

        get_branch(company_id,company_code,"get_branch");
        initMenu(view);
        return view;
    }

    private void scan_status(){
        if (!selectedBranch.equals("") && !selectedBuilding.equals("") && !selectedPen.equals("") && getPigsStatus.equals("1")){
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_green);
        } else {
            bg_scan_status.setBackgroundResource(R.drawable.bg_circle_red);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
    }

    public String set_new_tag(){
        new_tag = "wdy-"+String.valueOf(selected_swine);
        int max2 = 12-getCount(new_tag);

        for(int i=0;i<max2;i++){
            new_tag = new_tag+" ";
        }
        new_tag = asciiToHex(new_tag);
        return new_tag;
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
            rec_pigs.setVisibility(View.GONE);
        } else {
            loading_list.setVisibility(View.GONE);
            rec_pigs.setVisibility(View.VISIBLE);
        }
    }

    public void get_branch(final String company_id, final String company_code, final String get_type){
        layout_error.setVisibility(View.GONE);
        layout_.setVisibility(View.GONE);
        loading_.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"writter_tag/writter_pen_details2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    layout_.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);

                    writter_pen_model_list_branch.add(new WritterTag_model_branch(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_branch");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        writter_pen_model_list_branch.add(new WritterTag_model_branch(cusObj.getInt("branch_id"),
                                cusObj.getString("branch")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < writter_pen_model_list_branch.size(); i++) {
                        lables.add(writter_pen_model_list_branch.get(i).getBranch());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_branch.setAdapter(spinnerAdapter);
                    spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            WritterTag_model_branch click = writter_pen_model_list_branch.get(position);
                            if (!click.getBranch().equals("Please Select")){
                                selectedBranch = String.valueOf(click.getBranch_id());
                                get_building(company_id,company_code,"get_building",selectedBranch);
                                bg_branch.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBranch = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
                            spinner_building.setAdapter(null);
                            rec_pigs.setAdapter(null);
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

    public void get_building(final String company_id, final String company_code, final String get_type,final String branch_id){
        selectedBuilding = "";
        bg_building.setBackgroundResource(R.drawable.bg_border_red);
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        buildingLoading(true);
        String URL = getString(R.string.URL_online)+"writter_tag/writter_pen_details2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    buildingLoading(false);
                    writter_pen_model_list_building = new ArrayList<>();
                    writter_pen_model_list_building.add(new WritterTag_model_building(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_building");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        writter_pen_model_list_building.add(new WritterTag_model_building(cusObj.getInt("building_id"),
                                cusObj.getString("building")));
                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < writter_pen_model_list_building.size(); i++) {
                        lables.add(writter_pen_model_list_building.get(i).getBuilding());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_building.setAdapter(spinnerAdapter);
                    spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            WritterTag_model_building click = writter_pen_model_list_building.get(position);
                            if (!click.getBuilding().equals("Please Select")){
                                selectedBuilding = String.valueOf(click.getBuilding_id());
                                get_pen(company_id,company_code,"get_pen",selectedBranch,selectedBuilding);
                                bg_building.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBuilding = "";
                                bg_building.setBackgroundResource(R.drawable.bg_border_red);
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
                            spinner_pen.setAdapter(null);
                            rec_pigs.setAdapter(null);
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
                    scan_status();
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

    public void get_pen(final String company_id, final String company_code, final String get_type,final String branch_id,final String building_id){
        selectedPen = "";
        bg_pen.setBackgroundResource(R.drawable.bg_border_red);
        penLoading(true);
        writter_pen_model_list_pen = new ArrayList<>();
        spinner_pen.setAdapter(null);
        count_scanned=0;
        count_total_pigs=0;
        String URL = getString(R.string.URL_online)+"writter_tag/writter_pen_details2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    penLoading(false);
                    writter_pen_model_list_pen.add(new WritterTag_model_pen(0,"Please Select"));
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("response_pen");

                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        writter_pen_model_list_pen.add(new WritterTag_model_pen(cusObj.getInt("pen_assignment_id"),
                                cusObj.getString("pen_name")));

                    }

                    // Populate Spinner branch
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < writter_pen_model_list_pen.size(); i++) {
                        lables.add(writter_pen_model_list_pen.get(i).getPen_name());
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables);
                    spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_pen.setAdapter(spinnerAdapter);
                    spinner_pen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            WritterTag_model_pen click = writter_pen_model_list_pen.get(position);
                            if (!click.getPen_name().equals("Please Select")){
                                selectedPen = String.valueOf(click.getPen_assignment_id());

                                try{
                                    get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
                                    //  test(company_id,company_code,"get_swine",selectedBranch,selectedPen);
                                }catch (Exception e){}
                                bg_pen.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedPen = "";
                                bg_pen.setBackgroundResource(R.drawable.bg_border_red);
                            }
                            scan_status();
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
                    scan_status();
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


    String getPigsStatus = "";
    public void get_pigs(final String company_id, final String company_code, final String get_type,final String branch_id,final String pen_code){
        getPigsStatus = "";
        scan_status();
        tv_nip.setText("");
        selected_swine = 0;
        rec_pigs.setAdapter(null);
        listLoading(true);
        writter_pen_model_list_pig = new ArrayList<>();
        count_scanned=0;
        count_total_pigs=0;
        txt_error.setVisibility(View.GONE);
        txt_error.setTextColor(getResources().getColor(R.color.color_text_light_black));
        String URL = getString(R.string.URL_online)+"writter_tag/writter_pen_details2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    getPigsStatus = "1";
                    scan_status();
                    listLoading(false);

                    if(!response.equals("{\"response_swine\":[]}")){
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response_swine");

                        for(int i = 0; i < details.length(); i++){

                            JSONObject r = details.getJSONObject(i);
                            writter_pen_model_list_pig.add(new WritterTag_model_pig(r.getInt("swine_id"),
                                    r.getString("swine_code"),0, r.getInt("tag_counter")));
                        }
                        
                        //counter set text of total count of pigs inside the pen
                        count_total_pigs = details.length();
                        String total  = String.valueOf(count_scanned)+"/"+String.valueOf(count_total_pigs);
                        if(count_total_pigs>0){

                        }
                        adapter_pig = new WritterTag_adapter(getContext(), writter_pen_model_list_pig, WritterTag_main2.this);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                        rec_pigs.setLayoutManager(layoutManager);
                        rec_pigs.setAdapter(adapter_pig);
                        //btn_scan.setVisibility(View.VISIBLE);
                        rec_pigs.scrollToPosition(writter_pen_model_list_pig.size()-1);

                        init_epc();

                    } else {
                        rec_pigs.setVisibility(View.GONE);
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("Pen is empty");
                        txt_error.setTextColor(getResources().getColor(R.color.color_red));
                    }
                }
                catch (JSONException e){}
                catch (Exception e){}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    getPigsStatus = "";
                    scan_status();
                    listLoading(false);
                    txt_error.setVisibility(View.VISIBLE);
                    txt_error.setText(getResources().getString(R.string.volley_error));
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

    String temp_swine_id,selected_ear_tag;
    int temp_pos,selected_pos;

    @Override
    public void onEvent(int position,int swine_id,String ear_tag,int tag_counter) {
        try{
            JSONArray jsonArray = new JSONArray(new Gson().toJson(writter_pen_model_list_pig));
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject r = jsonArray.getJSONObject(i);
                String swine_code = r.getString("swine_code");
                int tag_count = r.getInt("tag_counter");
                int check_status = r.getInt("check_status");
                if(check_status==1){
                    writter_pen_model_list_pig.set(i,new WritterTag_model_pig(r.getInt("swine_id"),swine_code,0,tag_count));
                }else {
                    writter_pen_model_list_pig.set(i,new WritterTag_model_pig(r.getInt("swine_id"),swine_code,check_status,tag_count));
                }
            }
        }
        catch (Exception e){}
        selected_swine = swine_id;
        selected_count=tag_counter;
        selected_ear_tag = ear_tag;
        selected_pos = position;
        tv_nip.setText(ear_tag);

        writter_pen_model_list_pig.set(position,new WritterTag_model_pig(swine_id,ear_tag,1,tag_counter));
        adapter_pig.notifyDataSetChanged();
    }

    private static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
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

    public static int getCount(String number) {
        int flag = 0;
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                flag++;
            }
        }
        return flag;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            ((ActivityMain)getActivity()).setPower("max");
            if (epcReceiver != null) { getActivity().unregisterReceiver(epcReceiver); }
        }catch (Exception e){}
    }

    public void set_modal(String tittle, String text, String color){
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("text",text);
        bundle.putString("color",color);
        Modal_fragment fragment = new Modal_fragment();
        fragment.setTargetFragment(this, 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundle);
        fragment.show(ft, "UploadDialogFragment");
        fragment.setCancelable(false);
        isModalOpen = true;
    }

    @Override
    public void senddata(int okay) {

        if(okay==1){

            //selected_swine update single data counter add
            try{
                JSONArray jsonArray = new JSONArray(new Gson().toJson(writter_pen_model_list_pig));
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject r = jsonArray.getJSONObject(i);
                    String swine_code = r.getString("swine_code");
                    int tag_count = r.getInt("tag_counter");
                    int check_status = r.getInt("check_status");
                    writter_pen_model_list_pig.set(i,new WritterTag_model_pig(r.getInt("swine_id"),swine_code,check_status,tag_count));
                }

                selected_count = selected_count+1;

                writter_pen_model_list_pig.set(selected_pos,new WritterTag_model_pig(selected_swine,selected_ear_tag,2,selected_count));
                adapter_pig.notifyDataSetChanged();

            }catch (Exception e){}

        }
        // red
        else {

            if (selected_swine > 0){
                //selected_swine update single data counter add
                try{
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(writter_pen_model_list_pig));
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject r = jsonArray.getJSONObject(i);
                        String swine_code = r.getString("swine_code");
                        int tag_count = r.getInt("tag_counter");
                        int check_status = r.getInt("check_status");
                        writter_pen_model_list_pig.set(i,new WritterTag_model_pig(r.getInt("swine_id"),swine_code,check_status,tag_count));
                    }

                    selected_count = selected_count+1;

                    writter_pen_model_list_pig.set(selected_pos,new WritterTag_model_pig(selected_swine,selected_ear_tag,0,selected_count));
                    adapter_pig.notifyDataSetChanged();

                }catch (Exception e){}
            }
        }

        isVolleyLoad = false;
        isModalOpen = false;
        selected_swine = 0;
        selected_ear_tag = "";
        tv_nip.setText(selected_ear_tag);

        // get_pigs(company_id,company_code,"get_swine",selectedBranch,selectedPen);
        //set true
    }

    public void update_counter(final String target_tag,final String write_tag,final String counter){
        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
        String URL = getString(R.string.URL_online)+"writter_tag/update_counter.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                init_epc();

                if(response.equals("1")){

                    ((ActivityMain) getActivity()).write_tag(target_tag, set_new_tag(), max, min);

                }else{
                    Toast.makeText(getActivity(), "Error database connection", Toast.LENGTH_SHORT).show();
                    loading_.setVisibility(View.GONE);
                    layout_error.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Error internet connection", Toast.LENGTH_SHORT).show();
                    loading_.setVisibility(View.GONE);
                    layout_error.setVisibility(View.VISIBLE);
                    init_epc();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("ear_tag_counter", counter);
                hashMap.put("swine_id", String.valueOf(selected_swine));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    boolean isModalOpen = false;
    public void init_epc(){
        if(epcReceiver == null){

            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String tag = intent.getExtras().get("epc").toString();
                    String write =intent.getExtras().get("epc").toString();

                    if (!isModalOpen){
                        if (!isVolleyLoad) {

                            if (!tag.equals("No transponders seen")) {

                                // updateSingleItem(hexToASCII(tag));

                                if (tag.equals(String.valueOf(max))) {

                                    isVolleyLoad = true;
                                    set_modal("System message", "Write success", "green");

                                }else {

                                    if (selected_swine >0) {
                                        try {
                                            String eart_tag = hexToASCII(tag);

                                            String[] separated = eart_tag.split("-");
                                            String newstr = separated[0].replaceAll("[^A-Za-z]+", "");
                                            if (newstr.equals("wdy")) {
                                                target_tag = tag;
                                                ((ActivityMain) getActivity()).write_tag(target_tag, set_new_tag(), max, min);

                                                // update_counter(target_tag, String.valueOf(selected_swine), String.valueOf(selected_count + 1));

                                            } else {

                                                Toast.makeText(context, "invalid tag", Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){}
                                    } else {
                                        // Toast.makeText(context, "Please Select ear tag", Toast.LENGTH_SHORT).show();
                                        set_modal("System message", "Please Select ear tag", "red");
                                    }
                                }
                            } else {
                                Toast.makeText(context, "No eartag seen", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, "Please wait... tag is writting", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please finish action", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
    }

    @Override
    public void onResume() {
        super.onResume();
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
    }

    // swine card call back
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 199)
            {
                String callback = intent.getStringExtra("callback");
                init_epc();
                isModalOpen=false;
            }
        }
    }
}
