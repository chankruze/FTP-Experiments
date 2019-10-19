package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.activities.ConnectionActivity;
import in.geekofia.ftpfm.activities.FileListingActivity;
import in.geekofia.ftpfm.activities.FilesActivity;
import in.geekofia.ftpfm.adapters.FileListAdapter;
import in.geekofia.ftpfm.models.Item;


public class FTPClientFunctions {

    public static boolean ftpConnect(FTPClient mFTPClient, String host, String username, String password, int port) {
        try {
            // connecting to the host
            mFTPClient.connect(host, port);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);
                /*
                 * Set File Transfer Mode
                 * To avoid corruption issue you must specified a correct
                 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                 * transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP CONNECT", "Error: could not connect to host " + host);
        }
        return false;
    }

    public static boolean ftpListDirs(FTPClient mFTPClient){
        FTPFile[] ftpDirs = new FTPFile[0];
        String rootPath = new String();

        try {
            ftpDirs = mFTPClient.listDirectories();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ftpDirs.length; i++) {
            rootPath = ftpDirs[0].getName();
            Log.d("CONNECT", "Directories in the ftp server are "
                    + ftpDirs[i].getName());
        }

//        FTPFile[] ftpdirs2 = new FTPFile[0];
//        try {
//            ftpdirs2 = mFTPClient.listFiles(toppath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < ftpdirs2.length; i++) {
//            Log.d("CONNECT",
//                    "File i need is  " + ftpdirs2[i].getName());
//        }

        //Retrieve all files in this directory
//        File[] dirs = ftpDirs.listFiles();


        return false;
    }

    public static boolean ftpDisconnect(FTPClient mFTPClient) {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP DISCONNECT", "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    public static boolean ftpUpload(FTPClient mFTPClient, String srcFilePath, String desFileName, String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            // change working directory to the destination directory
            // if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            // }
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FTP FILE UPLOAD", "upload failed: " + e);
        }
        return status;
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    public static class ListFTPFiles implements Runnable {
        private List<Item> newDirectories;
        private FTPClient mFTPClient;
        private String mPath;

        public ListFTPFiles(FTPClient client, String path, List<Item> directories){
            this.mFTPClient = client;
            this.mPath = path;
            this.newDirectories = directories;
            newDirectories.clear();
        }

        @Override
        public void run() {
            try {
                List<Item> files = new ArrayList<Item>();
                FTPFile[] mFiles = mFTPClient.listFiles(mPath);

                for (FTPFile mFile : mFiles) {
                    Item item = new Item(Item.FILE, mFile.getName(), mFile.getSize(), mPath + "/" + mFile.getName());
                    System.out.println("#######################");
                    System.out.println("## Name " + mFile.getName());
                    System.out.println("## Size " + mFile.getSize());
                    System.out.println("## RawListing " + mFile.getRawListing());
                    System.out.println("## Timestamp " + mFile.getTimestamp());
                    System.out.println("## User " + mFile.getUser());
                    System.out.println("## Group " + mFile.getGroup());
                    System.out.println("## Type " + mFile.getType());
                    System.out.println("#######################");
                    System.out.println("## File Name : " + item.getName());
                    System.out.println("## File Size : " + item.getSize());
                    System.out.println("## File Abs Path : " + item.getAbsolutePath());
                    System.out.println("#######################");
                    files.add(item);
                }

                newDirectories.addAll(files);

                System.out.println("## path " + mPath);

                String[] splitedPath = mPath.split("/", 0);
                String parentPath = "";

                int depth = splitedPath.length;
                int j = 0;

                while (j < depth - 1){
                    parentPath += splitedPath[j] + "/";
                    j++;
                }

                System.out.println("## Parent Path : " + parentPath);

                newDirectories.add(0, new Item(Item.UP, "", parentPath));
//                List<Item> directories = new ArrayList<>(files);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public List<Item> getNewDirectories() {
            return newDirectories;
        }
    }
}
