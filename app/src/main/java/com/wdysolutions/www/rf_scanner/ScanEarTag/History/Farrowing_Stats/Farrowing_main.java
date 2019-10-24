package com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.Born_Alive.Farrowing_Born_alive_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addBreedingFailed.Farrowing_addBreedingFailed_main;
import com.wdysolutions.www.rf_scanner.ScanEarTag.History.Farrowing_Stats.addFarrowing.Farrowing_add_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Farrowing_main extends Fragment {

    RecyclerView recyclerView;
    TextView null_result, error_result, text_showaverage;
    ImageView img_showaverage;
    ProgressBar loading_;
    ArrayList<Farrowing_model> arrayList = new ArrayList<>();
    ArrayList<Farrowing_average_model> arrayList2 = new ArrayList<>();
    Farrowing_adapter adapter;
    showAverage_adapter adapter2;
    String company_code, company_id, swine_scanned_id;
    LinearLayout btn_showaverage, btn_addfarrowing;


    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Farrowing Stats");
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
        View view = inflater.inflate(R.layout.farrowing_main, container, false);
        SessionPreferences sessionPreferences = new SessionPreferences(getActivity());
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        swine_scanned_id = getArguments().getString("swine_scanned_id");

        text_showaverage = view.findViewById(R.id.text_showaverage);
        img_showaverage = view.findViewById(R.id.img_showaverage);
        btn_showaverage = view.findViewById(R.id.btn_showaverage);
        btn_addfarrowing = view.findViewById(R.id.btn_addfarrowing);
        recyclerView = view.findViewById(R.id.recyclerView);
        error_result = view.findViewById(R.id.error_result);
        null_result = view.findViewById(R.id.null_result);
        loading_ = view.findViewById(R.id.loading_);

        error_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_.setVisibility(View.VISIBLE);
                error_result.setVisibility(View.GONE);
                getFarrowingDetails(company_code, company_id, swine_scanned_id);
            }
        });
        
        btn_addfarrowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("swine_scanned_id", swine_scanned_id);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                Farrowing_add_main dialogFragment = new Farrowing_add_main();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(ft, "dialog");
            }
        });

        btn_showaverage.setOnClickListener(new View.OnClickListener() {
            boolean isButtonClicked = true;
            @Override
            public void onClick(View view) {
                if (isButtonClicked){
                    isButtonClicked = false;
                    img_showaverage.setImageResource(R.drawable.ic_flip_to_front_black_24dp);
                    text_showaverage.setText("Back to stats");
                    showAverageDetails(company_code, company_id, swine_scanned_id);
                } else {
                    isButtonClicked = true;
                    img_showaverage.setImageResource(R.drawable.ic_flip_to_back_black_24dp);
                    text_showaverage.setText("Show Average");
                    getFarrowingDetails(company_code, company_id, swine_scanned_id);
                }
            }
        });

        initMenu(view);
        getFarrowingDetails(company_code, company_id, swine_scanned_id);
        return view;
    }

    public void getFarrowingDetails(final String company_code, final String company_id, final String swine_id) {
        loading_.setVisibility(View.VISIBLE);
        error_result.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_farrowing.php";
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
                            arrayList.add(new Farrowing_model(r.getInt("id"),
                                    r.getString("count"),
                                    r.getString("date_farrowed"),
                                    r.getString("born_alive"),
                                    r.getString("birth_mort"),
                                    r.getString("litter_size"),
                                    r.getString("boar_mated"),
                                    r.getString("ave_birth_wt"),
                                    r.getString("pre_wean_mort"),
                                    r.getString("post_wean_mort"),
                                    r.getString("num_heads_wean"),
                                    r.getString("num_rebreed"),
                                    r.getString("dry_days"),
                                    r.getString("gestation_days"),
                                    r.getString("days_weaned"),
                                    r.getString("breeding_failed_days"),
                                    r.getString("farrowing_interval"),
                                    r.getString("date_weaned"),
                                    r.getString("ave_weaning_wt"),
                                    r.getString("wean_wt_a"),
                                    r.getString("wean_wt_b"),
                                    r.getString("wean_wt_c"),
                                    r.getString("ave_weight_at_70"),
                                    r.getString("breeding_date"),
                                    r.getString("breeding_id"),
                                    r.getString("status"),
                                    r.getString("allow"),
                                    r.getString("adg"),
                                    r.getString("fcr"),
                                    r.getString("sw_abnormal"),
                                    r.getString("sw_undersize"),
                                    r.getString("sw_mummified"),
                                    r.getString("sw_stillbirth"),
                                    r.getString("condemned"),
                                    r.getString("litter_size_color"),
                                    r.getString("born_alive_color"),
                                    r.getString("num_heads_wean_color"),
                                    r.getString("breeding_date_minus_21")));
                        }
                        adapter = new Farrowing_adapter(getContext(), arrayList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        btn_showaverage.setEnabled(false);
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

    public class Farrowing_adapter extends RecyclerView.Adapter<Farrowing_adapter.MyHolder> {
        ArrayList<Farrowing_model> data;
        private Context context;
        private LayoutInflater inflater;
        int num;

        public Farrowing_adapter(Context context, ArrayList<Farrowing_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.farrowing_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getCount = data.get(position).getCount();
            final String getDate_farrowed = data.get(position).getDate_farrowed();
            final String getBorn_alive = data.get(position).getBorn_alive();
            final String getBirth_mort = data.get(position).getBirth_mort();
            final String getLitter_size = data.get(position).getLitter_size();
            final String getBoar_mated = data.get(position).getBoar_mated();
            final String getAve_birth_wt = data.get(position).getAve_birth_wt();
            final String getPre_wean_mort = data.get(position).getPre_wean_mort();
            final String getNum_heads_wean = data.get(position).getNum_heads_wean();
            final String getNum_rebreed = data.get(position).getNum_rebreed();
            final String getDry_days = data.get(position).getDry_days();
            final String getGestation_days = data.get(position).getGestation_days();
            final String getDays_weaned = data.get(position).getDays_weaned();
            final String getBreeding_failed_days = data.get(position).getBreeding_failed_days();
            final String getFarrowing_interval = data.get(position).getFarrowing_interval();
            final String getDate_weaned = data.get(position).getDate_weaned();
            final String getAve_weaning_wt = data.get(position).getAve_weaning_wt();
            final String getWean_wt_a = data.get(position).getWean_wt_a();
            final String getWean_wt_b = data.get(position).getWean_wt_b();
            final String getWean_wt_c = data.get(position).getWean_wt_c();
            final String getAve_weight_at_70 = data.get(position).getAve_weight_at_70();
            final String getBreeding_date = data.get(position).getBreeding_date();
            final String getBreeding_id = data.get(position).getBreeding_id();
            final String getStatus = data.get(position).getStatus();
            final String getAllow = data.get(position).getAllow();
            final String getAdg = data.get(position).getAdg();
            final String getFcr = data.get(position).getFcr();
            final String getSw_abnormal = data.get(position).getSw_abnormal();
            final String getSw_undersize = data.get(position).getSw_undersize();
            final String getSw_mummified = data.get(position).getSw_mummified();
            final String getSw_stillbirth = data.get(position).getSw_stillbirth();
            final String getCondemned = data.get(position).getCondemned();
            final String getPost_wean_mort = data.get(position).getPost_wean_mort();
            final String getBorn_alive_color = data.get(position).getBorn_alive_color();
            final String getLitter_size_color = data.get(position).getLitter_size_color();
            final String getNum_heads_wean_color = data.get(position).getNum_heads_wean_color();
            final String getBreeding_date_minus21 = data.get(position).getBreeding_date_minus21();
            num = position;
            num++;

            holder.text_count.setText(String.valueOf(num));
            holder.text_a.setText(getWean_wt_a);
            holder.text_b.setText(getWean_wt_b);
            holder.text_c.setText(getWean_wt_c);
            holder.text_adg.setText(getAdg);
            holder.text_ave_wean_wt.setText(getAve_weaning_wt);
            holder.text_boar_mated.setText(getBoar_mated);
            holder.text_birth_wt.setText(getAve_birth_wt);
            holder.text_stillbirth.setText(getSw_stillbirth);
            holder.text_undersize.setText(getSw_undersize);
            holder.text_dry_days.setText(getDry_days);
            holder.text_rebred.setText(getNum_rebreed);
            holder.text_pre_wean_mort.setText(getPre_wean_mort);
            holder.text_post_wean_mort.setText(getPost_wean_mort);
            holder.text_mummified.setText(getSw_mummified);
            holder.text_heads_weaned.setText(getNum_heads_wean);
            holder.text_fcr.setText(getFcr);
            holder.text_farrowing_interval.setText(getFarrowing_interval);
            holder.text_gestation_days.setText(getGestation_days);
            holder.text_date_farrowed.setText(getDate_farrowed);
            holder.text_condemned.setText(getCondemned);
            holder.text_70_days_old.setText(getAve_weight_at_70);
            holder.text_breeding_failed_days.setText(getBreeding_failed_days);
            holder.text_littersize.setText(getLitter_size);
            holder.text_ab_normal.setText(getSw_abnormal);
            holder.text_date_weaned.setText(getDate_weaned);
            holder.text_weaning_days.setText(getDays_weaned);
            holder.text_born_alive.setText(getBorn_alive);


            if (getBorn_alive_color.equals("red")){
                holder.text_born_alive.setTextColor(getResources().getColor(R.color.btn_red_color1));
            } else if (getBorn_alive_color.equals("blue")){
                holder.text_born_alive.setTextColor(getResources().getColor(R.color.dark_blue));
                holder.text_born_alive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("farr_id", String.valueOf(getId));
                        bundle.putString("swine_scanned_id", swine_scanned_id);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        Farrowing_Born_alive_main dialogFragment = new Farrowing_Born_alive_main();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(ft, "dialog");
                    }
                });
            }

            if (getLitter_size_color.equals("red")){
                holder.text_littersize.setTextColor(getResources().getColor(R.color.btn_red_color1));
            } else if (getLitter_size_color.equals("blue")){
                holder.text_littersize.setTextColor(getResources().getColor(R.color.dark_blue));
            }

            if (getNum_heads_wean_color.equals("red")){
                holder.text_heads_weaned.setTextColor(getResources().getColor(R.color.btn_red_color1));
            } else if (getNum_heads_wean_color.equals("blue")){
                holder.text_heads_weaned.setTextColor(getResources().getColor(R.color.dark_blue));
            }

            // Hide or Show button Add failed
            if (getAllow.equals("1")){
                holder.btn_add_failed.setVisibility(View.VISIBLE);
            } else if (getAllow.equals("0")) {
                holder.btn_add_failed.setVisibility(View.GONE);
            }

            holder.btn_add_failed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("getBreeding_date", getBreeding_date);
                    bundle.putString("getBreeding_id", getBreeding_id);
                    bundle.putString("getBreeding_date_minus21", getBreeding_date_minus21);
                    bundle.putString("swine_scanned_id", swine_scanned_id);
                    bundle.putString("getStatus", getStatus);
                    bundle.putInt("getId", getId);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    Farrowing_addBreedingFailed_main dialogFragment = new Farrowing_addBreedingFailed_main();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(ft, "dialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_date_farrowed, text_born_alive, text_stillbirth, text_mummified, text_ab_normal, text_undersize, text_condemned, text_heads_weaned,
                    text_rebred, text_boar_mated, text_birth_wt, text_pre_wean_mort, text_post_wean_mort, text_dry_days, text_breeding_failed_days, text_gestation_days, text_weaning_days,
                    text_farrowing_interval, text_ave_wean_wt, text_a, text_b, text_c, text_70_days_old, text_fcr, text_adg, text_littersize,
                    text_date_weaned, text_count;
            CardView layout;
            ImageView btn_add_failed;
            public MyHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.layout);
                text_date_farrowed = itemView.findViewById(R.id.text_date_farrowed);
                text_born_alive = itemView.findViewById(R.id.text_born_alive);
                text_stillbirth = itemView.findViewById(R.id.text_stillbirth);
                text_mummified = itemView.findViewById(R.id.text_mummified);
                text_ab_normal = itemView.findViewById(R.id.text_ab_normal);
                text_undersize = itemView.findViewById(R.id.text_undersize);
                text_condemned = itemView.findViewById(R.id.text_condemned);
                text_heads_weaned = itemView.findViewById(R.id.text_heads_weaned);
                text_rebred = itemView.findViewById(R.id.text_rebred);
                text_boar_mated = itemView.findViewById(R.id.text_boar_mated);
                text_birth_wt = itemView.findViewById(R.id.text_birth_wt);
                text_pre_wean_mort = itemView.findViewById(R.id.text_pre_wean_mort);
                text_post_wean_mort = itemView.findViewById(R.id.text_post_wean_mort);
                text_dry_days = itemView.findViewById(R.id.text_dry_days);
                text_breeding_failed_days = itemView.findViewById(R.id.text_breeding_failed_days);
                text_gestation_days = itemView.findViewById(R.id.text_gestation_days);
                text_weaning_days = itemView.findViewById(R.id.text_weaning_days);
                text_farrowing_interval = itemView.findViewById(R.id.text_farrowing_interval);
                text_ave_wean_wt = itemView.findViewById(R.id.text_ave_wean_wt);
                text_a = itemView.findViewById(R.id.text_a);
                text_b = itemView.findViewById(R.id.text_b);
                text_c = itemView.findViewById(R.id.text_c);
                text_70_days_old = itemView.findViewById(R.id.text_70_days_old);
                text_fcr = itemView.findViewById(R.id.text_fcr);
                text_adg = itemView.findViewById(R.id.text_adg);
                text_littersize = itemView.findViewById(R.id.text_littersize);
                text_date_weaned = itemView.findViewById(R.id.text_date_weaned);
                text_count = itemView.findViewById(R.id.text_count);
                btn_add_failed = itemView.findViewById(R.id.btn_add_failed);
            }
        }
    }

    public void showAverageDetails(final String company_code, final String company_id, final String swine_id) {
        loading_.setVisibility(View.VISIBLE);
        error_result.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        String URL = getString(R.string.URL_online)+"scan_eartag/history/pig_farrowing_average.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    if (!response.equals("{\"data\":[]}")){
                        arrayList2.clear();
                        null_result.setVisibility(View.GONE);
                        error_result.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        loading_.setVisibility(View.GONE);
                        JSONObject Object = new JSONObject(response);
                        JSONArray details = Object.getJSONArray("data");
                        for(int i = 0; i < details.length(); i++){
                            JSONObject r = details.getJSONObject(i);
                            arrayList2.add(new Farrowing_average_model(r.getString("sum_born_alive"),
                                    r.getString("sum_count_stillbirth"),
                                    r.getString("sum_count_mummified"),
                                    r.getString("sum_ab"),
                                    r.getString("sum_us"),
                                    r.getString("sum_litter_size"),
                                    r.getString("sum_condemned"),
                                    r.getString("sum_heads_weaned"),
                                    r.getString("sum_heads_rebreed"),
                                    r.getString("sum_ave_birth_wt"),
                                    r.getString("sum_pre_wean_mort"),
                                    r.getString("sum_post_wean_mort"),
                                    r.getString("sum_dry_days"),
                                    r.getString("sum_breeding_failed_days"),
                                    r.getString("sum_gestation_days"),
                                    r.getString("sum_days_weaned"),
                                    r.getString("sum_farrowing_interval"),
                                    r.getString("sum_ave_weaning_wt"),
                                    r.getString("sum_wean_wt_a"),
                                    r.getString("sum_wean_wt_b"),
                                    r.getString("sum_wean_wt_c"),
                                    r.getString("sum_ave_weaning_at_70"),
                                    r.getString("sum_fcr_count"),
                                    r.getString("sum_adg_count")));
                        }
                        adapter2 = new showAverage_adapter(getContext(), arrayList2);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter2);
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

    public class showAverage_adapter extends RecyclerView.Adapter<showAverage_adapter.MyHolder> {
        ArrayList<Farrowing_average_model> data;
        private Context context;
        private LayoutInflater inflater;

        public showAverage_adapter(Context context, ArrayList<Farrowing_average_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.farrowing_ave_container, parent,false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            final String getSum_ab = data.get(position).getSum_ab();
            final String getSum_adg_count = data.get(position).getSum_adg_count();
            final String getSum_ave_birth_wt = data.get(position).getSum_ave_birth_wt();
            final String getSum_born_alive = data.get(position).getSum_born_alive();
            final String getSum_post_wean_mort = data.get(position).getSum_post_wean_mort();
            final String getSum_litter_size = data.get(position).getSum_litter_size();
            final String getSum_ave_weaning_at_70 = data.get(position).getSum_ave_weaning_at_70();
            final String getSum_ave_weaning_wt = data.get(position).getSum_ave_weaning_wt();
            final String getSum_pre_wean_mort = data.get(position).getSum_pre_wean_mort();
            final String getSum_heads_rebreed = data.get(position).getSum_heads_rebreed();
            final String getSum_dry_days = data.get(position).getSum_dry_days();
            final String getSum_days_weaned = data.get(position).getSum_days_weaned();
            final String getSum_breeding_failed_days = data.get(position).getSum_breeding_failed_days();
            final String getSum_farrowing_interval = data.get(position).getSum_farrowing_interval();
            final String getSum_heads_weaned = data.get(position).getSum_heads_weaned();
            final String getSum_wean_wt_a = data.get(position).getSum_wean_wt_a();
            final String getSum_wean_wt_b = data.get(position).getSum_wean_wt_b();
            final String getSum_wean_wt_c = data.get(position).getSum_wean_wt_c();
            final String getSum_condemned = data.get(position).getSum_condemned();
            final String getSum_count_stillbirth = data.get(position).getSum_count_stillbirth();
            final String getSum_count_mummified = data.get(position).getSum_count_mummified();
            final String getSum_fcr_count = data.get(position).getSum_fcr_count();
            final String getSum_us = data.get(position).getSum_us();
            final String getSum_gestation_days = data.get(position).getSum_gestation_days();

            holder.text_a.setText(getSum_wean_wt_a);
            holder.text_b.setText(getSum_wean_wt_b);
            holder.text_c.setText(getSum_wean_wt_c);
            holder.text_adg.setText(getSum_adg_count);
            holder.text_ave_wean_wt.setText(getSum_ave_weaning_wt);
            holder.text_birth_wt.setText(getSum_ave_birth_wt);
            holder.text_stillbirth.setText(getSum_count_stillbirth);
            holder.text_undersize.setText(getSum_us);
            holder.text_dry_days.setText(getSum_dry_days);
            holder.text_rebred.setText(getSum_heads_rebreed);
            holder.text_pre_wean_mort.setText(getSum_pre_wean_mort);
            holder.text_post_wean_mort.setText(getSum_post_wean_mort);
            holder.text_mummified.setText(getSum_count_mummified);
            holder.text_heads_weaned.setText(getSum_heads_weaned);
            holder.text_fcr.setText(getSum_fcr_count);
            holder.text_farrowing_interval.setText(getSum_farrowing_interval);
            holder.text_gestation_days.setText(getSum_gestation_days);
            holder.text_condemned.setText(getSum_condemned);
            holder.text_70_days_old.setText(getSum_ave_weaning_at_70);
            holder.text_breeding_failed_days.setText(getSum_breeding_failed_days);
            holder.text_littersize.setText(getSum_litter_size);
            holder.text_ab_normal.setText(getSum_ab);
            holder.text_weaning_days.setText(getSum_days_weaned);
            holder.text_born_alive.setText(getSum_born_alive);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            TextView text_born_alive, text_stillbirth, text_mummified, text_ab_normal, text_undersize, text_condemned, text_heads_weaned,
                    text_rebred, text_birth_wt, text_pre_wean_mort, text_post_wean_mort, text_dry_days, text_breeding_failed_days, text_gestation_days, text_weaning_days,
                    text_farrowing_interval, text_ave_wean_wt, text_a, text_b, text_c, text_70_days_old, text_fcr, text_adg, text_littersize;
            CardView layout;
            ImageView btn_add_failed;
            public MyHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.layout);
                text_born_alive = itemView.findViewById(R.id.text_born_alive);
                text_stillbirth = itemView.findViewById(R.id.text_stillbirth);
                text_mummified = itemView.findViewById(R.id.text_mummified);
                text_ab_normal = itemView.findViewById(R.id.text_ab_normal);
                text_undersize = itemView.findViewById(R.id.text_undersize);
                text_condemned = itemView.findViewById(R.id.text_condemned);
                text_heads_weaned = itemView.findViewById(R.id.text_heads_weaned);
                text_rebred = itemView.findViewById(R.id.text_rebred);
                text_birth_wt = itemView.findViewById(R.id.text_birth_wt);
                text_pre_wean_mort = itemView.findViewById(R.id.text_pre_wean_mort);
                text_post_wean_mort = itemView.findViewById(R.id.text_post_wean_mort);
                text_dry_days = itemView.findViewById(R.id.text_dry_days);
                text_breeding_failed_days = itemView.findViewById(R.id.text_breeding_failed_days);
                text_gestation_days = itemView.findViewById(R.id.text_gestation_days);
                text_weaning_days = itemView.findViewById(R.id.text_weaning_days);
                text_farrowing_interval = itemView.findViewById(R.id.text_farrowing_interval);
                text_ave_wean_wt = itemView.findViewById(R.id.text_ave_wean_wt);
                text_a = itemView.findViewById(R.id.text_a);
                text_b = itemView.findViewById(R.id.text_b);
                text_c = itemView.findViewById(R.id.text_c);
                text_70_days_old = itemView.findViewById(R.id.text_70_days_old);
                text_fcr = itemView.findViewById(R.id.text_fcr);
                text_adg = itemView.findViewById(R.id.text_adg);
                text_littersize = itemView.findViewById(R.id.text_littersize);
                btn_add_failed = itemView.findViewById(R.id.btn_add_failed);
            }
        }
    }


}
