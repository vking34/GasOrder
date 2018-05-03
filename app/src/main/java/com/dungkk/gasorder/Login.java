package com.dungkk.gasorder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText user;
    EditText pass;
    Button login, signup;
    TextView guest;
    private static final int REQUEST_CODE = 0x11;
    private String json = Environment.getExternalStorageDirectory() + File.separator + "Gas";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginstart);
        requestPermision();
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.bt_login);
        guest = (TextView) findViewById(R.id.guest);
        signup = (Button) findViewById(R.id.bt_signup);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        guest.setOnClickListener(this);


    }

    private void  wirteJson(String fileName, String body){
        File file = new File(json);
        if (!file.exists()){
            file.mkdirs();
        }
        File f = new File(file, fileName);
        try {
            FileWriter writer = new FileWriter(f, true);
            writer.write(body);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPermision(){
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                File file = new File(json);
                if (!file.exists()){
                    file.mkdirs();
                }
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                String username = user.getText().toString();
                String password = pass.getText().toString();

                String s = "{\"login\":[{\"username\":\""+username+"\", \"password\":\""+password+"\"}]}";
                wirteJson("login.json", s);

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_signup:
                Intent intent0 = new Intent(this, SignUp.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.guest:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
    }
}
