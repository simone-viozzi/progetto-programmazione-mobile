package com.example.receiptApp

import android.location.Location
import android.net.Uri
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
import com.example.receiptApp.repository.DbRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors


@RunWith(AndroidJUnit4::class)
class DatabaseRepositoryTests {

    private lateinit var aggregatesDao: PublicAggregatesDao
    private lateinit var elementsDao: PublicElementsDao
    private lateinit var tagsDao: TagsDao
    private lateinit var db: AppDatabase
    private lateinit var dbRepository: DbRepository

    private var tag = "DB_TEST"

    val exAggregate = Aggregate(
        date = Date(),
        location = Location("").also { it.latitude = 10.0; it.longitude = 10.0 },
        attachment = Uri.parse("/test"),
    )

    val exEement = Element(
        name = "test element",
        num = 3,
        cost = 10.0f
    )

    val aggregateTagsList = listOf<String>("alimentari", "banca", "macchina", "bollette", "viaggi")
    val elementTagsList = listOf<String>(
        "colazione",
        "tagliando auto",
        "acqua",
        "luce",
        "biglietto aereo",
        "cornetto",
        "mouse",
        "bullone",
        "taralli"
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() = runBlocking {
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

        dbRepository = DbRepository(
            db.aggregateDao(),
            db.elementsDao(),
            db.tagsDao()
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getPeriodStartDateTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getPeriodExpensesSumTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getPeriodExpensesTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getAggregateTagsAndCountByPeriodTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getAggregateTagsAndExpensesByPeriodTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getElementTagsAndCountByPeriodTest() = runBlocking{

    }

    @Test
    @Throws(Exception::class)
    fun getElementTagsAndExpensesByPeriodTest() = runBlocking{

    }

}