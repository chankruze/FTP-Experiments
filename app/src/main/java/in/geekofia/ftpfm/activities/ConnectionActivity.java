package in.geekofia.ftpfm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.net.ftp.FTPClient;

import in.geekofia.ftpfm.R;

import static in.geekofia.ftpfm.utils.FTPClientFunctions.*;

public class ConnectionActivity extends AppCompatActivity {

//    private Spinner mProtocalSpinner;
    private Button mButtonConnect;
    private TextInputLayout mHostLayout, mPortLayout, mUsernameLayout, mPasswordLayout;
    private TextInputEditText mHost, mPort, mUsername, mPassword;
    private String host, username, password;
    private int port;

    private FTPClient ftpclient;

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_layout);

        initViews();

        ftpclient = new FTPClient();

        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host = mHost.getText().toString();
                port = Integer.parseInt(mPort.getText().toString());
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                Intent intent = new Intent(getApplicationContext(), FilesActivity.class);
                intent.putExtra("host", host);
                intent.putExtra("port", port);
                intent.putExtra("user", username);
                intent.putExtra("pass", password);
                startActivity(intent);

                // Connect to FTP server
//                new Thread(new Runnable() {
//                    public void run() {
//                        boolean status = false;
//                        // host – your FTP address
//                        // username & password – for your secured login
//                        // 21 default gateway for FTP
//                        status = ftpConnect(ftpclient, host, username, password, port);
//                        if (status == true) {
//                            Log.d(TAG, "Connection Success");
//                        } else {
//                            Log.d(TAG, "Connection failed");
//                        }
//                    }
//                }).start();
            }
        });

    }

    private void initViews() {
        mButtonConnect = findViewById(R.id.btn_connect);

        mHostLayout = findViewById(R.id.host_layout);
        mPortLayout = findViewById(R.id.port_layout);
        mUsernameLayout = findViewById(R.id.username_layout);
        mPasswordLayout = findViewById(R.id.password_layout);


        mHost = findViewById(R.id.id_host);
        mPort = findViewById(R.id.id_port);
        mUsername = findViewById(R.id.id_username);
        mPassword = findViewById(R.id.id_password);

        mHost.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        mUsername.setInputType(InputType.TYPE_CLASS_TEXT);
        mPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }
}
