package modelobjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by em on 11/10/17.
 */

public class CareGiver {
    public String id;
    public String name;
    public String details;
    public boolean available;
    //public String location;

    public CareGiver(){
        //default constructor
    }

    public CareGiver(String name, String details, boolean available){
        this.name = name;
        this.details = details;
        this.available = available;

    }

    @Exclude
    public Map<String, Object> caregiverMapping(){
        HashMap<String, Object> careMap = new HashMap<>();
        careMap.put("name", name);
        careMap.put("details", details);
        careMap.put("available", available);
        return careMap;
    }
}
