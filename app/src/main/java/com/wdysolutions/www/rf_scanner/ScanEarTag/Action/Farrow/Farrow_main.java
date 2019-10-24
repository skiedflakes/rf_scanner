package com.wdysolutions.www.rf_scanner.ScanEarTag.Action.Farrow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerCustom;
import com.wdysolutions.www.rf_scanner.DatePicker.DatePickerSelectionInterfaceCustom;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Farrow_main extends DialogFragment implements DatePickerSelectionInterfaceCustom {

    Button btn_save, btn_continue;
    Spinner spinner_genetic_breed, spinner_genetic_line, spinner_progeny;
    TextView btn_date;
    RecyclerView recyclerView_add;
    ProgressBar loading_save;
    LinearLayout layout_2, layout_1;
    EditText editText;
    CheckBox checkbox_temp;
    int isCheck = 0;
    String selectedBreed = "", selectedLine = "", selectedProgeny = "", selectedGender = "", selectedStatus = "",
            selectedDate ="", latest_breeding_date, current_date;
    ArrayList<Genetic_breed_model> genetic_breedModels = new ArrayList<>();
    ArrayList<Genetic_line_model> genetic_line_models = new ArrayList<>();
    ArrayList<Farrow_model> farrow_models = new ArrayList<>();
    ArrayList<Progeny_model> progeny_models = new ArrayList<>();
    ArrayList<Eartag_model> eartag_models = new ArrayList<>();
    farrow_adapter farrowAdapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farrow_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        final String company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        final String company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        final String user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        final String swine_id = getArguments().getString("swine_scanned_id");
        final String pen_code = getArguments().getString("pen_code");
        latest_breeding_date = getArguments().getString("latest_breeding_date");

        spinner_genetic_breed = view.findViewById(R.id.spinner_genetic_breed);
        spinner_genetic_line = view.findViewById(R.id.spinner_genetic_line);
        spinner_progeny = view.findViewById(R.id.spinner_progeny);
        btn_date = view.findViewById(R.id.btn_date);
        recyclerView_add = view.findViewById(R.id.recyclerView_add);
        loading_save = view.findViewById(R.id.loading_save);
        btn_save = view.findViewById(R.id.btn_save);
        layout_2 = view.findViewById(R.id.layout_2);
        layout_1 = view.findViewById(R.id.layout_1);
        editText = view.findViewById(R.id.editText);
        checkbox_temp = view.findViewById(R.id.checkbox_temp);
        btn_continue = view.findViewById(R.id.btn_continue);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDate();
            }
        });

        checkbox_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_temp.isChecked()){
                    isCheck = 1;
                } else {
                    isCheck = 0;
                }
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().equals("")){
                    dialogBox("Please input valid value for number of piglets.");
                } else {
                    checkSwineLimit(company_code, company_id, swine_id, editText.getText().toString());

                    int total_piglets = Integer.valueOf(editText.getText().toString());
                    for (int i=0; i<total_piglets; i++){
                        farrow_models.add(new Farrow_model("", "", "", ""));
                    }

                    farrowAdapter = new farrow_adapter(getContext(), farrow_models);
                    RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                    recyclerView_add.setLayoutManager(layoutManager1);
                    recyclerView_add.setNestedScrollingEnabled(false);
                    recyclerView_add.setAdapter(farrowAdapter);
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countWeight = 0, countStatus = 0, countGender = 0, countEartag = 0;
                try {
                    // Check if is fill
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(farrow_models));
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("weight").equals("")){countWeight++;}
                        if (jsonObject.getString("status").equals("")){countStatus++;}
                        if (jsonObject.getString("gender").equals("")){countGender++;}
                        if (jsonObject.getString("swine_code").equals("")){countEartag++;}
                    }

                    if (selectedBreed.equals("")){
                        Toast.makeText(getActivity(), "Please select genetic breed", Toast.LENGTH_SHORT).show();
                    } else if (selectedLine.equals("")){
                        Toast.makeText(getActivity(), "Please select genetic line", Toast.LENGTH_SHORT).show();
                    } else if (selectedProgeny.equals("")){
                        Toast.makeText(getActivity(), "Please select progeny", Toast.LENGTH_SHORT).show();
                    } else if (selectedDate.equals("")){
                        Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
                    } else if (countStatus > 0){
                        Toast.makeText(getActivity(), "Please select status", Toast.LENGTH_SHORT).show();
                    } else if (countEartag > 0 && isCheck == 0){
                        Toast.makeText(getActivity(), "Please enter eartag", Toast.LENGTH_SHORT).show();
                    } else if (countGender > 0){
                        Toast.makeText(getActivity(), "Please select gender", Toast.LENGTH_SHORT).show();
                    } else if (countWeight > 0){
                        Toast.makeText(getActivity(), "Please enter weight", Toast.LENGTH_SHORT).show();
                    } else {
                        saveFarrow(company_code,
                                company_id,
                                swine_id,
                                user_id,
                                pen_code);
                    }

                } catch (JSONException e){}
            }
        });

        return view;
    }

    private void checkDate(){
        try {
            // plus 1 day to latest_breeding_date
            final Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(sdf.parse(latest_breeding_date));
            c.add(Calendar.DATE, 1);
            String date_min_plus1 = sdf.format(c.getTime());

            // if current_date same as latest_breeding_date
            String[] date = current_date.split(" ");
            if (date[0].equals(date_min_plus1)){
                Toast.makeText(getActivity(), "Can't select date right now", Toast.LENGTH_SHORT).show();
            } else {
                openDatePicker(false);
            }

        } catch (ParseException e) {}
    }

    private void checkSwineLimit(final String company_code, final String company_id, final String swine_id, final String number_of_piglets) {
        btn_continue.setText("Processing...");
        btn_continue.setEnabled(false);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/get_swine_limit.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if (response.equals("1")){
                        getFarrow(company_code, company_id, swine_id);
                    } else if (response.equals("0")){
                        dialogBox("You have reached the maximum number of non-sows for your company. Please contact your technical support for more info.");
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_continue.setText("Continue");
                    btn_continue.setEnabled(true);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("number_of_piglets", number_of_piglets);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getFarrow(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_farrow_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    layout_1.setVisibility(View.GONE);
                    layout_2.setVisibility(View.VISIBLE);
                    JSONObject Object = new JSONObject(response);


                    // Current date ----------------------------------------------------------------
                    final JSONArray response_date = Object.getJSONArray("response_date");
                    JSONObject json_date = (JSONObject) response_date.get(0);
                    current_date = json_date.getString("current_date");
                    String[] maxDate = current_date.split(" ");
                    selectedDate = maxDate[0];
                    btn_date.setText(selectedDate);


                    // Ear Tag ---------------------------------------------------------------------
                    final JSONArray diag_eartag = Object.getJSONArray("response_eartag");
                    for (int i = 0; i < diag_eartag.length(); i++) {
                        JSONObject cusObj_eartag = (JSONObject) diag_eartag.get(i);

                        eartag_models.add(new Eartag_model(cusObj_eartag.getString("swine_code")));
                    }


                    // Genetic breed ---------------------------------------------------------------
                    genetic_breedModels.add(new Genetic_breed_model("0","Please Select"));
                    final JSONArray diag_breed = Object.getJSONArray("response_genetic_breed");
                    for (int i = 0; i < diag_breed.length(); i++) {
                        JSONObject cusObj_breed = (JSONObject) diag_breed.get(i);

                        genetic_breedModels.add(new Genetic_breed_model(cusObj_breed.getString("genetic_id"),
                                cusObj_breed.getString("genetic")));
                    }


                    // Populate Spinner
                    List<String> lables_breed = new ArrayList<>();
                    for (int i = 0; i < genetic_breedModels.size(); i++) {
                        lables_breed.add(genetic_breedModels.get(i).getGenetic());
                    }
                    ArrayAdapter<String> spinnerAdapter_breed = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_breed);
                    spinnerAdapter_breed.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_genetic_breed.setAdapter(spinnerAdapter_breed);
                    spinner_genetic_breed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Genetic_breed_model click = genetic_breedModels.get(position);
                            if (!click.getGenetic().equals("Please Select")){
                                selectedBreed = String.valueOf(click.getGenetic_id());
                            } else {
                                selectedBreed = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Genetic line ----------------------------------------------------------------
                    genetic_line_models.add(new Genetic_line_model("0","Please Select"));
                    final JSONArray diag_line = Object.getJSONArray("response_genetic_line");
                    for (int i = 0; i < diag_line.length(); i++) {
                        JSONObject cusObj_line = (JSONObject) diag_line.get(i);

                        genetic_line_models.add(new Genetic_line_model(cusObj_line.getString("genetic_line_id"),
                                cusObj_line.getString("genetic_line")));
                    }

                    // Populate Spinner
                    List<String> lables_line = new ArrayList<>();
                    for (int i = 0; i < genetic_line_models.size(); i++) {
                        lables_line.add(genetic_line_models.get(i).getGenetic_line());
                    }
                    ArrayAdapter<String> spinnerAdapter_line = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_line);
                    spinnerAdapter_line.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_genetic_line.setAdapter(spinnerAdapter_line);
                    spinner_genetic_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Genetic_line_model click = genetic_line_models.get(position);
                            if (!click.getGenetic_line().equals("Please Select")){
                                selectedLine = String.valueOf(click.getGenetic_line_id());
                            } else {
                                selectedLine = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


                    // Progeny ---------------------------------------------------------------------
                    progeny_models.add(new Progeny_model("0","Please Select"));
                    final JSONArray diag_progeny = Object.getJSONArray("response_progeny");
                    for (int i = 0; i < diag_progeny.length(); i++) {
                        JSONObject cusObj_progeny = (JSONObject) diag_progeny.get(i);

                        progeny_models.add(new Progeny_model(cusObj_progeny.getString("classification_id"),
                                cusObj_progeny.getString("classification")));
                    }

                    // Populate Spinner
                    List<String> lables_progeny = new ArrayList<>();
                    for (int i = 0; i < progeny_models.size(); i++) {
                        lables_progeny.add(progeny_models.get(i).getClassification());
                    }
                    ArrayAdapter<String> spinnerAdapter_progeny = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, lables_progeny);
                    spinnerAdapter_progeny.setDropDownViewResource(R.layout.custom_spinner);
                    spinner_progeny.setAdapter(spinnerAdapter_progeny);
                    spinner_progeny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            Progeny_model click = progeny_models.get(position);
                            if (!click.getClassification().equals("Please Select")){
                                selectedProgeny = String.valueOf(click.getClassification_id());
                            } else {
                                selectedProgeny = "";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {}
                    });


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
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private class farrow_adapter extends RecyclerView.Adapter<farrow_adapter.MyHolder> {
        ArrayList<Farrow_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num, globalPosition;

        public farrow_adapter(Context context, ArrayList<Farrow_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.farrow_container, parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_count, text_available;
            EditText editText_eartag, editText_weight;
            Spinner spinner_status, spinner_gender;

            public MyHolder(View itemView) {
                super(itemView);
                text_available = itemView.findViewById(R.id.text_available);
                text_count = itemView.findViewById(R.id.text_count);
                spinner_status = itemView.findViewById(R.id.spinner_status);
                editText_eartag = itemView.findViewById(R.id.editText_eartag);
                spinner_gender = itemView.findViewById(R.id.spinner_gender);
                editText_weight = itemView.findViewById(R.id.editText_weight);
                globalPosition = getAdapterPosition();


                if (isCheck == 1){
                    editText_eartag.setText("Temporary");
                    editText_eartag.setEnabled(false);
                }
                else {
                    editText_eartag.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            String charString = editable.toString();
                            String selected = "";

                            // Check eartag if available
                            if(charString.equals("")){
                                text_available.setText("");
                                selected = "";
                            }
                            else if (checkNameIfAvailable(eartag_models, charString)){
                                text_available.setText("Not available");
                                text_available.setTextColor(getResources().getColor(R.color.btn_light_red_color1));
                                selected = "";
                            } else {
                                text_available.setText("Available");
                                text_available.setTextColor(Color.GREEN);
                                selected = charString;
                            }

                            // Save to array afterTextChanged
                            farrow_models.set(getAdapterPosition(), new Farrow_model(data.get(getAdapterPosition()).getStatus(),
                                    selected,
                                    data.get(getAdapterPosition()).getGender(),
                                    data.get(getAdapterPosition()).getWeight()));
                        }
                    });
                }

                editText_weight.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        farrow_models.set(getAdapterPosition(), new Farrow_model(data.get(getAdapterPosition()).getStatus(),
                                data.get(getAdapterPosition()).getSwine_code(),
                                data.get(getAdapterPosition()).getGender(),
                                String.valueOf(editable)));
                    }
                });

                ArrayAdapter<String> spinnerAdapter_gender = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.gender_array));
                spinnerAdapter_gender.setDropDownViewResource(R.layout.custom_spinner);
                spinner_gender.setAdapter(spinnerAdapter_gender);
                spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String selected = adapterView.getItemAtPosition(position).toString();
                        if (selected.equals("Select")){
                            selectedGender = "";
                        } else {
                            selectedGender = selected;
                        }
                        farrow_models.set(getAdapterPosition(), new Farrow_model(data.get(getAdapterPosition()).getStatus(),
                                data.get(getAdapterPosition()).getSwine_code(),
                                selectedGender,
                                data.get(getAdapterPosition()).getWeight()));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

                ArrayAdapter<String> spinnerAdapter_status = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.status_array));
                spinnerAdapter_status.setDropDownViewResource(R.layout.custom_spinner);
                spinner_status.setAdapter(spinnerAdapter_status);
                spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String selected = adapterView.getItemAtPosition(position).toString();

                        if (selected.equals("Select")){selectedStatus = "";}
                        else if(selected.equals("Alive - Normal")){selectedStatus = "1";}
                        else if(selected.equals("Alive - Abnormal")){selectedStatus = "2";}
                        else if(selected.equals("Alive - Undersize")){selectedStatus = "3";}
                        else if(selected.equals("Mummified")){selectedStatus = "4";}
                        else if(selected.equals("Stillbirth")){selectedStatus = "5";}

                        farrow_models.set(getAdapterPosition(), new Farrow_model(selectedStatus,
                                data.get(getAdapterPosition()).getSwine_code(),
                                data.get(getAdapterPosition()).getGender(),
                                data.get(getAdapterPosition()).getWeight()));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }
        }
    }

    boolean checkNameIfAvailable(ArrayList<Eartag_model> list, String name) {
        for (Eartag_model item : list) {
            if (item.getSwine_code().toLowerCase().equals(name.toLowerCase()) ||
                    item.getSwine_code().toUpperCase().equals(name.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private void saveFarrow(final String company_code, final String company_id, final String swine_id, final String user_id, final String pen_id) {
        btn_save.setVisibility(View.GONE);
        btn_save.setEnabled(false);
        loading_save.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"scan_eartag/action/pig_farrow_add.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    btn_save.setVisibility(View.VISIBLE);
                    btn_save.setEnabled(true);
                    loading_save.setVisibility(View.GONE);
                    if (response.equals("1")){
                        dismiss();
                        FragmentManager fm = getFragmentManager();
                        RFscanner_main fragment = (RFscanner_main)fm.findFragmentByTag("Main_menu");
                        fragment.get_details(company_code, company_id, swine_id);
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("0")){
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_save.setEnabled(false);
                    btn_save.setVisibility(View.VISIBLE);
                    loading_save.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_code", company_code);
                hashMap.put("company_id", company_id);
                hashMap.put("temp_id", String.valueOf(isCheck));
                hashMap.put("swine_id", swine_id);
                hashMap.put("user_id", user_id);
                hashMap.put("pen_id", pen_id);
                hashMap.put("p_genetic_breed", selectedBreed);
                hashMap.put("p_genetic_line", selectedLine);
                hashMap.put("p_progeny", selectedProgeny);
                hashMap.put("farrowing_date", selectedDate);
                hashMap.put("all_data", new Gson().toJson(farrow_models));
                hashMap.put("num_piglet", String.valueOf(farrow_models.size()));
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void openDatePicker(boolean isMinusDays21) {
        DatePickerCustom datePickerFragment = new DatePickerCustom();

        Bundle bundle = new Bundle();
        bundle.putString("maxDate", "");
        bundle.putString("minDate", latest_breeding_date);
        bundle.putString("maxDate_minus", "");
        bundle.putBoolean("isSetMinDate", true);
        bundle.putBoolean("isMinusDays", isMinusDays21);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.delegate = this;
        datePickerFragment.setCancelable(false);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(String date) {
        selectedDate = date;
        btn_date.setText(selectedDate);
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(name);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


}
