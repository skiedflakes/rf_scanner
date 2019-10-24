package com.wdysolutions.www.rf_scanner.LocateEartag;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LocateEartag_main extends Fragment implements Modal_fragment.dialog_interface {
    SessionPreferences sessionPreferences;
    ArrayList<LocateEartag_model_pig> LocateEartag_pig_list = new ArrayList<>();
    String company_code, company_id;
    RecyclerView rec_pigs;
    LocateEartag_adapter adapter_pig;
    Button btn_add, btn_find;
    EditText et_add, et_test;
    BroadcastReceiver epcReceiver;
    ProgressDialog loadingScan;
    TextView tv_clear,tv_refresh;
    MenuItem max, min,low;
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

        MenuItem test;
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

    private ProgressDialog showLoading(ProgressDialog loading){
        loading.setMessage("Checking eartag...");
        loading.setCancelable(false);
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.locateeartag_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        loadingScan = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        rec_pigs = view.findViewById(R.id.rec_list_pigs);
        btn_add = view.findViewById(R.id.btn_add);
        et_add = view.findViewById(R.id.et_add);
        tv_clear = view.findViewById(R.id.tv_clear);
        tv_refresh = view.findViewById(R.id.tv_refresh);
        tx_range = view.findViewById(R.id.tx_range);

        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LocateEartag_pig_list.isEmpty()){
                    Toast.makeText(getActivity(), "No ear tag found", Toast.LENGTH_SHORT).show();
                } else {
                    dialogBox();
                }
            }
        });

        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        rec_pigs.addItemDecoration(new DividerItemDecoration(rec_pigs.getContext(), DividerItemDecoration.VERTICAL));


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ear_tag = et_add.getText().toString();

                if (ear_tag.equals("")){
                    Toast.makeText(getActivity(), "Please enter eartag", Toast.LENGTH_SHORT).show();
                } else {
                    add_eartag(company_id, company_code, ear_tag);
                }
            }
        });

        if(epcReceiver == null){
            epcReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String tag = intent.getExtras().get("epc").toString();

                    if (!tag.equals("No transponders seen")){
                   //     updateSingleItem(hexToASCII(tag));

                        String eart_tag =  hexToASCII(tag);
                        String[] separated = eart_tag.split("-");

                        String str_characters = separated[0].replaceAll("[^A-Za-z]+", "");
                        String str_digits = separated[0].replaceAll("\\D+","");
                        if(str_characters.equals("wdy")){
                            updateSingleItem(separated[1]);
                        }else{
                            Toast.makeText(context, "ear tag invalid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        getActivity().registerReceiver(epcReceiver, new IntentFilter("epc_receive"));

        //open swine card
        Bundle arguments = getArguments();
        if(arguments!=null){
            String eartag = arguments.getString("eartag");
            String swine_id = arguments.getString("swine_id");
            if(!eartag.equals("")&&eartag!=null){

                //add_eartag(company_id, company_code, eartag);

                LocateEartag_pig_list.add(new LocateEartag_model_pig(Integer.valueOf(swine_id), eartag, 0));
                adapter_pig = new LocateEartag_adapter(getContext(), LocateEartag_pig_list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rec_pigs.setLayoutManager(layoutManager);
                rec_pigs.setAdapter(adapter_pig);
            }
        }

        initMenu(view);
        return view;
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

    public void add_eartag(final String company_id, final String company_code, final String ear_tag) {
        showLoading(loadingScan).show();
        String URL = getString(R.string.URL_online) + "locate_eartag/add_eartag.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showLoading(loadingScan).dismiss();

                    int swine_id;
                    String swine_code;
                    int found = 0;


                    if (response.equals("0")) {
                        set_modal_error("System error", "No eartag found","red");

                    } else {
                        et_add.setText("");
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("response_swine");
                        JSONObject x = details.getJSONObject(0);
                        swine_id = x.getInt("swine_id");
                        swine_code = x.getString("swine_code");

                        //Toast.makeText(getContext(),  String.valueOf(swine_id), Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray = new JSONArray(new Gson().toJson(LocateEartag_pig_list));
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject r = jsonArray.getJSONObject(i);
                            String swine_id2 = String.valueOf(r.getInt("swine_id"));
                            if (swine_id2.equals(String.valueOf(swine_id))) {

                                found = 1;
                            }
                        }

                        if (found == 1) {
                            Toast.makeText(getActivity(), "Ear tag already listed", Toast.LENGTH_SHORT).show();
                        } else {

                            LocateEartag_pig_list.add(new LocateEartag_model_pig(swine_id, swine_code, 0));
                            adapter_pig = new LocateEartag_adapter(getContext(), LocateEartag_pig_list);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            rec_pigs.setLayoutManager(layoutManager);
                            rec_pigs.setAdapter(adapter_pig);
                        }
                    }
                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    showLoading(loadingScan).dismiss();
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("ear_tag", ear_tag);
                hashMap.put("company_code", company_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void updateSingleItem(String scanned_swine_id) {
       try{
           int found = 0;

           JSONArray jsonArray = new JSONArray(new Gson().toJson(LocateEartag_pig_list));
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject r = jsonArray.getJSONObject(i);
               String swine_id2 = String.valueOf(r.getInt("swine_id"));
               if (swine_id2.equals(String.valueOf(scanned_swine_id))) {
                   String swine_code = r.getString("swine_code");
                   LocateEartag_pig_list.set(i,new LocateEartag_model_pig(r.getInt("swine_id"),swine_code,1));
                   adapter_pig.notifyDataSetChanged();
                   rec_pigs.smoothScrollToPosition(i);
               }
           }
       }catch (Exception e){}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(epcReceiver != null){getActivity().unregisterReceiver(epcReceiver);}
        ((ActivityMain)getActivity()).setPower("max");
    }

    public void set_modal_error(String tittle,String text,String color){
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
        fragment.setCancelable(true);
    }

    void dialogBox(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to clear all scanned tags?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        LocateEartag_pig_list.clear();
                        rec_pigs.setAdapter(null);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    public void refresh(){

        try{
            int found = 0;

            JSONArray jsonArray = new JSONArray(new Gson().toJson(LocateEartag_pig_list));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject r = jsonArray.getJSONObject(i);

                    String swine_code = r.getString("swine_code");
                    LocateEartag_pig_list.set(i,new LocateEartag_model_pig(r.getInt("swine_id"),swine_code,0));

            }
            adapter_pig = new LocateEartag_adapter(getContext(), LocateEartag_pig_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rec_pigs.setLayoutManager(layoutManager);
            rec_pigs.setAdapter(adapter_pig);
        }catch (Exception e){}
    }

    @Override
    public void senddata(int okay) {

    }
}
