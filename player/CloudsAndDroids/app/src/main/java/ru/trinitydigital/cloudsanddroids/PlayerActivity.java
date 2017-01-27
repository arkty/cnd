package ru.trinitydigital.cloudsanddroids;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity implements ChoosePlayerFragment.OnPlayerChooseListener {

    private static String TAG = "PlayerActivity";
    private static String EXTRA_BATTLE_ID = "EXTRA_BATTLE_ID";

    String battleId;

    public static void start(Context context, String battleId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_BATTLE_ID, battleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle extras = getIntent().getExtras();
        battleId = extras.getString(EXTRA_BATTLE_ID);

        if (savedInstanceState == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ChoosePlayerFragment.newInstance(battleId))
                    .commit();
    }

    @Override
    public void onPlayerChoose(String playerId, String opponentId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, StatsFragment.newInstance(battleId, playerId, opponentId)).addToBackStack(null)
                .commit();
    }
}
