package rick.bos.wahooandroid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.wahoofitness.common.datatypes.Rate;

import rick.bos.wahooandroid.service.WahooService;
import rick.bos.wahooandroid.service.WahooServiceListener;

public class MainActivity extends AppCompatActivity implements WahooServiceListener, View.OnClickListener {
    private boolean registeredListener = false;
    private long lastWarning = 0 ;
    private Ringtone mRingTone;
    private MediaPlayer mediaPlayer;
    static {
        com.wahoofitness.common.log.Logger.setLogLevel( android.util.Log.VERBOSE );
    }


    private static final String TAG = "WahooActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        wahooEvent("Starting service");
        Intent startIntent = new Intent(this, WahooService.class);
        startService(startIntent);
         mediaPlayer = MediaPlayer.create(this,R.raw.sound);

        wahooEvent("started service");

    }


public void onClick( View v) {

    Log.i(TAG,"onClick:"+ registeredListener+":"+WahooService.getInstance());
    if (!registeredListener) {
        if (WahooService.getInstance() != null) {

            WahooService.getInstance().addListener(this);
            registeredListener = true;
            WahooService.getInstance().startDiscovery();

        }

    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void wahooEvent(String str) {
           TextView mTextView = (TextView)findViewById(R.id.statusText);
        mTextView.setText(str);
       // Log.i(TAG,str);
    }

    private int getMaxHR() {
        String maxHRStr =  PreferenceManager.getDefaultSharedPreferences(this).getString("pref_maxhr","100");
        return Integer.parseInt(maxHRStr);
    }
    private void playAlarm() {
        long currentTime = ( new java.util.Date()).getTime();
        if ( (currentTime - lastWarning) > 20000) {
            Log.i(TAG,"Play sound:" + mediaPlayer);
            mediaPlayer.start();



        }

    }
    private void checkMax( Rate rate) {
       if ( rate.asEventsPerMinute() > getMaxHR() ) {
          playAlarm();
           Log.i(TAG,"checkMax:"+rate.asEventsPerMinute());
       }  else {
         if ( mediaPlayer.isPlaying()) {
             mediaPlayer.pause();
             mediaPlayer.seekTo(0);
         }

         //  finish();
       }
    }
    @Override
    public void wahooData(Rate rate) {
        TextView mTextView = (TextView)findViewById(R.id.dataText);
        String heartRateStr = ""+(int)rate.asEventsPerMinute();
        mTextView.setText(heartRateStr);

        checkMax(rate);
    }
}
