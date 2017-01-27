package ru.trinitydigital.cloudsanddroids;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChoosePlayerFragment extends Fragment {
    private static String TAG = "ChoosePlayerFragment";
    private static final String BATTLE_ID = "BATTLE_ID";

    private String battleId;
    FirebaseDatabase database;

    OnPlayerChooseListener onPlayerChooseListener;

    public ChoosePlayerFragment() {
        // Required empty public constructor
    }

    public static ChoosePlayerFragment newInstance(String balleId) {
        ChoosePlayerFragment fragment = new ChoosePlayerFragment();
        Bundle args = new Bundle();
        args.putString(BATTLE_ID, balleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            battleId = getArguments().getString(BATTLE_ID);
        }
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayerChooseListener) {
            onPlayerChooseListener = ((OnPlayerChooseListener) context);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_player, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final TextView noPlayers = (TextView) getView().findViewById(R.id.noPlayers);
        final LinearLayout choosePlayer = (LinearLayout) getView().findViewById(R.id.choosePlayer);

        database = FirebaseDatabase.getInstance();
        database.getReference().child("battles").child(battleId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot battle) {
                Log.d(TAG, "Value is: " + battle);
                if (!ChoosePlayerFragment.this.isResumed())
                    return;
                if (battle.hasChild("states") && battle.child("states").getChildrenCount() == 2) {
                    choosePlayer.setVisibility(View.VISIBLE);
                    noPlayers.setVisibility(View.GONE);

                    final Button firstPlayer = (Button) getView().findViewById(R.id.firstPlayer);
                    final Button secondPlayer = (Button) getView().findViewById(R.id.secondPlayer);

                    int i = 0;
                    for (DataSnapshot player : battle.child("states").getChildren()) {
                        if (i == 0)
                            firstPlayer.setText(player.getKey());
                        else {
                            secondPlayer.setText(player.getKey());
                            break;
                        }
                        i++;
                    }

                    firstPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onPlayerChooseListener != null)
                                onPlayerChooseListener.onPlayerChoose(firstPlayer.getText().toString(),
                                        secondPlayer.getText().toString());
                        }
                    });
                    secondPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onPlayerChooseListener != null)
                                onPlayerChooseListener.onPlayerChoose(secondPlayer.getText().toString(),
                                        firstPlayer.getText().toString());
                        }
                    });
                }
                else {
                    noPlayers.setVisibility(View.VISIBLE);
                    choosePlayer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public interface OnPlayerChooseListener {
        void onPlayerChoose(String playerId, String opponentId);
    }
}
