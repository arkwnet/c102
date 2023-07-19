package jp.arkw.alps.fe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text_view);
        textView.setText("小計: ￥ 0");

        ArrayList<Map<String, String>> listSelect = new ArrayList<>();
        ListView listViewSelect = findViewById(R.id.list_select);
        listViewSelect.setAdapter(new SimpleAdapter(
            this,
            listSelect,
            android.R.layout.simple_list_item_2,
            new String[] {"name", "detail"},
            new int[] {android.R.id.text1, android.R.id.text2}
        ));

        ArrayList<Map<String, Object>> listItem = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("name", "ひらがないれーす");
        item.put("detail", "￥ 100");
        item.put("image", R.drawable.gamecd);
        listItem.add(item);
        ListView listViewItem = findViewById(R.id.list_item);
        listViewItem.setAdapter(new SimpleAdapter(
            this,
            listItem,
            R.layout.list_item,
            new String[] {"name", "detail", "image"},
            new int[] {R.id.name, R.id.detail, R.id.image}
        ));
    }
}