package com.example.lab

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlin.collections.ArrayList


class MainMenuAdapter(dataArgs: ArrayList<MenuItem>, callback: AdapterCallback?) :
    RecyclerView.Adapter<MainMenuAdapter.RecyclerViewHolder>() {
    private var dataSource = ArrayList<MenuItem>()

    public fun fillMass(mas: ArrayList<MenuItem>) : Boolean {
        if (mas.count() < 7)
            return false
        if (dataSource.count() < 7) {
            dataSource.clear()
            for (i in 0..6)
                dataSource.add(mas[i])
            return true
        }
        else {
            for (i in 0..6) {
                dataSource[i].setText(mas[i].text)
                dataSource[i].setNominal(mas[i].nominal)
            }
            return true
        }
    }

    interface AdapterCallback {
        fun onItemClicked(menuPosition: Int?)
    }

    private val callback: AdapterCallback?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.main_menu_item, parent, false)
        return RecyclerViewHolder(view)
    }

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var menuContainer: RelativeLayout = view.findViewById(R.id.menu_container)
        var menuItem: TextView = view.findViewById(R.id.menu_item)
        var menuIcon: ImageView = view.findViewById(R.id.menu_icon)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data_provider = dataSource[position]
        if (data_provider.image == R.drawable.no)
        {
            var day : Int = data_provider.nominal%100
            var mou : Int = data_provider.nominal%10000/100
            var res : String = "  На "
            res = if (day < 10)
                res + "0" + day.toString() + "."
            else
                "$res$day."

            if (mou < 10)
                res = res + "0" + mou.toString()
            else
                res += mou.toString()

            holder.menuItem.text = res
        }
        else {
            holder.menuIcon.setImageResource(data_provider.image)
            if (data_provider.image == R.drawable.update) {
                holder.menuItem.text = " Обновить"
            }
            else if (data_provider.nominal > 1) {
                val tmp: String = " " + data_provider.text.toString() + "/" + data_provider.nominal.toString()
                holder.menuItem.text = tmp
            } else
                holder.menuItem.text = "  " + data_provider.text.toString()

            holder.menuContainer.setOnClickListener { callback?.onItemClicked(position) }
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    init {
        dataSource = dataArgs
        this.callback = callback
    }
}

class MenuItem(var image: Int = -1, var text: Double = 0.0, var nominal: Int = 1) {
    @JvmName("setText1")
    fun setText (txt: Double) {
        text = txt
    }

    @JvmName("setImage1")
    fun setImage (img : Int) {
        image = img
    }

    @JvmName("setNominal1")
    fun setNominal(nom : Int) {
        nominal = nom
    }
}