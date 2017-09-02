package ru.trinitydigital.cloudsanddroids

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class PlayerActivity : AppCompatActivity(), ChoosePlayerFragment.OnPlayerChooseListener {

    internal var battleId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val extras = intent.extras
        battleId = extras.getString(EXTRA_BATTLE_ID)

        if (savedInstanceState == null)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, ChoosePlayerFragment.newInstance(battleId))
                    .commit()
    }

    override fun onPlayerChoose(playerId: String, opponentId: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, StatsFragment.newInstance(battleId, playerId, opponentId)).addToBackStack(null)
                .commit()
    }

    companion object {

        private val TAG = "PlayerActivity"
        private val EXTRA_BATTLE_ID = "EXTRA_BATTLE_ID"

        fun start(context: Context, battleId: String) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(EXTRA_BATTLE_ID, battleId)
            context.startActivity(intent)
        }
    }
}
