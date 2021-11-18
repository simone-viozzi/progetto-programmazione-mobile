package com.example.receiptApp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.receiptApp.SHARED_PREF_DASHBOARD
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import com.example.receiptApp.pages.dashboard.DashboardElement
import com.example.receiptApp.pages.dashboard.TYPE
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import timber.log.Timber

class SharedPrefRepository(applicationContext: Context)
{
    private val sharedPrefDash = applicationContext.getSharedPreferences(SHARED_PREF_DASHBOARD, Context.MODE_PRIVATE)

    suspend fun writeDashboard(dashboard: Map<Int, DashboardElement>)
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

    private suspend fun writeDashboardElement(
        index: Int, element: DashboardElement,
        editor: SharedPreferences.Editor
    ) = withContext(Default)
    {
        val base = "el_$index"

        Timber.d("id -> ${element.id}, type -> ${element.type.name}")

        with (editor) {
            putInt("${base}_id", element.id)
            putString("${base}_type", element.type.name)
            putString("${base}_content", element.content)
        }
    }


    suspend fun readDashboard(): Map<Int, DashboardDataModel> = withContext(Default)
    {
        val size = sharedPrefDash.getInt("n_elements", -1) - 1

        val dashboard: MutableMap<Int, DashboardDataModel> = mutableMapOf()
        for (i in 0..size)
        {
            readDashboardElement(i)?.let {
                dashboard[i] = it as DashboardDataModel
            }
        }

        Timber.d("dashboard $dashboard")

        return@withContext dashboard
    }

    private fun readDashboardElement(
        index: Int
    ): DashboardElement?
    {
        var element: DashboardElement

        val base = "el_$index"

        with(sharedPrefDash)
        {
            val id = getInt("${base}_id", -1)
            val type = getString("${base}_type", "")
            val content = getString("${base}_content", "") ?: return null

            Timber.d("id -> $id, type -> $type")

            element = type?.let {

                kotlin.runCatching {
                    TYPE.valueOf(it).getObj()
                }.getOrDefault(null)

            } ?: return null

            element.id = id
            element.content = content
        }

        return element
    }

    fun clearDashboard()
    {
        with (sharedPrefDash.edit())
        {

            clear()
            apply()
        }
    }
}