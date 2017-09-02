package ru.trinitydigital.cloudsanddroids

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_choose_player.*

class ChoosePlayerFragment : Fragment() {

    internal var onPlayerChooseListener: OnPlayerChooseListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnPlayerChooseListener)
            onPlayerChooseListener = context
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_choose_player, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseDatabase.getInstance()
                .reference
                .child("battles")
                .child(arguments.getString(BATTLE_ID))
                .child("states")
                .getValueObservable(Types.listOfStatesType, listOf())
                .subscribe({
                    if (it.size == 2) {
                        choosePlayer.visibility = View.VISIBLE
                        noPlayers.visibility = View.GONE

                        firstPlayer.text = it[0].name
                        secondPlayer.text = it[1].name

                        firstPlayer.setOnClickListener {
                            if (onPlayerChooseListener != null)
                                onPlayerChooseListener!!.onPlayerChoose("0", "1")
                        }
                        secondPlayer.setOnClickListener {
                            if (onPlayerChooseListener != null)
                                onPlayerChooseListener!!.onPlayerChoose("1", "0")
                        }
                    } else {
                        noPlayers.visibility = View.VISIBLE
                        choosePlayer.visibility = View.GONE
                    }
                }, {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    interface OnPlayerChooseListener {
        fun onPlayerChoose(playerId: String, opponentId: String)
    }

    companion object {
        private val TAG = "ChoosePlayerFragment"
        private val BATTLE_ID = "BATTLE_ID"

        fun newInstance(balleId: String): ChoosePlayerFragment {
            val fragment = ChoosePlayerFragment()
            val args = Bundle()
            args.putString(BATTLE_ID, balleId)
            fragment.arguments = args
            return fragment
        }
    }
}
