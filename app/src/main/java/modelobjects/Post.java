package modelobjects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by em on 11/10/17.
 */

public class Post {
    public String home_id;
    public String pid;
    public String title;
    public String qualifications;
    public String responsibilities;
    public int vacancies;
    public Map<String, Object> taken_by;
    public boolean available;
   public Post(){
       //default construction to be used by FirebaseDB
   }

   public Post(String home_id, String post_title, String post_qualifications, String post_responsibilities, int post_vacancies){
       this.home_id =home_id;
       this.title=post_title;
       this.qualifications=post_qualifications;
       this.responsibilities=post_responsibilities;
       this.vacancies=post_vacancies;
   }


   @Exclude
    public Map<String, Object> toMap(){
       HashMap<String, Object> post_result = new HashMap<>();
       post_result.put("home_id", home_id);
       post_result.put("title", title);
       post_result.put("qualifications", qualifications);
       post_result.put("responsibilities", responsibilities);
       post_result.put("vacancies", vacancies);
       post_result.put("available", true);
       return  post_result;
   }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Map<String, Object> getTaken_by() {
        return taken_by;
    }

    public void setTaken_by(Map<String, Object> taken_by) {
        this.taken_by = taken_by;
    }
}
