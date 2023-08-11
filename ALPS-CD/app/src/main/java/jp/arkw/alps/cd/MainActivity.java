package jp.arkw.alps.cd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GetWebApiActivity";
    private Timer timer;
    private TimerTask timerTask;
    private Task task;
    private boolean running = false;
    private String BACKEND_URL;
    private LinearLayout linearLayout;
    private TextView textUpperLeft;
    private TextView textUpperRight;
    private TextView textLowerLeft;
    private TextView textLowerRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        linearLayout = findViewById(R.id.linear_layout);
        textUpperLeft = findViewById(R.id.text_upper_left);
        textUpperRight = findViewById(R.id.text_upper_right);
        textLowerLeft = findViewById(R.id.text_lower_left);
        textLowerRight = findViewById(R.id.text_lower_right);
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            try {
                inputStream = this.getAssets().open("BACKEND_URL.env");
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                BACKEND_URL = bufferedReader.readLine();
            } finally {
                if (inputStream != null) inputStream.close();
                if (bufferedReader != null) bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (running == false) {
                    running = true;
                    task = new Task();
                    task.execute(BACKEND_URL);
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private class Task extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            String str_recv = "";
            BufferedReader bufferedReader = null;
            try {
                TrustManager[] tm = {new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                }};
                SSLContext sslcontext = SSLContext.getInstance("SSL");
                sslcontext.init(null, tm, null);
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                URL url = new URL(urls[0]);
                HttpsURLConnection url_conn = (HttpsURLConnection)url.openConnection();
                url_conn.setReadTimeout(6000);
                url_conn.setRequestMethod("GET");
                url_conn.setSSLSocketFactory(sslcontext.getSocketFactory());
                Object content = url_conn.getContent();
                if (content instanceof InputStream) {
                    bufferedReader = new BufferedReader(new InputStreamReader((InputStream) content));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        str_recv += (line + "\n");
                    }
                } else {
                    str_recv = content.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return (str_recv);
        }

        @Override
        protected void onPostExecute(String str_recv) {
            try {
                JSONObject json = new JSONObject(str_recv);
                if (json.getString("upper_left").equals("") && json.getString("upper_right").equals("") && json.getString("lower_left").equals("") && json.getString("lower_right").equals("")) {
                    linearLayout.setVisibility(View.INVISIBLE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    textUpperLeft.setText(json.getString("upper_left"));
                    textUpperRight.setText(json.getString("upper_right"));
                    textLowerLeft.setText(json.getString("lower_left"));
                    textLowerRight.setText(json.getString("lower_right"));
                }
                running = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
