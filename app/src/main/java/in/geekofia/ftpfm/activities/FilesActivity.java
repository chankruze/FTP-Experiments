package in.geekofia.ftpfm.activities;

import androidx.appcompat.app.AlertDialog;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.adapters.FileListAdapter;
import in.geekofia.ftpfm.models.Item;
import in.geekofia.ftpfm.utils.FTPClientFunctions;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

public class FilesActivity extends ListActivity {

    List<Item> directories = new ArrayList<Item>();
    List<Item> files = new ArrayList<Item>();
    FTPFile[] currentDir = new FTPFile[0];
    private FileListAdapter adapter;
    private ListView listView;
    FTPFile[] ftpDirs = new FTPFile[0];
    String path = new String();
    private String host, username, password;
    private int port;

    private String TAG = getClass().getSimpleName();

    private FTPClient ftpclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        listView = findViewById(android.R.id.list);

        Bundle b = getIntent().getExtras();
        ftpclient = b.getParcelable("FTP_CLIENT");


        host = b.getString("host");
        port = b.getInt("port");
        username = b.getString("user");
        password = b.getString("pass");

        ftpclient = new FTPClient();

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

                    try {
                        ftpDirs = ftpclient.listDirectories();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    for (int i = 0; i < ftpDirs.length; i++) {
//                        rootPath = ftpDirs[0].getName();
//                        Log.d("CONNECT", "Directories in the ftp server are "
//                                + ftpDirs[i].getName());
//                    }

                    for (FTPFile f : ftpDirs) {
                        // is a directory
                        if (f.isDirectory()) {
                            Item item = new Item(Item.DIRECTORY, f.getName(), f.getSize(), f.getName());
                            directories.add(item);
                        }
                        // is a file
                        else {
                            Item item = new Item(Item.FILE, f.getName(), f.getSize(), f.getName());
                            files.add(item);
                        }
                    }

                    directories.addAll(files);

//                    if(!currentDir.getName().equals(rootPath)){
//                        directories.add(0, new Item(Item.UP, "", currentDir.getParent()));
//                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new FileListAdapter(getApplicationContext(), directories);
                            listView.setAdapter(adapter);
                        }
                    });

                } else {
                    Log.d(TAG, "Connection failed");
                }
            }
        }).start();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Item item = (Item) adapter.getItem(position);

        System.out.println("On List Item click");

        FTPClientFunctions.ListFTPFiles listFTPFiles = new FTPClientFunctions.ListFTPFiles(ftpclient, item.getAbsolutePath(), directories);
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


    public void chooseFile(final Item item) {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("Send file");
        newDialog.setMessage("Are you sure you want to choose this file ?");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                intent.putExtra("filePath", item.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        newDialog.show();
    }

//    @Override
//    public void onBackPressed() {
//        if(!currentDir.getName().equals(rootDirPath)){
//            fillDirectory(currentDir.getParentFile());
//            currentDir = currentDir.getParentFile();
//        }
//        else{
//            finish();
//        }
//    }
}
