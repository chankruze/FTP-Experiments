package in.geekofia.ftpfm.activities;

import android.Manifest;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.List;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.adapters.FileListAdapter;
import in.geekofia.ftpfm.models.Profile;
import in.geekofia.ftpfm.models.Item;
import in.geekofia.ftpfm.utils.ListFTPFiles;
import in.geekofia.ftpfm.utils.PermissionUtil;

import static in.geekofia.ftpfm.utils.CustomFunctions.showFileOperations;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class FilesActivity extends ListActivity {

    List<Item> directories = new ArrayList<Item>();
    List<Item> files = new ArrayList<Item>();
    FTPFile[] currentDir = new FTPFile[0];
    private FileListAdapter adapter;
    FTPFile[] ftpDirs = new FTPFile[0];
    String path = new String();
    private String host, username, password;
    private int port;

    // Views
    private View mLayout;
    private ListView mListView;
    private TextView mErrorText;
    private ImageView mImageView;
    private Button mBtnEditConnection;

    private String TAG = getClass().getSimpleName();

    private FTPClient ftpclient;
    private Profile mProfile;

    private static final int STORAGE_REQUEST_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        initViews();

        Bundle b = getIntent().getExtras();
        mProfile = (Profile) b.getSerializable("PROFILE");
        host = mProfile.getHost();
        port = mProfile.getPort();
        username = mProfile.getUser();
        password = mProfile.getPass();

        ftpclient = new FTPClient();
        ftpclient.setControlEncoding("UTF-8");

        // Connect to FTP server
        new Thread(new Runnable() {
            public void run() {
                boolean status = false;
                // host – your FTP address
                // username & password – for your secured login
                // 21 default gateway for FTP
                status = ftpConnect(ftpclient, host, username, password, port);
                if (status == true) {
                    Log.d(TAG, "Connection Success");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mErrorText.setText("Connection Success");
                            mImageView.setImageDrawable(getDrawable(R.drawable.ic_successful_connection));
                        }
                    });

                    ListFTPFiles listFTPFiles = new ListFTPFiles(ftpclient, "/", directories);
                    Thread thread = new Thread(listFTPFiles);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    directories = listFTPFiles.getNewDirectories();
                    directories.remove(0);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new FileListAdapter(getApplicationContext(), directories);
                            mListView.setAdapter(adapter);
                            mErrorText.setVisibility(View.GONE);
                            mImageView.setVisibility(View.GONE);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mErrorText.setText("Connection failed");
                            mErrorText.setVisibility(View.VISIBLE);
                            mImageView.setImageDrawable(getDrawable(R.drawable.ic_alert));
                            mImageView.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                            mBtnEditConnection.setVisibility(View.VISIBLE);
                            mBtnEditConnection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }
                    });

                    Log.d(TAG, "Connection failed");
                }
            }
        }).start();
    }

    private void initViews() {
        mLayout = findViewById(R.id.root_layout);
        mListView = findViewById(android.R.id.list);

        mErrorText = findViewById(R.id.error_text);
        mErrorText.setText("Connecting...");

        mImageView = findViewById(R.id.error_logo);
        mImageView.setImageDrawable(getDrawable(R.drawable.ic_hourglass));

        mBtnEditConnection = findViewById(R.id.btn_edit_details);
        mBtnEditConnection.setVisibility(View.GONE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Item item = (Item) adapter.getItem(position);
        int mItemType = item.getTypeItem();

        if (mItemType == Item.DIRECTORY || mItemType == Item.UP) {
            listFiles(item);
        } else {
            showFileOperations(this, this, v, item, mProfile);
        }
    }

    @Override
    public void onBackPressed() {
        if (!directories.isEmpty() && directories.get(0).getTypeItem() == Item.UP) {
            listFiles(directories.get(0));
        } else {
            finish();
        }
    }

    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permission_storage_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(FilesActivity.this, PERMISSIONS_STORAGE,
                                            STORAGE_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permission_storage_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(mLayout, R.string.permission_storage_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void listFiles(Item item) {
        ListFTPFiles listFTPFiles = new ListFTPFiles(ftpclient, item.getAbsolutePath(), directories);
        Thread thread = new Thread(listFTPFiles);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        directories = listFTPFiles.getNewDirectories();

        adapter.notifyDataSetChanged();
    }
}
