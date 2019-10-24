package com.wdysolutions.www.rf_scanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdysolutions.www.rf_scanner.Home.ActivityMain;
import com.wdysolutions.www.rf_scanner.Home.Main_menu;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login_main extends Fragment {

    EditText input_user, input_pass;
    LinearLayout btn_login;
    SessionPreferences sessionPreferences;
    String response_login, company_id, status, company_code, user_id,user_code,category_id;
    ProgressBar btn_loading;
    TextView btn_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_main, container, false);
        sessionPreferences = new SessionPreferences(getActivity());

        btn_txt = view.findViewById(R.id.btn_txt);
        btn_loading = view.findViewById(R.id.btn_loading);
        input_pass = view.findViewById(R.id.input_pass);
        input_user = view.findViewById(R.id.input_user);
        btn_login = view.findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = input_user.getText().toString();
                String password = input_pass.getText().toString();

                if (username.equals("")){
                    Toast.makeText(getActivity(), "Please enter username.", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")){
                    Toast.makeText(getActivity(), "Please enter password.", Toast.LENGTH_SHORT).show();
                } else {
                    checkLogin(username, password);
                }
            }
        });

        return view;
    }

    void checkLogin(final String user, final String pass) {
        btn_txt.setText("Checking...");
        btn_login.setEnabled(false);
        btn_loading.setVisibility(View.VISIBLE);
        String URL = getString(R.string.URL_online)+"login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject Object = new JSONObject(response);
                    JSONArray details = Object.getJSONArray("response_");
                    JSONObject r = details.getJSONObject(0);

                    company_code = r.getString("company_code");
                    response_login = r.getString("response_login");
                    company_id = r.getString("company_id");
                    status = r.getString("status");
                    user_id = r.getString("user_id");
                    user_code =  r.getString("user_code");
                    category_id =  r.getString("category_id");
                    if (status.equals("1")){
                        sessionPreferences.saveUserInfo(company_id, user_id, company_code,user_code,category_id);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Main_menu main_menu = new Main_menu();
                        fragmentTransaction.replace(R.id.container, main_menu);
                        fragmentTransaction.commit();
                        Toast.makeText(getActivity(), "Successfully Login.", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_txt.setText("Login");
                        btn_login.setEnabled(true);
                        btn_loading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Account not found.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    btn_txt.setText("Login");
                    btn_login.setEnabled(true);
                    btn_loading.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connection Error, please try again.", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("username", user);
                hashMap.put("password", pass);
                return hashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ActivityMain)getActivity()).hideFloatingActionButton();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((ActivityMain)getActivity()).showFloatingActionButton();
    }


}
