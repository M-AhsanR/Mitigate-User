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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

public class LoginActivity extends AppCompatActivity {

    ImageView go_forward;
    TextView sign_up_btn;
    EditText email, password;
    String timeZone;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    String firebaseToken;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(AppConstants.DATABASENAME);

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String prefs = "user_credentials";

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
            LoginUser(email.getText().toString(), password.getText().toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        if (sharedPreferences.getString("User", "") != null){
            if (!sharedPreferences.getString("User", "").isEmpty()){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        setContentView(R.layout.activity_login);

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String newToken = instanceIdResult.getToken();
//                Log.e("newToken",newToken);
//
//                firebaseToken = newToken;
//
//            }
//        });

        // Initialize Firebase App
        FirebaseApp.initializeApp(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(prefs, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        go_forward = findViewById(R.id.go_forward);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        email = findViewById(R.id.email_login);
        progressBar = findViewById(R.id.progress_bar);
        password = findViewById(R.id.password_login);
        go_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                if (email.getText().toString().isEmpty()){
                    email.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                }else if (password.getText().toString().isEmpty()){
                    password.startAnimation(shake);
                    Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }else {
                    go_forward.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    PermissionCheck();
                }
            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        timeZone = TimeZone.getDefault().getID();

    }

    private void LoginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LogedIn", "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

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

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LogedInFailed", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
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
                LoginUser(email.getText().toString(), password.getText().toString());
            }
        }
    }
}
