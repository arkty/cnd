package ru.trinitydigital.cloudsanddroids;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView noBattles = (TextView) findViewById(R.id.noBattles);
        final LinearLayout someBattles = (LinearLayout) findViewById(R.id.someBattles);
        final ListView battlesList = (ListView) findViewById(R.id.battlesList);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("battles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot battles) {

                Log.d(TAG, "Value is: " + battles);
                final List<String> battleIds = new ArrayList<String>();
                for (DataSnapshot battle : battles.getChildren())
                    battleIds.add(battle.getKey());

                if (battleIds.size() == 0) {
                    noBattles.setVisibility(View.VISIBLE);
                    someBattles.setVisibility(View.GONE);
                    return;
                }

                someBattles.setVisibility(View.VISIBLE);
                noBattles.setVisibility(View.GONE);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, battleIds.toArray(new String[battleIds.size()]));
                battlesList.setAdapter(adapter);
                battlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        PlayerActivity.start(MainActivity.this, battleIds.get(i));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }
}
