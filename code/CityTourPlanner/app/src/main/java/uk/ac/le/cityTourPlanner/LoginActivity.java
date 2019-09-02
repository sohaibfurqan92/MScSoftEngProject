package uk.ac.le.cityTourPlanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static int RC_SIGN_IN = 1 ;

    //private ProgressBar mProgressBar;
    private TextView mProgressStatusTextView;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //mProgressBar = findViewById(R.id.progressBar);




        mFirebaseAuth=FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //Toast.makeText(LoginActivity.this, "user signed in!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                }
                else{
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.bus_on_road)
                            .build()
                            ,RC_SIGN_IN);
                }
            }
        };
        

    }

    private void HandleProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //mProgressStatusTextView = findViewById(R.id.progressStatusTextView);
                    Thread.sleep(5000);
                   //mProgressStatusTextView.setText(getString(R.string.progress_message_1));
                    Thread.sleep(5000);
                    //mProgressStatusTextView.setText(getString(R.string.progress_message_2));
                    Thread.sleep(5000);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        HandleProgressBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

}
