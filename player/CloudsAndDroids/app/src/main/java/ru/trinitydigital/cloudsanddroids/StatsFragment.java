package ru.trinitydigital.cloudsanddroids;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import butterknife.Bind;
import butterknife.ButterKnife;

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

    @Bind(R.id.noStats)
    View noStats;

    @Bind(R.id.someStats)
    View someStats;

    @Bind(R.id.name)
    TextView nameView;

    @Bind(R.id.hp)
    TextView hpView;

    @Bind(R.id.mana)
    TextView manaView;

    @Bind(R.id.opponent)
    ImageView opponentView;

    @Bind(R.id.opponentName)
    TextView opponentName;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        database.getReference().child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot settings) {
                maxHp = Integer.parseInt(settings.child("max_hp").getValue().toString());
                maxMana = Integer.parseInt(settings.child("max_mana").getValue().toString());
                hpView.setText("HP: " + hp + "/" + maxHp);
                manaView.setText("MANA: " + mana + "/" + maxMana);
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

                        nameView.setText(player.child("name").getValue().toString());

                        if (player.hasChild("hp") && player.hasChild("mana")) {
                            someStats.setVisibility(View.VISIBLE);
                            noStats.setVisibility(View.GONE);

                            hp = player.child("hp").getValue().toString();
                            mana = player.child("mana").getValue().toString();

                            hpView.setText("HP: " + hp + "/" + maxHp);
                            manaView.setText("MANA: " + mana + "/" + maxMana);
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
                        opponentName.setText(opponent.child("name").getValue().toString());

                        if (opponent.hasChild("hp") && opponent.hasChild("mana")) {
                            int hp = Integer.parseInt(opponent.child("hp").getValue().toString());
                            float thidPart = maxHp / 3.0f;
                            if (hp == 0) {
                                opponentView.setImageResource(R.drawable.grumpy);
                                return;
                            }
                            if (hp < thidPart) {
                                opponentView.setImageResource(R.drawable.sad);
                                return;
                            }
                            if (hp < thidPart * 2) {
                                opponentView.setImageResource(R.drawable.neutral);
                                return;
                            }
                            opponentView.setImageResource(R.drawable.smile);
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
