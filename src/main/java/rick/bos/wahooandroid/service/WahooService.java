package rick.bos.wahooandroid.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.wahoofitness.common.datatypes.Rate;
import com.wahoofitness.connector.HardwareConnector;
import com.wahoofitness.connector.HardwareConnectorEnums;
import com.wahoofitness.connector.capabilities.Capability;
import com.wahoofitness.connector.capabilities.Heartrate;
import com.wahoofitness.connector.conn.connections.SensorConnection;
import com.wahoofitness.connector.conn.connections.params.ConnectionParams;
import com.wahoofitness.connector.listeners.discovery.DiscoveryListener;

import java.util.ArrayList;
import java.util.List;

public class WahooService extends Service implements DiscoveryListener, SensorConnection.Listener, Heartrate.Listener  {
    private HardwareConnector mHardwareConnector;
    private final HardwareConnector.Listener mHardwareConnectorListener=new WahooListener ();
    private static final String TAG = "WahooService";
    private List<WahooServiceListener> listenerList;

    public WahooService() {
        listenerList = new ArrayList<WahooServiceListener>();
    }
    private static WahooService instance_ ;
    public void addListener(  WahooServiceListener aListener) {
        listenerList.add(aListener);
        Log.i(TAG,"AddListener: "+aListener+ " " + listenerList.size());

    }
    public static  WahooService getInstance() {
        return instance_;
    }



    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate:" + instance_);
        super.onCreate();
        instance_ = this;
        Log.i(TAG,"onCreate:" + instance_);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startDiscovery() {
        mHardwareConnector=new HardwareConnector(this,mHardwareConnectorListener);

        mHardwareConnector.startDiscovery(this);


        updateListeners(" started discovery");
    }









    @Override
    public void onDeviceDiscovered(ConnectionParams connectionParams) {
        mHardwareConnector.requestSensorConnection(connectionParams,this);
        // Log.i(TAG, "onDeviceDiscovered");
        //  updateListeners("onDeviceDiscovered:"+ connectionParams.getName());
    }

    @Override
    public void onDiscoveredDeviceLost(ConnectionParams connectionParams) {
        updateListeners("onDiscoveredDeviceLost");
    }

    @Override
    public void onDiscoveredDeviceRssiChanged(ConnectionParams connectionParams, int i) {

    }

    @Override
    public void onSensorConnectionStateChanged(SensorConnection sensorConnection, HardwareConnectorEnums.SensorConnectionState sensorConnectionState) {
        updateListeners("onSensorConnectionStateChanged");
    }

    @Override
    public void onSensorConnectionError(SensorConnection sensorConnection, HardwareConnectorEnums.SensorConnectionError sensorConnectionError) {
        //      Log.i(TAG, "onSensorConnectionError");
        updateListeners("onSensorConnectionError");
    }

    @Override
    public void onNewCapabilityDetected(SensorConnection sensorConnection, Capability.CapabilityType capabilityType) {
        if(capabilityType== Capability.CapabilityType.Heartrate){
            Heartrate heartrate=(Heartrate)sensorConnection.getCurrentCapability(Capability.CapabilityType.Heartrate);
            heartrate.addListener(this);
            updateListeners("registered HR listener");
        }
        // updateListeners("onNewCapabilityDetected" + capabilityType);
    }

    @Override
    public void onHeartrateData(Heartrate.Data data) {
        //    Log.i(TAG, "onHeartRateData:" + data.getHeartrate());

        updateListeners(data.getHeartrate());
        updateListeners("onHeartRateData:"+data.getHeartrate().asEventsPerMinute());
    }
    private void updateListeners( Rate rate) {
        //   Log.i(TAG,message);
        for ( WahooServiceListener listener : listenerList) {
            listener.wahooData(rate);
        }
    }
    private void updateListeners( String message) {
        //   Log.i(TAG,message);
        for ( WahooServiceListener listener : listenerList) {
            listener.wahooEvent(message);
        }
    }
    @Override
    public void onHeartrateDataReset() {




    }
}

