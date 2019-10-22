package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

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

    public static boolean ftpListDirs(FTPClient mFTPClient) {
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

    public static void ftpFileDownload(FTPClient mFTPClient, String mRemoteFilePath, String mRemoteFileName, String localFilePath, String localFileName) throws IOException {
        String remoteFile = mRemoteFilePath;
        String mLocalFilePath, mLocalFileName;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success;

        if (localFilePath == "" || localFilePath == null){
            String FTP_FOLDER = "FTP";
            File FTP_folder = new File(Environment.getExternalStorageDirectory(), FTP_FOLDER);
            if (!FTP_folder.exists()){
                FTP_folder.mkdirs();
            }
            mLocalFilePath = FTP_folder.toString();
            System.out.println("## Local File Path" + mLocalFilePath);
        } else {
            mLocalFilePath = localFilePath;
        }

        if (localFileName == "" || localFileName == null){
            mLocalFileName = mRemoteFileName;
        } else {
            mLocalFileName = localFileName;
        }

        File downloadFile = new File(mLocalFilePath + "/" + mLocalFileName);

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("## Exception [outputStream] ");
        }

        try {
            inputStream = mFTPClient.retrieveFileStream(remoteFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("## Exception [inputStream] ");
        }
        byte[] bytesArray = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
            try {
                if (outputStream != null) {
                    outputStream.write(bytesArray, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("## Exception [outputStream.write] ");
            }
        }

        try {
            success = mFTPClient.completePendingCommand();

            if (success) {
                System.out.println(mRemoteFileName + " has been downloaded successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("## Exception [completePendingCommand()] ");
        }

        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("## Exception [outputStream.close()] ");
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("## Exception [inputStream.close()] ");
        }
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
}
