package com.wdysolutions.www.rf_scanner.WritterTag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class WritterTag_adapter extends RecyclerView.Adapter<WritterTag_adapter.MyHolder>{
    ArrayList<WritterTag_model_pig> mdata;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;

    EventListener listener;

    public WritterTag_adapter(Context context, ArrayList<WritterTag_model_pig> data, EventListener listener){
        this.context = context;
        this.mdata = data;
        this.listener=listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_writtertag, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        String eartag = mdata.get(position).getSwine_code();
        int check_status = mdata.get(position).getCheck_status();

        holder.cb_check_status.setText(eartag);
        if(check_status==1){
            holder.cb_check_status.setBackgroundResource(R.color.btn_lightgreen_color2);
        }else if(check_status==2){
            holder.cb_check_status.setBackgroundResource(R.color.btn_yellow_color1);
        }else{
            holder.cb_check_status.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView cb_check_status;
        Button btn_transfer;

        public MyHolder(View itemView) {
            super(itemView);

            cb_check_status = itemView.findViewById(R.id.cb_check_status);
            itemView.setOnClickListener(this);
            btn_transfer = itemView.findViewById(R.id.btn_transfer);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            int swine_id = mdata.get(getAdapterPosition()).getSwine_id();
            String eartag = mdata.get(getAdapterPosition()).getSwine_code();
            int tag_counter = mdata.get(getAdapterPosition()).getTag_counter();
            listener.onEvent(getAdapterPosition(),swine_id,eartag,tag_counter);
            //Toast.makeText(context, String.valueOf(swine_id), Toast.LENGTH_SHORT).show();
        }
    }

    int getItem(int id) {
        return mdata.get(id).getSwine_id();
    }

    // allows clicks events to be caught
    void setClickListener(WritterTag_adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface EventListener {
        void onEvent(int position, int swine_id, String eartag, int tag_counter);
    }

//    public void removeAt(final int position) {
//        mdata.remove(position);
//        mdata.set(position,new WritterTag_model_pig(1,"",1));
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mdata.size());
//    }
}
