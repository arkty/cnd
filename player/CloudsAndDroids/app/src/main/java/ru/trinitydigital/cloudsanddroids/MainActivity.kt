package ru.trinitydigital.cloudsanddroids

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseDatabase.getInstance()
                .reference
                .child("battles")
                .getKeysListObservable()
                .subscribe({
                    if (it.isEmpty()) {
                        noBattles.visibility = View.VISIBLE
                        someBattles.visibility = View.GONE
                    }
                    else {
                        someBattles.visibility = View.VISIBLE
                        noBattles.visibility = View.GONE
                        val adapter = ArrayAdapter(this@MainActivity,
                                android.R.layout.simple_list_item_1, it)
                        battlesList.adapter = adapter
                        battlesList.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, i, _ ->
                                    PlayerActivity.start(this@MainActivity, it[i]) }
                    }
                }, {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", it)
                })
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
