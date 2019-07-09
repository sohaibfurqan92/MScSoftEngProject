package uk.ac.le.cityTourPlanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AboutDialog.AboutDialogListener, FeedbackDialog.FeedbackDialogListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private OnSuccessListener mFeedbackSuccessListener;
    private OnFailureListener mFeedbackFailureListener;
    private ActionBarDrawerToggle mActionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);

        drawerLayout.addDrawerListener(mActionBarDrawerToggle);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /**
         * The {@link ViewPager} that will host the section contents.
         */
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mSectionsPagerAdapter);

        mFirebaseAuth=FirebaseAuth.getInstance();


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.setCheckedItem(R.id.nav_trips);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTedPermissions();  //check permissions
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView navUserNameTextView = (TextView) header.findViewById(R.id.NameNavHeaderTextView);
        navUserNameTextView.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
        TextView navUserEmailTextView = (TextView) header.findViewById(R.id.EmailNavHeaderTextView);
        navUserEmailTextView.setText(mFirebaseAuth.getCurrentUser().getEmail());


        mFeedbackSuccessListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,"Thank you for your feedback",Toast.LENGTH_LONG).show();
            }
        };

        mFeedbackFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"An error occurred. Please try again later.",Toast.LENGTH_LONG).show();
                Log.e("Feedback failure", e.getMessage() );
            }
        };

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }
        if(id == R.id.action_logout){
            signUserOut();
        }

        return super.onOptionsItemSelected(item);
    }


    private void signUserOut() {
        if(mUser!=null){
            try{
                mFirebaseAuth.signOut();
                Log.d("SignOut", "User Signed Out");
                startActivity(new Intent(this,LoginActivity.class));
            }
            catch (Error e){
                Log.d("Error",  e.getMessage());
            }
        }
    }

    private void StartTedPermissions() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                startActivity(new Intent(MainActivity.this, PlaceSearchActivity.class));
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                for (String item : deniedPermissions) {
                    TedPermission.with(MainActivity.this).setPermissions(item).check();
                }
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("This application needs to use certain features to work correctly. Please allow")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setRationaleConfirmText("OK")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE)
                .check();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.nav_trips){
            startActivity(new Intent(this,MainActivity.class));
        }
        if(id==R.id.nav_about){
            openAboutDialog();
        }
        if(id==R.id.nav_feedback){
            openFeedbackDialog();
        }
        if(id==R.id.nav_settings){

        }
        if(id==R.id.nav_share){
            shareApp();
        }

        return  true;
    }

    private void shareApp() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app - City Tour Planner!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void openAboutDialog() {
        AboutDialog dialog = new AboutDialog();
        dialog.show(getSupportFragmentManager(), "Nav bar About dialog");

    }

    private void openFeedbackDialog() {
        FeedbackDialog dialog = new FeedbackDialog();
        dialog.show(getSupportFragmentManager(), "Nav bar feedback dialog");

    }

    @Override
    public void passDataToActivity(String username, String useremail, String usercomments) {
        SaveFeedback(username,useremail,usercomments);
    }

    private void SaveFeedback(String username, String useremail, String usercomments) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        String appFeedbackPushKey = myRef.child("AppFeedback").push().getKey();

        //insert place details at appropriate location
        Map<String,Object> feedbackMap = new HashMap<>();
        feedbackMap.put("UserName",username);
        feedbackMap.put("UserEmail",useremail);
        feedbackMap.put("UserComments",usercomments);

        myRef.child("AppFeedback").child(appFeedbackPushKey).setValue(feedbackMap).addOnSuccessListener(mFeedbackSuccessListener).addOnFailureListener(mFeedbackFailureListener);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new ScheduledFragment();
                case 1:
                    return new PastFragment();
                case 2:
                    return new TrashFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}


