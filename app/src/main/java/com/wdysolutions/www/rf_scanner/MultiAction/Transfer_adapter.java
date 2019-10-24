package com.wdysolutions.www.rf_scanner.MultiAction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class Transfer_adapter extends RecyclerView.Adapter<Transfer_adapter.MyHolder> {
    ArrayList<Transfer_model_pig> mdata;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;
    EventListener listener;

    public Transfer_adapter(Context context, ArrayList<Transfer_model_pig> data,EventListener listener){
        this.context = context;
        this.mdata = data;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_transfer, parent,false);
        Transfer_adapter.MyHolder holder = new Transfer_adapter.MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Transfer_adapter.MyHolder holder, final int position) {
        final String eartag = mdata.get(position).getSwine_code();
        int check_status = mdata.get(position).getCheck_status();
        final int swine_id = mdata.get(position).getSwine_id();
        final String swine_code = mdata.get(position).getSwine_code();


        holder.text_.setText(eartag);
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btn_remove.setEnabled(false);
                dialogBox(holder,position, eartag);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text_;
        Button btn_remove;

        public MyHolder(View itemView) {
            super(itemView);
            text_ = itemView.findViewById(R.id.text_);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            int swine_id = mdata.get(getAdapterPosition()).getCheck_status();
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

//    public void update(final int position,int swine_id,String swine_code,int type) {
//        if(type==1){
//            mdata.set(position,new Transfer_model_pig(swine_id,swine_code,1));
//        }else{
//            mdata.set(position,new Transfer_model_pig(swine_id,swine_code,0));
//        }
//    }

    public void removeAt(final int position) {
        final int swine_id = mdata.get(position).getSwine_id();
        listener.onEvent(String.valueOf(swine_id));
        mdata.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mdata.size());
        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
    }

    void dialogBox(final MyHolder holder,final int position, String name){
        listener.open_delete(true);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Are you sure you want to delete "+name+"?");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        holder.btn_remove.setEnabled(true);
                        listener.open_delete(false);
                        removeAt(position);

                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        listener.open_delete(false);
                        holder.btn_remove.setEnabled(true);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public interface EventListener {
        void onEvent(String swine_id);
        void open_delete(boolean yes_no);

    }


}
