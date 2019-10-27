package in.geekofia.ftpfm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "in.geekofia.ftpfm.EXTRA_TITLE";
    public static final String EXTRA_ID = "in.geekofia.ftpfm.EXTRA_ID";
    public static final String EXTRA_NAME = "in.geekofia.ftpfm.EXTRA_NAME";
    public static final String EXTRA_HOST = "in.geekofia.ftpfm.EXTRA_HOST";
    public static final String EXTRA_PORT = "in.geekofia.ftpfm.EXTRA_PORT";
    public static final String EXTRA_USER_NAME = "in.geekofia.ftpfm.EXTRA_USER_NAME";
    public static final String EXTRA_PASSWORD = "in.geekofia.ftpfm.EXTRA_PASSWORD";

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String CONNECTION_FRAGMENT = "CONNECTION_FRAGMENT";
    public static final String ADD_EDIT_CONNECTION_FRAGMENT = "ADD_EDIT_CONNECTION_FRAGMENT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT).commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_toolbar, menu);
//        return true;
//    }
}
