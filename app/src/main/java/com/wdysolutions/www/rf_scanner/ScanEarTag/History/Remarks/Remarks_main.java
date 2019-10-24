package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Remarks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Remarks.Add.Remarks_add;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Vaccination.Add_Vaccine.addVaccine_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Remarks_main extends Fragment {

    RecyclerView recyclerView;
    TextView null_result, error_result;
    ProgressBar loading_;
    ArrayList<Remarks_model> arrayList = new ArrayList<>();
    Remarks_adapter adapter;
    String company_code, company_id, swine_scanned_id, user_id, currentDate="";
    Button btn_add;


    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Remarks");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remarks_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        btn_add = view.findViewById(R.id.btn_add);
        recyclerView = view.findViewById(R.id.recyclerView);
        error_result = view.findViewById(R.id.error_result);
        null_result = view.findViewById(R.id.null_result);
        loading_ = view.findViewById(R.id.loading_);

        error_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_.setVisibility(View.VISIBLE);
                error_result.setVisibility(View.GONE);
                getRemarksDetails(company_code, company_id, swine_scanned_id);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("swine_scanned_id", swine_scanned_id);
                bundle.putString("currentDate", currentDate);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {ft.remove(prev);}
                ft.addToBackStack(null);
                Remarks_add dialogFragment = new Remarks_add();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(ft, "dialog");
            }
        });

        initMenu(view);
        getRemarksDetails(company_code, company_id, swine_scanned_id);
        return view;
    }

    public void getRemarksDetails(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/history/remarks.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    arrayList.clear();
                    null_result.setVisibility(View.GONE);
                    error_result.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    loading_.setVisibility(View.GONE);

                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("data");

                    if (String.valueOf(details).equals("[]")){
                        null_result.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        loading_.setVisibility(View.GONE);
                    }
                    else {
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);

                            arrayList.add(new Remarks_model(r.getInt("id"),
                                    r.getString("count"),
                                    r.getString("remarks"),
                                    r.getString("date")));
                        }
                        adapter = new Remarks_adapter(getContext(), arrayList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    }

                    // get Current date
                    JSONArray data_date = Object.getJSONArray("data_date");
                    JSONObject r = data_date.getJSONObject(0);
                    currentDate = r.getString("current_date");

                } catch (Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    error_result.setVisibility(View.VISIBLE);
                    null_result.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    loading_.setVisibility(View.GONE);
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

    public class Remarks_adapter extends RecyclerView.Adapter<Remarks_adapter.MyHolder> {

        ArrayList<Remarks_model> data;
        private Context context;
        private LayoutInflater inflater;
        int lastPosition = -1, num;

        public Remarks_adapter(Context context, ArrayList<Remarks_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.remarks_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getDate = data.get(position).getDate();
            final String getCount = data.get(position).getCount();
            final String getRemarks = data.get(position).getRemarks();
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
            holder.text_date.setText(getDate);
            holder.text_remarks.setText(getRemarks);
            //setAnimation(holder.layout, position);
            holder.delete_loading.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN );

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBox_delete(String.valueOf(getId), holder, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_date, text_remarks, text_count;
            CardView layout;
            Button btn_delete;
            ProgressBar delete_loading;
            public MyHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.layout);
                text_count = itemView.findViewById(R.id.text_count);
                text_date = itemView.findViewById(R.id.text_date);
                text_remarks = itemView.findViewById(R.id.text_remarks);
                btn_delete = itemView.findViewById(R.id.btn_delete);
                delete_loading = itemView.findViewById(R.id.delete_loading);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        public void deleteRemrks(final String remarks_id, final MyHolder myHolder, final int position) {
            myHolder.delete_loading.setVisibility(View.VISIBLE);
            myHolder.btn_delete.setVisibility(View.GONE);
            String URL = getString(R.string.URL_online)+"scan_eartag/history/remarks_delete.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{
                        myHolder.delete_loading.setVisibility(View.GONE);
                        myHolder.btn_delete.setVisibility(View.VISIBLE);

                        if(response.equals("1")){
                            data.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try{
                        myHolder.delete_loading.setVisibility(View.GONE);
                        myHolder.btn_delete.setVisibility(View.VISIBLE);
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
                    hashMap.put("remarks_id", remarks_id);
                    hashMap.put("user_id", user_id);
                    return hashMap;
                }
            };
            AppController.getInstance().setVolleyDuration(stringRequest);
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

        void dialogBox_delete(final String remarks_id, final MyHolder myHolder, final int position){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("Are you sure to delete?");
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            deleteRemrks(remarks_id, myHolder, position);
                        }
                    });
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        alertDialog.setView(input);
        input.setText(name);
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
