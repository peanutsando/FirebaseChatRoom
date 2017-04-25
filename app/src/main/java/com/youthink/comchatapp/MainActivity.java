package com.youthink.comchatapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editText;
    private Button button;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> room = new ArrayList<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance()
            .getReference().getRoot();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, room);

        listView.setAdapter(arrayAdapter);

        createUserName();

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(editText.getText().toString(), "");
                reference.updateChildren(map);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }

                room.clear();
                room.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("chat_room_name", ((TextView) view).getText().toString());
                intent.putExtra("chat_user_name", name);
                startActivity(intent);
            }
        });
    }

    private void createUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("채팅방에 사용할 이름을 입력하세요");

        final EditText builder_input = new EditText(this);

        builder.setView(builder_input);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                name = builder_input.getText().toString();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                // 취소를 누르면 이름을 입력할 때 까지 요청
                createUserName();
            }
        });

        builder.show();
    }
}
