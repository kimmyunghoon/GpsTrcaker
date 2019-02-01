package com.example.gpsinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpsinfo.db.DBHelper;
import com.example.gpsinfo.db.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.lang.String;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private int count=0;
    String title;
    String getTime;
    String startAd;
    String endAd;
    String LaLo;
    String LoLo;
    int iLocation;
    String runningT;


    double dLocationLa[] = new double[1000];
    double dLocationLo[] = new double[1000];
    //리스트에 들어갈 원소를 초기화
    ArrayList<String> listItems=new ArrayList<String>();
   // private DBHelper dbHelper;
    //리스트의 데이터를 다루는 어댑터 선언
    ArrayAdapter<String> adapter;
    DBHelper dbHelper = new DBHelper(ListActivity.this,
                    "Last_Track_List",null,1);
    ArrayList<List_Table> list_tables;
    List_TableAdapter mMyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Intent intent=new Intent(this.getIntent());

        dbHelper.testDB();

        if(dbHelper ==null){
            dbHelper = new DBHelper(ListActivity.this,"TEST",null,1);
        }
        list_tables = dbHelper.getAllList_Tables();

        for (List_Table cn : list_tables) {
                String log = "Id: "+cn.getId()+" ,title: " + cn.getTitle() + " ,Daytime: " + cn.getDaytime()
                        +" / "+cn.getStartAdress()+" / "+cn.getEndAdress() +" / "+ cn.getLocationla() +" / "+
                        cn.getLocationlo() +" / "+ cn.getRunningTime();
                Log.v("db내용",log);
        }
        List_Table list = new List_Table();

        /* 위젯과 멤버변수 참조 획득 */

        mListView = (ListView)findViewById(R.id.listview);
        mMyAdapter = new List_TableAdapter( list_tables,ListActivity.this);
        mListView.setAdapter( mMyAdapter);

       /* Title = intent.getStringExtra("list_title");
        Daytime = intent.getStringExtra("list_day");*/
        /* 아이템 추가 및 어댑터 등록 */
        //dataSetting();
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(ListActivity.this);
                ad.setTitle("삭제하시겠습니까?");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List_Table list_table = new List_Table();
                        TextView tv1=(TextView)parent.getChildAt(position).findViewById(R.id.tv_name);
                        TextView tv2=(TextView)parent.getChildAt(position).findViewById(R.id.tv_contents);
                        //sqlLite db삭제부분
                        dbHelper.delList( list_table,tv1.getText().toString(),tv2.getText().toString());

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("tracks").orderByChild("title").equalTo(tv1.getText().toString());
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mListView.setAdapter( mMyAdapter);
                        mMyAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        finish();
                        Intent i = new Intent(getApplicationContext(), ListActivity.class);
                        startActivity(i);
                           //닫기

                        // Event
                    }
                });

                        // 취소 버튼 설정
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();     //닫기

                        // Event
                    }
                });
                ad.show();
                // 창 띄우기

                return true;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //sqlLite 값 불러우기 해당 리스트의 디비 값 불러오기
                list_tables=dbHelper.getAllList_Tables();
              /*  Log.v("테이블 값 테스트", "2 : "+list_tables.get(0).getTitle());
                Log.v("테이블 값 테스트", "2 : "+list_tables.get(0).getStartAdress());
                Log.v("테이블 값 테스트", "1 : "+list_tables.get(0).getEndAdress());
                Log.v("테이블 값 테스트", "3 : "+list_tables.get(0).getDaytime());
                Log.v("테이블 값 테스트", "4 : "+list_tables.get(0).getRunningTime());
                Log.v("테이블 값 테스트", "5 : "+list_tables.get(0).getLocationla());
                Log.v("테이블 값 테스트", "6 : "+list_tables.get(0).getLocationlo());*/
                title = list_tables.get(position).getTitle();
                getTime= list_tables.get(position).getDaytime();
                startAd= list_tables.get(position).getStartAdress();
                endAd= list_tables.get(position).getEndAdress();
                LaLo = list_tables.get(position).getLocationla();
                LoLo = list_tables.get(position).getLocationlo();
                runningT = list_tables.get(position).getRunningTime();
                iLocation=0;

                String[] substringsLaLo  = LaLo.split(",");
                String[] substringsLoLo  = LoLo.split(",");
                for(int i=0;i<substringsLaLo.length-1&&i<substringsLoLo.length-1;i++){
                    dLocationLa[i]=Double.parseDouble(substringsLaLo[i]);
                    dLocationLo[i]=Double.parseDouble(substringsLoLo[i]);
                    Log.v("좌표확인 : ",""+Double.parseDouble(substringsLaLo[i]));
                    Log.v("좌표확인 : ",""+Double.parseDouble(substringsLoLo[i]));
                    iLocation++;
                }
                Log.v("좌표확인 : ",""+Double.parseDouble(substringsLaLo[0]));
                Log.v("좌표확인 : ",""+Double.parseDouble(substringsLoLo[0]));

                Intent intent2=new Intent(ListActivity.this,StoreActivity.class);

                    intent2.putExtra("startAdress",startAd );
                    intent2.putExtra("endAdress",endAd);
                    intent2.putExtra("runningTime", runningT);
                    intent2.putExtra("getTime", getTime);
                    intent2.putExtra("laLocation",dLocationLa);
                    intent2.putExtra("loLocation",dLocationLo);
                    intent2.putExtra("title",title);
                    intent2.putExtra("iLocation",iLocation);

                startActivity(intent2);
            }
        });


    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list2, menu);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId())
        {
            case R.id.action_home: // search_btn 이 눌렸을 경우 이벤트 발생

                finish();
                //버튼 클릭 시 발생할 이벤트내
                return true;
          /*  case R.id.action_home: // setting_btn 이 눌렸을 경우 이벤트 발생
                return true;*/
            default:
                return super.onOptionsItemSelected(item); }


    }
}
