package in.geekofia.ftpfm.models;

public class FTPConf {
    String host, user, pass;
    int port;

    public FTPConf() {
    }

    public FTPConf(String host, int port, String user, String pass) {
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
}
