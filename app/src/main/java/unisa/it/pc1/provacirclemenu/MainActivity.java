package unisa.it.pc1.provacirclemenu;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.User;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth mAuth;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





                serviceIntent = new Intent(getApplicationContext(), ListenerService.class);
        startService(serviceIntent);


        mAuth = FirebaseAuth.getInstance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1000);
            }

            FragmentManager fm = getSupportFragmentManager();
            try {


                fm.beginTransaction().replace(R.id.switch_fragment, HomeFragment.class.newInstance()).commit();



            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            mDrawerLayout = findViewById(R.id.drawer_layout);



            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            //actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);



            NavigationView navigationView = findViewById(R.id.nav_view);
            setupDrawerContent(navigationView);


            mDrawerLayout.addDrawerListener(
                    new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            // Respond when the drawer's position changes
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                            // Respond when the drawer is opened
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            // Respond when the drawer is closed
                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            // Respond when the drawer motion state changes
                        }
                    }
            );




        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void setupDrawerContent(NavigationView navigationView) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            try {
                                selectDrawerItem(menuItem);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
        }

        public void selectDrawerItem(MenuItem menuItem) throws IllegalAccessException, InstantiationException {
            // Create a new fragment and specify the fragment to show based on nav item clicked
            Fragment fragment = null;
            Class fragmentClass;
            switch(menuItem.getItemId()) {
                case R.id.nav_Home:
                    fragmentClass = HomeFragment.class;
                    break;
                case R.id.nav_esci:
                    fragmentClass = null;
                    break;

                default:
                    fragmentClass = HomeFragment.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //ha cliccato su esci
            if (fragment == null){
                signOut();
            }

            // Insert the fragment by replacing any existing fragment
           FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.switch_fragment, (Fragment) fragmentClass.newInstance()).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawerLayout.closeDrawers();
        }



    private void signOut() {
        mAuth.signOut();
        stopService(serviceIntent);
        Intent i = new Intent(getApplicationContext(),Autenticazione.class);
        startActivity(i);
    }


}


