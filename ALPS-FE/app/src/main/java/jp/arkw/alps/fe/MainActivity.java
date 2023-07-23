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

public class MainActivity extends AppCompatActivity {
    private ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<Map<String, String>> listSelect = new ArrayList<>();
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text_view);
        textView.setText("小計: ￥ " + total);
        items.add(new Item("ひらがないれーす", 100, R.drawable.gamecd));
        items.add(new Item("ハコ単", 100, R.drawable.gamecd));

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                listSelect,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "detail"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        ListView listViewSelect = findViewById(R.id.list_select);
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
                updateListSelect();
                simpleAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateListSelect() {
        listSelect.clear();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getQuantity() >= 1) {
                Map<String, String> map = new HashMap<>();
                int subtotal = item.getPrice() * item.getQuantity();
                map.put("name", item.getName());
                map.put("detail", "￥ " + item.getPrice() + " × " + item.getQuantity() + " = ￥ " + subtotal);
                listSelect.add(map);
            }
        }
    }
}