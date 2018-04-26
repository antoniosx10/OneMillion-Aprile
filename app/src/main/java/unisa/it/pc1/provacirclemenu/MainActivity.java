package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(getApplicationContext(), ListenerService.class);
        startService(serviceIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1000);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Class fragmentClass = HomeFragment.class;
            try {
                fragmentManager.beginTransaction().replace(R.id.switch_fragment, HomeFragment.class.newInstance()).commit();
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
                            selectDrawerItem(menuItem);
                            return true;
                        }
                    });
        }

        public void selectDrawerItem(MenuItem menuItem) {
            // Create a new fragment and specify the fragment to show based on nav item clicked
            Fragment fragment = null;
            Class fragmentClass;
            switch(menuItem.getItemId()) {
                case R.id.nav_Home:
                    fragmentClass = HomeFragment.class;
                    Toast.makeText(getApplicationContext(), "Home: ", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_profilo:

                default:
                    fragmentClass = HomeFragment.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.switch_fragment, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawerLayout.closeDrawers();
        }




    }


