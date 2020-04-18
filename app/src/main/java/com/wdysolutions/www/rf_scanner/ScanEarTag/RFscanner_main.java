package com.wdysolutions.www.rf_scanner.ScanEarTag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
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
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.AI_Natural_Breeding.AI_Natural_Breeding_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Abort.Abort_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Breeding_Failed.Breeding_Failed_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Culling.Culling_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow.Farrow_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Mortality.Mortality_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Not_in_Pig.Not_in_Pig_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_All_to_Farrowing.Transfer_Farrowing_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Transfer_Pen.Transfer_Pen_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Wean.Wean_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Apc_dialog.Apc_dialog_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.AI_Materials_Semen.AI_Materials_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Birth_Mortality.birthMortality_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Farrowing_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Feeding.Feeding_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Mated_History.matedHistory_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Medication.Medication_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Remarks.Remarks_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Swine_History.swineHistory_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination.Vaccination_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Piglets_Action.Condemn.Piglets_Condemn_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.Piglets_Action.Mortality.Piglets_Mortality_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_add;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

public class RFscanner_main extends Fragment {

    private boolean isPiglets = false, isAction = true,callback=false;
    TextView txt_building, txt_details, txt_title, txt_pregnant, btn_action, btn_history, text_msg,txt_range;
    SessionPreferences sessionPreferences;
    String company_code, company_id, swine_scanned_id = "", gender = "", pen_code = "", piglets_count = "", pen_name ="",actual_scan="",
            user_id, schedule_for_culling_status = "", alert_parameter_status, swine_code, latest_breeding_date ="";
    ProgressDialog loadingScan;
    RecyclerView recyclerView;
    ScrollView scrollView;
    ArrayList<RFscanner_model> arrayList_rfscan = new ArrayList<>();
    ArrayList<RFscanner_piglets_model> arrayList_piglets = new ArrayList<>();
    ArrayList<RFscanner_piglets_add_model> RFscannerpiglets_add_models = new ArrayList<>();
    adapterRFscan adapter;
    adapterPigletsWeight adapterPigletsWeight;
    Button btn_piglets, btn_snowcard;
    LinearLayout layout_, whole_layout, bg_title, btn_layout, layout_actions, layout_buttons, layout_switch;
    RecyclerView recycler_, recycler_pigletsWeight;
    Switch parity_switch, litter_switch, times_rebred_switch, abort_switch;
    //set actions variables
    String swine_type, pen_type, pregnant_status;
    MenuItem selectDevice;
    ProgressBar parity_loading, litter_loading, abort_loading, times_rebred_loading;
    BroadcastReceiver epcReceiver;
    int checkedCounter = 0;
    Button_array buttonarray;
    boolean isVolleyLoad = false;
    String actual_id ="",category_id;

    private ProgressDialog showLoading(ProgressDialog loading, String msg){
        loading.setMessage(msg);
        loading.setCancelable(false);
        return loading;
    }

    TextView tx_range;
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rfscanner_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        category_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_CATEGORY_ID);
        buttonarray = new Button_array(getActivity());

        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        btn_layout = view.findViewById(R.id.btn_layout);
        layout_switch = view.findViewById(R.id.layout_switch);
        parity_loading = view.findViewById(R.id.parity_loading);
        layout_actions = view.findViewById(R.id.layout_actions);
        layout_buttons = view.findViewById(R.id.layout_buttons);
        btn_snowcard = view.findViewById(R.id.btn_sowcard);
        btn_piglets = view.findViewById(R.id.btn_piglets);
        txt_pregnant = view.findViewById(R.id.txt_pregnant);
        parity_switch = view.findViewById(R.id.parity_switch);
        litter_switch = view.findViewById(R.id.litter_switch);
        litter_loading = view.findViewById(R.id.litter_loading);
        layout_ = view.findViewById(R.id.layout_);
        btn_action = view.findViewById(R.id.btn_action);
        btn_history = view.findViewById(R.id.btn_history);
        bg_title = view.findViewById(R.id.bg_title);
        txt_title = view.findViewById(R.id.txt_title);
        txt_building = view.findViewById(R.id.txt_building);
        txt_details = view.findViewById(R.id.txt_details);
        whole_layout = view.findViewById(R.id.whole_layout);
        recycler_pigletsWeight = view.findViewById(R.id.recycler_pigletsWeight);
        recycler_ = view.findViewById(R.id.recycler_);
        recycler_.addItemDecoration(new DividerItemDecoration(recycler_.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        Button btn_scan = view.findViewById(R.id.btn_scan);
        text_msg = view.findViewById(R.id.text_msg);
        scrollView = view.findViewById(R.id.scrollView);
        times_rebred_switch = view.findViewById(R.id.times_rebred_switch);
        times_rebred_loading = view.findViewById(R.id.times_rebred_loading);
        abort_loading = view.findViewById(R.id.abort_loading);
        abort_switch = view.findViewById(R.id.abort_switch);
        tx_range = view.findViewById(R.id.tx_range);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getDetails(company_code, company_id, swine_scanned_id);
                try {
                   ((ActivityMain)getActivity()).mModel.scan();
                } catch (Exception e) {}
            }
        });

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAction = true;
                setButtonColor(true);
                btn_action.setPaintFlags(btn_action.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btn_history.setPaintFlags(0);

                if(isPiglets){setActionButtonPiglets(true);} else{setActionButtonSow(true);}
            }
        });

        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAction = false;
                setButtonColor(false);
                btn_history.setPaintFlags(btn_history.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                btn_action.setPaintFlags(0);

                if(isPiglets){setActionButtonPiglets(false);} else{setActionButtonSow(false);}
            }
        });

        parity_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parity_switch.isChecked()){
                    parity_switch.setText("Alert Parity On");
                    switchAlert(company_code, company_id, swine_scanned_id, "0", "1");
                } else {
                    parity_switch.setText("Alert Parity Off");
                    switchAlert(company_code, company_id, swine_scanned_id, "1", "1");
                }
            }
        });

        litter_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (litter_switch.isChecked()){
                    litter_switch.setText("Alert Litter Size On");
                    switchAlert(company_code, company_id, swine_scanned_id, "0", "2");
                } else {
                    litter_switch.setText("Alert Litter Size Off");
                    switchAlert(company_code, company_id, swine_scanned_id, "1", "2");
                }
            }
        });

        times_rebred_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (times_rebred_switch.isChecked()){
                    times_rebred_switch.setText("Alert Times Rebred On");
                    switchAlert(company_code, company_id, swine_scanned_id, "0", "3");
                } else {
                    times_rebred_switch.setText("Alert Times Rebred Off");
                    switchAlert(company_code, company_id, swine_scanned_id, "1", "3");
                }
            }
        });

        abort_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (abort_switch.isChecked()){
                    abort_switch.setText("Alert Abort On");
                    switchAlert(company_code, company_id, swine_scanned_id, "0", "4");
                } else {
                    abort_switch.setText("Alert Abort Off");
                    switchAlert(company_code, company_id, swine_scanned_id, "1", "4");
                }
            }
        });

        btn_piglets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!btn_piglets.getText().toString().equals("Piglets 0")){
                    showPigletCard();
                }
            }
        });

        btn_snowcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwineCard();
            }
        });

        loadingScan.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });


        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String tag = intent.getExtras().get("epc").toString();

                    if (!isModalOpen){

                        if (!isVolleyLoad){

                            if (!tag.equals("No transponders seen")){

//                            getDetails(company_code, company_id, hexToASCII(tag));

                                try {
                                    String eart_tag = hexToASCII(tag);
                                    String[] separated = eart_tag.split("-");
                                    // actual_id = separated[0];

                                    String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                                    String str_digits = separated[0].replaceAll("\\D+","");
                                    actual_id = str_digits;

                                    if(str_characters.substring(0,3).equals("wdy")){
                                        actual_scan = separated[1].trim().replace("\\s+"," ");
                                        get_details(company_code, company_id, actual_scan);
                                    }else{
                                        dialogBox_msg("Scanned ear tag is invalid");
                                    }

                                }catch (Exception e){
                                    dialogBox_msg("Scanned ear tag is invalid");
                                }
                            } else {
                                Toast.makeText(context, "No eartag seen", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(context, "Loading please wait...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please finish action.", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));


        //open swine card
        Bundle arguments = getArguments();
        if(arguments!=null){
            String swine_id = arguments.getString("swine_id");
            String power_level = arguments.getString("power_level");
            if(!swine_id.equals("")&&swine_id!=null){

                if(swine_id.equals("callback")){
                    callback=true;
                }else{
                    get_details(company_code, company_id, swine_id);
                }
                tx_range.setText(power_level);
            }

        } else {
            // set default power level
            Constant.power_level = "Medium";
            tx_range.setText(Constant.power_level);
            ((ActivityMain)getActivity()).setPower("med");
        }

        test_eartag(view);
        initMenu(view);
        return view;
    }

    private void test_eartag(View view){
        Button btn_test = view.findViewById(R.id.btn_test);
        final EditText et_test = view.findViewById(R.id.et_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String eart_tag =  hexToASCII(et_test.getText().toString());
                String eart_tag = et_test.getText().toString();
                String[] separated = eart_tag.split("-");
                String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                String str_digits = separated[0].replaceAll("\\D+","");
                //actual_id = separated[1];

                if(str_characters.equals("wdy")){
                    actual_scan = separated[1];
                    get_details(company_code, company_id, actual_scan);
                }else{
                    Toast.makeText(getActivity(), "Scanned ear tag is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        return output.toString();
    }

    /**
     * UI ------------------------------------------------------------------------------------------
     */

    public void showPigletCard(){
        isAction = true;
        isPiglets = true;
        btn_piglets.setBackgroundResource(R.drawable.btn_ripple_light_blue);
        btn_snowcard.setBackgroundResource(R.drawable.btn_ripple_blue);
        getPiglets(company_code, company_id, swine_scanned_id, pen_code);
    }

    public void showSwineCard(){
        isAction = true;
        isPiglets = false;
        btn_piglets.setBackgroundResource(R.drawable.btn_ripple_blue);
        btn_snowcard.setBackgroundResource(R.drawable.btn_ripple_light_blue);
        get_details(company_code, company_id, swine_scanned_id);
    }

    private void setButtonColor(boolean buttonStatus){
        if (buttonStatus){
            if (pen_type.equals("Gestating")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_brown);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light_brown);
            } else if (pen_type.equals("Farrowing")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_yellow);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light_yellow);
            } else if (pen_type.equals("Dry")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light_red);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_red);
            } else if (pen_type.equals("Nursery")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_violet);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light_violet);
            } else if (pen_type.equals("Growout")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light_green);
            }
        } else {
            if (pen_type.equals("Gestating")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light_brown);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_brown);
            } else if (pen_type.equals("Farrowing")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light_yellow);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_yellow);
            } else if (pen_type.equals("Dry")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_red);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light_red);
            } else if (pen_type.equals("Nursery")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light_violet);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_violet);
            } else if (pen_type.equals("Growout")){
                btn_action.setBackgroundResource(R.drawable.btn_ripple_light_green);
                btn_history.setBackgroundResource(R.drawable.btn_ripple_light);
            }
        }
    }

    private void setActionButtonSow(boolean isActions){
        ArrayList button_sow_array = buttonarray.setActionButton(isActions, swine_type, pen_type, pregnant_status, piglets_count , alert_parameter_status);
        adapterButtonList adapter = new adapterButtonList(getContext(), button_sow_array);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_.setLayoutManager(layoutManager);
        recycler_.setNestedScrollingEnabled(false);
        recycler_.setAdapter(adapter);
    }

    private void setActionButtonPiglets(boolean isActions){
        ArrayList button_piglet_array = buttonarray.setActionButtonPiglets(isActions);
        adapterButtonList adapter = new adapterButtonList(getContext(), button_piglet_array);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_.setLayoutManager(layoutManager);
        recycler_.setNestedScrollingEnabled(false);
        recycler_.setAdapter(adapter);
    }

    private void setDefaultButtonColor(){
        if (piglets_count.equals("0")){
            btn_piglets.setEnabled(false);
            btn_snowcard.setEnabled(false);
            btn_piglets.setBackgroundResource(R.drawable.btn_ripple_light_blue);
            btn_snowcard.setBackgroundResource(R.drawable.btn_ripple_light_blue);
        } else {
            btn_piglets.setEnabled(true);
            btn_snowcard.setEnabled(false);
            btn_piglets.setBackgroundResource(R.drawable.btn_ripple_blue);
            btn_snowcard.setBackgroundResource(R.drawable.btn_ripple_light_blue);
        }
    }


    //swine details
    String weaning_date="",weaning_wt="",age_days="",days_weaned="",
           swine_condition="",genetic_breed="",genetic_line="",
            swine_birthdate="",weight="",progeny="",swine_origin="",
            p_boar="",p_sow="",swine_parity="",times_rebreed="",
            abortion="",accumulated="",days_count="",days_to_farrow="",
            expected_date_to_farrow="",cost="",ave_gest_days="",
            ave_weaning_days="",sow_index="",estimated_weight="",
            parity_color="",rebred_color="",abortion_color="",
            days_count_title="",days_count_color="",days_to_farrow_color="",farrow_date_color="";

    public void get_details(final String company_code, final String company_id, final String swine_id){

        swine_condition = "";
        genetic_breed ="";
        genetic_line ="";
        swine_birthdate = "";
        weight = "";
        weaning_date = "";
        weaning_wt = "";
        age_days = "";
        days_weaned= "";
        progeny = "";
        swine_origin = "";
        p_boar = "";
        p_sow = "";
        swine_parity = "";
        parity_color = "";
        times_rebreed = "";
        rebred_color = "";
        abortion = "";
        abortion_color = "";
        days_count = "";
        days_count_title = "";
        days_count_color = "";
        days_to_farrow = "";
        days_to_farrow_color = "";
        expected_date_to_farrow = "";
        farrow_date_color = "";
        cost = "";
        ave_gest_days =  "";
        ave_weaning_days = "";
        sow_index = "";
        estimated_weight = "";
        piglets_count = "";
        accumulated="";

        // sidable
        isVolleyLoad = true;
        scanResult("scan");
        showLoading(loadingScan,"Loading data...").show();
        String URL = getString(R.string.URL_online)+"scan_eartag/get_swine_details3.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{ // enable
                    showLoading(loadingScan,null).dismiss();
                    get_computation(swine_id);
                    isVolleyLoad = false;
                    isPiglets = false;
                    arrayList_rfscan.clear();
                    recycler_pigletsWeight.setVisibility(View.GONE);
                    showLoading(loadingScan,null).dismiss();

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("response");
                    JSONObject r = details.getJSONObject(0);
                    String status = r.getString("status");

                    if(status.equals("okay")){
                        scanResult("found");
                        swine_scanned_id = r.getString("swine_id");
                        gender = r.getString("gender");
                        pen_code = r.getString("pen_code");
                        swine_type = r.getString("swine_type");
                        swine_code = r.getString("swine_code");
                        pen_type = r.getString("pen_type");
                        pen_name = r.getString("pen_name");
                        pregnant_status = r.getString("pregnant_status");
                        piglets_count = r.getString("piglets_count");
                        schedule_for_culling_status = r.getString("schedule_for_culling_status");
                        alert_parameter_status = r.getString("alert_parameter_status");
                        latest_breeding_date = r.getString("latest_breeding_date");

                        if(!r.getString("pregnant_status").equals("")){txt_pregnant.setText(r.getString("pregnant_status"));txt_pregnant.setVisibility(View.VISIBLE);}else{txt_pregnant.setVisibility(View.GONE);}
                        if(r.getString("parity_alert").equals("1")){parity_switch.setChecked(true);parity_switch.setText("Alert Parity On");}else{parity_switch.setChecked(false);parity_switch.setText("Alert Parity Off");}
                        if(r.getString("litter_alert").equals("1")){litter_switch.setChecked(true);litter_switch.setText("Alert Litter Size On");}else{litter_switch.setChecked(false);litter_switch.setText("Alert Litter Size Off");}
                        if(r.getString("times_rebred_alert").equals("1")){times_rebred_switch.setChecked(true);times_rebred_switch.setText("Alert Times Rebred On");}else{times_rebred_switch.setChecked(false);times_rebred_switch.setText("Alert Times Rebred Off");}
                        if(r.getString("abort_alert").equals("1")){abort_switch.setChecked(true);abort_switch.setText("Alert Abort On");}else{abort_switch.setChecked(false);abort_switch.setText("Alert Abort Off");}
                        txt_building.setText(isCulled() ? "("+r.getString("pen_type")+")" : isDeacesed() ? "("+r.getString("pen_type")+")" : r.getString("building")+" > "+r.getString("pen_name")+" ("+r.getString("pen_type")+")");


//                        if(!actual_id.equals("")){
//                            txt_details.setText("("+actual_id+") "+r.getString("swine_code")+" > "+r.getString("swine_type")+" "+(isGenderMale() ? "(M)" : "(F)"));
//                        }else{
//                            txt_details.setText("EAR TAG:"+r.getString("swine_code")+" > "+r.getString("swine_type")+" "+(isGenderMale() ? "(M)" : "(F)"));
//                        }
                        txt_details.setText("Ear Tag: "+r.getString("swine_code")+" > "+r.getString("swine_type")+" "+(isGenderMale() ? "(M)" : "(F)"));


                        swine_condition = r.getString("swine_condition");
                        genetic_breed = r.getString("genetic_breed");
                        genetic_line =r.getString("genetic_line");
                        swine_birthdate = r.getString("swine_birthdate");
                        weight =  r.getString("weight");
                        weaning_date = r.getString("weaning_date");
                        weaning_wt = r.getString("weaning_wt");
                        age_days = r.getString("age_days");
                        days_weaned= r.getString("days_weaned");
                        progeny = r.getString("progeny");
                        swine_origin = r.getString("swine_origin");
                        p_boar = r.getString("p_boar");
                        p_sow = r.getString("p_sow");
                        swine_parity = r.getString("swine_parity");
                        parity_color = r.getString("parity_color");
                        times_rebreed = r.getString("times_rebreed");
                        rebred_color = r.getString("rebred_color");
                        abortion = r.getString("abortion");
                        abortion_color =  r.getString("abortion_color");
                        days_count =  r.getString("days_count");
                        days_count_title =  r.getString("days_count_title");
                        days_count_color =  r.getString("days_count_color");
                        days_to_farrow =  r.getString("days_to_farrow");
                        days_to_farrow_color =  r.getString("days_to_farrow_color");
                        expected_date_to_farrow =  r.getString("expected_date_to_farrow");
                        farrow_date_color =  r.getString("farrow_date_color");
                        ave_gest_days =  r.getString("ave_gest_days");
                        ave_weaning_days =  r.getString("ave_weaning_days");
                        piglets_count =  r.getString("piglets_count");

                        accumulated =  r.getString("accumulated");
                        sow_index =  r.getString("sow_index");
                        cost =  r.getString("cost");
                        estimated_weight =  r.getString("estimated_weight");


                        arrayList_rfscan.add(new RFscanner_model("Swine Condition:", swine_condition,""));
                        arrayList_rfscan.add(new RFscanner_model("Genetic Breed:", genetic_breed,""));
                        arrayList_rfscan.add(new RFscanner_model("Genetic Line:", genetic_line,""));
                        arrayList_rfscan.add(new RFscanner_model("Date of Birth:",swine_birthdate,""));
                        arrayList_rfscan.add(new RFscanner_model(r.getString("purchase_title"), weight,""));
                        if(!weaning_date.equals("")){arrayList_rfscan.add(new RFscanner_model("Weaning Date:",weaning_date,""));}
                        if(!weaning_wt.equals("")){arrayList_rfscan.add(new RFscanner_model("Weaning Weight (kg):",weaning_wt,""));}
                        if(!age_days.equals("")){arrayList_rfscan.add(new RFscanner_model("Age in Days:", age_days, ""));}
                        if(!days_weaned.equals("")){arrayList_rfscan.add(new RFscanner_model("Day/s weaned:", days_weaned, ""));}
                        arrayList_rfscan.add(new RFscanner_model("Progeny:",progeny , ""));
                        arrayList_rfscan.add(new RFscanner_model("Swine Origin:",swine_origin, ""));
                        arrayList_rfscan.add(new RFscanner_model("Parent Boar:", p_boar, ""));
                        arrayList_rfscan.add(new RFscanner_model("Parent Sow:", p_sow, ""));
                        if(!swine_parity.equals("")){arrayList_rfscan.add(new RFscanner_model("Parity:",swine_parity, parity_color));}
                        if(!times_rebreed.equals("")){arrayList_rfscan.add(new RFscanner_model("Times Rebred:", times_rebreed, rebred_color));}
                        if(!abortion.equals("")){arrayList_rfscan.add(new RFscanner_model("Abortion:", abortion, abortion_color));}
                        if(!accumulated.equals("")){arrayList_rfscan.add(new RFscanner_model("Accumulated expense for piglet cost:", accumulated, ""));}
                        if(!days_count.equals("")){arrayList_rfscan.add(new RFscanner_model(days_count_title, days_count, days_count_color));}
                        if(!days_to_farrow.equals("")){arrayList_rfscan.add(new RFscanner_model("Days to Farrowing Pen:", days_to_farrow, days_to_farrow_color));}
                        if(!expected_date_to_farrow.equals("")){arrayList_rfscan.add(new RFscanner_model("Expected date to Farrow:", expected_date_to_farrow,farrow_date_color));}
                        arrayList_rfscan.add(new RFscanner_model("Cost:", cost, ""));
                        if(ave_gest_days.equals("")){}else{arrayList_rfscan.add(new RFscanner_model("Average Gestating Days:",ave_gest_days, ""));}
                        if(ave_weaning_days.equals("")){}else{arrayList_rfscan.add(new RFscanner_model("Average Weaning Days:",ave_weaning_days, ""));}
                        if(sow_index.equals("")){}else{arrayList_rfscan.add(new RFscanner_model("Sow Index:", sow_index, ""));}
                        if(!estimated_weight.equals("")){arrayList_rfscan.add(new RFscanner_model("Estimated Weight:", estimated_weight, ""));}
                        if(!piglets_count.equals("")){btn_layout.setVisibility(View.VISIBLE); btn_piglets.setText("Piglets "+piglets_count);}else{btn_layout.setVisibility(View.GONE);}

                        adapter = new adapterRFscan(getContext(), arrayList_rfscan);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setNestedScrollingEnabled(false);
                        recyclerView.setAdapter(adapter);


                        //set details to buttons ---------------------------------------------------
                        setActionButtonSow(true);
                        setDefaultButtonColor();

                        // set default underline
                        btn_action.setPaintFlags(btn_action.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        btn_history.setPaintFlags(0);

                        if (pen_type.equals("Gestating")){
                            txt_building.setTextColor(getResources().getColor(R.color.btn_brown_color1));
                            bg_title.setBackgroundResource(R.drawable.bg_custom_brown);
                            btn_action.setBackgroundResource(R.drawable.btn_ripple_brown);
                            btn_history.setBackgroundResource(R.drawable.btn_ripple_light_brown);
                            btn_action.setTextColor(getResources().getColor(R.color.white));
                            btn_history.setTextColor(getResources().getColor(R.color.white));
                            txt_title.setTextColor(getResources().getColor(R.color.white));
                            txt_details.setTextColor(getResources().getColor(R.color.white));
                            layout_switch.setVisibility(View.VISIBLE);
                            layout_buttons.setVisibility(View.VISIBLE);
                            isAction = true;
                        } else if (pen_type.equals("Farrowing")){
                            txt_building.setTextColor(getResources().getColor(R.color.btn_yellow_color1));
                            bg_title.setBackgroundResource(R.drawable.bg_custom_yellow);
                            btn_action.setBackgroundResource(R.drawable.btn_ripple_yellow);
                            btn_history.setBackgroundResource(R.drawable.btn_ripple_light_yellow);
                            btn_action.setTextColor(getResources().getColor(R.color.text_grey));
                            btn_history.setTextColor(getResources().getColor(R.color.text_grey));
                            txt_title.setTextColor(getResources().getColor(R.color.text_grey));
                            txt_details.setTextColor(getResources().getColor(R.color.text_grey));
                            layout_switch.setVisibility(View.VISIBLE);
                            layout_buttons.setVisibility(View.VISIBLE);
                            isAction = true;
                        } else if (pen_type.equals("Dry")){
                            txt_building.setTextColor(getResources().getColor(R.color.btn_light_red_color1));
                            bg_title.setBackgroundResource(R.drawable.bg_custom_light_red);
                            btn_action.setBackgroundResource(R.drawable.btn_ripple_light_red);
                            btn_history.setBackgroundResource(R.drawable.btn_ripple_red);
                            btn_action.setTextColor(getResources().getColor(R.color.white));
                            btn_history.setTextColor(getResources().getColor(R.color.white));
                            txt_title.setTextColor(getResources().getColor(R.color.white));
                            txt_details.setTextColor(getResources().getColor(R.color.white));
                            layout_switch.setVisibility(View.GONE);
                            layout_buttons.setVisibility(View.VISIBLE);
                            isAction = true;
                        } else if (pen_type.equals("Nursery")){
                            txt_building.setTextColor(getResources().getColor(R.color.btn_violet_color1));
                            bg_title.setBackgroundResource(R.drawable.bg_custom_violet);
                            btn_action.setBackgroundResource(R.drawable.btn_ripple_violet);
                            btn_history.setBackgroundResource(R.drawable.btn_ripple_light_violet);
                            btn_action.setTextColor(getResources().getColor(R.color.white));
                            btn_history.setTextColor(getResources().getColor(R.color.white));
                            txt_title.setTextColor(getResources().getColor(R.color.white));
                            txt_details.setTextColor(getResources().getColor(R.color.white));
                            layout_switch.setVisibility(View.GONE);
                            layout_buttons.setVisibility(View.VISIBLE);
                            isAction = true;
                        } else if (pen_type.equals("Growout")){
                            txt_building.setTextColor(getResources().getColor(R.color.btn_color1));
                            bg_title.setBackgroundResource(R.drawable.btn_ripple_light);
                            btn_action.setBackgroundResource(R.drawable.btn_ripple_light);
                            btn_history.setBackgroundResource(R.drawable.btn_ripple_light_green);
                            btn_action.setTextColor(getResources().getColor(R.color.white));
                            btn_history.setTextColor(getResources().getColor(R.color.white));
                            txt_title.setTextColor(getResources().getColor(R.color.white));
                            txt_details.setTextColor(getResources().getColor(R.color.white));
                            layout_switch.setVisibility(View.GONE);
                            layout_buttons.setVisibility(View.VISIBLE);
                            isAction = true;
                        } else if (pen_type.equals("Deceased") || pen_type.equals("Culled") || pen_type.equals("Sold")){
                            txt_building.setTextColor(getResources().getColor(R.color.text_grey));
                            bg_title.setBackgroundResource(R.drawable.bg_custom_light_black);
                            layout_switch.setVisibility(View.GONE);
                            layout_buttons.setVisibility(View.GONE);
                            isAction = false;
                        }

                    } else {
                        scanResult("no_eartag");
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan,null).dismiss();
                    scanResult("error");
                    isVolleyLoad = false;
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_id);
                hashMap.put("category_id", category_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    StringRequest computation_request;
    public void get_computation(final String swine_id){
//        AppController.getInstance().cancelPendingRequests(computation_request);

        String URL = getString(R.string.URL_online)+"scan_eartag/get_swine_computation.php";
        computation_request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                
               // String old_swine_id = swine_id;
                
                try {

                    if(actual_scan.equals(swine_id)){

                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response");
                        JSONObject r = details.getJSONObject(0);
                        String status = r.getString("status");

                        if(status.equals("okay")){

                            accumulated = r.getString("accumulated");
                            sow_index = r.getString("sow_index");
                            cost = r.getString("cost");
                            estimated_weight = r.getString("estimated_weight");

                            JSONArray jsonArray = new JSONArray(new Gson().toJson(arrayList_rfscan));
                            for(int i = 0;i<jsonArray.length();i++) {
                                JSONObject x = jsonArray.getJSONObject(i);
                                String name = x.getString("name");
                                if(name.equals("Accumulated expense for piglet cost:")){
                                    arrayList_rfscan.set(i,new RFscanner_model("Accumulated expense for piglet cost:", accumulated, ""));
                                    adapter.notifyItemChanged(i);
                                }
                                if(name.equals("Sow Index:")){
                                    arrayList_rfscan.set(i,new RFscanner_model("Sow Index:", sow_index, ""));
                                    adapter.notifyItemChanged(i);
                                }
                                if(name.equals("Cost:")){
                                    arrayList_rfscan.set(i,new RFscanner_model("Cost:", cost, ""));
                                    adapter.notifyItemChanged(i);
                                }
                                if(name.equals("Estimated Weight:")){
                                    arrayList_rfscan.set(i,new RFscanner_model("Estimated Weight:", estimated_weight, ""));
                                    adapter.notifyItemChanged(i);
                                }
                            }
                        }
                    }else{
                        // get_computation(swine_id);
                    }
                    
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
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(computation_request);
        AppController.getInstance().addToRequestQueue(computation_request);
    }

    private void scanResult(String result){
        if (result.equals("scan")){
            scrollView.setVisibility(View.GONE);
            text_msg.setVisibility(View.VISIBLE);
            text_msg.setText("");
        } else if (result.equals("error")){
            scrollView.setVisibility(View.GONE);
            text_msg.setVisibility(View.VISIBLE);
            text_msg.setText("Connection Error, please try again...");
        } else if (result.equals("no_eartag")){
            scrollView.setVisibility(View.GONE);
            text_msg.setVisibility(View.VISIBLE);
            text_msg.setText("Ear tag not found on the database...");
        } else if (result.equals("found")){
            scrollView.setVisibility(View.VISIBLE);
            text_msg.setVisibility(View.GONE);
        }
    }

    private boolean isCulled(){
        if (pen_type.equals("Deceased") || pen_type.equals("Culled") || pen_type.equals("Sold")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isGenderMale(){
        if (gender.equals("Male")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isDeacesed(){
        if (pen_type.equals("Deceased") || pen_type.equals("Culled")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isPigletsPressed(){
        if (isPiglets){
            return true;
        } else {
            return false;
        }
    }

    private class adapterRFscan extends RecyclerView.Adapter<adapterRFscan.MyHolder>{
        ArrayList<RFscanner_model> data;
        private Context context;
        private LayoutInflater inflater;

        public adapterRFscan(Context context, ArrayList<RFscanner_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.rfscanner_main_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getName = data.get(position).getName();
            final String getDetails = data.get(position).getDetails();
            final String getColorType = data.get(position).getColorType();

            holder.name.setText(getName);
            if(getName.equals("Swine Condition:")){
                String condition = null;
                if (getDetails.equals("1")){
                    condition = "Normal";
                } else if (getDetails.equals("2")){
                    condition = "Abnormal";
                } else if (getDetails.equals("3")){
                    condition = "Under size";
                }
                holder.details.setText(condition);
            } else {
                holder.details.setText(getDetails);
            }

            if(getName.equals("Days After Farrowed:")){
                if (getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            if(getName.equals("Days Pregnant:")){
                if (getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                } else if (getColorType.equals("green")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_green);
                }
            }

            if (getName.equals("Parity:")){
                if (getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            if (getName.equals("Times Rebred:")){
                if (getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            if (getName.equals("Abortion:")){
                if (getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            if(getName.equals("Days to Farrowing Pen:")){
                if(getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            if(getName.equals("Expected date to Farrow:")){
                if(getColorType.equals("red")){
                    holder.details.setBackgroundResource(R.drawable.bg_custom_red);
                }
            }

            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getName.equals("Accumulated expense for piglet cost:")){
                        Bundle bundle = new Bundle();
                        bundle.putString("company_id", company_id);
                        bundle.putString("company_code", company_code);
                        bundle.putString("swine_id", swine_scanned_id);
                        DialogFragment dialogFrag = new Apc_dialog_main();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {ft.remove(prev);}
                        ft.addToBackStack(null);
                        dialogFrag.setArguments(bundle);
                        dialogFrag.show(ft, "dialog");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView name, details;
            RelativeLayout bg_details;
            public MyHolder(View itemView) {
                super(itemView);
                bg_details = itemView.findViewById(R.id.bg_details);
                details = itemView.findViewById(R.id.details);
                name = itemView.findViewById(R.id.name);
            }
        }
    }

    private void switchAlert(final String company_code, final String company_id, final String swine_id, final String alert_status, final String param) {

        if (param.equals("1")){
            parity_switch.setVisibility(View.GONE);
            parity_loading.setVisibility(View.VISIBLE);
        } else if (param.equals("2")){
            litter_switch.setVisibility(View.GONE);
            litter_loading.setVisibility(View.VISIBLE);
        } else if (param.equals("3")){
            times_rebred_switch.setVisibility(View.GONE);
            times_rebred_loading.setVisibility(View.VISIBLE);
        } else if (param.equals("4")){
            abort_switch.setVisibility(View.GONE);
            abort_loading.setVisibility(View.VISIBLE);
        }

        String URL = getString(R.string.URL_online)+"scan_eartag/pig_alert_temporary.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    if (response.equals("1")){

                        if (param.equals("1")){ // parity
                            parity_switch.setVisibility(View.VISIBLE);
                            parity_loading.setVisibility(View.GONE);
                        } else if (param.equals("2")){ // litter size
                            litter_switch.setVisibility(View.VISIBLE);
                            litter_loading.setVisibility(View.GONE);
                        } else if (param.equals("3")){ // times rebred
                            times_rebred_switch.setVisibility(View.VISIBLE);
                            times_rebred_loading.setVisibility(View.GONE);
                        } else if (param.equals("4")){ // abort
                            abort_switch.setVisibility(View.VISIBLE);
                            abort_loading.setVisibility(View.GONE);
                        }

                        get_details(company_code, company_id, swine_scanned_id);
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    if (param.equals("1")){
                        parity_switch.setVisibility(View.VISIBLE);
                        parity_loading.setVisibility(View.GONE);
                        if (parity_switch.isChecked()){
                            parity_switch.setChecked(false);
                            parity_switch.setText("Alert Parity Off");
                        } else {
                            parity_switch.setChecked(true);
                            parity_switch.setText("Alert Parity On");
                        }
                    } else if (param.equals("2")){
                        litter_switch.setVisibility(View.VISIBLE);
                        litter_loading.setVisibility(View.GONE);
                        if (litter_switch.isChecked()){
                            litter_switch.setChecked(false);
                            litter_switch.setText("Alert Litter Size Off");
                        } else {
                            litter_switch.setChecked(true);
                            litter_switch.setText("Alert Litter Size On");
                        }
                    } else if (param.equals("3")){
                        times_rebred_switch.setVisibility(View.VISIBLE);
                        times_rebred_loading.setVisibility(View.GONE);
                        if (times_rebred_switch.isChecked()){
                            times_rebred_switch.setChecked(false);
                            times_rebred_switch.setText("Alert Times Rebred Off");
                        } else {
                            times_rebred_switch.setChecked(true);
                            times_rebred_switch.setText("Alert Times Rebred On");
                        }
                    } else if (param.equals("4")){
                        abort_switch.setVisibility(View.VISIBLE);
                        abort_loading.setVisibility(View.GONE);
                        if (abort_switch.isChecked()){
                            abort_switch.setChecked(false);
                            abort_switch.setText("Alert Abort Off");
                        } else {
                            abort_switch.setChecked(true);
                            abort_switch.setText("Alert Abort On");
                        }
                    }

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
                hashMap.put("alert_status", alert_status);
                hashMap.put("param", param);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    String piglet_genetic_breed="",piglet_genetic_line="",piglet_date_of_birth="",piglet_age="",piglet_progeny="",piglet_cost_of_piglets="";

    private void getPiglets(final String company_code, final String company_id, final String swine_id, final String pen_code) {
        layout_.setVisibility(View.GONE);
        showLoading(loadingScan,"Loading data...").show();
        piglet_genetic_breed =  "";
        piglet_genetic_line =  "";
        piglet_date_of_birth =  "";
        piglet_age =  "";
        piglet_progeny =  "";
        piglet_cost_of_piglets =  "";

        String URL = getString(R.string.URL_online)+"scan_eartag/pig_piglets.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    isPiglets = true;
                    btn_piglets.setEnabled(false);
                    btn_snowcard.setEnabled(true);
                    layout_switch.setVisibility(View.GONE);
                    layout_.setVisibility(View.VISIBLE);
                    recycler_pigletsWeight.setVisibility(View.VISIBLE);
                    showLoading(loadingScan,null).dismiss();
                    arrayList_rfscan.clear();
                    arrayList_piglets.clear();
                    RFscannerpiglets_add_models.clear();

                    setActionButtonPiglets(true);
                    setButtonColor(true);

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("response_details");
                    JSONObject r = details.getJSONObject(0);

                    piglet_genetic_breed =  r.getString("genetic_breed");
                    piglet_genetic_line =  r.getString("genetic_line");
                    piglet_date_of_birth =  r.getString("date_of_birth");
                    piglet_age =  r.getString("age");
                    piglet_progeny =  r.getString("progeny");
                    piglet_cost_of_piglets =  r.getString("cost_of_piglets");

                    arrayList_rfscan.add(new RFscanner_model("Genetic Breed:",piglet_genetic_breed, ""));
                    arrayList_rfscan.add(new RFscanner_model("Genetic Line:", piglet_genetic_line, ""));
                    arrayList_rfscan.add(new RFscanner_model("Date of Birth:",piglet_date_of_birth, ""));
                    arrayList_rfscan.add(new RFscanner_model("Age:", piglet_age, ""));
                    arrayList_rfscan.add(new RFscanner_model("Progeny:", piglet_progeny, ""));
                    arrayList_rfscan.add(new RFscanner_model("Cost of Piglets:", piglet_cost_of_piglets, ""));

                    adapter = new adapterRFscan(getContext(), arrayList_rfscan);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);


                    // Piglets Weight -------------------------------------------------------------------------
                    JSONArray details2 = Object.getJSONArray("response_piglets");

                    for (int i =0; i < details2.length(); i++){
                        JSONObject r1 = details2.getJSONObject(i);
                        arrayList_piglets.add(new RFscanner_piglets_model(r1.getInt("swine_id"),
                                r1.getString("swine_code"),
                                r1.getString("weight")));

                        RFscannerpiglets_add_models.add(new RFscanner_piglets_add_model(r1.getInt("swine_id"),"unchecked"));
                    }

                    adapterPigletsWeight = new adapterPigletsWeight(getContext(), arrayList_piglets);
                    RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getActivity(), 2);
                    recycler_pigletsWeight.setLayoutManager(layoutManager1);
                    recycler_pigletsWeight.setNestedScrollingEnabled(false);
                    recycler_pigletsWeight.setAdapter(adapterPigletsWeight);
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan,null).dismiss();
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
                hashMap.put("p", pen_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class adapterPigletsWeight extends RecyclerView.Adapter<adapterPigletsWeight.MyHolder>{
        ArrayList<RFscanner_piglets_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num;

        public adapterPigletsWeight(Context context, ArrayList<RFscanner_piglets_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.rfscanner_main_piglets_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getName = data.get(position).getName();
            final String getWeight = data.get(position).getWeight();
            num = position;
            num++;

            holder.txt_num.setText(String.valueOf(num));
            holder.txt_name.setText(getName);
            holder.txt_weight.setText("Weight: "+getWeight+" kg");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView txt_num, txt_name, txt_weight;
            CheckBox weight_checkbox;

            public MyHolder(View itemView) {
                super(itemView);
                weight_checkbox = itemView.findViewById(R.id.weight_checkbox);
                txt_name = itemView.findViewById(R.id.txt_name);
                txt_num = itemView.findViewById(R.id.txt_num);
                txt_weight = itemView.findViewById(R.id.txt_weight);

                weight_checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (weight_checkbox.isChecked()){
                            RFscannerpiglets_add_models.set(getAdapterPosition(),new RFscanner_piglets_add_model(data.get(getAdapterPosition()).getId(),"checked"));
                        } else {
                            RFscannerpiglets_add_models.set(getAdapterPosition(),new RFscanner_piglets_add_model(data.get(getAdapterPosition()).getId(),"unchecked"));
                        }
                    }
                });
            }
        }
    }


    // Actions & History button Adapter
    private class adapterButtonList extends RecyclerView.Adapter<adapterButtonList.MyHolder>{
        ArrayList<RFscanner_buttonActions_model> data;
        private Context context;
        private LayoutInflater inflater;

        public adapterButtonList(Context context, ArrayList<RFscanner_buttonActions_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.rfscanner_main_button_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getName = data.get(position).getName();
            final String getDisableStatus = data.get(position).getDisableStatus();
            final int getId = data.get(position).getId();

            if (pen_type.equals("Gestating")){
                holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_light2_brown);
            } else if (pen_type.equals("Farrowing")){
                holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_light2_yellow);
                holder.txt_name.setTextColor(getResources().getColor(R.color.text_grey));
            } else if (pen_type.equals("Dry")){
                holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_light2_red);
            } else if (pen_type.equals("Nursery")){
                holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_light2_violet);
            } else if (pen_type.equals("Deceased") || pen_type.equals("Culled") || pen_type.equals("Sold")){
                holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_light_black);
            }

            holder.txt_name.setText(getName);
            holder.btn_tap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment selectedFragment = null;
                    DialogFragment selectedDialogFragment = null;
                    switch (getId){
                        case 0:
                            if (getName.equals("AI/Natural Breeding")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new AI_Natural_Breeding_main();
                                }
                            }
                            else if (getName.equals("Transfer Pen")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Transfer_Pen_main();
                                }
                            }
                            else if (getName.equals("Convert to Gilt/Boar")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission("You are about to convert swine.", "convert_to_swine");
                                }
                            }

                            // Piglet --------------------------------------------------------------
                            else if (getName.equals("Mortality")){
                                selectedDialogFragment = new Piglets_Mortality_main();
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Swine History")){
                                selectedFragment = new swineHistory_main();
                            }
                            break;
                        case 1:
                            if (getName.equals("Breeding Failed")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Breeding_Failed_main();
                                }
                            }
                            else if (getName.equals("Culling")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Culling_main();
                                }
                            }
                            else if (getName.equals("Mortality")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Mortality_main();
                                }
                            }
                            else if (getName.equals("Transfer Pen")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Transfer_Pen_main();
                                }
                            }

                            // Piglet --------------------------------------------------------------
                            else if (getName.equals("Condemn")){
                                selectedDialogFragment = new Piglets_Condemn_main();
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Mated History")){
                                selectedFragment = new matedHistory_main();
                            }
                            else if (getName.equals("Medication")){
                                selectedFragment = new Medication_main();
                            }
                            break;
                        case 2:
                            if (getName.equals("Confirm Pregnant")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission(getString(R.string.confirm_pregnancy_msg), "confirm_pregnant");
                                }
                            }
                            else if (getName.equals("Transfer Pen")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Transfer_Pen_main();
                                }
                            }
                            else if (getName.equals("Transfer All to Farrowing")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Transfer_Farrowing_main();
                                }
                            }
                            else if (getName.equals("Add Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission(getString(R.string.schedule_cull_msg), "schedule_culling");
                                }
                            }
                            else if (getName.equals("Mortality")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Mortality_main();
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Farrowing Stats")){
                                selectedFragment = new Farrowing_main();
                            }
                            else if (getName.equals("Vaccination")){
                                selectedFragment = new Vaccination_main();
                            }
                            break;
                        case 3:
                            if (getName.equals("Culling")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Culling_main();
                                }
                            }
                            else if (getName.equals("Mortality")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Mortality_main();
                                }
                            }
                            else if (getName.equals("Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){

                                }
                            }
                            else if (getName.equals("Add Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission(getString(R.string.schedule_cull_msg), "schedule_culling");
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Birth Mortality")){
                                selectedFragment = new birthMortality_main();
                            }
                            else if(getName.equals("Feeding")){
                                selectedFragment = new Feeding_main();
                            }
                            break;
                        case 4:
                            if (getName.equals("Abort")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Abort_main();
                                }
                            }
                            else if (getName.equals("Add Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission(getString(R.string.schedule_cull_msg), "schedule_culling");
                                }
                            }
                            else if (getName.equals("Wean")){
                                if(!getDisableStatus.equals("1")){
                                    if (!piglets_count.equals("0")){
                                        selectedDialogFragment = new Wean_main();
                                    }
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("AI Materials/Semen")){
                                selectedFragment = new AI_Materials_main();
                            }
                            else if (getName.equals("Remarks")){
                                selectedFragment = new Remarks_main();
                            }
                            break;
                        case 5:
                            if (getName.equals("Not in Pig")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Not_in_Pig_main();
                                }
                            }
                            else if (getName.equals("Farrow")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Farrow_main();
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Medication")){
                                selectedFragment = new Medication_main();
                            }
                            break;
                        case 6:
                            if (getName.equals("Transfer Pen")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Transfer_Pen_main();
                                }
                            }
                            else if (getName.equals("Abort")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Abort_main();
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Vaccination")){
                                selectedFragment = new Vaccination_main();
                            }
                            break;
                        case 7:
                            if (getName.equals("Mortality")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Mortality_main();
                                }
                            }
                            else if (getName.equals("Not in Pig")){
                                if(!getDisableStatus.equals("1")){
                                    selectedDialogFragment = new Not_in_Pig_main();
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Feeding")){
                                selectedFragment = new Feeding_main();
                            }
                            break;
                        case 8:
                            if (getName.equals("Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){

                                }
                            }
                            else if (getName.equals("Add Schedule for Culling")){
                                if(!getDisableStatus.equals("1")){
                                    dialogBox_permission(getString(R.string.schedule_cull_msg), "schedule_culling");
                                }
                            }

                            // History -------------------------------------------------------------
                            else if (getName.equals("Remarks")){
                                selectedFragment = new Remarks_main();
                            }
                            break;
                    }

                    // Count piglet check ----------------------------------------------------------
                    if(isPigletsPressed()){
                        checkedCounter = 0;
                        try {
                            JSONArray jsonArray = new JSONArray(new Gson().toJson(RFscannerpiglets_add_models));
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject r = jsonArray.getJSONObject(i);

                                if (r.getString("status").equals("checked")){
                                    checkedCounter++;
                                }
                            }
                        } catch (JSONException e){}
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("array_piglets", new Gson().toJson(RFscannerpiglets_add_models));
                    bundle.putString("swine_scanned_id", swine_scanned_id);
                    bundle.putString("selectView", isPigletsPressed() ? "piglet" : "sow");
                    bundle.putString("pen_code", pen_code);
                    bundle.putString("swine_type", swine_type);
                    bundle.putString("pen_type", pen_type);
                    bundle.putString("pen_name", pen_name);
                    bundle.putString("latest_breeding_date", latest_breeding_date);
                    bundle.putString("checkedCounter", String.valueOf(checkedCounter));

                    if (selectedFragment != null || selectedDialogFragment != null){
                        // Action ------------------------------------------------------------------
                        if (isAction){
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                            if (prev != null) {ft.remove(prev);}
                            ft.addToBackStack(null);
                            selectedDialogFragment.setArguments(bundle);

                            // if piglet
                            if (isPigletsPressed()){
                                if (checkedCounter == 0){
                                    Toast.makeText(getActivity(), "Please select piglet", Toast.LENGTH_SHORT).show();
                                } else {
                                    selectedDialogFragment.show(ft, "dialog");
                                }
                            }
                            // else sow
                            else {
                                selectedDialogFragment.show(ft, "dialog");
                            }
                        }
                        // History -----------------------------------------------------------------
                        else {
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            selectedFragment.setArguments(bundle);
                            fragmentTransaction.add(R.id.container, selectedFragment, "fragmentRF").addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                }
            });


            // View
            switch (getId){
                case 0:
                    if (getName.equals("AI/Natural Breeding")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Transfer Pen")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Convert to Gilt/Boar")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    break;
                case 1:
                    if (getName.equals("Breeding Failed")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Mortality")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    break;
                case 2:
                    if (getName.equals("Confirm Pregnant")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Transfer Pen")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Transfer All to Farrowing")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Schedule for Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    else if (getName.equals("Add Schedule for Culling")){
                        if(getDisableStatus.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                        } else if(schedule_for_culling_status.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_red);
                            holder.txt_name.setText("Remove Schedule for Culling");
                            holder.txt_name.setTextColor(Color.WHITE);}
                    }
                    break;
                case 3:
                    if (getName.equals("Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Mortality")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Schedule for Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    else if (getName.equals("Add Schedule for Culling")){
                        if(getDisableStatus.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                        } else if(schedule_for_culling_status.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_red);
                            holder.txt_name.setText("Remove Schedule for Culling");
                            holder.txt_name.setTextColor(Color.WHITE);}
                    }
                    break;
                case 4:
                    if (getName.equals("Abort")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Schedule for Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Add Schedule for Culling")){
                        if(getDisableStatus.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                        } else if(schedule_for_culling_status.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_red);
                            holder.txt_name.setText("Remove Schedule for Culling");
                            holder.txt_name.setTextColor(Color.WHITE);}
                    }
                    else if (getName.equals("Wean")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                        else if (piglets_count.equals("0")){
                            holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                            btn_piglets.setBackgroundResource(R.drawable.btn_ripple_light_blue);
                            btn_piglets.setEnabled(false);
                        }
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    break;
                case 5:
                    if (getName.equals("Not in Pig")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Farrow")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    break;
                case 6:
                    if (getName.equals("Transfer Pen")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Abort")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    break;
                case 7:
                    if (getName.equals("Mortality")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Not in Pig")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    break;
                case 8:
                    if (getName.equals("Schedule for Culling")){
                        if(getDisableStatus.equals("1")){holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);}
                    }
                    else if (getName.equals("Add Schedule for Culling")){
                        if(getDisableStatus.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                        } else if(schedule_for_culling_status.equals("1")){
                            holder.btn_tap.setBackgroundResource(R.drawable.btn_ripple_red);
                            holder.txt_name.setText("Remove Schedule for Culling");
                            holder.txt_name.setTextColor(Color.WHITE);}
                    }
                    else if (getName.equals("")){
                        holder.btn_tap.setBackgroundResource(R.drawable.bg_swinecard_disable_action_button);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView txt_name;
            LinearLayout btn_tap;
            public MyHolder(View itemView) {
                super(itemView);
                btn_tap = itemView.findViewById(R.id.btn_tap);
                txt_name = itemView.findViewById(R.id.txt_name);
            }
        }
    }

    void dialogBox_permission(String msg, final String status){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        if (status.equals("schedule_culling")){
                            Add_sched_for_cull_main();
                        }
                        else if (status.equals("confirm_pregnant")){
                            confirm_pregnancy();
                        }
                        else if (status.equals("convert_to_swine")){
                            convert_to_swine();
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void Add_sched_for_cull_main(){
        showLoading(loadingScan, "Saving...").show();
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_add_sched_cull.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    showLoading(loadingScan, null).dismiss();
                    if(response.equals("1")){
                        get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Schedule for Culling Successfully Saved!", Toast.LENGTH_SHORT).show();
                    }else if(response.equals("2")){
                        get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Schedule for Culling Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).show();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("user_id", user_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void confirm_pregnancy(){
        showLoading(loadingScan, "Saving...").show();
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_confirm_pregnant.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    showLoading(loadingScan, null).dismiss();
                    if(response.replace("\n", "").equals("1")){
                        get_details(company_code, company_id, swine_scanned_id);
                        Toast.makeText(getActivity(), "Pregnancy Successfully Saved!", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).show();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void convert_to_swine(){
        showLoading(loadingScan, "Saving...").show();
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_convert_to_swine.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    showLoading(loadingScan, null).dismiss();
                    if(response.equals("1")){
                        get_details(company_code, company_id, swine_scanned_id);

                        Toast.makeText(getActivity(), "Swine Successfully Converted.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    showLoading(loadingScan, null).show();
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("swine_id", swine_scanned_id);
                hashMap.put("user_id", user_id);
                hashMap.put("pen_id", pen_code);
                hashMap.put("swine_code", swine_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(epcReceiver != null){
            getActivity().unregisterReceiver(epcReceiver);
        }

        if(!callback){
            ((ActivityMain)getActivity()).setPower("max");
        }else{
            intent_frag();
        }
    }

    public void intent_frag(){
        Intent intent = new Intent(getContext(), SwineSales_add.class);
        intent.putExtra("callback", "callback");
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        // getFragmentManager().popBackStack();
    }

    boolean isModalOpen = false;
    void dialogBox_msg(String msg){
        isModalOpen = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        isModalOpen = false;
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
