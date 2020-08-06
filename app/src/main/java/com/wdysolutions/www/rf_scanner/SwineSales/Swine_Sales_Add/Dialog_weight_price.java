package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wdysolutions.www.rf_scanner.R;

public class Dialog_weight_price extends DialogFragment {

LinearLayout l_layout_1,l_layout_2;
Button btn_yes,btn_no,btn_continue,btn_cancel;
EditText et_weight,et_price;
String ave_weight="",ave_price="";
String branch_id="",dr_header_id="";
String dr_date="",dr_num="",add_status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_weight_price, container, false);
        l_layout_1 = view.findViewById(R.id.l_layout_1);
        l_layout_2 = view.findViewById(R.id.l_layout_2);
        l_layout_1.setVisibility(View.VISIBLE);
        l_layout_2.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        if(bundle != null){

            branch_id = bundle.getString("branch_id");
            dr_header_id = bundle.getString("dr_header_id");
            dr_date = bundle.getString("dr_date");
            dr_num = bundle.getString("dr_num");
            add_status = bundle.getString("add_status");

        }

        btn_yes = view.findViewById(R.id.btn_yes);
        btn_no = view.findViewById(R.id.btn_no);

        btn_continue = view.findViewById(R.id.btn_continue);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        et_weight = view.findViewById(R.id.et_weight);
        et_price = view.findViewById(R.id.et_price);
        et_weight.setText("");
        et_price.setText("");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ave_weight = et_weight.getText().toString();
//                ave_price = et_price.getText().toString();

//
//                    dismiss();
//                    interface_weight_price.senddata_weight_price(ave_weight,ave_price);
//
//                    if(add_status.equals("yes")){
//                        Bundle bundle = new Bundle();
//                        bundle.putString("ave_weight",ave_weight);
//                        bundle.putString("ave_price",ave_price);
//                        bundle.putString("branch_id",branch_id);
//                        bundle.putString("dr_header_id",dr_header_id);
//                        bundle.putString("dr_date",dr_date);
//                        bundle.putString("dr_num",dr_num);
//
//
//                        Fragment fragment = new SwineSales_scan();
//                        fragment.setArguments(bundle);
//                        FragmentManager fragmentManager = getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,0);
//                        fragmentTransaction.add(R.id.container, fragment, "Main_menu").addToBackStack(null);
//                        fragmentTransaction.commit();
//                    }else{
//                        Bundle bundle = new Bundle();
//                        bundle.putString("ave_weight",ave_weight);
//                        bundle.putString("ave_price",ave_price);
//                        bundle.putString("branch_id",branch_id);
//                        bundle.putString("dr_header_id",dr_header_id);
//                        bundle.putString("dr_date",dr_date);
//                        bundle.putString("dr_num",dr_num);
//
//
//                        Fragment fragment = new SwineSales_scan();
//                        fragment.setArguments(bundle);
//                        FragmentManager fragmentManager = getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,0);
//                        fragmentTransaction.replace(R.id.container, fragment, "Main_menu").addToBackStack(null);
//                        fragmentTransaction.commit();
//                    }


            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ave_weight = et_weight.getText().toString();
                ave_price = et_price.getText().toString();

                dismiss();

                interface_weight_price.senddata_weight_price(ave_weight,ave_price,"no");

            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ave_weight = et_weight.getText().toString();
                ave_price = et_price.getText().toString();

                dismiss();

                interface_weight_price.senddata_weight_price(ave_weight,ave_price,"yes");

            }
        });

        return view;
    }

    weight_price interface_weight_price;

    public interface weight_price {
        void senddata_weight_price(String ave_weight, String ave_price, String yes_no);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interface_weight_price= (weight_price) getTargetFragment();
    }

}
