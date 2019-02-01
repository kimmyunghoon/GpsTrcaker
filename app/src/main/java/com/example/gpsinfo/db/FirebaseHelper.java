package com.example.gpsinfo.db;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.ID;

@IgnoreExtraProperties
public class FirebaseHelper {

    public String id;
    public String daytime;
    public String Adress;

    public String locationLA;
    public String locationLO;
    public String runTime;
    public String sAdress;
    public String eAdress;
    public String title;

    public FirebaseHelper(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public FirebaseHelper( String title, String daytime,String locationLA,String locationLO,
                          String runTime, String sAdress,String eAdress) {

        this.daytime=daytime;
        this.Adress=Adress;
        this.locationLA= locationLA;
        this.locationLO=locationLO;
        this.runTime = runTime;
        this.sAdress = sAdress;
        this.eAdress=eAdress;
        this.title = title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("daytime", daytime);
        result.put("locationLA", locationLA);
        result.put("locationLO", locationLO);
        result.put("runTime", runTime);
        result.put("sAdress", sAdress);
        result.put("eAdress", eAdress);

        return result;
    }



}
