package com.example.gpsinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpsinfo.db.DBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.graphics.Canvas;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;

public class StoreActivity extends AppCompatActivity implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    String startAdress;
    String endAdress;
    String runningTime;
    String getTime;
    String title;
    MarkerOptions myLocationMarker;
    private TextView txtSAd;
    private TextView txtEAd;
    private TextView txttime;
    private TextView txttitle;
    private TextView txtday;
    private Button btnEndLocation;
    private Button btnSubmitLocation;

    double dLocationLa[] = new double[1000];
    double dLocationLo[] = new double[1000];
    private ProgressBar progressBar;
    private LayoutInflater inflater;
    private View header;
    SupportMapFragment mapFragment;

    int iLocation;
    GpsActivity gpsA;
    GoogleMap gMap;

    private  Bitmap bitmap;
    Uri uri;
    MagnetView midSurf;
    static File fileRoute = null;
    String str_name="";
    FileOutputStream out;
    static String pathStr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gpsinfo);
    /*    gpsA = new GpsActivity();
        gMap = gpsA.GM();
        ((GpsActivity)GpsActivity.mContext).onMapReady(gMap);

*/


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        mapFragment.getMapAsync(this);
        txtSAd = (TextView) findViewById(R.id.tv_StartAdress);
        txtEAd = (TextView) findViewById(R.id.tv_EndAdress);
        txttime = (TextView) findViewById(R.id.tv_time);
        txttitle = (TextView) findViewById(R.id.tv_title);
        txtday = (TextView) findViewById(R.id.tv_day);
        btnEndLocation = (Button) findViewById(R.id.btn_end);
        btnSubmitLocation = (Button) findViewById(R.id.btn_submit);

        Intent intent = getIntent();

        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);
        txtEAd.setVisibility(View.VISIBLE);
        txttitle.setVisibility(View.VISIBLE);

        startAdress = getIntent().getStringExtra("startAdress");
        endAdress = getIntent().getStringExtra("endAdress");
        runningTime = getIntent().getStringExtra("runningTime");
        title = getIntent().getStringExtra("title");
        dLocationLa = getIntent().getDoubleArrayExtra("laLocation");
        dLocationLo = getIntent().getDoubleArrayExtra("loLocation");
        //Log.v("도대체 뭔상황이지? ",dLocationLa[0]+"/"+dLocationLo[0]);
        iLocation = getIntent().getIntExtra("iLocation", 1);

        getTime = getIntent().getStringExtra("getTime");
        txtSAd.setText(startAdress);
        txtEAd.setText(endAdress);
        txttime.setText(runningTime);
        txtday.setText(getTime);
        txttitle.setText(title);
        //액션바 숨기기
        //hideActionBar();
        btnEndLocation.setVisibility(View.GONE);
        btnSubmitLocation.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("test", "1번 실행");
        getMenuInflater().inflate(R.menu.menu_list1, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_list: // search_btn 이 눌렸을 경우 이벤트 발생
                Intent intent5 = new Intent(StoreActivity.this, ListActivity.class);
                // TODO Auto-generated method stub
                startActivity(intent5);
                finish();
                //버튼 클릭 시 발생할 이벤트내
                return true;
            case R.id.action_home: // setting_btn 이 눌렸을 경우 이벤트 발생
                finish();
                return true;
            case R.id.action_share: // 공유버튼 이 눌렸을 경우 이벤트 발생
                CaptureMapScreen();
                File file = new File(pathStr);
                file.delete();
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void shareBittmap(){

        //saveBitmapToJpeg(getApplicationContext(),captureView,"share");
        uri = FileProvider.getUriForFile(getApplicationContext(),"com.example.gpsinfo.fileprovider",new File(saveBitmapToJpeg(getApplicationContext(),drawBitmap(),"share")));

    }
    /*public File saveFile(){
        return
    }*/
    public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name){
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = name + ".jpg";  // 파일이름은 마음대로!
        File tempFile = new File(storage,fileName);
        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }

    public void CaptureMapScreen() {
        Intent it3=getIntent();
        str_name=it3.getStringExtra("it3_name");
        fileRoute = Environment.getExternalStorageDirectory();
        Log.v("실행 ","");
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;
            @Override
            public void onSnapshotReady(Bitmap snapshot) {

                bitmap = snapshot;
                File storage = getApplicationContext().getCacheDir(); // 이 부분이 임시파일 저장 경로
                String fileName = snapshot+".jpg";  // 파일이름은 마음대로!
                File tempFile = new File(storage,fileName);
                try {
                File path = getApplicationContext().getCacheDir();
                        out = new FileOutputStream(tempFile);
                        pathStr = tempFile.getPath();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    Log.v("저장되엇습니다. 경로 - ",""+tempFile.getPath());

                        out.close(); // 마무리로 닫아줍니다.

                    uri = FileProvider.getUriForFile(getApplicationContext(),"com.example.gpsinfo.fileprovider",new File(pathStr));
                    Log.v("test3 : ", "" + uri);
                    if(uri!=null){
                        Intent shareintent = new Intent(Intent.ACTION_SEND);
                        shareintent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareintent.setType("image/+");
                        startActivity(Intent.createChooser(shareintent, "공유"));
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };

        gMap.snapshot(callback);

    }




    public Bitmap drawBitmap() {
        midSurf = new MagnetView(this);
        View container;
        container = getWindow().getDecorView().getRootView();
        int width_container = container.getWidth() ;//캡쳐할 레이아웃 크기
        int height_container = container.getHeight() ;//캡쳐할 레이아웃 크기
          Bitmap bitmap = Bitmap.createBitmap(width_container, height_container, Bitmap.Config.ARGB_8888);
             Canvas canvas = new Canvas(bitmap);
            midSurf.surfaceDestroyed(null); //Thread 잠시 멈춤(pause)
            //container = getWindow().getDecorView();
            //container.setDrawingCacheEnabled(true);
            //container.buildDrawingCache(true);
            //onDraw(canvas);
            container.draw(canvas);
            midSurf.surfaceCreated(null); //Thread 재개(resume)
             return bitmap;
          }



    public Bitmap bitmapToFile(){

        EGL10 egl = (EGL10) EGLContext.getEGL();
        GL10 gl = (GL10)egl.eglGetCurrentContext().getGL();
        View container;
        container = getWindow().getDecorView();
        int width_container = container.getWidth() ;//캡쳐할 레이아웃 크기
        int height_container = container.getHeight() ;//캡쳐할 레이아웃 크기
        container.setDrawingCacheEnabled(true);
        container.buildDrawingCache(true);
        Bitmap captureView=Bitmap.createBitmap(container.getMeasuredWidth(), container.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas screenShotCanvas = new Canvas(captureView);
        container.draw(screenShotCanvas);


        /***********************핵심부분**********************************/
       // Bitmap captureView = Bitmap.createBitmap(container.getMeasuredWidth(), container.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //Bitmap captureView= createBitmapFromGLSurface(0,0,container.getMeasuredWidth(), container.getMeasuredHeight(),gl);
       // Bitmap captureView = captureView1.copy(Bitmap.Config.ARGB_8888,true);




        /***********************핵심부분*****************************************/
        FileOutputStream fos = null;
        Intent it3=getIntent();
        String str_name=it3.getStringExtra("it3_name");
        File fileRoute = null;
        fileRoute = Environment.getExternalStorageDirectory();
        try {
            File path = new File(fileRoute,"temp");
            if(!path.exists()){//
                 if(!path.isDirectory()){
                     path.mkdirs(); }

                    fos = new FileOutputStream(fileRoute+"/temp/"+str_name+"-.jpeg");
                }
                Log.d("[screenshot]", " : " + container.getDrawingCache());
                 captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                 container.setDrawingCacheEnabled(false);

            } catch (FileNotFoundException e) {
                 e.printStackTrace();
    }

    return captureView;
}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng curPoint = new LatLng(dLocationLa[0], dLocationLo[0]);
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curPoint)      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder*/
        //gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 16));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17));


        for (int i = 0; i < iLocation-1; i++) {
            LatLng loc1 = new LatLng(dLocationLa[i], dLocationLo[i]);
            LatLng loc2 = new LatLng(dLocationLa[i+1], dLocationLo[i+1]);
            gMap.addPolyline(new PolylineOptions().add(loc1, loc2).width(15).color(Color.RED));
            Log.v("La/Lo", "" + dLocationLa[i] + "/" + dLocationLo[i]);

        }



        myLocationMarker = new MarkerOptions();
        myLocationMarker.position(new LatLng(dLocationLa[0], dLocationLo[0]));
        myLocationMarker.title("● 출발 위치\n");
        myLocationMarker.snippet("● GPS로 확인한 위치");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
        gMap.addMarker(myLocationMarker);

        myLocationMarker = new MarkerOptions();
        myLocationMarker.position(new LatLng(dLocationLa[iLocation-1], dLocationLo[iLocation-1]));
        myLocationMarker.title("● 도착 위치\n");
        myLocationMarker.snippet("● GPS로 확인한 위치");
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location));
        gMap.addMarker(myLocationMarker);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

