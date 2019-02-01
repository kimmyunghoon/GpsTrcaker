package com.example.gpsinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.gpsinfo.db.DBHelper;
import com.example.gpsinfo.db.FirebaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/** GPS 샘플 */
public class GpsActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static Context mContext;

    private Button btnShowLocation;
    private Button btnEndLocation;
    private Button btnSubmitLocation;
    /*   private TextView txtLat;
       private TextView txtLon;*/
    private TextView txtSAd;
    private TextView txtEAd;
    private TextView txtday;
    private TextView txttitle;
    private TextView txttime;
    private ProgressBar progressBar;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private DatabaseReference mDatabase;
    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;
    public static boolean type = false;
    public static boolean endP = false;
    // GPSTracker class
    private Gpsinfo gps;
    double latitude;
    double longitude;
    double slatitude;
    double slongitude;
    double dLocationLa[]=new double[1000];
    double dLocationLo[]=new double[1000];
    MarkerOptions friendMarker[] = new MarkerOptions[1000];
    int iLocation ;
    Thread th;
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    SupportMapFragment mapFragmentRecord;
    MapView mapView;
    MarkerOptions myLocationMarker;
    int mainTime=0 ;
    int tRun=1;
    boolean hFlag = true;
    int count=0;
    boolean isOptionalMsgShown;
    String getTime;
    String LaLo = "";
    String LoLo = "";
    String startAd ;
    String endAd;
    String runningT;
    String value;
    DBHelper dbHelper = new DBHelper(GpsActivity.this,
            "Last_Track_List",null,1);
    SharedPreferences defaultSharedPref;
    NotificationManager notificationManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gpsinfo);
        startService(new Intent(GpsActivity.this, MyService.class)); // 서비스 시작
        Intent intent=new Intent(this.getIntent());
        mContext = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();

       defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

       isOptionalMsgShown = defaultSharedPref.getBoolean("useUserTrack", false);

        startService(new Intent(GpsActivity.this, MyService.class)); // 서비스 시작
        btnEndLocation = (Button) findViewById(R.id.btn_end);
        btnSubmitLocation = (Button) findViewById(R.id.btn_submit);
        iLocation=1;
        txtSAd = (TextView) findViewById(R.id.tv_StartAdress);
        txtEAd = (TextView) findViewById(R.id.tv_EndAdress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        txttime = (TextView) findViewById(R.id.tv_time);
        btnEndLocation.setVisibility(View.VISIBLE);
        btnSubmitLocation.setVisibility(View.GONE);
        txtEAd.setVisibility(View.GONE);
        txtday = (TextView) findViewById(R.id.tv_day);
        txttitle = (TextView) findViewById(R.id.tv_title);
        txttitle.setVisibility(View.GONE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomGesturesEnabled(false);
        MapFragment.newInstance(options);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        getTime = sdf.format(date);

        txtday.setText(getTime);
        Log.v("시간 테스트222",getTime);
        callPermission();  // 권한 요청을 해야 함
        timeHandeler().sendEmptyMessage(1);

        btnEndLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                    timeHandeler().removeMessages(0);
                //txtEAd.setText("도착 주소 : " + conAdress(gps.getLatitude(), gps.getLongitude()));
                //리스트 뷰에 저장할 도착 주소.
                type = true;
                endP = true;
                gps.stopUsingGPS();
                btnEndLocation.setVisibility(View.GONE);
                btnSubmitLocation.setVisibility(View.VISIBLE);
                stopService(new Intent(GpsActivity.this, MyService.class)); // 서비스 종료
                //removeNotification();
                progressBar.setVisibility(View.GONE);


                //이후 리스트에 기록하는 거 해야됨.
                //End 버튼과 동시에 맵 프레그먼트에 기록된 라인과 +출발/도착 주소 표현해주기.
                //그 값이 리스트에 저장되기
                //액티비티 상태 저장하는 법. 찾아서 저장해볼것.
            }
        });

        btnSubmitLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                if(endP){
                    Toast.makeText(
                            getApplicationContext(),
                            "도착위치 마킹중입니다.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    alertDialog();
                    removeNotification();
                }

            }
        });


    }
    private MyService mService;
    private boolean isBind;

    ServiceConnection sconn = new ServiceConnection() {
        @Override //서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            mService = myBinder.getService();

            isBind = true;
            Log.e("LOG", "onServiceConnected()");
        }

        @Override //서비스가 종료될 때 호출
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBind = false;

            Log.e("LOG", "onServiceDisconnected()");
        }
    };




    public Handler timeHandeler(){
        Handler mHandler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                /** 초시간을 잰다 */
                if (hFlag) {


                int div = msg.what;
                int min = mainTime / 60;
                int sec = mainTime % 60;
                String strTime = String.format("%02d : %02d", min, sec);


                txttime.setText("실행시간 "+ strTime);

                txttime.invalidate();

                mainTime++;

                    createNotification();
                if(!type)
                    this.sendEmptyMessageDelayed(0, 1000);
                }

            }

        };

        return  mHandler;
    }

    public void setSMF(SupportMapFragment mapFragment){
        mapFragmentRecord = mapFragment;
    }
    public SupportMapFragment getSMF(){
        return mapFragmentRecord;
    }
    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default").setOngoing(true);


        builder.setSmallIcon(R.drawable.app_icon);
        builder.setContentTitle("GpsTracker 실행중");
        builder.setContentText(txttime.getText().toString());
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }

    public void alertDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(GpsActivity.this);

        ad.setTitle("Track Record");       // 제목 설정
        // EditText 삽입하기
        final EditText et = new EditText(GpsActivity.this);
        Random rd = new Random();
        count = rd.nextInt(1000);
        et.setText("트랙 "+(count));


        ad.setView(et);



        // 확인 버튼 설정
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("다이얼로그 test", "Yes Btn Click");

                Intent intent = new Intent(GpsActivity.this, StoreActivity.class);
                Intent intent2 = new Intent(GpsActivity.this, ListActivity.class);

                startAd = txtSAd.getText().toString();
                endAd = "도착 주소 : " + conAdress(gps.getLatitude(), gps.getLongitude());
                runningT = txttime.getText().toString();
                intent.putExtra("startAdress", startAd);
                intent.putExtra("endAdress", endAd);
                intent.putExtra("runningTime", runningT);
                intent.putExtra("getTime", getTime);

                intent.putExtra("laLocation", dLocationLa);
                intent.putExtra("loLocation", dLocationLo);
                intent.putExtra("iLocation", iLocation);
                // Text 값 받아서 로그 남기기
                value = et.getText().toString();
                intent.putExtra("title", value);
                Log.v("다이얼로그 test", value);

                for (int i = 0; i < iLocation; i++) {
                    LaLo += "" + dLocationLa[i];
                    LoLo += "" + dLocationLo[i];
                   // if (i < iLocation - 1) {
                        LaLo += ",";
                        LoLo += ",";
                    //}
                }
                LaLo += 0;
                LoLo += 0;
                Log.v("로그 test", LaLo);
                Log.v("로그 test", LoLo);
                List_Table list_table = new List_Table();
                //제목, 기록시간, 실행시간, 출발 주소, 도착주소, La좌표,Lo좌표
                list_table.setTitle(value);
                list_table.setDaytime(getTime);
                list_table.setStartAdress(startAd);
                list_table.setEndAdress(endAd);
                list_table.setLocationla(LaLo);
                list_table.setLocationlo(LoLo);
                list_table.setRunningTime(runningT);
                //sqlLite db에 값 저장하는 부분
                dbHelper.addList(list_table);
                if(isOptionalMsgShown) {
                    writeNewTrack(value, getTime, LaLo, LoLo, runningT, startAd, endAd);
                }
                intent2.putExtra("list_title", value);
                intent2.putExtra("list_day", getTime);
               /*  intent2.putExtra("laLocation",dLocatonLa);
                intent2.putExtra("loLocation",dLocatonLo);
                intent2.putExtra("startAdress",startAd );
                intent2.putExtra("endAdress",endAd);
                intent2.putExtra("runningTime", runningT);
*/
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);


                startActivity(intent);


                dialog.dismiss();     //닫기
                finish();
                // Event
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("다이얼로그 test", "No Btn Click");

                dialog.dismiss();     //닫기
                finish();
                // Event
            }
        });

        // 취소 버튼 설정

        // 창 띄우기
        ad.show();

    }

    private void writeNewTrack(String title, String daytime, String locationLA,String locationLO,
                               String runTime, String sAdress,String eAdress) {
        FirebaseHelper user = new FirebaseHelper( title,  daytime, locationLA, locationLO,
                runTime,  sAdress, eAdress);
        mDatabase.child("tracks").child(title).setValue(user);

    }
    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    public void callDrawRoute(Location location) {

       /* DrawRoute.getInstance(this, GpsActivity.this).setFromLatLong(slatitude, slongitude)
                .setToLatLong(location.getLatitude(), location.getLongitude()).setGmapAndKey("AIzaSyBdV2PVvDNMlswG59ZwXIKJS1g7D_2PgYE", gMap)
                .run();*/
/*
        DrawRoute.getInstance(GpsActivity.this).setFromLatLong(36.9011158, 127.1321606)
                .setToLatLong(36.9011158+0.01, 127.1321606+0.01).setGmapAndKey("AIzaSyAsBsRXQqwo_qlF-6fTWlhOlYZGYPIiA7E", gMap)
                .run();*/
        LatLng loc1 = new LatLng(slatitude, slongitude);
        LatLng loc2 = new LatLng(location.getLatitude(), location.getLongitude());

        double distance = getDistance(slatitude,slongitude,location.getLatitude(),location.getLongitude());
        if(distance<=10.0) {
            gMap.addPolyline(new PolylineOptions().add(loc1, loc2).width(15).color(Color.RED));
            dLocationLa[iLocation] = location.getLatitude();
            dLocationLo[iLocation] = location.getLongitude();
            slatitude = location.getLatitude();
            slongitude = location.getLongitude();

            showMyLocationMarker(location);

            iLocation++;
            Log.v("그려짐 확인", "오차범위 내에서 그려지는중");
        }
        else if(distance>10.0f){
            Log.v("그려지지않음", "오차범위 초과");
        }

    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 16));
        showMyLocationMarker(location);


    }

    private void requestMyLocation() {

        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        try {


            long minTime = 1000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.v("test","GPS 실행됨");
                            showCurrentLocation(location);
                            if (!type) {
                                callDrawRoute(location);
                            }

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
                    }
            );

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);


            }

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.v("test","Network 실행됨");
                            showCurrentLocation(location);
                            if (!type) {
                                callDrawRoute(location);
                            }
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
                    }
            );


        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }

    }

    private void showMyLocationMarker(Location location) {


        if (endP) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(dLocationLa[0], dLocationLo[0]));
            myLocationMarker.title("● 출발 위치\n");
            myLocationMarker.snippet("시작점");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            gMap.addMarker(myLocationMarker);
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(dLocationLa[iLocation-1], dLocationLo[iLocation-1]));
            myLocationMarker.title("● 도착 위치\n");
            myLocationMarker.snippet("도착점");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location));
            gMap.addMarker(myLocationMarker);
            endP = false;
        } else {
            if (myLocationMarker == null) {
                gMap.clear();
                myLocationMarker = new MarkerOptions();
                myLocationMarker.position(new LatLng(dLocationLa[0], dLocationLo[0]));
                myLocationMarker.title("● 출발 위치\n");
                myLocationMarker.snippet("시작점");
                myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
                gMap.addMarker(myLocationMarker);
                if(iLocation>1) {
                    friendMarker[iLocation] = new MarkerOptions();
                    friendMarker[iLocation].position(new LatLng(dLocationLa[iLocation - 1], dLocationLo[iLocation - 1]));
                    friendMarker[iLocation].title("현재 이동중인 위치");
                    friendMarker[iLocation].icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation_run));
                    gMap.addMarker(friendMarker[iLocation]);
                }
            } else {
                myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            }

        }
    }
   

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // GPS 정보를 보여주기 위한 이벤트 클래스 등록
        // 권한 요청을 해야 함
        if (!isPermission) {
            callPermission();
            return;
        }
        gps = new Gpsinfo(GpsActivity.this);


        if (gps.isGetLocation()) {

            slatitude = gps.getLatitude();
            slongitude = gps.getLongitude();

            dLocationLa[0]=slatitude;
            dLocationLo[0]=slongitude;
            LatLng curPoint = new LatLng(dLocationLa[0], dLocationLo[0]);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            txtSAd.setText("현재 주소 : " + conAdress( dLocationLa[0], dLocationLo[0]));
                            /*    txtLat.setText(String.valueOf(latitude));
                                txtLon.setText(String.valueOf(longitude));*/
     /*       Toast.makeText(
                    getApplicationContext(),
                    "시작 \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();*/

            //현재 위치를 시작점으로 설정

            type = false;

            endP = false;
            requestMyLocation();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }


    }

    public String conAdress(double lat, double lon) {
        final Geocoder geocoder = new Geocoder(this);
        String Result = "";

        List<Address> list = null;
        try {
            double d1 = lat;
            double d2 = lon;

            list = geocoder.getFromLocation(
                    d1, // 위도
                    d2, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size() == 0) {
                Log.v("오류", "리스트가 없습니다.");
            } else {
                String currentLocationAddress = list.get(1).getAddressLine(0).toString();
                Result  = currentLocationAddress;

                /*Address mAddress = list.get(0);
                Result = list.get(0).toString();*/
               /* Result = mAddress.getLocality() + " "
                        + mAddress.getThoroughfare() + " "
                        + mAddress.getFeatureName();*/
                /*Log.v("test", Result);*/
            }
        }

        return Result;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("test","99999999999999번 실행");
        getMenuInflater().inflate(R.menu.menu_none, menu);
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();

        //removeNotification();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        hFlag=false;
        removeNotification();
    }


}

