@file:Suppress("DEPRECATION")

package com.example.lab

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.widget.ProgressBar
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.myapplication.*
import kotlinx.coroutines.launch

const val EXTRA_MESSAGE = "com.example.lab.MESSAGE"

class MainActivity : WearableActivity() {

    private var menuItems: ArrayList<MenuItem> = ArrayList()
    private lateinit var recyclerView: WearableRecyclerView
    private lateinit var bar : ProgressBar

    private fun createMes(I : Int) : String {
        return """${I}-${menuItems[I].text}-${menuItems[0].nominal}"""
    }

    private fun updateList() {
        bar.visibility = ProgressBar.VISIBLE
        recyclerView.visibility = View.INVISIBLE

        scope.launch {
            menuItems = getData("https://www.cbr.ru/scripts/XML_daily.asp")
            runOnUiThread {
                recyclerView.adapter = MainMenuAdapter(menuItems, object :
                        MainMenuAdapter.AdapterCallback {
                    override fun onItemClicked(menuPosition: Int?) {
                        when (menuPosition) {
                            8 -> updateList()
                            0 -> {}
                            null -> {}
                            else -> sendMessage(createMes(menuPosition))
                        }
                    }
                })

                bar.visibility = ProgressBar.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                recyclerView.requestFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        recyclerView = findViewById(R.id.recycler_launcher_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.isEdgeItemsCenteringEnabled = true
        recyclerView.layoutManager = WearableLinearLayoutManager(this)

        recyclerView.apply {
            isCircularScrollingGestureEnabled = true
            bezelFraction = 0.5f
            scrollDegreesPerScreen = 90f
        }

        bar = findViewById(R.id.main_progress)

        updateList()

        // Enables Always-on
        setAmbientEnabled()
    }

    fun sendMessage(msg : String) {
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, msg)
        }
        startActivity(intent)
    }
}

