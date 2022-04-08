package com.myapp.srstransport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.srstransport.fragments.DriverFragment;
import com.myapp.srstransport.fragments.HomeFragment;
import com.myapp.srstransport.fragments.PayOnlineFragment;
import com.myapp.srstransport.fragments.TrackingFragment;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private MeowBottomNavigation meowBottomNavigation;
    private String routeNo, mobileNo, userId;
    private DatabaseReference reference, dbRef;
    private FirebaseAuth fAuth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isConnected(MainActivity.this)){
            buildDialog(MainActivity.this).show();
        }

        reference = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        userId = getIntent().getStringExtra("userID");

        dbRef = reference.child("Login Details").child(userId);
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        DataSnapshot dataSnapshot = task.getResult();
                        mobileNo = String.valueOf(dataSnapshot.child("registeredMobileNo").getValue());
                        routeNo = String.valueOf(dataSnapshot.child("routeNo").getValue());

                    } else {
                        Toast.makeText(getApplicationContext(), "Session Expired!!! Please login again...", Toast.LENGTH_LONG).show();
                        fAuth.signOut();
                        Intent i=new Intent(getApplicationContext(),AuthenticationActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Session Expired!!! Please login again...", Toast.LENGTH_LONG).show();
                    fAuth.signOut();
                    Intent i=new Intent(getApplicationContext(),AuthenticationActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

//        routeNo = "Route No 2";
//        mobileNo="9996229815";

        meowBottomNavigation = findViewById(R.id.meow);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.driver));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.live_tracking));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.pay_online));

        meowBottomNavigation.show(1, true);
        meowBottomNavigation.setSelectedIconColor(R.drawable.ic_home);
        replace(new HomeFragment());
        meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                Bundle bundle;
                FragmentTransaction transaction;

                switch (model.getId()) {

                    case 1:
                        replace(new HomeFragment());
                        meowBottomNavigation.setSelectedIconColor(R.drawable.ic_home);
                        break;

                    case 2:
                        DriverFragment driverFragment = new DriverFragment();
                        transaction = getSupportFragmentManager().beginTransaction();

                        bundle = new Bundle();
                        bundle.putString("route", routeNo);
                        driverFragment.setArguments(bundle);

                        transaction.replace(R.id.frameLayout, driverFragment).commit();
                        meowBottomNavigation.setSelectedIconColor(R.drawable.driver);
                        break;

                    case 3:
//                        bundle=new Bundle();
//                        bundle.putString("route",routeNo);
//                        Fragment fragment=new TrackingFragment();
//                        fragment.setArguments(bundle);
//                        replace(new TrackingFragment());
                        TrackingFragment trackingFragment = new TrackingFragment();
                        transaction = getSupportFragmentManager().beginTransaction();

                        bundle = new Bundle();
                        bundle.putString("route", routeNo);
                        trackingFragment.setArguments(bundle);

                        transaction.replace(R.id.frameLayout, trackingFragment).commit();
                        meowBottomNavigation.setSelectedIconColor(R.drawable.live_tracking);
                        break;

                    case 4:
                        PayOnlineFragment payOnlineFragment = new PayOnlineFragment();
                        transaction = getSupportFragmentManager().beginTransaction();

                        bundle = new Bundle();
                        bundle.putString("route", routeNo);
                        bundle.putString("mobile", mobileNo);
                        payOnlineFragment.setArguments(bundle);

                        transaction.replace(R.id.frameLayout, payOnlineFragment).commit();
                        meowBottomNavigation.setSelectedIconColor(R.drawable.pay_online);
                        break;
                }

                return null;
            }
        });
    }

    public boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(Context context){
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Dear user you are not connected to Internet.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        return builder;
    }

    private void replace(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.signout:
                fAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logout Successfully!!!", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,AuthenticationActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Uri uri;
        Intent i;

        switch (item.getItemId()) {

            case R.id.navigation_myProfile:
                i = new Intent(this, ParentProfileActivity.class);
                i.putExtra("route",routeNo);
                i.putExtra("mobile",mobileNo);
                startActivity(i);
                break;

            case R.id.navigation_website:
                drawerLayout.closeDrawers();

                uri = Uri.parse("http://srinstitutes.com/");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

                break;
            case R.id.navigation_share:
                drawerLayout.closeDrawers();

                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(i.EXTRA_SUBJECT, "Share");
                String shareMessage = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                i.putExtra(i.EXTRA_TEXT, shareMessage);
                Intent chooser = Intent.createChooser(i, "Share the App using...");
                startActivity(chooser);
                break;
            case R.id.navigation_rateus:
                drawerLayout.closeDrawers();

                uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                i = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to open!!!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.navigation_developer:
                i = new Intent(MainActivity.this, DeveloperActivity.class);
                startActivity(i);
                break;

            case R.id.navigation_logout:

                fAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logout Successfully!!!", Toast.LENGTH_SHORT).show();
                i=new Intent(this,AuthenticationActivity.class);
                startActivity(i);
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}