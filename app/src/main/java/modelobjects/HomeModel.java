package modelobjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by em on 11/10/17.
 */

public class HomeModel {

    public String name;
    public String address;
    public Map<String, Object> services;
    public Map<String, Object> ourposts;
    public Map<String, Object> caregivers;

    public HomeModel(){
        //default constructor
    }

    public HomeModel(String name, String address, Map<String, Object> services){
        this.name = name;
        this.address = address;
        this.services = services;
    }

    @Exclude
    public Map<String, Object> homeToMap(){
        HashMap<String, Object> homeModel = new HashMap<>();
        homeModel.put("name",name);
        homeModel.put("address",address);
        homeModel.put("services", services);
        return homeModel;
    }
}
