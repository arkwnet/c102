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

import java.util.ArrayList;
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
            startActivity(intent);
        } else if (v.getId() == R.id.button_card) {
            printImage(BitmapFactory.decodeResource(getResources(), R.drawable.card));
            feedPaper();
        } else if (v.getId() == R.id.button_clear) {
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setQuantity(0);
            }
            total = 0;
            update();
        }
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

    private void feedPaper() {
        try {
            sunmiPrinterService.lineWrap(5, new InnerResultCallback() {
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
