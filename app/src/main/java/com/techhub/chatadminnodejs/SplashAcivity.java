package com.techhub.chatadminnodejs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.techhub.chatadminnodejs.ClassUse.CheckinternetToat;
import com.techhub.chatadminnodejs.Pref.Userinfo;
import com.techhub.chatadminnodejs.Pref.Usersession;

public class SplashAcivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    CircularProgressButton btnlogin;
    EditText edtmail,edtpass,edtuid;
    private    static final String TAG =SplashAcivity.class.getSimpleName();
    private Usersession session;
    private Userinfo userinfo;
    private boolean inProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_acivity);
        if(  CheckinternetToat.haveNetworkConnection(SplashAcivity.this)){
            btnlogin=(CircularProgressButton) findViewById(R.id.btnlogin);
            edtmail=(EditText)findViewById(R.id.edtemail);
            edtpass=(EditText)findViewById(R.id.edtpass);
            edtuid=(EditText)findViewById(R.id.edtuid);
            mAuth=FirebaseAuth.getInstance();

            session= new Usersession(this);
            userinfo = new Userinfo(this);
            if(session.isUserLoggedin()){
                startActivity(new Intent(this,TrangChuActivity.class));
                finish();
            }






            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(  CheckinternetToat.haveNetworkConnection(SplashAcivity.this)){

                    loginClick();
                    }else {
                        AlertDialog.Builder builder =new AlertDialog.Builder(SplashAcivity.this);

                        builder.setTitle("Note!");
                        builder.setMessage("No have Network Connection,Try again later!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                }
            });
            btnlogin.setIndeterminateProgressMode(true);
        }else {
            AlertDialog.Builder builder =new AlertDialog.Builder(SplashAcivity.this);

            builder.setTitle("Note!");
            builder.setMessage("No have Network Connection,Try again later!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        finish();
                }
            });
            builder.show();
        }








       // String usn="thiennhat27031996@gmail.com";//edtmail.getText().toString();
        //String pass="1312131213";//edtpass.getText().toString();
        //resger( usn, pass);




    }

    private void loginClick() {
        btnlogin.setProgress(0);
        if (TextUtils.isEmpty(edtmail.getText().toString())|| TextUtils.isEmpty(edtuid.getText().toString()) || TextUtils.isEmpty(edtpass.getText().toString())) {
            if (TextUtils.isEmpty(edtmail.getText().toString())) {
                edtmail.setError("please input!");
            }
            if (TextUtils.isEmpty(edtuid.getText().toString())){
                edtuid.setError("please input!");
            }
            if(TextUtils.isEmpty(edtpass.getText().toString())) {
                edtpass.setError("please input!");
            }
            btnlogin.setProgress(-1);
            return;
        } else {
            inProgress=true;
            edtmail.setEnabled(false);
            edtpass.setEnabled(false);
            edtuid.setEnabled(false);
            if(btnlogin.getProgress()==0)
            {
                btnlogin.setProgress(5);
            }
            else if(btnlogin.getProgress()==-1)
            {
                btnlogin.setProgress(0);
            }
            else if(btnlogin.getProgress()==100)
            {




            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resger(edtmail.getText().toString(),edtpass.getText().toString());




                }
            },2000);




        }
    }

    private void resger( String email, String pass) {

        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            edtmail.setError("");
                            edtpass.setError("");
                            btnlogin.setProgress(-1);
                            edtmail.setEnabled(true);
                            edtpass.setEnabled(true);
                            edtuid.setEnabled(true);
                            inProgress=false;


                        }
                        else {
                            edtmail.setEnabled(true);
                            edtpass.setEnabled(true);
                            edtuid.setEnabled(true);
                            inProgress=false;
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                           mUserDatabase= FirebaseDatabase.getInstance().getReference().child(currentFirebaseUser.getUid()).child("DeviceAndroid");
                            mUserDatabase.child( currentFirebaseUser.getUid()).child("device_token").setValue(deviceToken);
                            mUserDatabase.child( currentFirebaseUser.getUid()).child("user_id").setValue( currentFirebaseUser.getUid());

                            //CheckinternetToat.toastcheckinternet(SplashAcivity.this,currentFirebaseUser.getUid());

                            if(edtuid.getText().toString().equals(currentFirebaseUser.getUid())){
                                session.setLoggedin(true);
                                userinfo.setUserid( currentFirebaseUser.getUid());
                                userinfo.setUsertoken(deviceToken);
                                Intent intent = new Intent(SplashAcivity.this, TrangChuActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                btnlogin.setProgress(-1);
                                edtuid.setError("wrong UID!");
                                edtuid.setText("");

                            }

                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.


                        // ...
                    }
                });

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!inProgress)
            return super.dispatchTouchEvent(ev);
        return true;
    }
}

