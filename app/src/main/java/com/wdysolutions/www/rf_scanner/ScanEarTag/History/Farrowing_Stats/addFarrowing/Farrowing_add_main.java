package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addFarrowing;

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
import android.widget.Button;
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
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Farrowing_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Farrowing_add_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    TextView btn_date_breeding, btn_date_farrowed, btn_date_weaned;
    EditText input_stillbirth, input_mummified, input_bornalive, input_abnormal, input_undersize, input_pre_wean,
            input_post_wean, input_ave_birth_wt, input_condemn,
    input_ave_weaning_wt, input_of_heads_weaned;
    Button btn_save;
    ProgressBar loading_save, progressBar;
    String dateBreeding, dateFarrowed, dateWeaned, selected;
    String company_code, company_id, swine_scanned_id;
    String latest_breeding_date, latest_weaning_date, latest_farrowing_date, selectedMaxDate;
    SessionPreferences sessionPreferences;
    LinearLayout layout_add;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farrowing_add_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        input_condemn = view.findViewById(R.id.input_condemn);
        layout_add = view.findViewById(R.id.layout_add);
        progressBar = view.findViewById(R.id.progressBar);
        btn_date_breeding = view.findViewById(R.id.btn_date_breeding);
        btn_date_farrowed = view.findViewById(R.id.btn_date_farrowed);
        btn_date_weaned = view.findViewById(R.id.btn_date_weaned);
        input_stillbirth = view.findViewById(R.id.input_stillbirth);
        input_mummified = view.findViewById(R.id.input_mummified);
        input_bornalive = view.findViewById(R.id.input_bornalive);
        input_abnormal = view.findViewById(R.id.input_abnormal);
        input_undersize = view.findViewById(R.id.input_undersize);
        input_pre_wean = view.findViewById(R.id.input_pre_wean);
        input_post_wean = view.findViewById(R.id.input_post_wean);
        input_ave_birth_wt = view.findViewById(R.id.input_ave_birth_wt);
        input_ave_weaning_wt = view.findViewById(R.id.input_ave_weaning_wt);
        input_of_heads_weaned = view.findViewById(R.id.input_of_heads_weaned);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);

        btn_date_breeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = "Breeding";
                selectedMaxDate = latest_breeding_date;
                openDatePicker(false);
            }
        });

        btn_date_farrowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = "Farrowed";
                selectedMaxDate = latest_farrowing_date;
                openDatePicker(false);
            }
        });

        btn_date_weaned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = "Weaned";
                selectedMaxDate = latest_weaning_date;
                openDatePicker(false);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_date_breeding.getText().equals("")){
                    Toast.makeText(getActivity(), "Please select Breeding date.", Toast.LENGTH_SHORT).show();
                } else if (btn_date_farrowed.getText().equals("")){
                    Toast.makeText(getActivity(), "Please select Farrowed date.", Toast.LENGTH_SHORT).show();
                } else if (btn_date_weaned.getText().equals("")){
                    Toast.makeText(getActivity(), "Please select Weaned date.", Toast.LENGTH_SHORT).show();
                } else if (input_bornalive.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Please enter born alive.", Toast.LENGTH_SHORT).show();
                } else if (input_of_heads_weaned.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter # of heads weaned.", Toast.LENGTH_SHORT).show();
                } else if (input_stillbirth.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter stillbirth.", Toast.LENGTH_SHORT).show();
                } else if (input_mummified.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter mummified.", Toast.LENGTH_SHORT).show();
                } else if (input_abnormal.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter abnormal.", Toast.LENGTH_SHORT).show();
                } else if (input_undersize.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter under size.", Toast.LENGTH_SHORT).show();
                } else if (input_ave_birth_wt.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter average birth weight.", Toast.LENGTH_SHORT).show();
                } else if (input_pre_wean.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter pre wean mortality.", Toast.LENGTH_SHORT).show();
                } else if (input_post_wean.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter post wean mortality.", Toast.LENGTH_SHORT).show();
                } else if (input_ave_weaning_wt.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter average weaning weight.", Toast.LENGTH_SHORT).show();
                } else if (input_condemn.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "please enter condemned.", Toast.LENGTH_SHORT).show();
                }
                else {
                     if (compareDates(dateBreeding, dateFarrowed)){
                        Toast.makeText(getActivity(), "Date Farrow must greater than Breeding Date.", Toast.LENGTH_LONG).show();
                     } else {
                         addFarrowing(company_code, company_id, swine_scanned_id,
                                 input_bornalive.getText().toString(),
                                 input_of_heads_weaned.getText().toString(),
                                 input_stillbirth.getText().toString(),
                                 input_mummified.getText().toString(),
                                 input_abnormal.getText().toString(),
                                 input_undersize.getText().toString(),
                                 input_ave_birth_wt.getText().toString(),
                                 input_pre_wean.getText().toString(),
                                 input_post_wean.getText().toString(),
                                 input_ave_weaning_wt.getText().toString(),
                                 input_condemn.getText().toString());
                     }
                }
            }
        });

        limitDates(company_code, company_id, swine_scanned_id);
        return view;
    }

    public static boolean compareDates(String d1,String d2) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            if(date1.after(date2)){
                return true;
            }
        }
        catch(ParseException ex){}
        return false;
    }

    private String minus1day(String date){
        try {
            if (date.equals("0000-00-00")){
                return "";
            }
            else {
                final Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                c.setTime(sdf.parse(date));
                c.add(Calendar.DATE, -1);
                String date_min_minus1 = sdf.format(c.getTime());
                return date_min_minus1;
            }
        } catch (ParseException e){}
        return null;
    }

    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        Bundle bundle = new Bundle();
        bundle.putString("maxDate", selectedMaxDate);
        bundle.putString("maxDate_minus", "");
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(String date) {
        if (selected.equals("Breeding")){
            dateBreeding = date;
            btn_date_breeding.setText(dateBreeding);
        }
        else if (selected.equals("Farrowed")){
            dateFarrowed = date;
            btn_date_farrowed.setText(dateFarrowed);
        }
        else if (selected.equals("Weaned")){
            dateWeaned = date;
            btn_date_weaned.setText(dateWeaned);
        }
    }

    public void limitDates(final String company_code, final String company_id, final String swine_id) {
        progressBar.setVisibility(View.VISIBLE);
        layout_add.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/limit_dates.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressBar.setVisibility(View.GONE);
                    layout_add.setVisibility(View.VISIBLE);
                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("data");
                    JSONObject r = details.getJSONObject(0);
                    latest_breeding_date = r.getString("latest_breeding_date");
                    latest_weaning_date = r.getString("latest_weaning_date");
                    latest_farrowing_date = r.getString("latest_farrowing_date");


                } catch (JSONException e) {}
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

    public void addFarrowing(final String company_code, final String company_id, final String swine_id, final String born_alive, final String heads_weaned,
                             final String still_birth, final String mummified, final String abnormal, final String undersize, final String ave_birth_wt, final String pre_wean_mort,
                             final String post_wean_mort, final String ave_weaning_wt, final String condemned) {
        loading_save.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_farrowing_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    //dialogBox(response);
                    loading_save.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                    if (response.equals("okay")){
                        FragmentManager fm = getFragmentManager();
                        Farrowing_main fragment = (Farrowing_main)fm.findFragmentByTag("fragmentRF");
                        fragment.getFarrowingDetails(company_code, company_id, swine_id);
                        Toast.makeText(getActivity(), "Successfully saved.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Failed to execeute query.", Toast.LENGTH_SHORT).show();
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
                hashMap.put("swine_id", swine_id);
                hashMap.put("breeding_date", dateBreeding);
                hashMap.put("date_weaned", dateWeaned);
                hashMap.put("date_farrow", dateFarrowed);
                hashMap.put("born_alive", born_alive);
                hashMap.put("heads_weaned", heads_weaned);
                hashMap.put("still_birth", still_birth);
                hashMap.put("mummified", mummified);
                hashMap.put("abnormal", abnormal);
                hashMap.put("undersize", undersize);
                hashMap.put("ave_birth_wt", ave_birth_wt);
                hashMap.put("pre_wean_mort", pre_wean_mort);
                hashMap.put("post_wean_mort", post_wean_mort);
                hashMap.put("ave_weaning_wt", ave_weaning_wt);
                hashMap.put("condemned", condemned);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
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
