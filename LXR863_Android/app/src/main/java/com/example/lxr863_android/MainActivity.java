package com.example.lxr863_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    // Instantiate GPS class
    LocationManager locationManager;
    // Initialise global variables
    final private  int REQUEST_COURSE_ACCESS=123;
    boolean permissionGranted=false;
    double Lat,Long,Alt;
    boolean state=false;
    // Initialise dynamic UI
    TextView TextViewLat, TextViewLong, TextViewAlt;
    EditText IPText, PortText;
    Button BtnStream;
    // global variables
    String ip="192.168.0.14";
    int port=1010;
    // Handler for periodic task execution
    public Handler pHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BtnStream = findViewById(R.id.btnstream);
        TextViewLat = findViewById(R.id.textViewLat);
        TextViewLong = findViewById(R.id.textViewLong);
        TextViewAlt = findViewById(R.id.textViewAlt);
        IPText = findViewById(R.id.etext1);
        PortText = findViewById(R.id.etext2);

        // GPS instantiation and permission check
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_COURSE_ACCESS);
            return;
        }else {
            permissionGranted=true;
        }
        if(permissionGranted){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,netlistener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,gpslistener);
            locationManager.requestLocationUpdates(provider,0,0,bestlistener);
        }
    }
    ///// manual button stream
    public void StreamData(View view){
        if (state){
            BtnStream.setText("Stream");
            pHandler.removeCallbacks(pRunnable);
            state=false;
        }else{
            if(IPText.getText()!=null&&PortText.getText()!=null){
                try {
                    ip = IPText.getText().toString();
                    String tmp = PortText.getText().toString();
                    port = Integer.parseInt(tmp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                BtnStream.setText("Streaming");
                pHandler.post(pRunnable);
                state=true;
            }
        }
//        Toast.makeText(getBaseContext(),"Lat: "+Lat+"Long: "+Long,Toast.LENGTH_LONG).show();
    }
    ///// edittext1 click
    public void etext1changed(View view){
        Toast.makeText(getApplicationContext(),"Edit text 1 clicked",Toast.LENGTH_SHORT).show();
    }
    ///// Runnable periodic task
    private Runnable pRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder strb = new StringBuilder();
            strb.append(Lat); strb.append(','); strb.append(Long);
            strb.append(','); strb.append(Alt); strb.append('\n');
            String msg = strb.toString();
            sendMessage(msg);
            pHandler.postDelayed(pRunnable,100);
        }

    };

    ///// UDP sender Thread
    private void sendMessage(final String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    DatagramPacket dp;
                    dp = new DatagramPacket(message.getBytes(), message.length(), serverAddr, port);
                    ds.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {ds.close();}
                }
            }
        });
        thread.start();
    }

    ///// Location sensor updates
    private final LocationListener netlistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Lat=location.getLatitude();
            Long=location.getLongitude();
            Alt=location.getAltitude();
            TextViewLat.setText(String.valueOf(Long));
            TextViewLong.setText(String.valueOf(Lat));
            TextViewAlt.setText(String.valueOf(Alt));
            Toast.makeText(getBaseContext(),"Network Location Update",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private final LocationListener gpslistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Lat=location.getLatitude();
            Long=location.getLongitude();
            Alt=location.getAltitude();
            TextViewLat.setText(String.valueOf(Long));
            TextViewLong.setText(String.valueOf(Lat));
            TextViewAlt.setText(String.valueOf(Alt));
            Toast.makeText(getBaseContext(),"GNSS Location Update",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private final LocationListener bestlistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Lat=location.getLatitude();
            Long=location.getLongitude();
            Alt=location.getAltitude();
            TextViewLat.setText(String.valueOf(Long));
            TextViewLong.setText(String.valueOf(Lat));
            TextViewAlt.setText(String.valueOf(Alt));
            Toast.makeText(getBaseContext(),"Best Location Update",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
