package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

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
        if (v.getId() == R.id.button_payment_money) {
            LayoutInflater inflater = LayoutInflater.from(PurchaseActivity.this);
            View view = inflater.inflate(R.layout.edit_dialog, null);
            final EditText editText = (EditText)view.findViewById(R.id.edit_text);
            new AlertDialog.Builder(PurchaseActivity.this)
                .setTitle("預かり金額を入力")
                .setView(view)
                .setPositiveButton(
                    "OK",
                        (dialog, which) -> {
                            String text = editText.getText().toString();
                            if (isInteger(text) == true) {
                                int cash = Integer.parseInt(text);
                                if (cash >= 0 && cash < 99999 && total <= cash) {
                                    int change = cash - total;
                                    finishPurchase(getString(R.string.payment_money), cash, change);
                                } else {
                                    showAlert("入力値が不正です");
                                }
                            }
                        })
                .setNegativeButton(
                    "キャンセル",
                        (dialog, which) -> {
                        })
                .show();
        } else if (v.getId() == R.id.button_payment_credit) {
            finishPurchase(getString(R.string.payment_credit), total, 0);
        } else if (v.getId() == R.id.button_payment_quicpay) {
            finishPurchase(getString(R.string.payment_quicpay), total, 0);
        } else if (v.getId() == R.id.button_payment_id) {
            finishPurchase(getString(R.string.payment_id), total, 0);
        } else if (v.getId() == R.id.button_payment_ic) {
            finishPurchase(getString(R.string.payment_ic), total, 0);
        } else if (v.getId() == R.id.button_cancel) {
            cancelPurchase();
        }
    }

    public void finishPurchase(String type, int cash, int change) {
        Intent intent = new Intent(PurchaseActivity.this, MainActivity.class);
        intent.putExtra("payment", type);
        intent.putExtra("cash", cash);
        intent.putExtra("change", change);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelPurchase() {
        Intent intent = new Intent(PurchaseActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public boolean isInteger(String text) {
        Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
        boolean res = pattern.matcher(text).matches();
        return res;
    }

    public void showAlert(String text) {
        new AlertDialog.Builder(this)
            .setTitle("エラー")
            .setMessage(text)
            .setPositiveButton("OK", null)
            .show();
    }
}
