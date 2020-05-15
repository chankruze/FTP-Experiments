package in.geekofia.ftpfm.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "in.geekofia.ftpfm.EXTRA_TITLE";
    public static final String EXTRA_PROFILE = "in.geekofia.ftpfm.EXTRA_PROFILE";
    public static final String EXTRA_OPERATION_CODE = "in.geekofia.ftpfm.fragments.EXTRA_OPERATION";
    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    public static final String CONNECTION_FRAGMENT = "CONNECTION_FRAGMENT";
    public static final String ADD_EDIT_CONNECTION_FRAGMENT = "ADD_EDIT_CONNECTION_FRAGMENT";
    public static final int OPERATION_CODE_INSERT = 1, OPERATION_CODE_UPDATE = 2;

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


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage("Do you really want to close the app ?")
                .setPositiveButton("Yeh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Nope", null)
                .show();
    }
}
