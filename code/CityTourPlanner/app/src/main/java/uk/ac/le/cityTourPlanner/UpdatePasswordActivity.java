package uk.ac.le.cityTourPlanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private TextInputEditText mCurrentPasswordEditText;
    private TextInputEditText mNewPasswordEditText;
    private TextInputEditText mConfirmedNewPasswordEditText;
    private Button mUpdatePasswordButton;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        mNewPasswordEditText = findViewById(R.id.newPasswordEditText);
        mConfirmedNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        mUpdatePasswordButton = findViewById(R.id.updatePasswordButton);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUpdatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current user

                String useremail = mUser.getEmail();

                //get credentials for reauthentication
                AuthCredential credential = EmailAuthProvider.getCredential(useremail,mCurrentPasswordEditText.getText().toString());

                mUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("user auth success", "User successfully authenticated");
                                updateUserPassword();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Auth exception update", "User not authenticated  "+e);
                                Toast.makeText(getApplicationContext(),"Couldn't authenticate user. Please try later or if the problem persists contact developer",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


    }

    private void updateUserPassword() {
        if(mNewPasswordEditText.getText().toString().trim().matches(mConfirmedNewPasswordEditText.getText().toString().trim())){
            String newPassword = mNewPasswordEditText.getText().toString();
            mUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                startActivity(new Intent(UpdatePasswordActivity.this,SettingsActivity.class));
                                Toast.makeText(getApplicationContext(),"Password successfully updated",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Couldn't update password",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            mNewPasswordEditText.setError("Passwords don't match");
            mConfirmedNewPasswordEditText.setError("Passwords don't match");
        }
    }

}
