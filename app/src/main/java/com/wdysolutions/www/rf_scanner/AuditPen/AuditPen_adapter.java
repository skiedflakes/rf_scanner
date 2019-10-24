package com.wdysolutions.www.rf_scanner.AuditPen;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.Constant;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.LocateEartag.LocateEartag_main;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;

import java.util.ArrayList;

public class AuditPen_adapter extends RecyclerView.Adapter<AuditPen_adapter.MyHolder>{
    ArrayList<AuditPen_model_pig> mdata;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;
    EventListener listener;

    public AuditPen_adapter(Context context, ArrayList<AuditPen_model_pig> data, EventListener listener){
        this.context = context;
        this.mdata = data;
        this.listener=listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_audit_pen, parent,false);
        AuditPen_adapter.MyHolder holder = new AuditPen_adapter.MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AuditPen_adapter.MyHolder holder, final int position) {
        final String eartag = mdata.get(position).getSwine_code();
        int check_status = mdata.get(position).getCheck_status();
        final int swine_id = mdata.get(position).getSwine_id();
        int scan_counter = mdata.get(position).getScanned_counter();

        holder.cb_check_status.setText("("+String.valueOf(scan_counter)+") "+eartag);

        if(check_status==1){//if eartag is found within the pen
            holder.l_layout_row.setBackgroundColor(Color.WHITE);
            holder.cb_check_status.setTextColor(Color.BLACK);
            holder.cb_check_status.setChecked(true);
            holder.cb_check_status.setClickable(false);
            holder.btn_transfer.setVisibility(View.GONE);
            holder.btn_locate.setVisibility(View.GONE);
            if(scan_counter>1){
                holder.btn_locate.setVisibility(View.VISIBLE);
                holder.btn_locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("eartag", String.valueOf(eartag));
                        bundle.putString("swine_id", String.valueOf(swine_id));
                        FragmentTransaction fragmentTransaction = ((ActivityMain) context).getSupportFragmentManager().beginTransaction();
                        LocateEartag_main frag = new LocateEartag_main();
                        frag.setArguments(bundle);
                        fragmentTransaction.add(R.id.container, frag);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

            }
        }else if(check_status ==2){ //if eartag is found but different pen
            holder.l_layout_row.setBackgroundColor(Color.WHITE);
            holder.cb_check_status.setTextColor(Color.RED);
            holder.cb_check_status.setChecked(false);
            holder.cb_check_status.setClickable(false);
            holder.btn_locate.setVisibility(View.GONE);
//            holder.btn_transfer.setVisibility(View.VISIBLE);
//            holder.btn_transfer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onEvent(String.valueOf(swine_id));
//                }
//            });
            holder.btn_transfer.setVisibility(View.VISIBLE);
            holder.btn_transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEvent(String.valueOf(swine_id));
                }
            });

            if(scan_counter>1){

                holder.btn_locate.setVisibility(View.VISIBLE);
                holder.btn_locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("eartag", String.valueOf(eartag));
                        bundle.putString("swine_id", String.valueOf(swine_id));
                        FragmentTransaction fragmentTransaction = ((ActivityMain) context).getSupportFragmentManager().beginTransaction();
                        LocateEartag_main frag = new LocateEartag_main();
                        frag.setArguments(bundle);
                        fragmentTransaction.add(R.id.container, frag);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

        }else if(check_status ==3){ // else if dead, sold, culled
            holder.l_layout_row.setBackgroundColor(Color.RED);
            holder.cb_check_status.setTextColor(Color.WHITE);
            holder.cb_check_status.setChecked(false);
            holder.cb_check_status.setClickable(false);
            holder.btn_transfer.setVisibility(View.GONE);
            holder.btn_locate.setVisibility(View.GONE);

            holder.l_layout_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString("swine_id", String.valueOf(swine_id));
                    bundle.putString("power_level", Constant.power_level);
                    FragmentTransaction fragmentTransaction = ((ActivityMain) context).getSupportFragmentManager().beginTransaction();
                    RFscanner_main frag = new RFscanner_main();
                    frag.setArguments(bundle);
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.slide_out_right);
                    fragmentTransaction.add(R.id.container, frag);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

        }else{
            holder.l_layout_row.setBackgroundColor(Color.WHITE);
            holder.cb_check_status.setChecked(false);
            holder.cb_check_status.setTextColor(Color.BLACK);
            holder.cb_check_status.setClickable(false);
            holder.btn_transfer.setVisibility(View.GONE);
            holder.btn_locate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CheckBox cb_check_status;
        Button btn_transfer,btn_locate;
        LinearLayout l_layout_row;

        public MyHolder(View itemView) {
            super(itemView);

            cb_check_status = itemView.findViewById(R.id.cb_check_status);
            itemView.setOnClickListener(this);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
            l_layout_row = itemView.findViewById(R.id.l_layout_row);
            btn_locate = itemView.findViewById(R.id.btn_locate);
        }
        @Override
        public void onClick(View view) {

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            int swine_id = mdata.get(getAdapterPosition()).getSwine_id();
//            Toast.makeText(context,String.valueOf(swine_id), Toast.LENGTH_SHORT).show();
        }
    }

    int getItem(int id) {
        return mdata.get(id).getSwine_id();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface EventListener {
        void onEvent(String swine_id);
        void locate_eartag(String swine_id);
    }

    public void removeAt(final int position) {
        mdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mdata.size());
    }
}
