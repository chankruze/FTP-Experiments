package in.geekofia.ftpfm.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.activities.FilesActivity;
import in.geekofia.ftpfm.adapters.FileListAdapter;
import in.geekofia.ftpfm.models.Profile;
import in.geekofia.ftpfm.models.Item;

import static in.geekofia.ftpfm.activities.FilesActivity.listFiles;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpDisconnect;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpFileDownload;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpFileUpload;

public class CustomFunctions {

    // Custom getDRawable function
    public static Drawable fetchDrawable(Context context, int resDrawable) {
        return ContextCompat.getDrawable(context, resDrawable);
    }

    // Custom fetchString function
    public static String fetchString(Context context, int resString) {
        return context.getResources().getString(resString);
    }

    // Toggle relative layout specific child properties
    public static void toggleProp(View view, int prop, boolean value) {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if (value) {
            layoutParams.addRule(prop);
        } else {
            layoutParams.removeRule(prop);
        }

        view.setLayoutParams(layoutParams);
    }

    // Show file operations
    public static void showFileOperations(final FilesActivity activity, final Context context, View view, final Item mItem, final Profile profile, final List<Item> directories, final FileListAdapter fileListAdapter) {
        // Setup Popup Menu
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_frag_file_operations, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.setGravity(Gravity.END);

        // setup click listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_info:
                        fileInfo(context, mItem);
                        return true;
                    case R.id.option_download:
                        fileDownload(activity, context, profile, mItem);
                        return true;
                    case R.id.option_rename:
                        fileRename(activity, context, profile, mItem, directories, fileListAdapter);
                        return true;
                    case R.id.option_delete:
                        fileDelete(activity, context, profile, mItem, directories, fileListAdapter);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        });

        // show popup
        optionsMenu.show();
    }

    // FTP File Info
    private static void fileInfo(Context context, Item item) {
        String message = context.getResources().getString(R.string.alert_info_message, item.getPermission(), item.getUser(),
                item.getGroup(), item.getSize(), item.getUnit(), item.getDate(), item.getTime(), item.getAbsolutePath(),
                fetchString(context, item.getTypeId()));
        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(item.getName());
        newDialog.setIcon(item.getIconId());
        newDialog.setMessage(message);

        newDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    // FTP File Download
    private static void fileDownload(final FilesActivity activity, final Context context, final Profile profile, final Item item) {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(fetchString(context, R.string.confirm_download));
        newDialog.setMessage("Are you sure you want to download " + item.getName() + " ?");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Downloading " + item.getName(), Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FTPClient mFTPClient = new FTPClient();
                                mFTPClient.setControlEncoding("UTF-8");
                                ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());
                                ftpFileDownload(mFTPClient, context, item.getAbsolutePath(), item.getName(), null, null, item.getSizeInBytes());
                                ftpDisconnect(mFTPClient);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    activity.requestStoragePermission();
                }
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

    // FTP File Rename
    public static void fileRename(final Activity activity, Context context, final Profile profile, final Item item, final List<Item> directories, final FileListAdapter fileListAdapter) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename_file, null);
        final TextInputEditText mEditTextNewName = view.findViewById(R.id.id_edit_rename_file);

        final AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setView(view);
        newDialog.setTitle("Rename " + item.getName());
        newDialog.setIcon(item.getIconId());

        newDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        newDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FTPClient mFTPClient = new FTPClient();
                            mFTPClient.setControlEncoding("UTF-8");
                            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());

                            String currentFilePath = getCurrentPath(item).toString();
                            mFTPClient.rename(item.getAbsolutePath(), currentFilePath + Objects.requireNonNull(mEditTextNewName.getText()).toString());

                            ftpDisconnect(mFTPClient);

                            FilesActivity filesActivity = (FilesActivity) activity;
                            final String path = currentFilePath.toString();
                            filesActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listFiles(profile, path, directories, fileListAdapter);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        newDialog.show();
    }

    public static void fileDelete(final Activity activity, final Context context, final Profile profile, final Item item, final List<Item> directories, final FileListAdapter fileListAdapter) {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(fetchString(context, R.string.confirm_delete));
        newDialog.setMessage("Are you sure you want to delete " + item.getName() + " ?");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FTPClient mFTPClient = new FTPClient();
                            mFTPClient.setControlEncoding("UTF-8");
                            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());

                            mFTPClient.deleteFile(item.getAbsolutePath());

                            ftpDisconnect(mFTPClient);

                            FilesActivity filesActivity = (FilesActivity) activity;
                            final String path = getCurrentPath(item).toString();
                            filesActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listFiles(profile, path, directories, fileListAdapter);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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

    // Custom function to get item directory
    private static StringBuilder getCurrentPath(Item item){
        List<String> filters = new ArrayList<>();
        filters.add("");

        List<String> splitPath = new ArrayList<>(Arrays.asList(item.getAbsolutePath().split("/")));
        splitPath.removeAll(filters);

        StringBuilder currentFilePath = new StringBuilder();
        for (int i = 0; i < splitPath.size() - 1; i++) {
            currentFilePath.append(splitPath.get(i)).append("/");
            currentFilePath = new StringBuilder(currentFilePath.toString().trim());
        }
        return currentFilePath;
    }

    public static void fileUpload(final FilesActivity activity, final Context context, final Profile profile, final String uploadDir, final DocumentFile documentFile){
        final String fileName = documentFile.getName();

        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(fetchString(context, R.string.confirm_upload));
        newDialog.setMessage(fileName + " will be uploaded to (/" + uploadDir + ")");

        newDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Uploading " + fileName, Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FTPClient mFTPClient = new FTPClient();
                            mFTPClient.setControlEncoding("UTF-8");
                            ftpConnect(mFTPClient, profile.getHost(), profile.getUser(), profile.getPass(), profile.getPort());
                            try {
                                ftpFileUpload(context, mFTPClient, documentFile, uploadDir);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ftpDisconnect(mFTPClient);
                        }
                    }).start();
                } else {
                    activity.requestStoragePermission();
                }
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

//    public static void FTPclose(FTPClient ftp) throws IOException{
//        try {
//            // checks if connected
//            if (ftp.isConnected()) {
//                // close FTP connection
//                ftp.logout();
//            }
//        } finally {
//            if (ftp.isConnected()) {
//                try {
//                    ftp.disconnect();
//                } catch (IOException e) {
//                    // ignore
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
