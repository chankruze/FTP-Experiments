package in.geekofia.ftpfm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.net.ftp.FTPClient;

import in.geekofia.ftpfm.R;

// TODO: change this to EditConnection fragment

public class ConnectionActivity extends AppCompatActivity {

    // private Spinner mProtocolSpinner;
    private String host, username, password;
    private int port;

    // Views
    // TODO: Replace with a save button in title bar
    private Button mButtonConnect;
    private TextInputLayout mHostLayout, mPortLayout, mUsernameLayout, mPasswordLayout;
    private TextInputEditText mHost, mPort, mUsername, mPassword;
    private RadioGroup mRadioConnectionTypeGroup, mRadioRememberPasswordGroup;

    private FTPClient ftpclient;

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_layout);

        initViews();

        ftpclient = new FTPClient();

        mRadioConnectionTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId != R.id.connection_registered) {
                    mUsernameLayout.setVisibility(View.GONE);
                    mPasswordLayout.setVisibility(View.GONE);
                    mRadioRememberPasswordGroup.setVisibility(View.GONE);
                    mUsername.setText("");
                    mPassword.setText("");
                } else {
                    mUsernameLayout.setVisibility(View.VISIBLE);
                    mPasswordLayout.setVisibility(View.VISIBLE);
                    mRadioRememberPasswordGroup.setVisibility(View.VISIBLE);
                }
            }
        });

        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host = mHost.getText().toString();
                port = Integer.parseInt(mPort.getText().toString());
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                // TODO: Push these data to database

                Intent intent = new Intent(getApplicationContext(), FilesActivity.class);
                intent.putExtra("host", host);
                intent.putExtra("port", port);
                intent.putExtra("user", username);
                intent.putExtra("pass", password);
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        mButtonConnect = findViewById(R.id.btn_connect);

        mHostLayout = findViewById(R.id.host_layout);
        mPortLayout = findViewById(R.id.port_layout);
        mUsernameLayout = findViewById(R.id.username_layout);
        mPasswordLayout = findViewById(R.id.password_layout);
        mRadioConnectionTypeGroup = findViewById(R.id.radio_connection_type);
        mRadioRememberPasswordGroup = findViewById(R.id.radio_remember_passowrd);


        mHost = findViewById(R.id.id_edit_host);
        mPort = findViewById(R.id.id_edit_port);
        mUsername = findViewById(R.id.id_edit_username);
        mPassword = findViewById(R.id.id_edit_password);

        mPort.setInputType(InputType.TYPE_CLASS_NUMBER);
        mUsername.setInputType(InputType.TYPE_CLASS_TEXT);
    }
}
