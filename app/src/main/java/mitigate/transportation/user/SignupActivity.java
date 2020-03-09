package mitigate.transportation.user;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SignupActivity extends AppCompatActivity {
    LinearLayout email_layout, name_layout, password_layout;
    ImageView go_forward;
    EditText email, password, confirm_password, f_name, l_name;
    String timeZone;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(AppConstants.DATABASENAME);

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String prefs = "user_credentials";
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    String firebaseToken;

    private void PermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS},
                    123);
        }else {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SignUpActivity.this,  new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String newToken = instanceIdResult.getToken();
//                Log.e("newToken",newToken);
//                firebaseToken = newToken;
//            }
//        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        Initialization();
        Action();
        timeZone = TimeZone.getDefault().getID();
    }

    private void Action(){
        go_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChecksandFinalstep();
            }
        });
    }

    private void ChecksandFinalstep(){
        Animation shake = AnimationUtils.loadAnimation(SignupActivity.this, R.anim.shake);
        if (email_layout.getVisibility() == View.VISIBLE){
            if (email.getText().toString().isEmpty()){
                email.startAnimation(shake);
                Toast.makeText(SignupActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            }else {
                email_layout.setVisibility(View.GONE);
                name_layout.setVisibility(View.VISIBLE);
            }
        }else if (name_layout.getVisibility() == View.VISIBLE){
            if (f_name.getText().toString().isEmpty()){
                f_name.startAnimation(shake);
                Toast.makeText(SignupActivity.this, "First Name is empty", Toast.LENGTH_SHORT).show();
            }else if (l_name.getText().toString().isEmpty()){
                l_name.startAnimation(shake);
                Toast.makeText(SignupActivity.this, "Last Name is empty", Toast.LENGTH_SHORT).show();
            } else {
                name_layout.setVisibility(View.GONE);
                password_layout.setVisibility(View.VISIBLE);
            }
        }else if (password_layout.getVisibility() == View.VISIBLE){
            if (password.getText().toString().isEmpty()){
                password.startAnimation(shake);
                Toast.makeText(SignupActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            }else if (confirm_password.getText().toString().isEmpty()){
                confirm_password.startAnimation(shake);
                Toast.makeText(SignupActivity.this, "Confirm Password is empty", Toast.LENGTH_SHORT).show();
            }else if (!password.getText().toString().equals(confirm_password.getText().toString())){
                password.startAnimation(shake);
                confirm_password.startAnimation(shake);
                password.setText("");
                confirm_password.setText("");
                Toast.makeText(SignupActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            }else {
                go_forward.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String name = f_name.getText().toString() + " " + l_name.getText().toString();
                FirebaseUserSignUp(email.getText().toString(), password.getText().toString(), name);
            }
        }
    }

    private void FirebaseUserSignUp(String email, String password, final String name){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignedUp", "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            UserModel userModel = new UserModel(user.getEmail(), name, user.getUid(), 0, null);

                            FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.USERS);

                            FirebaseDatabase.getInstance().getReference(AppConstants.DATABASENAME).child(AppConstants.USERS).child(userModel.getUserID()).setValue(userModel);

                            myRef.child(AppConstants.USERS).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                        UserModel userModel = snapshot.getValue(UserModel.class);
                                        if (userModel.getUserID().equals(user.getUid())){
                                            mEditor.putString("User", new Gson().toJson(userModel));
                                            mEditor.commit();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            go_forward.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            PermissionCheck();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignedUpFailed", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            go_forward.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
                                Manifest.permission.SEND_SMS},
                        123);
            }else {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }
    }

    private void Initialization(){
        email_layout = findViewById(R.id.email_layout);
        name_layout = findViewById(R.id.name_layout);
        password_layout = findViewById(R.id.password_layout);
        go_forward = findViewById(R.id.go_forward);
        email = findViewById(R.id.email_signup);
        f_name = findViewById(R.id.f_name_signup);
        l_name = findViewById(R.id.l_name_signup);
        password = findViewById(R.id.password_signup);
        confirm_password = findViewById(R.id.confirm_password_signup);
        progressBar = findViewById(R.id.progress_bar);
    }
}
