package com.wdysolutions.www.rf_scanner.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uk.tsl.rfid.DeviceListActivity;
import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;
import com.uk.tsl.rfid.asciiprotocol.DeviceProperties;
import com.uk.tsl.rfid.asciiprotocol.device.ConnectionState;
import com.uk.tsl.rfid.asciiprotocol.device.IAsciiTransport;
import com.uk.tsl.rfid.asciiprotocol.device.ObservableReaderList;
import com.uk.tsl.rfid.asciiprotocol.device.Reader;
import com.uk.tsl.rfid.asciiprotocol.device.ReaderManager;
import com.uk.tsl.rfid.asciiprotocol.device.TransportType;
import com.uk.tsl.rfid.asciiprotocol.enumerations.Databank;
import com.uk.tsl.rfid.asciiprotocol.enumerations.QuerySession;
import com.uk.tsl.rfid.asciiprotocol.enumerations.TriState;
import com.uk.tsl.rfid.asciiprotocol.parameters.AntennaParameters;
import com.uk.tsl.rfid.asciiprotocol.responders.LoggerResponder;
import com.uk.tsl.utils.HexEncoding;
import com.uk.tsl.utils.Observable;
import com.wdysolutions.www.rf_scanner.Bluetooth.InventoryModel;
import com.wdysolutions.www.rf_scanner.Bluetooth.ModelBase;
import com.wdysolutions.www.rf_scanner.Bluetooth.WeakHandler;
import com.wdysolutions.www.rf_scanner.BuildConfig;
import com.wdysolutions.www.rf_scanner.Login_main;
import com.wdysolutions.www.rf_scanner.R;
import com.wdysolutions.www.rf_scanner.ScanEarTag.RFscanner_main;
import com.wdysolutions.www.rf_scanner.SessionManager.SessionPreferences;
import com.wdysolutions.www.rf_scanner.SwineSales.IOnBackPressed;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_main;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_scan;

import static com.uk.tsl.rfid.DeviceListActivity.EXTRA_DEVICE_ACTION;
import static com.uk.tsl.rfid.DeviceListActivity.EXTRA_DEVICE_INDEX;

public class ActivityMain extends AppCompatActivity {

    SessionPreferences sessionPreferences;
    public static final boolean D = BuildConfig.DEBUG;
    public int mPowerLevel = AntennaParameters.MaximumCarrierPower;
    public Reader mReader = null;
    public InventoryModel mModel;
    public boolean mIsSelectingReader = false, isDeviceConnected = true;
    FloatingActionButton fab;
    int offset, length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        sessionPreferences = new SessionPreferences(ActivityMain.this);
        setDefaultFragment(savedInstanceState);
        fab = findViewById(R.id.fab);
        //setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentSelectDevice();
            }
        });

        Toast.makeText(this, sessionPreferences.getUserDetails().get(sessionPreferences.KEY_COMPANY_CODE), Toast.LENGTH_SHORT).show();

        //initBluetooth();
    }

    private void setDefaultFragment(Bundle savedInstanceState){
        if (savedInstanceState == null){
            if (sessionPreferences.isLogin()){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Main_menu main_menu = new Main_menu();
                fragmentTransaction.add(R.id.container, main_menu);
                fragmentTransaction.commit();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Login_main loginMain = new Login_main();
                fragmentTransaction.add(R.id.container, loginMain);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void intentSelectDevice(){
        mIsSelectingReader = true;
        int index = -1;
        if( mReader != null )
        {
            index = ReaderManager.sharedInstance().getReaderList().list().indexOf(mReader);
        }
        Intent selectIntent = new Intent(this, DeviceListActivity.class);
        if( index >= 0 )
        {
            selectIntent.putExtra(EXTRA_DEVICE_INDEX, index);
        }
        startActivityForResult(selectIntent, DeviceListActivity.SELECT_DEVICE_REQUEST);
    }

    public void initBluetooth(){
        // Ensure the shared instance of AsciiCommander exists
        AsciiCommander.createSharedInstance(this);

        AsciiCommander commander = getCommander();

        // Ensure that all existing responders are removed
        commander.clearResponders();

        // Add the LoggerResponder - this simply echoes all lines received from the reader to the log
        // and passes the line onto the next responder
        // This is added first so that no other responder can consume received lines before they are logged.
        commander.addResponder(new LoggerResponder());

        // Add a synchronous responder to handle synchronous commands
        commander.addSynchronousResponder();

        // Create the single shared instance for this ApplicationContext
        ReaderManager.create(this);

        // Add observers for changes
        ReaderManager.sharedInstance().getReaderList().readerAddedEvent().addObserver(mAddedObserver);
        ReaderManager.sharedInstance().getReaderList().readerUpdatedEvent().addObserver(mUpdatedObserver);
        ReaderManager.sharedInstance().getReaderList().readerRemovedEvent().addObserver(mRemovedObserver);

        mGenericModelHandler = new GenericHandler(this);

        //Create a (custom) model and configure its commander and handler
        mModel = new InventoryModel();
        mModel.setCommander(getCommander());
        mModel.setHandler(mGenericModelHandler);

        mModel.getCommand().setUsefastId(TriState.NO);
        mModel.updateConfiguration();
        mModel.getCommand().setQuerySession(QuerySession.SESSION_0);
        mModel.updateConfiguration();


        // write
        offset = mModel.getReadCommand().getOffset();
        length  = mModel.getReadCommand().getLength();
        if( mModel.getReadCommand() != null ) {
            mModel.getReadCommand().setLength(length);
        }
        if( mModel.getWriteCommand() != null ) {
            mModel.getWriteCommand().setLength(length);
        }


        if( mModel.getReadCommand() != null ) {
            mModel.getReadCommand().setOffset(offset);
        }
        if( mModel.getWriteCommand() != null ) {
            mModel.getWriteCommand().setOffset(offset);
        }
    }

    //----------------------------------------------------------------------------------------------
    // ReaderList Observers
    //----------------------------------------------------------------------------------------------
    Observable.Observer<Reader> mAddedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader)
        {
            // See if this newly added Reader should be used
            AutoSelectReader(true);
        }
    };

    Observable.Observer<Reader> mUpdatedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader) {
        }
    };

    Observable.Observer<Reader> mRemovedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader)
        {
            mReader = null;
            // Was the current Reader removed
            if( reader == mReader)
            {
                mReader = null;

                // Stop using the old Reader
                getCommander().setReader(mReader);
            }
        }
    };

    private void AutoSelectReader(boolean attemptReconnect)
    {
        ObservableReaderList readerList = ReaderManager.sharedInstance().getReaderList();
        Reader usbReader = null;
        if( readerList.list().size() >= 1)
        {
            // Currently only support a single USB connected device so we can safely take the
            // first CONNECTED reader if there is one
            for (Reader reader : readerList.list())
            {
                IAsciiTransport transport = reader.getActiveTransport();
                if (reader.hasTransportOfType(TransportType.USB))
                {
                    usbReader = reader;
                    break;
                }
            }
        }

        if( mReader == null )
        {
            if( usbReader != null )
            {
                // Use the Reader found, if any
                mReader = usbReader;
                getCommander().setReader(mReader);
            }
        }
        else
        {
            // If already connected to a Reader by anything other than USB then
            // switch to the USB Reader
            IAsciiTransport activeTransport = mReader.getActiveTransport();
            if ( activeTransport != null && activeTransport.type() != TransportType.USB && usbReader != null)
            {
                mReader.disconnect();

                mReader = usbReader;

                // Use the Reader found, if any
                getCommander().setReader(mReader);
            }
        }

        // Reconnect to the chosen Reader
        if( mReader != null && (mReader.getActiveTransport()== null || mReader.getActiveTransport().connectionStatus().value() == ConnectionState.DISCONNECTED))
        {
            // Attempt to reconnect on the last used transport unless the ReaderManager is cause of OnPause (USB device connecting)
            if( attemptReconnect )
            {
                if( mReader.allowMultipleTransports() || mReader.getLastTransportType() == null )
                {
                    // Reader allows multiple transports or has not yet been connected so connect to it over any available transport
                    mReader.connect();
                }
                else
                {
                    // Reader supports only a single active transport so connect to it over the transport that was last in use
                    mReader.connect(mReader.getLastTransportType());
                }
            }
        }
    }

    private class GenericHandler extends WeakHandler<ActivityMain> {

        public GenericHandler(ActivityMain t) {
            super(t);
        }

        @Override
        public void handleMessage(Message msg, ActivityMain t) {
            try {
                switch (msg.what) {
                    case ModelBase.BUSY_STATE_CHANGED_NOTIFICATION:
                        //TODO: process change in model busy state
                        break;

                    case ModelBase.MESSAGE_NOTIFICATION:
                        // Examine the message for prefix
                        String message = (String)msg.obj;
                        if( message.startsWith("ER:")) {
                            //t.mResultTextView.setText( message.substring(3));
                        }
                        else if( message.startsWith("BC:")) {
                            //t.mBarcodeResultsArrayAdapter.add(message);
                            //t.scrollBarcodeListViewToBottom();
                        } else {
                            Intent i = new Intent("epc_receive");
                            i.putExtra("epc", message);
                            sendBroadcast(i);
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e){}
        }
    }

    // The handler for model messages
    private static GenericHandler mGenericModelHandler;

    //----------------------------------------------------------------------------------------------
    // UI state and display update
    //----------------------------------------------------------------------------------------------

    private void displayReaderState() {

        String connectionMsg = "Reader: ";
        switch( getCommander().getConnectionState()) {
            case CONNECTED:
                //connectionMsg += "Connected to "+getCommander().getConnectedDeviceName();
                isDeviceConnected = true;
                hideFloatingActionButton();
                fab.setImageResource(R.drawable.ic_bluetooth_connected_white_24dp);
                Toast.makeText(this, "Connected to "+getCommander().getConnectedDeviceName(), Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                showFloatingActionButton();
                fab.setImageResource(R.drawable.ic_bluetooth_searching_white_24dp);
                break;
            default:
                isDeviceConnected = false;
                showFloatingActionButton();
                fab.setImageResource(R.drawable.ic_bluetooth_white_24dp);
        }
    }

    public void showFloatingActionButton() {
        fab.hide();
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

    private void hideFloatingButtonOnPause(boolean isHide){
        if (isHide && isDeviceConnected){
            fab.hide();
            fab.show();
        } else if (!isHide && isDeviceConnected){
            fab.hide();
        }
    }

    //----------------------------------------------------------------------------------------------
    // AsciiCommander message handling
    //----------------------------------------------------------------------------------------------

    /**
     * @return the current AsciiCommander
     */
    protected AsciiCommander getCommander()
    {
        return AsciiCommander.sharedInstance();
    }

    //
    // Handle the messages broadcast from the AsciiCommander
    //
    private BroadcastReceiver mCommanderMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (D) { Log.d(getClass().getName(), "AsciiCommander state changed - isConnected: " + getCommander().isConnected()); }

            String connectionStateMsg = intent.getStringExtra(AsciiCommander.REASON_KEY);

            displayReaderState();

            if( getCommander().isConnected() )
            {
                // Update for any change in power limits
                setPowerBarLimits();
                // This may have changed the current power level setting if the new range is smaller than the old range
                // so update the model's inventory command for the new power value
                mModel.getCommand().setOutputPower(mPowerLevel);

                mModel.resetDevice();
                mModel.updateConfiguration();
            }
        }
    };

    //
    // Set the seek bar to cover the range of the currently connected device
    // The power level is set to the new maximum power
    //
    private void setPowerBarLimits()
    {
        DeviceProperties deviceProperties = getCommander().getDeviceProperties();

//        mPowerSeekBar.setMax(deviceProperties.getMaximumCarrierPower() - deviceProperties.getMinimumCarrierPower());
        mPowerLevel = deviceProperties.getMaximumCarrierPower();
//        mPowerSeekBar.setProgress(mPowerLevel - deviceProperties.getMinimumCarrierPower());
    }

    public void setPower(String level){
        if (level.equals("max")){
            DeviceProperties deviceProperties = getCommander().getDeviceProperties();
            mPowerLevel = deviceProperties.getMaximumCarrierPower();
        } else if (level.equals("med")){
            mPowerLevel = 15;
        } else {
            mPowerLevel = 5;
        }
        mModel.getCommand().setOutputPower(mPowerLevel);
        mModel.resetDevice();
        mModel.updateConfiguration();
    }

    //----------------------------------------------------------------------------------------------
    // Handler for DeviceListActivity
    //----------------------------------------------------------------------------------------------

    //
    // Handle Intent results
    //

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case DeviceListActivity.SELECT_DEVICE_REQUEST:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    int readerIndex = data.getExtras().getInt(EXTRA_DEVICE_INDEX);
                    Reader chosenReader = ReaderManager.sharedInstance().getReaderList().list().get(readerIndex);

                    int action = data.getExtras().getInt(EXTRA_DEVICE_ACTION);

                    // If already connected to a different reader then disconnect it
                    if( mReader != null )
                    {
                        if( action == DeviceListActivity.DEVICE_CHANGE || action == DeviceListActivity.DEVICE_DISCONNECT)
                        {
                            mReader.disconnect();
                            if(action == DeviceListActivity.DEVICE_DISCONNECT)
                            {
                                mReader = null;
                            }
                        }
                    }

                    // Use the Reader found
                    if( action == DeviceListActivity.DEVICE_CHANGE || action == DeviceListActivity.DEVICE_CONNECT)
                    {
                        mReader = chosenReader;
                        getCommander().setReader(mReader);
                    }
                    displayReaderState();
                }
                break;
        }
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Remove observers for changes
//        ReaderManager.sharedInstance().getReaderList().readerAddedEvent().removeObserver(mAddedObserver);
//        ReaderManager.sharedInstance().getReaderList().readerUpdatedEvent().removeObserver(mUpdatedObserver);
//        ReaderManager.sharedInstance().getReaderList().readerRemovedEvent().removeObserver(mRemovedObserver);
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        mModel.setEnabled(true);
//
//        // Register to receive notifications from the AsciiCommander
//        LocalBroadcastManager.getInstance(this).registerReceiver(mCommanderMessageReceiver, new IntentFilter(AsciiCommander.STATE_CHANGED_NOTIFICATION));
//
//        // Remember if the pause/resume was caused by ReaderManager - this will be cleared when ReaderManager.onResume() is called
//        boolean readerManagerDidCauseOnPause = ReaderManager.sharedInstance().didCauseOnPause();
//
//        // The ReaderManager needs to know about Activity lifecycle changes
//        ReaderManager.sharedInstance().onResume();
//
//        // The Activity may start with a reader already connected (perhaps by another App)
//        // Update the ReaderList which will add any unknown reader, firing events appropriately
//        ReaderManager.sharedInstance().updateList();
//
//        // Locate a Reader to use when necessary
//        AutoSelectReader(!readerManagerDidCauseOnPause);
//
//        mIsSelectingReader = false;
//
//        displayReaderState();
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        mModel.setEnabled(false);
//
//        // Unregister to receive notifications from the AsciiCommander
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCommanderMessageReceiver);
//
//        // Disconnect from the reader to allow other Apps to use it
//        // unless pausing when USB device attached or using the DeviceListActivity to select a Reader
//        if( !mIsSelectingReader && !ReaderManager.sharedInstance().didCauseOnPause() && mReader != null )
//        {
//            mReader.disconnect();
//        }
//
//        ReaderManager.sharedInstance().onPause();
//    }

    public void read_tag(){

        mModel = new InventoryModel();
        mModel.setCommander(getCommander());
        mModel.setHandler(mGenericModelHandler);

        mModel.getCommand().setUsefastId(TriState.NO);
        mModel.updateConfiguration();
        mModel.getCommand().setQuerySession(QuerySession.SESSION_0);
        mModel.updateConfiguration();

        mModel.read();
    }

    public void write_tag(String target_tag,String new_tag,int max,int min){

        if( mModel.getReadCommand() != null ) {
            mModel.getReadCommand().setLength(max);
        }
        if( mModel.getWriteCommand() != null ) {
            mModel.getWriteCommand().setLength(max);
        }

        if( mModel.getReadCommand() != null ) {
            mModel.getReadCommand().setOffset(min);
        }
        if( mModel.getWriteCommand() != null ) {
            mModel.getWriteCommand().setOffset(min);
        }

        if( mModel.getReadCommand() != null ) {
            mModel.getReadCommand().setSelectData(target_tag);
        }
        if( mModel.getWriteCommand() != null ) {
            mModel.getWriteCommand().setSelectData(target_tag);
        }

        mModel.getReadCommand().setBank(Databank.ELECTRONIC_PRODUCT_CODE);
        mModel.getWriteCommand().setBank(Databank.ELECTRONIC_PRODUCT_CODE);

        if( mModel.getWriteCommand() != null ) {
            byte[] data = null;
            try {
                data = HexEncoding.stringToBytes(new_tag);
                mModel.getWriteCommand().setData(data);
            } catch (Exception e) {
                // Ignore if invalid
            }
        }

        mModel.write();
        //  mModel.resetDevice();
    }

    public void dialogBox(String name){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.this);
        EditText text = new EditText(ActivityMain.this);
        text.setText(name);
        alertDialog.setView(text);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
