package com.example.receiptApp

import android.location.Location
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.AggregatesDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.db.tag.TagsDao
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class DatabaseTests
{

    private lateinit var aggregatesDao: AggregatesDao
    private lateinit var elementsDao: ElementsDao
    private lateinit var tagsDao: TagsDao
    private lateinit var db: AppDatabase
    private var tag = "DB_TEST"

    val exAggregate = Aggregate(
        tag_id = 5,
        date = Date(),
        location = Location("").also{it.latitude=0.0; it.longitude=0.0},
        attachment = Uri.parse("/test"),
        total_cost = 16.8f
    )

    val exEement = Element(
        aggregate_id = 0,
        name = "test element",
        num = 3,
        parent_tag_id = 4,
        elem_tag_id = 3,
        cost = 34.6f
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlocking{
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // without setTransactionExecutor test will wait forever
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        aggregatesDao = db.aggregateDao()
        elementsDao = db.elementsDao()
        tagsDao = db.tagsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAggregate() = runBlocking{

        // generate test values
        val expectedAggregate = exAggregate
        val expectedListOfElements: List<Element> = listOf(exEement, exEement, exEement)

        // insert values to the database
        val resultId = aggregatesDao.insertAggregateWithElements(expectedAggregate, expectedListOfElements)
        val result = aggregatesDao._getAggregateWithElementsById(resultId)

        // check for result
        if (result == null) {
            Assert.fail("result is null")
        }

        //val mapResult = result.getOrAwaitValue()
        val mapResult = result

        // check if map is null
        if (mapResult != null){
            // ceck if map contain expected agregate
            if(mapResult.size == 1){
                for((aggregate, elements) in mapResult){
                    CustomAsserts.aggregates(expectedAggregate, aggregate)
                    CustomAsserts.elementsList(expectedListOfElements, elements)
                }
            }else{
                Assert.fail("result has size ${mapResult.size}, expected 1")
            }
        }else{
            Assert.fail("returned map is null")
        }
    }

    /*
    @Test
    @Throws(Exception::class)
    fun countAndDeleteEntities() = runBlocking{

        aggregatesDao.insert(exAggregate)
        elementsDao.insert(exEement)

        var aggregatesCount = aggregatesDao.countAllAggregates()
        var elementsCount = elementsDao.countAllElements()

        Log.d(tag, "Counts before deletion: ")
        Log.d(tag, "aggregtesCount: ${aggregatesCount}")
        Log.d(tag, "elementsCount: ${elementsCount}")

        aggregatesDao.deleteAll()
        elementsDao.deleteAll()

        aggregatesCount = aggregatesDao.countAllAggregates()
        elementsCount = elementsDao.countAllElements()

        Log.d(tag, "Counts after deletion: ")
        Log.d(tag, "aggregtesCount: ${aggregatesCount}")
        Log.d(tag, "elementsCount: ${elementsCount}")

        assertEquals(aggregatesCount, 0)
        assertEquals(elementsCount, 0)
    }*/
}
