package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SunmiPrinterService sunmiPrinterService;
    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;
    public int sunmiPrinter = CheckSunmiPrinter;

    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Map<String, String>> listSelect = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private TextView textView;
    private int total = 0;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSunmiPrinterService(this);

        items.add(new Item("ひらがないれーす", 100, R.drawable.gamecd));
        items.add(new Item("ハコ単", 100, R.drawable.gamecd));

        textView = findViewById(R.id.text_view);
        ListView listViewSelect = findViewById(R.id.list_select);
        simpleAdapter = new SimpleAdapter(
            this,
            listSelect,
            android.R.layout.simple_list_item_2,
            new String[] {"name", "detail"},
            new int[] {android.R.id.text1, android.R.id.text2}
        );
        listViewSelect.setAdapter(simpleAdapter);

        ArrayList<Map<String, Object>> listItem = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", items.get(i).getName());
            item.put("detail", "￥ " + items.get(i).getPrice());
            item.put("image", items.get(i).getImage());
            listItem.add(item);
        }
        ListView listViewItem = findViewById(R.id.list_item);
        listViewItem.setAdapter(new SimpleAdapter(
            this,
            listItem,
            R.layout.list_item,
            new String[] {"name", "detail", "image"},
            new int[] {R.id.name, R.id.detail, R.id.image}
        ));
        listViewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                items.get(index).setQuantity(items.get(index).getQuantity() + 1);
                update();
            }
        });

        findViewById(R.id.button_purchase).setOnClickListener(this);
        findViewById(R.id.button_card).setOnClickListener(this);
        findViewById(R.id.button_clear).setOnClickListener(this);
        update();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_purchase) {
            Intent intent = new Intent(getApplication(), PurchaseActivity.class);
            intent.putExtra("total", total);
            startActivityForResult(intent, 1);
        } else if (v.getId() == R.id.button_card) {
            printImage(BitmapFactory.decodeResource(getResources(), R.drawable.card));
            feedPaper(5);
        } else if (v.getId() == R.id.button_clear) {
            clearQuantity();
        }
    }

    private void clearQuantity() {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setQuantity(0);
        }
        total = 0;
        update();
    }

    private void update() {
        listSelect.clear();
        total = 0;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getQuantity() >= 1) {
                Map<String, String> map = new HashMap<>();
                int subtotal = item.getPrice() * item.getQuantity();
                map.put("name", item.getName());
                map.put("detail", "￥ " + item.getPrice() + " × " + item.getQuantity() + " = ￥ " + subtotal);
                listSelect.add(map);
                total += subtotal;
            }
        }
        simpleAdapter.notifyDataSetChanged();
        textView.setText("合計: ￥ " + total);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            id++;
            String payment = intent.getStringExtra("payment");
            int cash = intent.getIntExtra("cash", 0);
            int change = intent.getIntExtra("change", 0);
            printImage(BitmapFactory.decodeResource(getResources(), R.drawable.receipt));
            printText("ご購入になりました商品の\n", 0);
            printText("サポート情報はこちら ↓\n", 0);
            printText("https://arkw.work/doujin\n", 0);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd(E) HH:mm");
            printText("レジ0001 " + simpleDateFormat.format(date) + "\n", 0);
            printText("取" + String.format("%04d", id) + " 責: 01 荒川\n\n", 0);
            int total = 0;
            for (int i = 0; i < items.size(); i++) {
                final Item item = items.get(i);
                if (item.getQuantity() >= 1) {
                    printText("" + item.getName() + " × " + item.getQuantity() + "\n", 0);
                    final int subtotal = item.getPrice() * item.getQuantity();
                    total += subtotal;
                    printText("" + subtotal + "\n", 2);
                }
            }
            printLine();
            printText("合計\n", 0);
            printText("￥ " + total + "\n", 2);
            if (payment.equals(R.id.button_payment_money) == true) {
                printText("お預かり\n", 0);
            } else {
                printText(payment + "\n", 0);
            }
            printText("￥ " + cash + "\n", 2);
            printText("お釣り\n", 0);
            printText("￥ " + change + "\n", 2);
            printLine();
            printText("Arakawa Laboratory\n", 0);
            printText("Web: https://arkw.net/\n", 0);
            printText("E-Mail: mail@arkw.net\n", 0);
            printText("Twitter: @arkw0\n", 0);
            printText("Misskey: @arkw@mi.arkw.work", 0);
            feedPaper(5);
            clearQuantity();
        }
    }

    private void printText(String text, int alignment) {
        try {
            sunmiPrinterService.setAlignment(alignment, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                }
                @Override
                public void onReturnString(String result) throws RemoteException {
                }
                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                }
                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        };
        try {
            sunmiPrinterService.printText(text, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                }
                @Override
                public void onReturnString(String result) throws RemoteException {
                }
                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                }
                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        };
    }

    private void printImage(Bitmap bitmap) {
        try {
            sunmiPrinterService.printBitmap(bitmap, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                }
                @Override
                public void onReturnString(String result) throws RemoteException {
                }
                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                }
                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        };
    }

    private void printLine() {
        printText("--------------------------------\n", 1);
    }

    private void feedPaper(int n) {
        try {
            sunmiPrinterService.lineWrap(n, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                }
                @Override
                public void onReturnString(String result) throws RemoteException {
                }
                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                }
                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        };
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            sunmiPrinterService = service;
            checkSunmiPrinterService(service);
        }
        @Override
        protected void onDisconnected() {
            sunmiPrinterService = null;
            sunmiPrinter = LostSunmiPrinter;
        }
    };

    private void checkSunmiPrinterService(SunmiPrinterService service) {
        boolean ret = false;
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        sunmiPrinter = ret?FoundSunmiPrinter:NoSunmiPrinter;
    }

    public void initSunmiPrinterService(Context context) {
        try {
            boolean ret =  InnerPrinterManager.getInstance().bindService(context, innerPrinterCallback);
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }
}
