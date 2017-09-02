package ru.trinitydigital.cloudsanddroids

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.fragment_stats.*

class StatsFragment : Fragment() {

    internal var maxHp: Int = 0
    internal var maxMana: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseDatabase.getInstance()
                .reference
                .child("settings")
                .getValueSingle(Types.settingsType, Settings())
                .subscribe({
                    maxHp = it.max_hp
                    maxMana = it.max_mana
                    trackMyState()
                    trackOpponentState()
                    trackSpells()
                },{
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    fun trackMyState() {
        FirebaseDatabase.getInstance()
                .reference
                .child("battles")
                .child(arguments.getString(BATTLE_ID))
                .child("states")
                .child(arguments.getString(PLAYER_ID))
                .getValueObservable(Types.stateType, State())
                .subscribe({
                    name.text = it.name
                    hpText.text = "HP: ${it.hp}/$maxHp"
                    manaText.text = "MANA: ${it.mana}/$maxMana"
                },{
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    fun trackOpponentState() {
        FirebaseDatabase.getInstance()
                .reference
                .child("battles")
                .child(arguments.getString(BATTLE_ID))
                .child("states")
                .child(arguments.getString(OPPONENT_ID))
                .getValueObservable(Types.stateType, State())
                .subscribe({
                    opponentName.text = it.name
                    if (it.hp <= 0)
                        opponentImage.setImageResource(R.drawable.grumpy)
                    else if (it.hp < maxHp / 3.0f)
                        opponentImage.setImageResource(R.drawable.sad)
                    else if (it.hp < maxHp / 3.0f * 2)
                        opponentImage.setImageResource(R.drawable.neutral)
                    opponentImage.setImageResource(R.drawable.smile)
                },{
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    fun trackSpells() {
        FirebaseDatabase.getInstance()
                .reference
                .child("battles")
                .child(arguments.getString(BATTLE_ID))
                .child("turns")
                .orderByKey()
                .limitToLast(1)
                .getLastValueObservable(Types.turnType, Turn())
                .subscribe({
                    val target = it.target.toString()
                    if (!it.card.isNullOrEmpty())
                        FirebaseDatabase.getInstance()
                                .reference
                                .child("cards")
                                .child(it.card)
                                .getValueSingle(Types.cardType, Card())
                                .subscribe({
                                    spellText.text = it.name
                                    if (target == arguments.getString(OPPONENT_ID))
                                        spellText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_down_black_24dp, 0)
                                    else if (target == arguments.getString(PLAYER_ID))
                                        spellText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trending_up_black_24dp, 0)
                                },{
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", it)
                                })
                },{
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    companion object {
        private val TAG = "StatsFragment"
        private val PLAYER_ID = "PLAYER_ID"
        private val OPPONENT_ID = "OPPONENT_ID"
        private val BATTLE_ID = "BATTLE_ID"

        fun newInstance(battleId: String, playerId: String, opponentId: String): StatsFragment {
            val fragment = StatsFragment()
            val args = Bundle()
            args.putString(PLAYER_ID, playerId)
            args.putString(BATTLE_ID, battleId)
            args.putString(OPPONENT_ID, opponentId)
            fragment.arguments = args
            return fragment
        }
    }
}
