package com.example.receiptApp

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
import kotlin.math.pow

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

class databaseTestHelper {
    companion object{
        suspend fun generateAgregatesAndElements(
            aggregatesList: MutableList<Aggregate>,
            listOfElementsLists: MutableList<List<Element>>,
            aggregateIdsList: MutableList<Long>,
            aggregateTagsList: List<String>,
            elementTagsList: List<String>,
            aggregatesDao: PublicAggregatesDao,
            aggr_num: Long = 10,
            elem_num: Long = 10,
            elem_num_casual: Boolean = false
        ): Long{

            var elements_num = 0L

            // objects generation
            for(i in 0..(aggr_num-1)){
                // agregate generation
                val new_aggr = Aggregate(
                    date = Date(i*1000),
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
    }
}

