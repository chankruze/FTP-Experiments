package in.geekofia.ftpfm.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profiles_table")
public class Profile {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private String name;

    private String host;

    private int port;

    private String user;

    private String pass;

    public Profile(String name, String host, int port, String user, String pass) {
        if (!name.isEmpty()){
            this.name = name;
        } else {
            this.name = "New Connection " + Math.random() * 500;
        }

        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }
}
