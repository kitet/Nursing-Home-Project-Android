package tulipdev.nursinghomemng;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelobjects.CareGiver;
import modelobjects.HomeModel;

public class SignUpOrSignIn extends AppCompatActivity {

    boolean available;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_or_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signInBtnClick(View view) {
        startActivity(new Intent(this, SignInActivity.class
        ));
    }

    public void signUpBtnClick(View view) {
        //startActivity(new Intent(this, SignUpActivity.class));
        signUpOptionDialog();
    }

    private void signUpOptionDialog() {
        Button hmBtn, cgBtn;
        LayoutInflater inflater = LayoutInflater.from(this);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        View view = inflater.inflate(R.layout.sign_up_alert_dialog, null);
        hmBtn = view.findViewById(R.id.signUpHome);
        cgBtn = view.findViewById(R.id.signUpCareGiver);
        hmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //builder.dismiss();
                builder.dismiss();
                View view = prepHomeSignUpView();
                if (view != null) {
                    dynamicAlert(view);
                }
            }
        });
        cgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle click
                builder.dismiss();
                View view = prepareCGSignUpView();
                if (view != null) {
                    dynamicAlert(view);
                }
            }
        });

        builder.setView(view);
        builder.show();
    }

    private View prepareCGSignUpView() {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.register_cg, null);
        final TextInputEditText full_name = view.findViewById(R.id.full_name);
        final TextInputEditText emailE = view.findViewById(R.id.email);
        final SwitchCompat switchCompat = view.findViewById(R.id.switchCompat);
        final TextInputEditText detailsE = view.findViewById(R.id.address);
        final TextInputEditText password = view.findViewById(R.id.passwordT);
        final Button button = view.findViewById(R.id.registerCGBtn);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //available
                    available = true;
                } else {
                    //unavailable...just registering
                    available = false;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = full_name.getText().toString();
                String email = emailE.getText().toString();
                String details = detailsE.getText().toString();
                String pass = password.getText().toString();

                if (!fullname.isEmpty() && !email.isEmpty() && !details.isEmpty() && !pass.isEmpty()) {
                    //signUp user
                    createNewCareGiver(email, pass, fullname, details, available);
                } else {
                    Toast.makeText(SignUpOrSignIn.this, "Half Filled Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void dynamicAlert(View view) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setView(view);
        alert.show();
    }

    private View prepHomeSignUpView() {
        View view = null;
        final List<String> services_list = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        final Map<String, Object> services = new HashMap<>();

        view = LayoutInflater.from(this).inflate(R.layout.register_home, null);
        final TextInputEditText home_name = view.findViewById(R.id.home_name);
        final TextInputEditText addressTV = view.findViewById(R.id.addressInput);
        ListView listView = view.findViewById(R.id.servicesList);
        final TextInputEditText serviceInput = view.findViewById(R.id.serviceInput);
        Button addBtn = view.findViewById(R.id.addBtn);
        Button registerHomeBtn = view.findViewById(R.id.registerHomeBtn);

        final TextInputEditText emailTV = view.findViewById(R.id.email);
        final TextInputEditText passwordTV = view.findViewById(R.id.password);

        adapter = new ArrayAdapter<>(this, R.layout.recent_list_item, services_list);
        listView.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add service inboput to list and map
                String new_service = serviceInput.getText().toString();
                if (!new_service.isEmpty()) {
                    services_list.add(new_service);
                    services.put(new_service, true);
                } else {
                    Toast.makeText(SignUpOrSignIn.this, "Please enter a service", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                serviceInput.setText("");
            }
        });

        registerHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = home_name.getText().toString();
                String address = addressTV.getText().toString();
                String email = emailTV.getText().toString();
                String pass = passwordTV.getText().toString();
                //chck for empty inputs
                if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !address.isEmpty() && services.size() > 0) {
                    //register new Home
                    createNewHome(email, pass, name, address, services);
                }
            }
        });

        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in
        //check if user exists in either homes or caregivers
        //if home take to home panel showing details home UI
        //if cargiver take them to that specific caregiver UI
    }

    private void writeNewHome(String h_uid, String h_name, String address, Map<String, Object> services) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HomeModel homeModel = new HomeModel(h_name, address, services);
        Map<String, Object> homeValues = homeModel.homeToMap();

        Map<String, Object> homesChildUpdate = new HashMap<>();
        homesChildUpdate.put("/homes/" + h_uid, homeValues);

        databaseReference.updateChildren(homesChildUpdate);
    }


    private void writeNewCareGiver(String uid, String name, String details, boolean available) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        CareGiver careGiver = new CareGiver(name, details, available);
        Map<String, Object> cgValues = careGiver.caregiverMapping();

        Map<String, Object> cgUpdate = new HashMap<>();
        cgUpdate.put("/caregivers/" + uid, cgValues);

        databaseReference.updateChildren(cgUpdate);
    }


    private void createNewHome(String email, String passw, final String name, final String address, final Map services) {
        mAuth.createUserWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewHome(user.getUid(), name, address, services);
                            startActivity(new Intent(SignUpOrSignIn.this, NursingHomeActivity.class));
                        } else {
                            Toast.makeText(SignUpOrSignIn.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewCareGiver(String email, String password,  final String name, final String details, final boolean availabl) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewCareGiver(user.getUid(), name, details, availabl);
                            startActivity(new Intent(SignUpOrSignIn.this, PostsActivity.class));
                        }
                        else{
                            Toast.makeText(SignUpOrSignIn.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
