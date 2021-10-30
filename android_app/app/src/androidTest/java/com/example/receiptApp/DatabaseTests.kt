package com.example.receiptApp

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.ElementsDao
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class DatabaseTests
{

    private lateinit var aggregatesDao: AggregatesDao
    private lateinit var elemntsDao: ElementsDao
    private lateinit var db: AppDatabase
    private var tag = "DB_TEST"

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlocking{
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        aggregatesDao = db.aggregateDao()
        elemntsDao = db.elementsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAggregate() = runBlocking{

        val expected = Aggregate(
            tag_id = 5,
            date = Date(),
            location = Location("").also{it.latitude=0.0; it.longitude=0.0},
            attachment = Uri.parse("/test"),
            total_cost = 16.8f
        )

        aggregatesDao.insert(expected)
        val result = aggregatesDao.getLastAggregate()

        Log.d(tag, "insertAndGetAggregate()")
        Log.d(tag, "expected.location= ${expected.location.toString()}")
        Log.d(tag, "result.location= ${result.location.toString()}")
        CustomAsserts.aggregates(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetElement() = runBlocking{

        val expected = Element(
            aggregate_id = 0,
            name = "test element",
            num = 3,
            parent_tag_id = 4,
            tag_id = 3,
            cost = 34.6f
        )



    }

}
