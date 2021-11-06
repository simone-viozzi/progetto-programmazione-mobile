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
import com.example.receiptApp.db.aggregate.PublicAggregatesDao
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.PublicElementsDao
import com.example.receiptApp.db.tag.TagsDao
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class DatabaseTests
{

    private lateinit var aggregatesDao: PublicAggregatesDao
    private lateinit var elementsDao: PublicElementsDao
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

    val aggregateTagsList = listOf<String>("alimentari", "banca", "macchina", "bollette", "viaggi")
    val elementTagsList = listOf<String>("colazione", "tagliando auto", "acqua", "luce", "biglietto aereo", "cornetto", "mouse", "bullone", "taralli")


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
    fun insertAndGetAggregatesWithElements() = runBlocking{

        // lists
        val aggregatesList = mutableListOf<Aggregate>()
        val listOfElementsLists = mutableListOf<List<Element>>()
        val aggregateIdsList = mutableListOf<Long>()

        // this function popolate the database and the lists passed with the generated objects
        val elem_tot = databaseTestHelper.generateAgregatesAndElements(
            aggregatesList = aggregatesList,
            listOfElementsLists = listOfElementsLists,
            aggregateIdsList = aggregateIdsList,
            aggregateTagsList = aggregateTagsList,
            elementTagsList = elementTagsList,
            aggregatesDao = aggregatesDao,
            aggr_num = 10,
            elem_num = 10
        )

        val result = aggregatesDao.getAllAggregatesWithElements()

        // check for result
        if (result == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result.size != aggregatesList.size) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result.size}, expected = ${aggregatesList.size}" )

        var idx: Int = 0

        for((aggregate, elements) in result){
            CustomAsserts.aggregates(aggregatesList[idx], aggregate)
            CustomAsserts.elementsList(listOfElementsLists[idx], elements)
            CustomAsserts.compareAggregateAndElements(aggregate, elements)
            idx++
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertCountAndDeleteAggregatesWithElements() = runBlocking{

        // lists
        val aggregatesList = mutableListOf<Aggregate>()
        val listOfElementsLists = mutableListOf<List<Element>>()
        val aggregateIdsList = mutableListOf<Long>()

        val agregateExpectedCount = 5L


        // this function popolate the database and the lists passed with the generated objects
        val tot_elem_num = databaseTestHelper.generateAgregatesAndElements(
            aggregatesList = aggregatesList,
            listOfElementsLists = listOfElementsLists,
            aggregateIdsList = aggregateIdsList,
            aggregateTagsList = aggregateTagsList,
            elementTagsList = elementTagsList,
            aggregatesDao = aggregatesDao,
            aggr_num = agregateExpectedCount,
            elem_num = 5
        )

        var aggregatesCount = aggregatesDao.countAllAggregates()
        var elementsCount = elementsDao.countAllElements()
        var aggregateTagsCount = tagsDao.getAggregateTagsCount()
        var elementTagsCount = tagsDao.getElementTagsCount()

        Log.d(tag, "Counts after insertion: ")
        Log.d(tag, "aggregtesCount: ${aggregatesCount}")
        Log.d(tag, "elementsCount: ${elementsCount}")
        Log.d(tag, "aggregte tags Count: ${aggregateTagsCount}")
        Log.d(tag, "element tags Count: ${elementTagsCount}")

        Assert.assertEquals(aggregatesCount, agregateExpectedCount)
        Assert.assertEquals(elementsCount, tot_elem_num)

        aggregatesDao.deleteAll()

        aggregatesCount = aggregatesDao.countAllAggregates()
        elementsCount = elementsDao.countAllElements()
        aggregateTagsCount = tagsDao.getAggregateTagsCount()
        elementTagsCount = tagsDao.getElementTagsCount()

        Log.d(tag, "Counts after deletion: ")
        Log.d(tag, "aggregtesCount: ${aggregatesCount}")
        Log.d(tag, "elementsCount: ${elementsCount}")
        Log.d(tag, "aggregte tags Count: ${aggregateTagsCount}")
        Log.d(tag, "element tags Count: ${elementTagsCount}")

        Assert.assertEquals(aggregatesCount, 0)
        Assert.assertEquals(elementsCount, 0)
        Assert.assertEquals(aggregateTagsCount, 0)
        Assert.assertEquals(elementTagsCount, 0)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteSingleElements() = runBlocking{

        // lists
        val aggregatesList = mutableListOf<Aggregate>()
        val listOfElementsLists = mutableListOf<List<Element>>()
        val aggregateIdsList = mutableListOf<Long>()

        val agregatesExpectedCount = 1L
        val elementsExpectedCount = 2L

        // this function popolate the database and the lists passed with the generated objects
        val tot_elem_num = databaseTestHelper.generateAgregatesAndElements(
            aggregatesList = aggregatesList,
            listOfElementsLists = listOfElementsLists,
            aggregateIdsList = aggregateIdsList,
            aggregateTagsList = aggregateTagsList,
            elementTagsList = elementTagsList,
            aggregatesDao = aggregatesDao,
            aggr_num = agregatesExpectedCount,
            elem_num = elementsExpectedCount
        )

        /////////////////////////////////////////////////////////////////////
        // CHECK IF THE NUMBER OF ELEMENETS AND AGGREGATES IS CORRECT AND DELETE ONE CASUAL
        // ELEMENT FROM EACH AGGREGATE.

        val result_1 = aggregatesDao.getAllAggregatesWithElements()
        var delettedElements = mutableListOf<Element>()

        // check for result
        if (result_1 == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result_1.size != aggregatesList.size) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result_1.size}, expected = ${aggregatesList.size}" )

        for((aggregate, elements) in result_1){
            if(elements.size.toLong() !=  elementsExpectedCount) Assert.fail("elements list has unexpected size = ${elements.size}, expected = ${elementsExpectedCount}")
            CustomAsserts.compareAggregateAndElements(aggregate, elements)

            // delete one casual element from each aggregates
            delettedElements.add(elements.random())
            elementsDao.deleteElement(delettedElements.last())
        }

        /////////////////////////////////////////////////////////////////////
        // CHECK IF THE ELEMENT IS CORECTLY REMOVED FROM THE AGGREGATE
        // AND ADD AGAIN IT TO THE AGGREGATE.

        val result_2 = aggregatesDao.getAllAggregatesWithElements()

        // check for result
        if (result_2 == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result_2.size != aggregatesList.size) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result_2.size}, expected = ${aggregatesList.size}" )

        // extracting aggregates from the first query
        val agregatesList_1 = result_1.keys.toList()
        var idx = 0

        for((aggregate, elements) in result_2){

            // check for the size of each list of elements
            if(elements.size.toLong() !=  (elementsExpectedCount-1))
                Assert.fail("elements list has unexpected size = ${elements.size}, expected = ${elementsExpectedCount}")

            // check if aggregate and elements are correctly related
            CustomAsserts.compareAggregateAndElements(aggregate, elements)

            // check if the cost difference between the old and the new aggregate is equal to the removed element
            if(abs((agregatesList_1[idx].total_cost - aggregate.total_cost) -
                        (delettedElements[idx].cost * delettedElements[idx].num)) > 0.001f)
                    Assert.fail("aggregate with idx = ${aggregate.id} has total_cost = ${aggregate.total_cost}, unexpectd, " +
                            "should be = ${agregatesList_1[idx].total_cost - (delettedElements[idx].cost * delettedElements[idx].num)}")

            // add again to the same aggregate the deletted element
            aggregatesDao.addElementToAggregate(delettedElements[idx], aggregate)
            idx++
        }

        /////////////////////////////////////////////////////////////////////
        // CHECK IF THE ELEMENTS ARE ADDED CORRECTLY

        val result_3 = aggregatesDao.getAllAggregatesWithElements()

        // check for result
        if (result_3 == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result_3.size != aggregatesList.size) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result_2.size}, expected = ${aggregatesList.size}" )

        idx = 0

        for((aggregate, elements) in result_3){

            // check if the size of each list of elements is came back to the original value
            if(elements.size.toLong() !=  elementsExpectedCount)
                Assert.fail("elements list has unexpected size = ${elements.size}, expected = ${elementsExpectedCount}")

            // check if aggregate and elements are correctly related
            CustomAsserts.compareAggregateAndElements(aggregate, elements)

            // check if the new agregates are come back to the original values
            CustomAsserts.aggregates(agregatesList_1[idx], aggregate)

            idx++
        }
    }
}
