package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener {
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        findViewById(R.id.button_payment_money).setOnClickListener(this);
        findViewById(R.id.button_payment_credit).setOnClickListener(this);
        findViewById(R.id.button_payment_quicpay).setOnClickListener(this);
        findViewById(R.id.button_payment_id).setOnClickListener(this);
        findViewById(R.id.button_payment_ic).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        Intent intent = getIntent();
        total = intent.getIntExtra("total", 0);
        TextView textView = findViewById(R.id.text_total);
        textView.setText("￥ " + total);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(PurchaseActivity.this, MainActivity.class);
        if (v.getId() == R.id.button_payment_money) {
            intent.putExtra("payment", getString(R.string.payment_money));
            intent.putExtra("cash", 0);
            intent.putExtra("change", 0);
            setResult(RESULT_OK, intent);
        } else if (v.getId() == R.id.button_payment_credit) {
            intent.putExtra("payment", getString(R.string.payment_credit));
            intent.putExtra("cash", total);
            intent.putExtra("change", 0);
            setResult(RESULT_OK, intent);
        } else if (v.getId() == R.id.button_payment_quicpay) {
            intent.putExtra("payment", getString(R.string.payment_quicpay));
            intent.putExtra("cash", total);
            intent.putExtra("change", 0);
            setResult(RESULT_OK, intent);
        } else if (v.getId() == R.id.button_payment_id) {
            intent.putExtra("payment", getString(R.string.payment_id));
            intent.putExtra("cash", total);
            intent.putExtra("change", 0);
            setResult(RESULT_OK, intent);
        } else if (v.getId() == R.id.button_payment_ic) {
            intent.putExtra("payment", getString(R.string.payment_ic));
            intent.putExtra("cash", total);
            intent.putExtra("change", 0);
            setResult(RESULT_OK, intent);
        } else if (v.getId() == R.id.button_cancel) {
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }
}
