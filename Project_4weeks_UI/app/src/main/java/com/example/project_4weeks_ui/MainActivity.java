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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    private Retrofit mRetrofit;
    String lon ; // 경도
    String lat ; // 위도
    String address ; // 주소
    TextView textView_temperature; // 온도출력 텍스트뷰
    TextView textView_address; // 주소출력 텍스트뷰

    static RequestQueue requestQueue;
    public static String baseURL = "http://211.237.50.150:7080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLocationService(); // 위도 경도 받아오기
        textView_temperature = findViewById(R.id.textView16);
        textView_address = findViewById(R.id.textView21);

        //날씨 받아오기
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            makeRequest();
        }// 날씨 end

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), select_menu.class);
                startActivity(intent);
            }
        });


        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeInterface recipeInterface = mRetrofit.create(RecipeInterface.class);

        Call<DataClass> call = recipeInterface.getRecipe("568996252afd631611d12bb9f54eb5a5d431445805a6fccabe8c16b3df67495f",
                "json", "Grid_20150827000000000226_1", 1, 2);
        call.enqueue(new Callback<DataClass>() {
            @Override
            public void onResponse(Call<DataClass> call, Response<DataClass> response) {
                DataClass result = response.body();
                Log.d("mainAcitvity", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<DataClass> call, Throwable t) {
                Log.d("mainAcitvity","failure");
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
    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location){
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatus(String provider, int status, Bundle extras){}
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
                        textView_temperature.setText(Double.toString(t) + "℃");
                        textView_address.setText(address);
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

}
interface RecipeInterface {
    @GET("openapi/{API_KEY}/{TYPE}/{API_URL}/{START_INDEX}/{END_INDEX}")
    Call<DataClass> getRecipe(@Path("API_KEY") String api_key,
                              @Path("TYPE") String type,
                              @Path("API_URL") String api_url,
                              @Path("START_INDEX") int start_index,
                              @Path("END_INDEX") int end_index
    )
            ;
}

class DataClass {

    // @SerializedName으로 일치시켜 주지않을 경우엔 클래스 변수명이 일치해야함
    @SerializedName("RECIPE_ID")
    public String recipeId;

    @SerializedName("body")
    public String body;


    // toString()을 Override 해주지 않으면 객체 주소값을 출력함
    @Override
    public String toString() {
        return "PostResult{" +
                "name=" + recipeId +
                ", nickname=" + body + '\'' +
                '}';
    }
}

