package com.example.project_4weeks_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String lon ; // 경도
    String lat ; // 위도
    String address ; // 주소
    String selected_category_KR; // 선택된카테고리 한글
    String selected_category_ENG; // 선택된카테고리 영어
    // 한글과 영어 구분한 이유는 db에서 한글로 받아올 수 없어서 영어 이름도 넘겨줘야함

    TextView tv_temperature; // 온도출력 텍스트뷰
    TextView tv_location; // 주소출력 텍스트뷰

    public static Context context_MainActivity; // selected_category 넘겨주기 위한 context 선언
    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocationService(); // 위도 경도 받아오기
        get_weatherAPI(); // 날씨 받아오기

        context_MainActivity = this; // selected_category 넘겨주기 위한 context
        tv_location = findViewById(R.id.tv_location);
        tv_temperature = findViewById(R.id.tv_temperature);

        ImageButton button_noodle = findViewById(R.id.ib_noodle);
        ImageButton button_bowl = findViewById(R.id.ib_bowl);
        ImageButton button_maindish = findViewById(R.id.ib_maindish);
        ImageButton button_rice = findViewById(R.id.ib_rice);
        ImageButton button_bread = findViewById(R.id.ib_bread);
        ImageButton button_alcohol = findViewById(R.id.ib_alcohol);
        TextView tv_noodle = findViewById(R.id.tv_noodle);
        TextView tv_bowl = findViewById(R.id.tv_bowl);
        TextView tv_maindish = findViewById(R.id.tv_maindish);
        TextView tv_rice = findViewById(R.id.tv_rice);
        TextView tv_bread = findViewById(R.id.tv_bread);
        TextView tv_alcohol = findViewById(R.id.tv_alcohol);

        button_noodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_noodle.getText();
                selected_category_ENG = "noodle";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });
        button_bowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_bowl.getText();
                selected_category_ENG = "bowl";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });
        button_maindish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_maindish.getText();
                selected_category_ENG = "maindish";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });
        button_rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_rice.getText();
                selected_category_ENG = "rice";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });
        button_bread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_bread.getText();
                selected_category_ENG = "bread";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });
        button_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category_KR = (String) tv_alcohol.getText();
                selected_category_ENG = "alcohol";
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });

        // 자동 위험권한 받아오기
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                    }
                })
                .start();
        /* end of 자동 위험권한 받아오기 */
    }
    
    public void get_weatherAPI(){
        //날씨 받아오기
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            makeRequest();
        }// 날씨 end

    }
    
    
    public void startLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());
            }
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Geocoder gCoder = new Geocoder(getApplicationContext(), Locale.KOREAN);
            StringBuilder sb = new StringBuilder();
            try{
                List<Address> addr = gCoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(lon),1);;
                Address a = addr.get(0);
                for(int i = 0 ; i <= a.getMaxAddressLineIndex(); i++){
                    sb.append(a.getAddressLine(i));
                }
                address = sb.toString();
                String[] arr = address.split( " ");
                address = arr[1] + " " + arr[2] + " " + arr[3];
                // arr[1] 시, arr[2] 구, arr[3] 동
            }
            catch(IOException e){
                e.printStackTrace();
            }

        }
        catch(SecurityException e){
            e.printStackTrace();
        }
    }

    public void makeRequest(){
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat + "&lon=" + lon + "&appid=70d0dd949829ada26da501a0cfbe0fad";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                    public void processResponse(String response){
                        Gson gson = new Gson();
                        WeatherData weather = gson.fromJson(response, WeatherData.class);
                        String temp = weather.main.temp;
                        Double t = Math.floor(Double.parseDouble(temp)) / 10;
                        tv_temperature.setText(Double.toString(t) + "℃");
                        tv_location.setText(address);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ){
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);

    }
    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location){
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatus(String provider, int status, Bundle extras){}
    }

}
