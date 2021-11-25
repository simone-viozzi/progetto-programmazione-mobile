package com.example.receiptApp

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.receiptApp.Utils.DatabaseTestHelper
import com.example.receiptApp.db.AppDatabase
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.PublicAggregateDao
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

@RunWith(AndroidJUnit4::class)
class DatabaseTests
{

    private lateinit var aggregatesDao: PublicAggregateDao
    private lateinit var elementsDao: PublicElementsDao
    private lateinit var tagsDao: TagsDao
    private lateinit var db: AppDatabase

    private var tag = "DB_TEST"

    val exAggregate = Aggregate(
        date = Date(),
        location = Location("").also{it.latitude=10.0; it.longitude=10.0},
        attachment = Uri.parse("/test"),
    )

    val exEement = Element(
        name = "test element",
        num = 3,
        cost = 10.0f
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
        val elem_tot = DatabaseTestHelper.generateAgregatesAndElements(
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
        val tot_elem_num = DatabaseTestHelper.generateAgregatesAndElements(
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

        Assert.assertEquals(aggregatesCount, 0L)
        Assert.assertEquals(elementsCount, 0L)
        Assert.assertEquals(aggregateTagsCount, 0L)
        Assert.assertEquals(elementTagsCount, 0L)
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
        val tot_elem_num = DatabaseTestHelper.generateAgregatesAndElements(
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

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateAggregatesAndElements() = runBlocking{

        val new_aggr = Aggregate(
            date = Date(1000),
            attachment = Uri.parse("/test_1")
        )

        new_aggr.tag = "test_tag_1"

        val new_elem_list = listOf<Element>(
            Element(
                name = "test element 1",
                num = 1,
                cost = 10.0f
            ),
            Element(
                name = "test element 2",
                num = 2,
                cost = 10.0f
            ),
            Element(
                name = "test element 3",
                num = 3,
                cost = 10.0f
            )
        )

        var idx = 0
        new_elem_list.forEach {
            it.elem_tag = elementTagsList[idx]
            idx++
        }

        aggregatesDao.insertAggregateWithElements(new_aggr, new_elem_list)

        // getting back all the values
        val result = aggregatesDao.getAllAggregatesWithElements()

        // check for result
        if (result == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result.size != 1) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result.size}, expected = ${1}" )

        // check if all the values are correct
        result.forEach{
            if(it.value.size !=  3) Assert.fail("elements list has unexpected size = ${it.value.size}, expected = ${3}")
            CustomAsserts.compareAggregateAndElements(it.key, it.value)
            CustomAsserts.assertEqualFloats(
                it.key.total_cost,
                60.0f
            )
        }

        result.forEach{
            // update aggregate
            aggregatesDao.updateAggregate(
                it.key,
                tag_name = "updated_tag",
                date = Date(100_000),
                location = Location("").also{
                    it.latitude=45.0
                    it.longitude=45.0
                },
                attachment = Uri.parse("/test_2")
            )

            //update elements list
            it.value.forEach{ elem ->
                elementsDao.updateElement(
                    elem,
                    name = "updated_elem_" + elem.elem_id,
                    num = elem.num + 1,
                    cost = elem.cost * 2.0f,
                    elem_tag = "updated_elem_tag_" + elem.elem_id
                )
            }
        }

        // getting back all the values
        val result_2 = aggregatesDao.getAllAggregatesWithElements()

        // check for result
        if (result_2 == null) Assert.fail("getAllAggregatesWithElements result is null")
        if (result_2.size != 1) Assert.fail("getAllAggregatesWithElements result has unexpected size = ${result_2.size}, expected = ${1}" )

        // check if it happened correctly
        result_2.forEach{
            CustomAsserts.compareAggregateAndElements(it.key, it.value)

            // total cost must be checked manualy bea
            CustomAsserts.assertEqualFloats(
                it.key.total_cost,
                180.0f
            )
            Assert.assertEquals("updated_tag", it.key.tag)
            it.value.forEach { elem ->
                Assert.assertEquals("updated_elem_tag_" + elem.elem_id, elem.elem_tag)
            }

            CustomAsserts.aggregates(it.key, result.keys.first())
            CustomAsserts.elementsList(it.value, result.values.first())

            /*
            Assert.assertEquals(it.key.tag, "updated_tag")
            Assert.assertEquals(it.key.date, Date(100_000))
            Assert.assertEquals(it.key.location, Location("").also{ loc -> loc.latitude=45.0; loc.longitude=45.0 })
            Assert.assertEquals(it.key.attachment, Uri.parse("/test_2"))
            */
        }

    }
}
