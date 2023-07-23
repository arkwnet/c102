package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Map<String, String>> listSelect = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private TextView textView;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        findViewById(R.id.button_clear).setOnClickListener(this);
        update();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_clear) {
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
}