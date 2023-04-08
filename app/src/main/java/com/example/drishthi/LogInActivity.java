package com.example.drishthi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Button googleSignUpbtn;
    private  String Username, Name, Number, Email, Address, UID;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private EditText edttxt1, edttxt2, edttxt3;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        googleSignUpbtn = findViewById(R.id.googleSignUpbtn);
        edttxt1 = findViewById(R.id.edttxt1);
        edttxt2 = findViewById(R.id.edttxt2);
        edttxt3 = findViewById(R.id.edttxt3);

        Username = edttxt1.getText().toString();
        Number = edttxt2.getText().toString();
        Address = edttxt3.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

    }

    private void SignIn() {
        Intent SignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(Database()){
                                Intent i = new Intent(LogInActivity.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }else {
                                Toast.makeText(LogInActivity.this, "Database not updated...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LogInActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public boolean Database(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Username = edttxt1.getText().toString();
        Name = currentUser.getDisplayName();
        Number = edttxt2.getText().toString();
        Email = currentUser.getEmail();
        Address = edttxt3.getText().toString();
        UID = currentUser.getUid();
        if(!(Username.isEmpty() || Number.isEmpty() || Address.isEmpty())){
            Users users = new Users(Username, Name, Number, Email,Address, UID);
            db = FirebaseDatabase.getInstance();
            reference = db.getReference("Users");
            reference.child(UID).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(LogInActivity.this, "Database Updated...", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }else {
            return false;
        }
    }
}