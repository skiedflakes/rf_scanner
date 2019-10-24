package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Feeding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Feeding_main extends Fragment {

    RecyclerView recyclerView;
    TextView null_result, error_result;
    ProgressBar loading_;
    ArrayList<Feeding_model> arrayList = new ArrayList<>();
    feeding_adapter adapter;
    String company_code, company_id, swine_scanned_id, selectView;


    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Feeding");
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
        View view = inflater.inflate(R.layout.feeding_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");
        selectView = getArguments().getString("selectView");

        recyclerView = view.findViewById(R.id.recyclerView);
        error_result = view.findViewById(R.id.error_result);
        null_result = view.findViewById(R.id.null_result);
        loading_ = view.findViewById(R.id.loading_);

        error_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_.setVisibility(View.VISIBLE);
                error_result.setVisibility(View.GONE);
                getFeedingDetails(company_code, company_id, swine_scanned_id);
            }
        });

        initMenu(view);
        getFeedingDetails(company_code, company_id, swine_scanned_id);
        return view;
    }

    private boolean isPiglets(){
        if (selectView.equals("piglet")){
            return true;
        } else {
            return false;
        }
    }

    public void getFeedingDetails(final String company_code, final String company_id, final String swine_id) {
        String URL = getString(R.string.URL_online)+"scan_eartag/history/"+ (isPiglets() ? "pig_piglets_feeding.php" : "feeding_history.php");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    if (!response.equals("{\"data\":[]}")){
                        arrayList.clear();
                        null_result.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loading_.setVisibility(View.GONE);
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("data");
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);
                            arrayList.add(new Feeding_model(r.getInt("id"),
                                    r.getString("count"),
                                    r.getString("product_id"),
                                    r.getString("pen_id"),
                                    r.getString("date"),
                                    r.getString("amount"),
                                    r.getString("total_cost"),
                                    r.getString("swine_id")));
                        }
                        adapter = new feeding_adapter(getContext(), arrayList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        null_result.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        loading_.setVisibility(View.GONE);
                    }
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

    public class feeding_adapter extends RecyclerView.Adapter<feeding_adapter.MyHolder> {

        ArrayList<Feeding_model> data;
        private Context context;
        private LayoutInflater inflater;
        int lastPosition = -1, num;

        public feeding_adapter(Context context, ArrayList<Feeding_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.feeding_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getAmount = data.get(position).getAmount();
            final String getCount = data.get(position).getCount();
            final String getDate = data.get(position).getDate();
            final String getPen_id = data.get(position).getPen_id();
            final String getProduct_id = data.get(position).getProduct_id();
            final String getTotal_cost = data.get(position).getTotal_cost();
            final String getSwine_id = data.get(position).getSwine_id();
            num = position;
            num++;

            holder.text_eartag.setText(getSwine_id);
            holder.text_count.setText(String.valueOf(num));
            holder.text_amount.setText(getAmount);
            holder.text_date.setText(getDate);
            holder.text_pen.setText(getPen_id);
            holder.text_feeds.setText(getProduct_id);
            holder.text_total_cost.setText(getTotal_cost);
            //setAnimation(holder.layout, position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_date, text_feeds, text_pen, text_amount, text_total_cost, text_count, text_eartag,
                    textDisplay_eartag, textDisplay_pen, textDisplay_feeds;
            CardView layout;
            Button btn_delete;
            ProgressBar delete_loading;
            public MyHolder(View itemView) {
                super(itemView);
                delete_loading = itemView.findViewById(R.id.delete_loading);
                btn_delete = itemView.findViewById(R.id.btn_delete);
                layout = itemView.findViewById(R.id.layout);
                text_feeds = itemView.findViewById(R.id.text_feeds);
                text_pen = itemView.findViewById(R.id.text_pen);
                text_amount = itemView.findViewById(R.id.text_amount);
                text_total_cost = itemView.findViewById(R.id.text_total_cost);
                text_date = itemView.findViewById(R.id.text_date);
                text_count = itemView.findViewById(R.id.text_count);
                text_eartag = itemView.findViewById(R.id.text_eartag);
                textDisplay_eartag = itemView.findViewById(R.id.textDisplay_eartag);
                textDisplay_pen = itemView.findViewById(R.id.textDisplay_pen);
                textDisplay_feeds = itemView.findViewById(R.id.textDisplay_feeds);

                if (isPiglets()){
                    textDisplay_eartag.setVisibility(View.VISIBLE);
                    text_eartag.setVisibility(View.VISIBLE);
                    textDisplay_pen.setVisibility(View.GONE);
                    text_pen.setVisibility(View.GONE);
                    textDisplay_feeds.setText("Feed Type: ");
                } else {
                    textDisplay_eartag.setVisibility(View.GONE);
                    text_eartag.setVisibility(View.GONE);
                    textDisplay_pen.setVisibility(View.VISIBLE);
                    text_pen.setVisibility(View.VISIBLE);
                    textDisplay_feeds.setText("Feeds: ");
                }
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
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
