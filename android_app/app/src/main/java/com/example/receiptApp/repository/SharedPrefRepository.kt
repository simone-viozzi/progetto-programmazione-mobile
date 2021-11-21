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


    /**
     * Write the dashboard into the shared preferences
     */
    suspend fun writeDashboard(dashboard: Map<Int, DashboardElement>)
    {
        with (sharedPrefDash.edit())
        {
            // it's easier to clear the table and save everything from scratch
            clear()

            dashboard.entries.forEach {

                //Timber.d("it.value -> ${it.value}")

                writeDashboardElement(it.key, it.value, this)
            }

            // i need to know how many elements i wrote down
            putInt("n_elements", dashboard.size)

            apply()
        }
    }

    /**
     * Write dashboard element
     *  elements are wrote following:
     *      -> the base string for every element is: el_1, el_2 etc.
     *      -> to this get concatenated the values of DashboardElement
     *          -> el_1_id
     *          -> el_1_type
     *          -> el_1_content
     *
     * @param index
     * @param element
     * @param editor
     */
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


    /**
     * Read the dashboard
     *
     * @return -> the current dashboard
     */
    suspend fun readDashboard(): Map<Int, DashboardDataModel> = withContext(Default)
    {
        // with "n_elements" i can know how many element i need to read
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

    /**
     * Read a dashboard element
     *
     * @param index -> the index of the element i need to read right now
     * @return
     */
    private fun readDashboardElement(
        index: Int
    ): DashboardElement?
    {
        var element: DashboardElement

        val base = "el_$index"

        with(sharedPrefDash)
        {
            // i read the values the same way i wrote them
            val id = getInt("${base}_id", -1)
            val type = getString("${base}_type", "")
            val content = getString("${base}_content", "") ?: return null

            Timber.d("id -> $id, type -> $type")

            // to convert the generic DashboardElement to the real type of this object i use TYPE.getObj()
            element = type?.let {

                // if something changed and the values in the shared preferences are not valid anymore i just return
                //  null and this particular element will be skipped
                kotlin.runCatching {
                    TYPE.valueOf(it).getObj()
                }.getOrDefault(null)

            } ?: return null

            // fill the other data into the newly created object
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