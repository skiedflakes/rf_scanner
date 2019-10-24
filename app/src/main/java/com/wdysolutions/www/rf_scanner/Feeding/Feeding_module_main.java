package com.wdysolutions.www.rf_scanner.Feeding;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.Feeding.Add.Feeding_module_add_main;
import com.wdysolutions.www.rf_scanner.Feeding.FeedingPerPen_Details.Feeding_Pen_Details;
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


public class Feeding_module_main extends Fragment implements DatePickerSelectionInterfaceCustom, resultListener {

    RecyclerView recyclerView;
    String isDate = "", selectedDate_start = "", selectedDate_end="", selectedBranch="", current_date="";
    String company_code, company_id, user_id, category_id;
    TextView start_date, end_date, txt_nodata, tx_range;
    Spinner spinner_branch;
    LinearLayout layout_, layout_error, loading_, layout_loading, layout_nodata, bg_branch, bg_start_date, bg_end_date;
    HorizontalScrollView layout_data;
    CheckBox checkBox;
    feedingAdapter feedingAdapter;
    Button btn_generate;
    ArrayList<Feeding_model_data> feeding_model_data = new ArrayList<>();
    ArrayList<Feeding_model_branch> feeding_model_branches = new ArrayList<>();


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
        Constant.power_level = "Max";
        tx_range.setText(Constant.power_level);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeding_module_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);

        recyclerView = view.findViewById(R.id.recyclerView);
        start_date = view.findViewById(R.id.start_date);
        end_date = view.findViewById(R.id.end_date);
        spinner_branch = view.findViewById(R.id.spinner_branch);
        layout_ = view.findViewById(R.id.layout_);
        layout_error = view.findViewById(R.id.layout_error);
        loading_ = view.findViewById(R.id.loading_);
        layout_data = view.findViewById(R.id.layout_data);
        layout_loading = view.findViewById(R.id.layout_loading);
        layout_nodata = view.findViewById(R.id.layout_nodata);
        txt_nodata = view.findViewById(R.id.txt_nodata);
        checkBox = view.findViewById(R.id.checkBox);
        bg_branch = view.findViewById(R.id.bg_branch);
        bg_start_date = view.findViewById(R.id.bg_start_date);
        bg_end_date = view.findViewById(R.id.bg_end_date);
        tx_range = view.findViewById(R.id.tx_range);
        btn_generate = view.findViewById(R.id.btn_generate);

        view.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment selectedDialogFragment = new Feeding_module_add_main();
                selectedDialogFragment.setTargetFragment(Feeding_module_main.this, 0);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {ft.remove(prev);}
                ft.addToBackStack(null);
                selectedDialogFragment.show(ft, "dialog");
            }
        });

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDate = "start";
                openDatePicker(false);
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDate = "end";
                openDatePicker(false);
            }
        });

        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBranch.equals("")){
                    Toast.makeText(getActivity(), "Please select branch", Toast.LENGTH_SHORT).show();
                } else if (selectedDate_start.equals("")){
                    Toast.makeText(getActivity(), "Please select start date", Toast.LENGTH_SHORT).show();
                } else if (selectedDate_end.equals("")){
                    Toast.makeText(getActivity(), "Please select end date", Toast.LENGTH_SHORT).show();
                }
                else {
                    getFeedingData();
                }
            }
        });

        layout_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_branch(company_id, company_code, "get_branch");
            }
        });

        get_branch(company_id, company_code, "get_branch");
        initMenu(view);
        return view;
    }

    private void dataLoading(boolean status){
        if (status){
            btn_generate.setEnabled(false);
            layout_loading.setVisibility(View.VISIBLE);
            layout_data.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
        } else {
            btn_generate.setEnabled(true);
            layout_loading.setVisibility(View.GONE);
            layout_data.setVisibility(View.VISIBLE);
        }
    }

    private void get_branch(final String company_id, final String company_code, final String get_type){
        layout_error.setVisibility(View.GONE);
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
                    feeding_model_branches.clear();
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
                                bg_branch.setBackgroundResource(R.drawable.bg_border);
                            } else {
                                selectedBranch = "";
                                bg_branch.setBackgroundResource(R.drawable.bg_border_red);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });

                    JSONArray diag1 = Object.getJSONArray("response_date");
                    JSONObject j = (JSONObject) diag1.get(0);
                    current_date = j.getString("current_date");

                }
                catch (JSONException e) {}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    loading_.setVisibility(View.GONE);
                    layout_error.setVisibility(View.VISIBLE);
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

    private void getFeedingData(){
        dataLoading(true);
        String URL = getString(R.string.URL_online)+"feeding/feeding_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    checkBox.setChecked(false);
                    dataLoading(false);
                    feeding_model_data.clear();
                    JSONObject Object = new JSONObject(response);
                    JSONArray diag = Object.getJSONArray("data");
                    for (int i = 0; i < diag.length(); i++) {
                        JSONObject cusObj = (JSONObject) diag.get(i);

                        feeding_model_data.add(new Feeding_model_data(cusObj.getString("id"),
                                cusObj.getString("num_heads"),
                                cusObj.getString("pen_name"),
                                cusObj.getString("building_name"),
                                cusObj.getString("quantity"),
                                cusObj.getString("product_name"),
                                cusObj.getString("date_of_feeding"),
                                ""));
                    }

                    if (feeding_model_data.size() == 0){
                        txt_nodata.setText("No data found...");
                        layout_nodata.setVisibility(View.VISIBLE);
                        layout_data.setVisibility(View.GONE);
                    }
                    else {
                        layout_nodata.setVisibility(View.GONE);
                        layout_data.setVisibility(View.VISIBLE);

                        feedingAdapter = new feedingAdapter(getContext(), feeding_model_data);
                        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager1);
                        recyclerView.setNestedScrollingEnabled(false);
                        recyclerView.setAdapter(feedingAdapter);
                    }

                }
                catch (JSONException e) {}
                catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    dataLoading(false);
                    txt_nodata.setText("Connection error, please try again.");
                    layout_nodata.setVisibility(View.VISIBLE);
                    layout_data.setVisibility(View.GONE);
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("branch_id", selectedBranch);
                hashMap.put("start_date", selectedDate_start);
                hashMap.put("end_date", selectedDate_end);
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class feedingAdapter extends RecyclerView.Adapter<feedingAdapter.MyHolder>{
        ArrayList<Feeding_model_data> data;
        private Context context;
        private LayoutInflater inflater;
        int num;

        public feedingAdapter(Context context, ArrayList<Feeding_model_data> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.feeding_module_main_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getId = data.get(position).getId();
            final String getBuilding_name = data.get(position).getBuilding_name();
            final String getDate_of_feeding = data.get(position).getDate_of_feeding();
            final String getQuantity = data.get(position).getQuantity();
            final String getProduct_name = data.get(position).getProduct_name();
            final String getNum_of_heads = data.get(position).getNum_of_heads();
            final String getPen_name = data.get(position).getPen_name();
            final String getIsCheck = data.get(position).getIsCheck();
            num = position;
            num++;

            if (getIsCheck.equals("1")){ holder.checkBox.setChecked(true); }
            else { holder.checkBox.setChecked(false); }

            holder.txt_count.setText(String.valueOf(num));
            holder.tv_amount.setText(getQuantity);
            holder.tv_building.setText(getBuilding_name);
            holder.tv_date.setText(getDate_of_feeding);
            holder.txt_num_of_heads.setText(getNum_of_heads);
            holder.tv_feedtype.setText(getProduct_name);
            holder.tv_pen.setText(getPen_name);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String check;
                    if (getIsCheck.equals("1")){
                        check = "0";
                    } else {
                        check = "1";
                    }

                    feeding_model_data.set(position, new Feeding_model_data(getId,
                            getNum_of_heads,
                            getPen_name,
                            getBuilding_name,
                            getQuantity,
                            getProduct_name,
                            getDate_of_feeding,
                            check));

                    notifyItemChanged(position);
                }
            });

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("getProduct_name", getProduct_name);
                    bundle.putString("getPen_name", getPen_name);
                    bundle.putString("getBuilding_name", getBuilding_name);
                    bundle.putString("selectedBranch", selectedBranch);
                    bundle.putString("getId", getId);
                    DialogFragment selectedDialogFragment = new Feeding_Pen_Details();
                    selectedDialogFragment.setTargetFragment(Feeding_module_main.this, 1001);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {ft.remove(prev);}
                    ft.addToBackStack(null);
                    selectedDialogFragment.setArguments(bundle);
                    selectedDialogFragment.show(ft, "dialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView txt_count, txt_num_of_heads, tv_building, tv_pen, tv_amount, tv_feedtype, tv_date;
            CheckBox checkBox;
            ImageView btn_edit;

            public MyHolder(View itemView) {
                super(itemView);
                btn_edit = itemView.findViewById(R.id.btn_edit);
                checkBox = itemView.findViewById(R.id.checkBox);
                txt_count = itemView.findViewById(R.id.txt_count);
                txt_num_of_heads = itemView.findViewById(R.id.txt_num_of_heads);
                tv_building = itemView.findViewById(R.id.txt_building);
                tv_pen = itemView.findViewById(R.id.txt_pen);
                tv_amount = itemView.findViewById(R.id.txt_feed_amount);
                tv_feedtype = itemView.findViewById(R.id.txt_feed_type);
                tv_date = itemView.findViewById(R.id.txt_date);
            }
        }
    }

    @Override
    public void onDateSelected(String date) {
        if (isDate.equals("start")){
            selectedDate_start = date;
            start_date.setText(selectedDate_start);
            bg_start_date.setBackgroundResource(R.drawable.bg_border);
        } else {
            selectedDate_end = date;
            end_date.setText(selectedDate_end);
            bg_end_date.setBackgroundResource(R.drawable.bg_border);
        }
    }

    @Override
    public void listener() {
        if (selectedBranch.equals("") || selectedDate_start.equals("") || selectedDate_end.equals("")){ }
        else {
            getFeedingData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((ActivityMain)getActivity()).setPower("max");
    }
}
