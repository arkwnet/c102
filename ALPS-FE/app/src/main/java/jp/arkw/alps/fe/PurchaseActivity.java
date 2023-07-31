package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        Intent intent = getIntent();
        int total = intent.getIntExtra("total", 0);
        TextView textView = findViewById(R.id.text_total);
        textView.setText("￥ " + total);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_cancel) {
            finish();
        }
    }
}