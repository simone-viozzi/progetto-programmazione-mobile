package com.example.receiptApp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.receiptApp.SHARED_PREF_DASHBOARD
import com.example.receiptApp.pages.home.DashboardElement
import com.example.receiptApp.pages.home.TYPE
import timber.log.Timber

class SharedPrefRepository(applicationContext: Context)
{
    private val sharedPrefDash = applicationContext.getSharedPreferences(SHARED_PREF_DASHBOARD, Context.MODE_PRIVATE)

    fun writeDashboard(dashboard: Map<Int, DashboardElement>)
    {
        with (sharedPrefDash.edit())
        {

            clear()

            dashboard.entries.forEach {

                Timber.d("it.value -> ${it.value}")

                writeDashboardElement(it.key, it.value, this)
            }

            putInt("n_elements", dashboard.size)

            apply()
        }
    }

    private fun writeDashboardElement(index: Int, element: DashboardElement, editor: SharedPreferences.Editor)
    {
        val base = "el_$index"

        with (editor) {
            putInt("${base}_id", element.id)
            putString("${base}_type", element.type.name)
        }
    }


    fun readDashboard(): Map<Int, DashboardElement>
    {
        val size = sharedPrefDash.getInt("n_elements", -1) - 1

        val dashboard: MutableMap<Int, DashboardElement> = mutableMapOf()
        for (i in 0..size)
        {
            dashboard[i] = readDashboardElement(i)
        }

        return dashboard
    }

    private fun readDashboardElement(index: Int): DashboardElement
    {
        var element: DashboardElement

        val base = "el_$index"

        with(sharedPrefDash)
        {
            val id = getInt("${base}_id", -1)
            val type = getString("${base}_type", "")

            Timber.d("type -> $type")

            element = type?.let { TYPE.valueOf(it).getObj() }!!

            element.id = id
        }

        return element
    }
}