@file:Suppress("DEPRECATION")

package com.example.lab

import android.graphics.Color
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.launch
import com.example.myapplication.R

class SecondActivity : WearableActivity() {

    private var secondData: ArrayList<MenuItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)

        intViewVars()

        val message = intent.getStringExtra(EXTRA_MESSAGE)

        readMessage(message.toString())

        when(numI) {
            1 -> nameV.text = "Доллар США"
            2 -> nameV.text = "Евро"
            3 -> nameV.text = "Белорусский\nрубль"
            4 -> nameV.text = "Украинская\nгривна"
            5 -> nameV.text = "Турецкая\nлира"
            6 -> nameV.text = "Фунт\nстерлингов CК"
            7 -> nameV.text = "Японская\nйена"
        }

        open()

        // Enables Always-on
        setAmbientEnabled()

    }

    private var numI : Int = 0
    private var lastNumer : Double = 0.0
    private var lastDate : Int = 0

    fun readMessage (mes : String) {
        var i : Int = 0
        while (mes[i] != '-')
            i++
        numI = mes.substring(0, i).toInt()
        i++
        var j : Int = i
        while (mes[j] != '-')
            j++
        lastNumer = mes.substring(i, j).toDouble()
        lastDate = mes.substring(j+1, mes.length).toInt()
    }

    fun intViewVars() {
        bar2 = findViewById(R.id.about_progress)
        aboutView = findViewById(R.id.information)

        nameV = findViewById(R.id.text_about)
        firstDate = findViewById(R.id.first_date)
        firstNum = findViewById(R.id.first_num)
        secondDate = findViewById(R.id.second_date)
        secondNum = findViewById(R.id.second_num)
        dif = findViewById(R.id.difference)
        noms = findViewById(R.id.noms)
    }

    private fun getLastDay() : String { //я понимаю, что тут чушь, но я спешил
        var res : String = ""
        var tmp : Int = (lastDate%100 - 1)
        if (tmp < 10) res = "0$tmp/"
        else res = "$tmp/"
        tmp = (lastDate%10000/100).toInt()
        if (tmp < 10) res = res + "0" + tmp.toString()
        else res += tmp.toString()
        return res + "/" + (lastDate/10000).toInt().toString()
    }

    private fun daySet(num : Int) : String  {
        var day : Int = num%100
        var mou : Int = num%10000/100
        var res : String = ""
        if (day < 10)
            res = "0$day."
        else
            res = "$day."

        if (mou < 10)
            res = res + "0" + mou.toString() + ":"
        else
            res = "$res$mou:"
        return res
    }

    private fun open() {

        var tmpStr: ArrayList<String> = ArrayList()

        if (secondData.isEmpty()) {

            scope.launch {
                secondData = getData("https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + getLastDay())
                val d : Double = lastNumer - secondData[numI].text
                if (d > 0)
                    tmpStr.add("+" + String.format("%.4f", d))
                else
                    tmpStr.add(String.format("%.4f", d))
                tmpStr.add(daySet(lastDate))
                tmpStr.add(daySet(secondData[0].nominal))
                runOnUiThread {
                    firstDate.text = tmpStr[1]
                    secondDate.text = tmpStr[2]
                    dif.text = tmpStr[0]
                    if (tmpStr[0][0] == '+')
                        dif.setTextColor(Color.GREEN)
                    else dif.setTextColor(Color.RED)
                    firstNum.text = lastNumer.toString()
                    secondNum.text = secondData[numI].text.toString()
                    noms.text = "от " +  secondData[numI].nominal + " ₽"
                    aboutView.visibility = LinearLayout.VISIBLE
                    bar2.visibility = ProgressBar.INVISIBLE
                }
            }
        }

        else {
            secondData = getData("https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + getLastDay())
            val d : Double = lastNumer - secondData[numI].text
            if (d > 0)
                tmpStr.add("+" + String.format("%.4f", d))
            else
                tmpStr.add(String.format("%.4f", d))
            firstDate.text = tmpStr[1]
            secondDate.text = tmpStr[2]
            dif.text = tmpStr[0]
            if (tmpStr[0][0] == '+')
                dif.setTextColor(Color.GREEN)
            else dif.setTextColor(Color.RED)
            firstNum.text = lastNumer.toString()
            secondNum.text = secondData[numI].text.toString()
            noms.text = "от " +  secondData[numI].nominal + " ₽"
        }
    }

    lateinit var bar2 : ProgressBar
    lateinit var aboutView : LinearLayout

    lateinit var nameV : TextView
    lateinit var firstDate : TextView
    lateinit var firstNum : TextView
    lateinit var secondDate : TextView
    lateinit var secondNum : TextView
    lateinit var dif : TextView
    lateinit var noms : TextView

}