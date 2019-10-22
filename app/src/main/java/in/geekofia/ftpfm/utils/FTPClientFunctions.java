package in.geekofia.ftpfm.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
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
