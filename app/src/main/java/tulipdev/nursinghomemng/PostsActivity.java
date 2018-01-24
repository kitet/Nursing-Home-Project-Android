package tulipdev.nursinghomemng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import helperclasses.PostsArrayAdapter;
import modelobjects.CareGiver;
import modelobjects.HomeModel;
import modelobjects.Post;

public class PostsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ListView postsListView;

    public static ArrayList<Post> posts = new ArrayList();
    public static ArrayList<HomeModel> homes = new ArrayList();
    List<String> homes_list = new ArrayList<>();
    private PostsArrayAdapter arrayAdapter;
    private ArrayAdapter<String> homesArrayAdapter;

    TextView cgName, cgDetails;

    ImageView imageView;
    ListView assigneListView;

    FirebaseUser user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();

        cgName = findViewById(R.id.cgNameTV);
        cgDetails = findViewById(R.id.cgDetailsTV);
        postsListView = findViewById(R.id.availableposts);
        assigneListView = findViewById(R.id.assigned_homesLV);
        imageView = findViewById(R.id.avatar);
        Picasso.with(this).load(R.drawable.avatar).into(imageView);

        arrayAdapter = new PostsArrayAdapter(this, R.layout.list_item, posts);
        postsListView.setAdapter(arrayAdapter);
        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(PostsActivity.this, PostDetails.class).putExtra("position", position));
            }
        });

        //show homes siged to
        homesArrayAdapter = new ArrayAdapter<String>(this, R.layout.recent_list_item, homes_list);
        assigneListView.setAdapter(homesArrayAdapter);
        assigneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PostsActivity.this, "Preview Home", Toast.LENGTH_SHORT).show();
            }
        });

        posts.clear();

        getSignedUser();
        getAssignedHomes();

        if (posts.size() == 0) {
            getAllAvailablePosts();
        }
    }

    private void getAssignedHomes() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("caregivers").child(uid).child("homes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()
                        ) {
                    getHome(dataSnapshot1.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getHome(String s) {
        DatabaseReference ref = firebaseDatabase.getReference("homes").child(s);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HomeModel homeModel = dataSnapshot.getValue(HomeModel.class);
                addHomeToList(homeModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addHomeToList(HomeModel homeModel) {
        homes.add(homeModel);
        homes_list.add(homeModel.name);
        homesArrayAdapter.notifyDataSetChanged();
    }

    private void getSignedUser() {
        DatabaseReference cgref = firebaseDatabase.getReference("caregivers").child(uid);
        cgref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CareGiver careGiver = dataSnapshot.getValue(CareGiver.class);
                updateProfile(careGiver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile(CareGiver careGiver) {
        cgName.setText("Name : " + careGiver.name);
        cgDetails.setText("Details : " + careGiver.details);
    }

    private void getAllAvailablePosts() {
        DatabaseReference postsRef = firebaseDatabase.getReference("posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()
                        ) {
                    Post post = postSnapShot.getValue(Post.class);
                    post.setPid(postSnapShot.getKey().toString());
                    posts.add(post);
                    updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {
        arrayAdapter.notifyDataSetChanged();
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }
}
