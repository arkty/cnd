package ru.trinitydigital.cloudsanddroids;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatsFragment extends Fragment {
    private static String TAG = "StatsFragment";
    private static final String PLAYER_ID = "PLAYER_ID";
    private static final String OPPONENT_ID = "OPPONENT_ID";
    private static final String BATTLE_ID = "BATTLE_ID";

    private String playerId;
    private String opponentId;
    private String battleId;
    FirebaseDatabase database;

    int maxHp;
    int maxMana;

    String hp;
    String mana;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(String battleId, String playerId, String opponentId) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(PLAYER_ID, playerId);
        args.putString(BATTLE_ID, battleId);
        args.putString(OPPONENT_ID, opponentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerId = getArguments().getString(PLAYER_ID);
            battleId = getArguments().getString(BATTLE_ID);
            opponentId = getArguments().getString(OPPONENT_ID);
        }
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        database.getReference().child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot settings) {
                maxHp = Integer.parseInt(settings.child("max_hp").getValue().toString());
                maxMana = Integer.parseInt(settings.child("max_mana").getValue().toString());
                ((TextView) getView().findViewById(R.id.hp)).setText("HP: " + hp + "/" + maxHp);
                ((TextView) getView().findViewById(R.id.mana)).setText("MANA: " + mana + "/" + maxMana);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        database.getReference().child("battles").child(battleId).child("states").child(playerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot player) {
                        final TextView noStats = (TextView) getView().findViewById(R.id.noStats);
                        final LinearLayout someStats = (LinearLayout) getView().findViewById(R.id.someStats);

                        ((TextView) getView().findViewById(R.id.name)).setText(player.child("name").getValue().toString());

                        if (player.hasChild("hp") && player.hasChild("mana")) {
                            someStats.setVisibility(View.VISIBLE);
                            noStats.setVisibility(View.GONE);

                            hp = player.child("hp").getValue().toString();
                            mana = player.child("mana").getValue().toString();

                            ((TextView) getView().findViewById(R.id.hp)).setText("HP: " + hp + "/" + maxHp);
                            ((TextView) getView().findViewById(R.id.mana)).setText("MANA: " + mana + "/" + maxMana);
                        }
                        else {
                            noStats.setVisibility(View.VISIBLE);
                            someStats.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });

        database.getReference().child("battles").child(battleId).child("states").child(opponentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot opponent) {
                        ((TextView) getView().findViewById(R.id.opponentName)).setText(opponent.child("name").getValue().toString());
                        final ImageView noStats = (ImageView) getView().findViewById(R.id.opponent);

                        if (opponent.hasChild("hp") && opponent.hasChild("mana")) {
                            int hp = Integer.parseInt(opponent.child("hp").getValue().toString());
                            float thidPart = maxHp / 3.0f;
                            if (hp == 0) {
                                noStats.setImageResource(R.drawable.grumpy);
                                return;
                            }
                            if (hp < thidPart) {
                                noStats.setImageResource(R.drawable.sad);
                                return;
                            }
                            if (hp < thidPart * 2) {
                                noStats.setImageResource(R.drawable.neutral);
                                return;
                            }
                            noStats.setImageResource(R.drawable.smile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
    }
}
