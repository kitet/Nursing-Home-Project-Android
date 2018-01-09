package tulipdev.nursinghomemng;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import modelobjects.Post;

public class NewPost extends AppCompatActivity {

    private DatabaseReference databaseReference;
    TextView titleTV, qualTV, respoTV, vacanciesTV;

    FirebaseUser user;
    String hid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        user = FirebaseAuth.getInstance().getCurrentUser();
        hid = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //initialize views
        titleTV = findViewById(R.id.post_title);
        qualTV = findViewById(R.id.post_qualifications);
        respoTV = findViewById(R.id.post_responsibilities);
        vacanciesTV = findViewById(R.id.post_vacancies);
    }


    private void writeNewPost(String hid, String title, String qua, String respo, int va) {
        String postid = databaseReference.child("posts").push().getKey();
        Post post = new Post(hid, title, qua, respo, va);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + postid, postValues);

        databaseReference.updateChildren(childUpdates);

        Map<String, Object> map = new HashMap<>();
        map.put("/homes/" + hid + "/ourposts/" + postid, true);
        databaseReference.updateChildren(map);
        // writeNewCareGiver("Nichols","Dedicated zombie");
    }

    public void savePost(View view) {
        if (!titleTV.getText().toString().isEmpty() && !qualTV.getText().toString().isEmpty() && !respoTV.getText().toString().isEmpty() && !vacanciesTV.getText().toString().isEmpty()) {
            writeNewPost(
                    hid,
                    titleTV.getText().toString(),
                    qualTV.getText().toString(),
                    respoTV.getText().toString(),
                    Integer.parseInt(vacanciesTV.getText().toString()));
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Partially filled details", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
