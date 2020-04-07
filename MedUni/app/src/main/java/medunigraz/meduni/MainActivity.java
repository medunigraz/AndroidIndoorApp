package medunigraz.meduni;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager manager;
    boolean IsMainFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, OpenerFragment.newInstance()).commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!IsMainFragment) {
            manager.beginTransaction().replace(R.id.content, OpenerFragment.newInstance()).commit();
            IsMainFragment = true;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        IsMainFragment = false;
        if (id == R.id.nav_Map) {
            manager.beginTransaction().replace(R.id.content, MapFragment.newInstance()).commit();
            getSupportActionBar().setTitle("Map");
        } else if (id == R.id.nav_News) {
            manager.beginTransaction().replace(R.id.content, NewsList.newInstance()).commit();
            getSupportActionBar().setTitle("News");
        } else if (id == R.id.nav_Events) {
            manager.beginTransaction().replace(R.id.content, EventList.newInstance()).commit();
            getSupportActionBar().setTitle("Events");
        } else if (id == R.id.nav_Impressum) {
            manager.beginTransaction().replace(R.id.content, Impressum.newInstance()).commit();
            getSupportActionBar().setTitle("Impressum");
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
