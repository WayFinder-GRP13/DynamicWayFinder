package com.group13.dynamicwayfinder.Activities.Authentication;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group13.dynamicwayfinder.Activities.Map.MapActivity;
import com.group13.dynamicwayfinder.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginbutton, signupbutton;
    private EditText emailText, passwordtxt;
    private ProgressBar progressBar, progressForgotPword;
    private TextView forgotPassword, closeforgotPword;
    private Dialog forgotp;
    private FirebaseAuth.AuthStateListener AuthListener;

    Button newPasswordEmail;
    EditText emailForPassword;

    DatabaseReference rootref, userref;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();
        userref = rootref.child("users");

        loginbutton = findViewById(R.id.loginbutton);
        signupbutton = findViewById(R.id.signupbutton);
        emailText = findViewById(R.id.user);
        passwordtxt = findViewById(R.id.password);
        progressBar = findViewById(R.id.loginprogress);
        forgotPassword = findViewById(R.id.forgotpasswordbutton);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        requestPermissions();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent goHome = new Intent(MainActivity.this, MapActivity.class);
            startActivity(goHome);
            finish();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (AuthListener != null) {
            mAuth.removeAuthStateListener(AuthListener);
        }
    }

    public void registerUser(View v){
        String user = emailText.getText().toString().trim();
        String password = passwordtxt.getText().toString().trim();

        if(user.isEmpty()){
            emailText.setError("Enter Email");
            emailText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            emailText.setError("Enter Valid User Name");
            emailText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordtxt.setError("Enter Password");
            passwordtxt.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordtxt.setError("Minimum Password must be 6 Characters");
            passwordtxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {



                            Toast.makeText(MainActivity.this, "User Registration Successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent goHome = new Intent(MainActivity.this, MapActivity.class);
                            MainActivity.this.startActivity(goHome);
                            MainActivity.this.finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public void loginUser(View v){

        String user = emailText.getText().toString().trim();
        String password = passwordtxt.getText().toString().trim();

        if(user.isEmpty()){
            emailText.setError("Enter Email");
            emailText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            emailText.setError("Enter Valid Username");
            emailText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordtxt.setError("Enter Password");
            passwordtxt.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordtxt.setError("Minimum Password length must be 6");
            passwordtxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent goHome = new Intent(MainActivity.this, MapActivity.class);
                            MainActivity.this.startActivity(goHome);
                            MainActivity.this.finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    public void forgotuserpword(View v){
        forgotp = new Dialog(this);
        forgotp.setContentView(R.layout.fpassword);
        forgotp.show();
        progressForgotPword = forgotp.findViewById(R.id.changepwordprogress);
        closeforgotPword = forgotp.findViewById(R.id.closepwordmenu);

        newPasswordEmail = forgotp.findViewById(R.id.newpword);
        emailForPassword = forgotp.findViewById(R.id.forgotpwordemail);
        newPasswordEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailNewPassword = emailForPassword.getText().toString().trim();
                if(emailNewPassword.isEmpty()){
                    emailForPassword.setError("Enter Your Email");
                    emailForPassword.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(emailNewPassword).matches()){
                    emailForPassword.setError("Enter A Valid Email");
                    emailForPassword.requestFocus();
                    return;
                }

                progressForgotPword.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(emailNewPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressForgotPword.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    forgotp.dismiss();
                                    Toast.makeText(MainActivity.this, "Reset Email Sent, Please check your Email" ,Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Reset Email Failed, Try Again" ,Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
        closeforgotPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotp.dismiss();
            }
        });
    }
    public void requestPermissions(){
        android.location.LocationManager lm = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        int ALL_PERMISSIONS_RESULT = 101;

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                }
            }
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}

