package com.warmit.stoeff.warmitclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.warmit.stoeff.warmitclient.model.Ip;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IdActivity extends AppCompatActivity {

    public static final String IP = "IP";

    private EditText etTermaId;
    private Button btnSearch;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);
        initComponents();
        setComponentListeners();
        progressDialog.setMessage("Searching...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void initComponents() {
        etTermaId = (EditText) findViewById(R.id.et_termal_head_id);
        btnSearch = (Button) findViewById(R.id.btn_search);
        progressDialog = new ProgressDialog(this);
    }

    private void setComponentListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String termalId = etTermaId.getText().toString();
                sendTermalId(termalId);
            }
        });
    }

    private void sendTermalId(String termalId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://224.0.0.1")
                .build();

        retrofit.create(ClientAPI.class)
                .fetchIP(termalId)
                .enqueue(new Callback<Ip>() {
                    @Override
                    public void onResponse(Call<Ip> call, Response<Ip> response) {
                        if (response.isSuccessful()) {
                            String ip = response.body().getIp();
                            showToast(ip);
                            //that's the id, but maybe its json so i have to parse id, one model could have made it easier but still....
                            startActivityWithIp(ip);
                        }else {
                            showToast("Something went wrong, IDK maybe no such termal id");
                        }

                        progressDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<Ip> call, Throwable t) {
                        progressDialog.hide();
                        showToast("Something went wrong, check your connectivity and try again");
                        t.printStackTrace();
                    }
                });
    }

    private void startActivityWithIp(String ip) {
        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra(IP, ip);
        startActivity(intent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
