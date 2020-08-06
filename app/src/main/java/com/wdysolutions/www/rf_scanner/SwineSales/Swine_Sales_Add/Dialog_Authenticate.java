package com.wdysolutions.www.rf_scanner.SwineSales.Swine_Sales_Add;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.AppController;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import java.util.HashMap;
import java.util.Map;

public class Dialog_Authenticate extends DialogFragment {

    EditText et_username, et_password;
    Button btn_save,btn_close;
    //session
    SessionPreferences sessionPreferences;
    String company_code, company_id, user_id,user_code;

    String branch_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_authenticate, container, false);
        sessionPreferences = new SessionPreferences(getActivity());
        //session
        company_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE);
        company_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_ID);
        user_id = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_ID);
        user_code = sessionPreferences.getUserDetails().get(sessionPreferences.KEY_USER_CODE);

        //bundle
        Bundle bundle = getArguments();
        if(bundle != null){

            branch_id = bundle.getString("branch_id");

        }else{
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        }

        et_password = view.findViewById(R.id.et_password);
        et_username = view.findViewById(R.id.et_username);
        btn_save = view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                if (username.equals("")){
                    Toast.makeText(getActivity(), "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")){
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                }
                else {
                    save(company_id,company_code,"check_changeprice_discount_module",username,password);
                }
            }
        });

        btn_close = view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                interfaceObj.senddata_auth("invalid");
            }
        });
        return view;
    }


    public interface uploadDialogInterface {
        void senddata_auth(String data);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceObj= (uploadDialogInterface) getTargetFragment();
    }

    uploadDialogInterface interfaceObj;

    public void save(final String company_id, final String company_code,final String module,final String username,final String password){
        String URL = getString(R.string.URL_online)+"swine_sales/owner_authentication.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               if(response.equals("1")){
                    dismiss();
                   interfaceObj.senddata_auth("okay");

               }else if(response.equals("2")){
                   Toast.makeText(getActivity(), "Invalid Account", Toast.LENGTH_SHORT).show();
                   dismiss();
                   interfaceObj.senddata_auth("invalid");
                }else{
                   dismiss();
                   interfaceObj.senddata_auth("invalid");
                   Toast.makeText(getActivity(), "You don't have the privilege to continue this transaction", Toast.LENGTH_SHORT).show();
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Toast.makeText(getActivity(), "Connection error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){}
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("company_id", company_id);
                hashMap.put("company_code", company_code);
                hashMap.put("user_id", user_id);
                hashMap.put("branch_id", branch_id);
                hashMap.put("owner_username", username);
                hashMap.put("owner_password", password);
                hashMap.put("module", module);
                hashMap.put("user_code", user_code);
                return hashMap;
            }
        };
        AppController.getInstance().setVolleyDuration(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
