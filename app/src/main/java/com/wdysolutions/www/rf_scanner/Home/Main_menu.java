package com.wdysolutions.www.rf_scanner.Home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_main;
import com.wdysolutions.www.rf_scanner.ChangeNameTemp.Change_temp_name_main;
import com.wdysolutions.www.rf_scanner.Feeding.Feeding_module_main;
import com.wdysolutions.www.rf_scanner.LocateEartag.LocateEartag_main;
import com.wdysolutions.www.rf_scanner.Login_main;
import com.wdysolutions.www.rf_scanner.Medication_Vaccination.Med_Vac_main;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.MultiAction.TransferPen_main;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_main;
import com.wdysolutions.www.rf_scanner.WritterTag.WritterTag_main2;

import java.util.ArrayList;


public class Main_menu extends Fragment {

    SessionPreferences sessionPreferences;
    ArrayList<Main_menu_model> arrayMenu = new ArrayList<>();
    adapterMenu adapter;
    BroadcastReceiver connectionReceiver;

    private void initMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.menu_logout);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_logout);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dialogBox();
                return false;
            }
        });
        getActivity().registerReceiver(connectionReceiver, new IntentFilter("deviceConnection"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(connectionReceiver != null){
            getActivity().unregisterReceiver(connectionReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_menu, container, false);
        sessionPreferences = new SessionPreferences(getActivity());

        initMenu(view);

        addMenuButton();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    void dialogBox(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
        alertDialog2.setMessage("Are you sure you want to logout ?");
        alertDialog2.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog2.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sessionPreferences.editor.clear();
                        sessionPreferences.editor.commit();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Login_main loginMain = new Login_main();
                        fragmentTransaction.replace(R.id.container, loginMain);
                        fragmentTransaction.commit();
                    }
                });
        alertDialog2.show();
    }

    Boolean isFeeding = false;
    private class adapterMenu extends RecyclerView.Adapter<adapterMenu.MyHolder>{
        ArrayList<Main_menu_model> data;
        private Context context;
        private LayoutInflater inflater;

        public adapterMenu(Context context, ArrayList<Main_menu_model> data){
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public adapterMenu.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.main_menu_container, parent,false);
            adapterMenu.MyHolder holder = new adapterMenu.MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final adapterMenu.MyHolder holder, final int position) {
            final int getId = data.get(position).getId();
            final String getName = data.get(position).getName();
            final String getImg = data.get(position).getImg();
            final int getIconColor = data.get(position).getIconColor();
            final String getDetails = data.get(position).getDetails();
            final int getDisable_status = data.get(position).getDisable_status();

            // set background if disabled
            if (getDisable_status == 1){
                holder.tap_.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
            } else {
                holder.tap_.setBackgroundResource(R.drawable.btn_ripple_transparent);
            }

            holder.name.setText(getName);
            holder.icon.setImageResource(getImageName(getActivity(), getImg));
            holder.icon.setBackgroundResource(getIconColor);
            holder.tap_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment selectedFragment = null;
                    switch (getId){
                        case 0:
                            selectedFragment = new RFscanner_main();
                            isFeeding=false;
                            break;
                        case 1:
                            selectedFragment = new TransferPen_main();
                            isFeeding=false;
                            break;
                        case 2:
                            selectedFragment = new AuditPen_main();
                            isFeeding=false;
                            break;
                        case 3:
                            selectedFragment = new LocateEartag_main();
                            isFeeding=false;
                            break;
                        case 4:
                            selectedFragment = new WritterTag_main2();
                            isFeeding=false;
                            break;
                        case 5:
                            selectedFragment = new SwineSales_main();
                            isFeeding=false;
                            break;
                        case 6:
                            selectedFragment = new Med_Vac_main();
                            isFeeding=false;
                            break;
                        case 7:
                            selectedFragment = new Feeding_module_main();
                            isFeeding=true;
                            break;
                        case 8:
                            selectedFragment = new Change_temp_name_main();
                            isFeeding=false;
                            break;
                    }

                    if (getDisable_status != 1){

                        if(isFeeding){
                            if (selectedFragment != null){
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container, selectedFragment, "Main_menu").addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            isFeeding=false;

                        } else {
                            if (((ActivityMain)getActivity()).isDeviceConnected){
                                if (selectedFragment != null){
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container, selectedFragment, "Main_menu").addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                            } else {
                                Toast.makeText(context, "Device is not connected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "This module is under development", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder{
            ImageView icon;
            TextView name;
            LinearLayout tap_;
            public MyHolder(View itemView) {
                super(itemView);
                tap_ = itemView.findViewById(R.id.tap);
                name = itemView.findViewById(R.id.name);
                icon = itemView.findViewById(R.id.img);
            }
        }
    }

    private static int getImageName(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    private void addMenuButton(){
        arrayMenu.clear();
        arrayMenu.add(new Main_menu_model(0, "Scan Ear Tag", "ic_barcode_scanner", R.drawable.bg_menu_icon_0, "", 0));
        arrayMenu.add(new Main_menu_model(1, "Multi-Action", "ic_move_arrows", R.drawable.bg_menu_icon_1, "", 0));
        arrayMenu.add(new Main_menu_model(2, "Audit Pen", "ic_audit", R.drawable.bg_menu_icon_2, "", 0));
        arrayMenu.add(new Main_menu_model(3, "Locate Ear Tag", "ic_loupe", R.drawable.bg_menu_icon_5, "", 0));
        arrayMenu.add(new Main_menu_model(4, "Write Tag", "ic_edit", R.drawable.bg_menu_icon_4, "", 0));
        arrayMenu.add(new Main_menu_model(5, "Swine Sales", "ic_peso", R.drawable.bg_menu_icon_3, "", 0));
        arrayMenu.add(new Main_menu_model(6, "Vaccination / Medication", "ic_syringe", R.drawable.bg_menu_icon_6, "", 0));
        arrayMenu.add(new Main_menu_model(7, "Feeding", "ic_restaurant", R.drawable.bg_menu_icon_7, "", 0));
        arrayMenu.add(new Main_menu_model(8, "Change Temp Name", "ic_restaurant", R.drawable.bg_menu_icon_7, "", 0));
        adapter = new adapterMenu(getContext(), arrayMenu);
    }

}
