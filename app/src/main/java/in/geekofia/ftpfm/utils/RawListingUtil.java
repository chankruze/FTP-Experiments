package in.geekofia.ftpfm.utils;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawListingUtil {
    // Splits " " & save all elements to string array
    private String[] rawListing;

    // List generated from string array to perform filter & remove operation
    private List<String> splitedRawList;

    // Variables to save item props
    private String permission, user, group, date, time;
    private long size;

    // Filtering List
    private List<String> filters = new ArrayList<>();

    public RawListingUtil(FTPFile ftpFile){
        this.rawListing = ftpFile.getRawListing().split(" ");
        splitedRawList = new ArrayList<>(Arrays.asList(rawListing));

        filters.add("");
        splitedRawList.removeAll(filters);

        this.permission = splitedRawList.get(0).substring(1);
        this.user = splitedRawList.get(2);
        this.group = splitedRawList.get(3);
        this.size = Long.parseLong(splitedRawList.get(4));
        this.date = splitedRawList.get(6) + " " + splitedRawList.get(5);
        this.time = splitedRawList.get(7);
    }

    public String getPermission() {
        return permission;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public long getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
