package com.example.gpsinfo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.gpsinfo.List_Table;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
    private Context context;
    private String name;
    public DBHelper(Context context,String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        this.context = context;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE "+name+"(");
        sb.append("_ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append("TITLE TEXT  ,");
        sb.append("DAYTIME TEXT,");
        sb.append("START_ADRESS TEXT,");
        sb.append("END_ADRESS TEXT,");
        sb.append("RUNNING_TIME TEXT,");
        sb.append("LOCATIONLA TEXT,");
        sb.append("LOCATIONLO TEXT);");

        db.execSQL(sb.toString());
    }
    public ArrayList<List_Table> getAllList_Tables(){
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM "+name+";");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<List_Table> list_tables = new ArrayList<List_Table>();
        Cursor cursor = db.rawQuery(sb.toString(),null);
        List_Table list_table = null;
        if (cursor.moveToFirst()) {
            do {
                list_table = new List_Table();
                list_table.set_id(Integer.parseInt(cursor.getString(0)));
                list_table.setTitle(cursor.getString(1));
                list_table.setDaytime(cursor.getString(2));
                list_table.setStartAdress(cursor.getString(3));
                list_table.setEndAdress(cursor.getString(4));
                list_table.setRunningTime(cursor.getString(5));
                list_table.setLocationla(cursor.getString(6));
                list_table.setLocationlo(cursor.getString(7));
                list_tables.add(list_table);
            } while (cursor.moveToNext());
        }
        return list_tables;

    }
    public void addList(List_Table list_table){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO "+name+" (");
        sb.append("TITLE, DAYTIME,START_ADRESS,END_ADRESS,RUNNING_TIME, LOCATIONLA ,LOCATIONLO) ");
        sb.append("VALUES(#TITLE#,#DAYTIME#,#START_ADRESS#,#END_ADRESS#,#RUNNING_TIME#, #LOCATIONLA#,#LOCATIONLO#);");

        String query = sb.toString();
        query = query.replace("#TITLE#","'"+list_table.getTitle()+"'");
        query = query.replace("#DAYTIME#","'"+list_table.getDaytime()+"'");
        query = query.replace("#START_ADRESS#","'"+list_table.getStartAdress()+"'");
        query = query.replace("#END_ADRESS#","'"+list_table.getEndAdress()+"'");
        query = query.replace("#RUNNING_TIME#","'"+list_table.getRunningTime()+"'");
        query = query.replace("#LOCATIONLA#","'"+list_table.getLocationla()+"'");
        query = query.replace("#LOCATIONLO#","'"+list_table.getLocationlo()+"'");
        db.execSQL(query);

        Toast.makeText(context,"트랙 기록 완료",Toast.LENGTH_SHORT).show();

    }
    public String checkList(List_Table list_table,String Value){
        SQLiteDatabase db = getWritableDatabase();
        String id="";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT _ID ");
        sb.append("FROM "+name);
        sb.append("WHERE _ID = "+Value+";");

        Cursor cursor =  db.rawQuery(sb.toString() ,null);
        while(cursor.moveToNext()) {
            id=""+(Integer.parseInt(cursor.getString(0)));
        }
        return id;
    }

    public void delList(List_Table list_table, String title,String time){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();

        db.execSQL(String.format("DELETE FROM %s WHERE TITLE = '%s' AND DAYTIME = '%s';",name, title ,time));
       /* if(Value)
        db.execSQL(String.format("UPDATE %s SET _id = _id - 1 ",name);*/

        Toast.makeText(context,"트랙 삭제 완료",Toast.LENGTH_SHORT).show();

    }
    public void testDB(){
        SQLiteDatabase db = getReadableDatabase();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context,"Version 올라감",Toast.LENGTH_SHORT).show();
    }
}
