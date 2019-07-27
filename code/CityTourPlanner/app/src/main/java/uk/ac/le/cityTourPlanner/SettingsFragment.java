package uk.ac.le.cityTourPlanner;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingsFragment extends PreferenceFragment {

    boolean emailChangeStatus=false;
    boolean accountDeletionStatus = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String settings = getArguments().getString("settings");

        if(settings.equals("userprofile")){
            addPreferencesFromResource(R.xml.user_profile_settings);
            final EditTextPreference DisplayNamePref = (EditTextPreference) findPreference("user_display_name_pref");
            final EditTextPreference EmailPref = (EditTextPreference) findPreference("user_email_pref");
            final Preference DeleteAccountPref = findPreference("acc_delete_pref");

            DisplayNamePref.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            EmailPref.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            Preference PasswordPref = findPreference("user_password_pref");
            PasswordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("ClickedPref!", "onPreferenceClick: password pref clicked");
                    startActivity(new Intent(getActivity(),UpdatePasswordActivity.class));
                    return false;
                }
            });

            EmailPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(reauthenticateAndUpdateEmail(newValue)){
                        preference.setDefaultValue(newValue);
                    }
                    return true;
                }
            });

            DeleteAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                   reauthenticateAndDeleteAccount();
                   return true;
                }
            });


        }
        else if(settings.equals("trips")){
            addPreferencesFromResource(R.xml.trips_settings);

            final SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getActivity());

            final SwitchPreference CustomPlacesSwitch = (SwitchPreference) findPreference("key_customize_trips");
            CustomPlacesSwitch.setSummaryOn("Custom place retrieval enabled");
            CustomPlacesSwitch.setSummaryOff("Custom place retrieval disabled");

            CustomPlacesSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String oldPlaceType = null;
                    if(CustomPlacesSwitch.isChecked()){
                        oldPlaceType = sharedPreferences.getString("specify_places_type_pref","point_of_interest");
                        CustomPlacesSwitch.setChecked(false);
                        Toast.makeText(getActivity(),"Switch unchecked",Toast.LENGTH_LONG).show();
                        sharedPreferences.edit().putString("specify_places_type_pref","point_of_interest").apply();
                    }
                    else {
                        CustomPlacesSwitch.setChecked(true);
                        ListPreference listPreference = (ListPreference)findPreference("specify_places_type_pref");
                        String selected = listPreference.getValue();
                        Toast.makeText(getActivity(),"Switch checked",Toast.LENGTH_LONG).show();
                        sharedPreferences.edit().putString("specify_places_type_pref",selected).apply();
                    }
                    return false;
                }
            });

            //final ListPreference listPreference = (ListPreference) findPreference("specify_places_type_pref");
           String selectedType = sharedPreferences.getString("specify_places_type_pref","point_of_interest");
           Toast.makeText(getActivity(),"You selected " + selectedType,Toast.LENGTH_LONG).show();

        }



    }

    private boolean reauthenticateAndDeleteAccount() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View passwordPrompt = layoutInflater.inflate(R.layout.password_reauth_prompt_dialog,null);
        AlertDialog.Builder passwordPromptBuilder = new AlertDialog.Builder(getActivity());
        passwordPromptBuilder.setView(passwordPrompt);
        final EditText passwordReauthEditText = passwordPrompt.findViewById(R.id.passwordPromptEditText);
        passwordPromptBuilder.setCancelable(false)
                .setTitle("Enter current password")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String passwordInput = passwordReauthEditText.getText().toString();
                        AuthCredential credential = EmailAuthProvider.getCredential(useremail,passwordInput);
                        mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mCurrentUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        accountDeletionStatus=true;
                                                        Toast.makeText(getActivity(),"User Account Deleted!",Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getActivity(),LoginActivity.class));

                                                    }
                                                    else{
                                                        accountDeletionStatus=false;
                                                        Toast.makeText(getActivity(),"Error deleting user account",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                                else {
                                    Toast.makeText(getActivity(),"Error authenticating user",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = passwordPromptBuilder.create();
        alertDialog.show();
        return accountDeletionStatus;

    }

    private boolean reauthenticateAndUpdateEmail(final Object newValue) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View passwordPrompt = layoutInflater.inflate(R.layout.password_reauth_prompt_dialog,null);
        AlertDialog.Builder passwordPromptBuilder = new AlertDialog.Builder(getActivity());
        passwordPromptBuilder.setView(passwordPrompt);
        final EditText passwordReauthEditText = passwordPrompt.findViewById(R.id.passwordPromptEditText);
        passwordPromptBuilder.setCancelable(false)
                .setTitle("Enter current password")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String passwordInput = passwordReauthEditText.getText().toString();
                        AuthCredential credential = EmailAuthProvider.getCredential(useremail,passwordInput);
                        mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mCurrentUser.updateEmail((String) newValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getActivity(),"Email updated successfully",Toast.LENGTH_LONG).show();
                                                emailChangeStatus=true;
                                            }
                                            else {
                                                Toast.makeText(getActivity(),"Couldn't update email",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getActivity(),"Error authenticating user",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = passwordPromptBuilder.create();
        alertDialog.show();

        return emailChangeStatus;

    }


}
