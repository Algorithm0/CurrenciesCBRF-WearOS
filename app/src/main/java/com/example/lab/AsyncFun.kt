package com.example.lab

import com.example.myapplication.R
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.URL

val job: Job = Job()
val scope = CoroutineScope(Dispatchers.Default + job)

fun getDoubleComma(str: String): Double {
    return str.replace(',', '.').toDouble()
}

fun getIntDate(str: String): Int {
    var res: Int = str.substring(6, 10).toInt() * 10000
    res += str.substring(3, 5).toInt() * 100
    res += str.substring(0, 2).toInt()
    return res
}

fun getData(site: String): ArrayList<MenuItem> {
    val menuItems: ArrayList<MenuItem> = ArrayList()
    try {
        menuItems.add(MenuItem(R.drawable.no))
        menuItems.add(MenuItem(R.drawable.usd))
        menuItems.add(MenuItem(R.drawable.eur))
        menuItems.add(MenuItem(R.drawable.bur))
        menuItems.add(MenuItem(R.drawable.ukr))
        menuItems.add(MenuItem(R.drawable.try_))
        menuItems.add(MenuItem(R.drawable.gbp))
        menuItems.add(MenuItem(R.drawable.jpy))
        menuItems.add(MenuItem(R.drawable.update))

        val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance();
        factory.isNamespaceAware = true;
        val xpp: XmlPullParser = factory.newPullParser();
        var stopCount: Int = 0
        var number: Int = -1
        val tmp: String = URL(site).readText()


//        val file : File = File(Environment.getDownloadCacheDirectory().toString() + File.separator + "latestMoney.xml")
//        if (file.exists()) file.createNewFile()
//        val cop = tmp
//        var a : FileOutputStream = FileOutputStream(file)
//        a.write(cop.toByteArray())
//        a.close()

        menuItems[0].setNominal(getIntDate(tmp.substring(60, 70)))
        xpp.setInput(StringReader(tmp))
        while (xpp.eventType != XmlPullParser.END_DOCUMENT && stopCount < 7) {
            if (xpp.eventType == XmlPullParser.START_TAG && xpp.name == "CharCode") {
                xpp.next()
                when (xpp.text) {
                    "BYN" -> number = 3 //Белорусский рубль
                    "GBP" -> number = 6 //Фунт стерлингов Соединенного королевства
                    "USD" -> number = 1 //Доллар США
                    "JPY" -> number = 7 //Японских йен
                    "UAH" -> number = 4 //Украинских гривен
                    "TRY" -> number = 5 //Турецких лир
                    "EUR" -> number = 2 //Евро
                }
                if (number != -1) {
                    for (i in 0..8) {
                        xpp.next()
                        if (i == 2)
                            menuItems[number].setNominal(xpp.text.toInt())
                    }
                    menuItems[number].setText(getDoubleComma(xpp.text))
                    number = -1
                    stopCount++
                }
            }
            xpp.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return menuItems
}