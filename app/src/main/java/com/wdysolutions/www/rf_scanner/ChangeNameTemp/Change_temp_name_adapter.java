package com.wdysolutions.www.rf_scanner.ChangeNameTemp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.R;

import java.util.ArrayList;

public class Change_temp_name_adapter extends RecyclerView.Adapter<Change_temp_name_adapter.MyHolder> implements Filterable {

    ArrayList<Change_temp_name_model> mdata;
    ArrayList<Change_temp_name_model> data_filter;
    private Context context;
    private LayoutInflater inflater;
    clickCallback callback;

    public Change_temp_name_adapter(Context context, ArrayList<Change_temp_name_model> data, clickCallback callback){
        this.context = context;
        this.mdata = data;
        this.data_filter = data;
        this.callback = callback;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.change_temp_name_row, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final String getId = mdata.get(position).getId();
        final String getSwine_code = mdata.get(position).getSwine_code();

        holder.swine_code.setText(getSwine_code);
        holder.btn_tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.c_callback(getId, getSwine_code);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView swine_code;
        LinearLayout btn_tap;
        public MyHolder(View itemView) {
            super(itemView);
            btn_tap = itemView.findViewById(R.id.btn_tap);
            swine_code = itemView.findViewById(R.id.swine_code);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    mdata = data_filter;
                } else {
                    ArrayList<Change_temp_name_model> filteredList = new ArrayList<>();
                    for (Change_temp_name_model row : data_filter) {

                        if (row.getSwine_code().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mdata = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mdata;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mdata = (ArrayList<Change_temp_name_model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
