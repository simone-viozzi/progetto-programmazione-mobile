package com.example.receiptApp.Utils

import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.aggregate.PublicAggregatesDao
import com.example.receiptApp.db.element.Element
import java.util.*

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Gets the value of a [LiveData] or waits for it to have one, with a timeout.
 *
 * Use this extension from host-side (JVM) tests. It's recommended to use it alongside
 * `InstantTaskExecutorRule` or a similar mechanism to execute tasks synchronously.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {

    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

object DatabaseTestHelper {


    /**
     * Generate agregates and elements
     *
     * @param aggregatesList
     * @param listOfElementsLists
     * @param aggregateIdsList
     * @param aggregateTagsList
     * @param elementTagsList
     * @param aggregatesDao
     * @param aggr_num
     * @param elem_num
     * @param elem_num_casual if true each aggregate can has a random number of elements between 1 and elem_num otherwise each aggregate will has elem_num elements
     * @param elem_fixed_num if
     * @param elem_fixed_cost
     * @param date_fixed_increment
     * @return
     */
    suspend fun generateAgregatesAndElements(
        aggregatesList: MutableList<Aggregate>,
        listOfElementsLists: MutableList<List<Element>>,
        aggregateIdsList: MutableList<Long>,
        aggregateTagsList: List<String>,
        elementTagsList: List<String>,
        aggregatesDao: PublicAggregatesDao,
        aggr_num: Long = 10,
        elem_num: Long = 10,
        elem_num_casual: Boolean = false,
    ): Long{

        var elements_num = 0L
        var today = Calendar.getInstance()

        // objects generation
        for(i in 0..(aggr_num-1)){

            today.add(Calendar.DAY_OF_YEAR, -1)

            // agregate generation
            val new_aggr = Aggregate(
                date = today.time,
                location = Location("").also{
                    it.latitude=(-900..900).random().toDouble()/10.0
                    it.longitude=(-1800..1800).random().toDouble()/10.0
                },
                attachment = Uri.parse("/test/" + (0..1000).random().toString()),
            )
            // tag isn't in the constructor must be added later
            new_aggr.tag = aggregateTagsList.random()

            val new_elem_list = mutableListOf<Element>()

            // chose casual or not iterator
            val elem_iter = if(elem_num_casual) (0..(1 until elem_num).random()) else (0 until elem_num)

            // elements list generation
            for(j in elem_iter){
                var new_elem = Element(
                    name = "elem_" + i.toString() + "_" + j.toString(),
                    num = (1..5).random().toLong(),
                    cost = (1..100).random().toFloat()/10.0f
                )
                new_elem.elem_tag = elementTagsList.random()
                new_elem_list.add(new_elem)
                elements_num++ // increment counter foreach new element
            }

            // add each generated element to a list
            aggregatesList.add(new_aggr)
            listOfElementsLists.add(new_elem_list)

            // add each new aggregate id inside this list for later comparison
            aggregateIdsList.add(aggregatesDao.insertAggregateWithElements(new_aggr, new_elem_list))
        }
        return elements_num
    }

    /**
     * Generate predictable agregates and elements
     *
     * This function fill the database with instances of agregates and elements, disaccordingly with
     * the last one this method make the database content predictable based on the passed parameters.
     *
     * NOTES:
     *  - default total expenses: 1000.0f
     *  - each aggregates is placed in a different day, from now, backward
     *  - every month should have nearly 300.0f total expenses
     *  - tags are placed iteratively
     *
     * @param aggregatesList
     * @param listOfElementsLists
     * @param aggregateIdsList
     * @param aggregateTagsList
     * @param elementTagsList
     * @param aggregatesDao
     * @param aggr_num
     * @param elem_num
     * @param elem_cost cost of each singleelement
     * @param elem_sub_num number of repetitions of each element
     * @return
     */
    suspend fun generatePredictableAgregatesAndElements(
        aggregatesList: MutableList<Aggregate>,
        listOfElementsLists: MutableList<List<Element>>,
        aggregateIdsList: MutableList<Long>,
        aggregateTagsList: List<String>,
        elementTagsList: List<String>,
        aggregatesDao: PublicAggregatesDao,
        aggr_num: Long = 10,
        elem_num: Long = 10,
        elem_cost: Float = 10.0f,
        elem_sub_num: Long = 1
    ): Long{

        var elements_num = 0L
        var aTagIdx = 0
        var eTagIdx = 0
        var today = Calendar.getInstance()

        // objects generation
        for(i in 0..(aggr_num-1)){

            // generate a new day foreach new aggregate
            today.add(Calendar.DAY_OF_YEAR, -1)

            // agregate generation
            val new_aggr = Aggregate(
                date = today.time,
                location = Location("").also{
                    it.latitude=(-900..900).random().toDouble()/10.0
                    it.longitude=(-1800..1800).random().toDouble()/10.0
                },
                attachment = Uri.parse("/test/" + (0..1000).random().toString()),
            )
            // tag isn't in the constructor must be added later
            new_aggr.tag = aggregateTagsList[aTagIdx++]
            aTagIdx = if(aTagIdx > aggregateTagsList.lastIndex) 0 else aTagIdx // if the idx is greater of the list size reset it

            val new_elem_list = mutableListOf<Element>()

            // elements list generation
            for(j in (0 until elem_num)){
                var new_elem = Element(
                    name = "elem_" + i.toString() + "_" + j.toString(),
                    num = elem_sub_num,
                    cost = elem_cost
                )

                new_elem.elem_tag = elementTagsList[eTagIdx++]
                eTagIdx = if(eTagIdx > elementTagsList.lastIndex) 0 else eTagIdx // if the idx is greater of the list size reset it
                new_elem_list.add(new_elem)
                elements_num++ // increment counter foreach new element
            }

            // add each generated element to a list
            aggregatesList.add(new_aggr)
            listOfElementsLists.add(new_elem_list)

            // add each new aggregate id inside this list for later comparison
            aggregateIdsList.add(aggregatesDao.insertAggregateWithElements(new_aggr, new_elem_list))
        }
        return elements_num
    }
}

