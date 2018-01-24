package tulipdev.nursinghomemng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import modelobjects.CareGiver;
import modelobjects.HomeModel;
import modelobjects.Post;

public class NursingHomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView hTitle, hAddress, hServices;
    HomeModel homeModel = new HomeModel();
    Button button;
    ArrayList<Post> posts = new ArrayList();
    List<String> post_titles = new ArrayList<>();

    ArrayList<CareGiver> cglist = new ArrayList();
    List<String> cgnames = new ArrayList<>();

    private ListView recentlist, caregivers;

    FirebaseDatabase database;
    ArrayAdapter<String> recentAdapter, caregAdapter;
    FirebaseUser user;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursing_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance();

        hTitle = findViewById(R.id.homeName);
        hAddress = findViewById(R.id.homeAddress);
        hServices = findViewById(R.id.homeServices);
        recentlist = findViewById(R.id.recentpostsListView);
        caregivers = findViewById(R.id.listedCaregivers);

        //showing list of recent posts
        recentAdapter = new ArrayAdapter<String>(this, R.layout.recent_list_item, post_titles);
        recentlist.setAdapter(recentAdapter);
        recentlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NursingHomeActivity.this, "to Show Details Preview as Dialog", Toast.LENGTH_SHORT).show();
            }
        });


        //showing list of caregivers
        caregAdapter = new ArrayAdapter<String>(this, R.layout.recent_list_item, cgnames);
        caregivers.setAdapter(caregAdapter);
        caregivers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NursingHomeActivity.this, "to Show CareGiver Preview Details", Toast.LENGTH_SHORT).show();
            }
        });

        button = findViewById(R.id.newpost);
        button.setOnClickListener(this);
        readData();
        readRecentPosts();
        readCareGivers();
    }

    private void readRecentPosts() {
        Query ref = database.getReference("homes").child(uid).child("ourposts").limitToFirst(5);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()
                        ) {
                    getPost(postSnapShot.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readCareGivers() {
        DatabaseReference caredDatabaseReference = database.getReference("homes").child(uid).child("caregivers");
        caredDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot careGive : dataSnapshot.getChildren()
                        ) {
                    getCareGiver(careGive.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCareGiver(String s) {
        DatabaseReference databaseReference = database.getReference("caregivers").child(s);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CareGiver careGiver = dataSnapshot.getValue(CareGiver.class);
                updateCareGiversList(careGiver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCareGiversList(CareGiver careGiver) {
        cglist.add(careGiver);
        cgnames.add(careGiver.name);
        caregAdapter.notifyDataSetChanged();
    }

    private Post getPost(String s) {
        DatabaseReference posts = database.getReference("posts").child(s);
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                updateList(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    private void updateList(Post post) {
        posts.add(post);
        post_titles.add(post.title);
        recentAdapter.notifyDataSetChanged();
    }

    private void readData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("homes").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HomeModel homeModel = dataSnapshot.getValue(HomeModel.class);
                updateUI(homeModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(HomeModel homeModel) {
        if (homeModel != null) {
            hTitle.setText(homeModel.name);
            hAddress.setText(homeModel.address);
            String services = "";
            for (String service : homeModel.services.keySet()
                    ) {
                services += "*" + service + "\n";
            }
            hServices.setText(services);
            button.setVisibility(View.VISIBLE);
        } else {
            //something not working...home details missing
            //alertDialog()
            //consider redirecting to sign in again
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newpost:
                startActivity(new Intent(this, NewPost.class));
                break;
            default:
                break;
        }
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }
}
