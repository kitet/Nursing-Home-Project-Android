package tulipdev.nursinghomemng;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelobjects.CareGiver;
import modelobjects.Post;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    // private String TAG = "VVV";
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Button button = findViewById(R.id.goNursing);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNursingHome(v);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> services = new HashMap<>();
        services.put("Companionship ", true);
        services.put("Personal Care", true);
        services.put("Home Help", true);
        services.put("Health Services Support", true);
    }

    private void readData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<Map<String, Post>> t = new GenericTypeIndicator<Map<String, Post>>(){};
//                Map<String, Post> posts = dataSnapshot.getValue(t);
//                posts.values();

                //Alternative
                ArrayList<Post> posts = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()
                        ) {
                    posts.add(child.getValue(Post.class));
                }

                // System.out.println(posts.get(0).taken_by);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Post newPost = dataSnapshot.getValue(Post.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }


    //    private void signOut(){
//        FirebaseAuth.getInstance().signOut();
//    }
    private void goToNursingHome(View view) {
        startActivity(new Intent(this, NursingHomeActivity.class));
    }

    public void loginCareGiver(View view) {
        startActivity(new Intent(this, PostsActivity.class));
    }

    public void logOut(View view){
       AuthUI.getInstance()
               .signOut(this)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       startActivity(new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                   }
               });
       //delete user
      // AuthUI.getInstance().delete(this);
    }
}