package com.example.gpsinfo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button btnShowLocation;
    private DrawerLayout drawer;
    private TextView loginemail;
    private TextView loginname;
    GpsActivity gpa;
    boolean tg_Icon = false;
    private ListView listview = null ;
    SupportMapFragment mapFragment;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    private GoogleApiClient mGoogleApiClient = null;
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;
    private      FirebaseAuth mAuth;
    FirebaseUser user;
    Gpsinfo gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1=(Button)findViewById(R.id.btn_start);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        loginname = (TextView) findViewById(R.id.login_name);
        loginemail = (TextView) findViewById(R.id.login_email);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();


        loginname.setText(intent.getStringExtra("name"));
        loginemail.setText(intent.getStringExtra("email"));


        gps = new Gpsinfo(MainActivity.this);

        if (!gps.isGetLocation()) {
            gps.showSettingsAlert();
        }
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomGesturesEnabled(false);
        MapFragment.newInstance(options);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps.isGetLocation()) {
                    Intent intent = new Intent(MainActivity.this, GpsActivity.class);
                    startActivity(intent);



                }
                else
                    gps.showSettingsAlert();
            }
        });


        final String[] items = {"TrackList", "", "", "", ""} ;
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items) ;

        listview = (ListView) findViewById(R.id.drawer_menulist) ;
        dataSetting();

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                    switch (position) {
                        case 0 :
                            Intent intent=new Intent(MainActivity.this,ListActivity.class);
                            // TODO Auto-generated method stub
                            startActivity(intent);
                            break;
                        case 1:
                            Intent intentsetting=new Intent(MainActivity.this,SettingActivity.class);
                            // TODO Auto-generated method stub
                            startActivity(intentsetting);
                            break ;
            }

            }
        });

       }







    private void dataSetting(){

        MenuAdapter mMyAdapter = new MenuAdapter();
        mMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.list_gpsicon), "TrackList");
        mMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.list_setting), "Setting");
        /* 리스트뷰에 어댑터 등록 */
        listview.setAdapter(mMyAdapter);
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("test","1번 실행");

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem home_item = menu.findItem(R.id.action_home);
        MenuItem list_item = menu.findItem(R.id.action_list);
        if(tg_Icon){
            home_item.setVisible(true);
            list_item.setVisible(false);
        }
        else{
            home_item.setVisible(false);
            list_item.setVisible(true);
        }
         return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {

        // TODO Auto-generated method stub
        switch (item.getItemId()) {

            case R.id.action_list: // listicon 이 눌렸을 경우 이벤트 발생
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.openDrawer(Gravity.RIGHT);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    tg_Icon=true;
                    invalidateOptionsMenu();
                }

                /*Intent intent=new Intent(MainActivity.this,ListActivity.class);
                // TODO Auto-generated method stub
                startActivity(intent);
                //버튼 클릭 시 발생할 이벤트내
                return true;*/
          /*  case R.id.action_home: // setting_btn 이 눌렸을 경우 이벤트 발생
                return true;*/
            case R.id.action_home:
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                    tg_Icon=false;
                    invalidateOptionsMenu();
                }


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });
    }

    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }





}

