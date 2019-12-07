package in.geekofia.ftpfm.models;

import org.apache.commons.net.ftp.FTPFile;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.utils.RawListingUtil;

public class RemoteFile {

    public static final int DIRECTORY = 1;
    public static final int FILE = 2;
    public static final int UP = 3;

    private int typeItem, numItems, iconId, typeId;
    private String permission, name, user, group, date, time, absolutePath, unit;
    private double size;
    private long sizeInBytes;

    // Constructors //

    // for directory
    public RemoteFile(int typeItem, String name, int numItems, String absolutePath) {
        this.iconId = R.drawable.ic_folder;
        this.typeItem = typeItem;
        this.name = name;
        this.numItems = numItems;
        this.absolutePath = absolutePath;
    }

    // for file
    public RemoteFile(int typeItem, FTPFile file, int iconId, String absolutePath, int typeId) {
        RawListingUtil rawListingUtil = new RawListingUtil(file);
        this.typeItem = typeItem;
        this.iconId = iconId;
        this.permission = rawListingUtil.getPermission();
        this.name = file.getName();
        this.user = rawListingUtil.getUser();
        this.group = rawListingUtil.getGroup();
        this.sizeInBytes = file.getSize();
        this.size = rawListingUtil.getSize();
        this.unit = rawListingUtil.getUnit();
        this.date = rawListingUtil.getDate();
        this.time = rawListingUtil.getTime();
        this.absolutePath = absolutePath;
        this.typeId = typeId;
    }

    // for UP link
    public RemoteFile(int typeItem, String display, String parentPath) {
        this.iconId = R.drawable.ic_back;
        this.typeItem = typeItem;
        this.name = display;
        this.absolutePath = parentPath;
    }

    // Getters //
    public int getIconId() {
        return iconId;
    }

    public String getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public double getSize() {
        return size;
    }

    public String getUnit () { return unit; }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public int getNumItems() {
        return numItems;
    }

    public int getTypeItem() {
        return typeItem;
    }

    public int getTypeId() {
        return typeId;
    }

    // setters //

    public void setTypeItem(int typeItem) {
        this.typeItem = typeItem;
    }
}
