package tulipdev.nursinghomemng;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import modelobjects.Post;

public class PostDetails extends AppCompatActivity {
    private int index;
    private Post post;

    TextView pdtitle, pdqual, pdrespo;

    DatabaseReference databaseReference;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();


        pdtitle = findViewById(R.id.pdtitle);
        pdqual = findViewById(R.id.pdqual);
        pdrespo = findViewById(R.id.pdrespon);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            index = bundle.getInt("position");
            post = PostsActivity.posts.get(index);
        }

        if (post != null) {
            pdtitle.setText(post.title);
            pdqual.setText(post.qualifications);
            pdrespo.setText(post.responsibilities);
        }
    }

    public void takeJob(View view) {
        takeJob(post.home_id, post.getPid(), user.getUid());
    }

    private void takeJob(String homeid, final String postid, String cgid) {
        Map<String, Object> map = new HashMap<>();
        map.put("/caregivers/" + cgid + "/homes/" + homeid, true);
        databaseReference.updateChildren(map);

        Map<String, Object> othermap = new HashMap<>();
        othermap.put("/homes/" + homeid + "/caregivers/" + cgid, true);
        databaseReference.updateChildren(othermap);

        Map<String, Object> finalmap = new HashMap<>();
        finalmap.put("/posts/" + postid + "/taken_by/" + cgid, true);
        databaseReference.updateChildren(finalmap);

        //update availability of cargiver
        //databaseReference.child("caregivers").child(cgid).child("available").setValue(false);
        //update vacancies
    }
}
