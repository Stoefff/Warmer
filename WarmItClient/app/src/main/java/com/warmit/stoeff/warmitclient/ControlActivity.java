package com.warmit.stoeff.warmitclient;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.warmit.stoeff.warmitclient.model.Heat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControlActivity extends AppCompatActivity {

    private TextView tvSliderValue;
    private Button btnWarmIt;
    private SeekBar seekBar;
    private ProgressDialog progressDialog;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initComponents();
        setComponentsListeners();

        progressDialog.setMessage("Sending...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        ip = getIntent().getStringExtra(IdActivity.IP);
        showToast("Yo I received the ip: " + ip);
    }

    private void initComponents() {
        tvSliderValue = (TextView) findViewById(R.id.tv_seekbar_value);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        btnWarmIt = (Button) findViewById(R.id.btn_warm_it);
        progressDialog = new ProgressDialog(this);
    }

    private void setComponentsListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSliderValue.setText(String.valueOf(progress));

                //send progress value
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnWarmIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip == null || ip.isEmpty()) {
                    showToast("Invalid IP");
                }else {
                    progressDialog.show();
                    int progress = seekBar.getProgress();
                    warmItWithValue(progress);
                }
            }
        });
    }

    private void warmItWithValue(int value) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Heat heat = new Heat(value);

        retrofit.create(ClientAPI.class)
                .changeHeat(heat)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            showToast("Calma, your home will be warmed");
                        }else {
                            showToast("Something went wrong, IDK maybe no such IP");
                        }

                        progressDialog.hide();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.hide();
                        showToast("Something went wrong, check your connectivity and try again");
                        t.printStackTrace();
                    }
                });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
