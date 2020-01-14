package in.ac.vnrvjiet.easypay;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class LaunchActivity extends AppCompatActivity {

    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), homeFragment, R.id.fragment_container);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = homeFragment;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = homeFragment;
                            break;
                        case R.id.nav_history:
                            selectedFragment = historyFragment;
                            break;
                        case R.id.nav_profile:
                            selectedFragment = profileFragment;
                            break;
                    }

                    ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), selectedFragment, R.id.fragment_container);

                    return true;
                }
            };
}

