package in.geekofia.ftpfm.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import in.geekofia.ftpfm.models.Profile;
import in.geekofia.ftpfm.models.RemoteFile;
import in.geekofia.ftpfm.services.DownloadService;
import in.geekofia.ftpfm.services.UploadService;

import static in.geekofia.ftpfm.activities.FilesActivity.listFiles;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpDisconnect;
import static in.geekofia.ftpfm.utils.FTPClientFunctions.ftpConnect;

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
    public static void showFileOperations(final FilesActivity activity, View view, final RemoteFile mRemoteFile) {
        final Context context = activity.getContext();
        // Setup Popup Menu
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_file_operations, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.setGravity(Gravity.END);

        // setup click listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_info:
                        fileInfo(context, mRemoteFile);
                        return true;
                    case R.id.option_download:
                        fileDownload(activity, mRemoteFile);
                        return true;
                    case R.id.option_rename:
                        fileRename(activity, mRemoteFile);
                        return true;
                    case R.id.option_delete:
                        fileDelete(activity, mRemoteFile);
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
    private static void fileInfo(Context context, RemoteFile remoteFile) {
        String message = context.getResources().getString(R.string.alert_info_message, remoteFile.getPermission(), remoteFile.getUser(),
                remoteFile.getGroup(), remoteFile.getSize(), remoteFile.getUnit(), remoteFile.getDate(), remoteFile.getTime(), remoteFile.getAbsolutePath(),
                fetchString(context, remoteFile.getTypeId()));
        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(remoteFile.getName());
        newDialog.setIcon(remoteFile.getIconId());
        newDialog.setMessage(message);

        newDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    /*
     * DOWNLOAD FUNCTION
     *
     **/
    private static void fileDownload(final FilesActivity activity, final RemoteFile remoteFile) {
        final Context context = activity.getContext();
        final Profile profile = activity.getProfile();

        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(fetchString(context, R.string.confirm_download));
        newDialog.setMessage("Are you sure you want to download " + remoteFile.getName() + " ?");

        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Downloading " + remoteFile.getName(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("mProfile", profile);
                    intent.putExtra("mRemoteFilePath", remoteFile.getAbsolutePath());
                    intent.putExtra("RemoteFileName", remoteFile.getName());
                    intent.putExtra("mLocalFilePath", "");
                    intent.putExtra("mLocalFileName", "");
                    intent.putExtra("mFileSize", remoteFile.getSizeInBytes());
                    activity.startService(intent);
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

    /*
     * RENAME FUNCTION
     *
     **/
    public static void fileRename(final FilesActivity activity, final RemoteFile remoteFile) {
        final Context context = activity.getContext();
        final Profile profile = activity.getProfile();

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename_file, null);
        final TextInputEditText mEditTextNewName = view.findViewById(R.id.id_edit_rename_file);

        final AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setView(view);
        newDialog.setTitle("Rename " + remoteFile.getName());
        newDialog.setIcon(remoteFile.getIconId());

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

                            String currentFilePath = getCurrentPath(remoteFile).toString();
                            mFTPClient.rename(remoteFile.getAbsolutePath(), currentFilePath + Objects.requireNonNull(mEditTextNewName.getText()).toString());

                            ftpDisconnect(mFTPClient);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listFiles(profile, getCurrentPath(remoteFile).toString(), activity.getDirectories(), activity.getRemoteFilesAdapter());
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


    /*
     * DELETE FUNCTION
     *
     **/
    public static void fileDelete(final FilesActivity activity, final RemoteFile remoteFile) {
        final Context context = activity.getContext();
        final Profile profile = activity.getProfile();

        AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
        newDialog.setTitle(fetchString(context, R.string.confirm_delete));
        newDialog.setMessage("Are you sure you want to delete " + remoteFile.getName() + " ?");

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

                            mFTPClient.deleteFile(remoteFile.getAbsolutePath());

                            ftpDisconnect(mFTPClient);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listFiles(profile, getCurrentPath(remoteFile).toString(), activity.getDirectories(), activity.getRemoteFilesAdapter());
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

    // Custom function to get remoteFile directory
    private static StringBuilder getCurrentPath(RemoteFile remoteFile) {
        List<String> filters = new ArrayList<>();
        filters.add("");

        List<String> splitPath = new ArrayList<>(Arrays.asList(remoteFile.getAbsolutePath().split("/")));
        splitPath.removeAll(filters);

        StringBuilder currentFilePath = new StringBuilder();
        for (int i = 0; i < splitPath.size() - 1; i++) {
            currentFilePath.append(splitPath.get(i)).append("/");
            currentFilePath = new StringBuilder(currentFilePath.toString().trim());
        }
        return currentFilePath;
    }

    /*
     * UPLOAD FUNCTION
     *
     **/
    public static void fileUpload(final FilesActivity activity, final String uploadDir, final DocumentFile documentFile) {
        final Context context = activity.getContext();
        final Profile profile = activity.getProfile();
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

                    Intent uploadIntent = new Intent(context, UploadService.class);
                    uploadIntent.putExtra("mProfile", profile);
                    uploadIntent.putExtra("mDocumentFileUri", documentFile.getUri());
                    uploadIntent.putExtra("mDocumentFileName", documentFile.getName());
                    uploadIntent.putExtra("mRemoteDir", uploadDir);
                    activity.startService(uploadIntent);
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
}
