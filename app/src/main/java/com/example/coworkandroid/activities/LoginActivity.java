package com.example.coworkandroid.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coworkandroid.R;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etLogin, etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

    }

    public void onLoginClicked(View view){

        String email = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        // Show toast if email or password not filled
        if (email.equals("") || password.equals("")) {
            showToast("Login and Password are mandatory");
        } else {

            try {
                new LoginUser().execute(email, password).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoginUser extends AsyncTask<String, Void, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            String Id = strings[0];
            String Password = strings[1];

            JSONObject json = new JSONObject();
            try {
                json.put("identifiant", Id);
                json.put("password", Password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonString = json.toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);


            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apicowork.herokuapp.com")
                    .addPathSegment("user")
                    .addPathSegment("auth")
                    .build();


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .post(body)
                    .build();

            Response response;

            try {

                response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful()) {

                    String result;

                    if (response.body() != null) {
                        result = response.body().string();

                        if (result.equals("employee")) {

                            Intent i = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            showToast("Email or Password mismatched!");
                        }
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                showToast("Connexion problem");

            }
            return null;
        }
    }


    public void showToast(final String Text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,
                        Text, Toast.LENGTH_LONG).show();
            }
        });
    }

}
