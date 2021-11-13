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
import com.example.receiptApp.repository.DbRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
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

    private var tag = "DB_REPO_TEST"

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
    fun createDb(): Unit = runBlocking {

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

        val aggregatesList = mutableListOf<Aggregate>()
        val listOfElementsLists = mutableListOf<List<Element>>()
        val aggregateIdsList = mutableListOf<Long>()

        databaseTestHelper.generatePredictableAgregatesAndElements(
            aggregatesList = aggregatesList,
            listOfElementsLists = listOfElementsLists,
            aggregateIdsList = aggregateIdsList,
            aggregateTagsList = aggregateTagsList,
            elementTagsList = elementTagsList,
            aggregatesDao = aggregatesDao,
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getPeriodStartDateTest(): Unit = runBlocking{

        val dayStart = dbRepository.getPeriodStartDate(DbRepository.Period.DAY)
        val firstDayOfWeek = dbRepository.getPeriodStartDate(DbRepository.Period.WEEK)
        val firstDayOfMonth = dbRepository.getPeriodStartDate(DbRepository.Period.MONTH)
        val firstDayOfYear = dbRepository.getPeriodStartDate(DbRepository.Period.YEAR)

        Log.d(tag, dayStart.toString())
        Log.d(tag, firstDayOfWeek.toString())
        Log.d(tag, firstDayOfMonth.toString())
        Log.d(tag, firstDayOfYear.toString())
    }

    @Test
    @Throws(Exception::class)
    fun getSubPeriodsDatesTest(): Unit = runBlocking{

        val monthSubs = dbRepository.getSubPeriodsDates(DbRepository.Period.MONTH)
        val yearSubs = dbRepository.getSubPeriodsDates(DbRepository.Period.YEAR)

        var monthSubsStr: String = " Sottointervalli del periodo MONTH:"
        monthSubs.forEach {
            monthSubsStr += "\n start: " + it[0].toString() + " end: " + it[1].toString()
        }

        var yearSubsStr: String = " Sottointervalli del periodo YEAR:"
        yearSubs.forEach {
            yearSubsStr += "\n start: " + it[0].toString() + "  end: " + it[1].toString()
        }

        Log.d(tag, monthSubsStr)
        Log.d(tag, yearSubsStr)

    }

    @Test
    @Throws(Exception::class)
    fun getPeriodExpensesSumTest(): Unit = runBlocking{

        val totalExpenses = dbRepository.getExpenses()

        Log.d(tag, "total expenses: $totalExpenses")
        Assert.assertEquals(1000.0f, totalExpenses)
    }

    @Test
    @Throws(Exception::class)
    fun getPeriodExpensesTest(): Unit = runBlocking{

        Log.d(tag, "getPeriodExpensesSum(WEEK) result: " + dbRepository.getPeriodExpensesSum(DbRepository.Period.WEEK).toString())
        Log.d(tag, "getPeriodExpensesSum(MONTH) result: " + dbRepository.getPeriodExpensesSum(DbRepository.Period.MONTH).toString())
        Log.d(tag, "getPeriodExpensesSum(YEAR) result: " + dbRepository.getPeriodExpensesSum(DbRepository.Period.YEAR).toString())

        val monthExpenses = dbRepository.getPeriodExpenses(DbRepository.Period.MONTH)
        val yearExpenses = dbRepository.getPeriodExpenses(DbRepository.Period.YEAR)

        var idx = 1
        var stringResult = "getPeriodExpenses(MONTH) result: "
        for(dayExpense in monthExpenses) stringResult += "\nday " + (idx++).toString() + ": " + dayExpense.toString()
        Log.d(tag, stringResult)

        idx = 1
        stringResult = "getPeriodExpenses(YEAR) result:"
        for(monthExpense in yearExpenses) stringResult += "\n month " + (idx++).toString() + ": " + monthExpense.toString()
        Log.d(tag, stringResult)
    }

    @Test
    @Throws(Exception::class)
    fun getAggregateTagsAndCountByPeriodTest(): Unit = runBlocking{

        val aTagsAndCountsMonth = dbRepository.getAggregateTagsAndCountByPeriod(DbRepository.Period.MONTH)
        val aTagsAndCountsYear = dbRepository.getAggregateTagsAndCountByPeriod(DbRepository.Period.YEAR)

        var aTagsAndCountsMonthStr = "count of each aggregate divided by tag in this month: "
        aTagsAndCountsMonth.forEach{

            if(it.key == null) Assert.fail("getAggregateTagsAndCountByPeriodTest(): MONTH check: key shouldn't be null")
            if(it.value == null) Assert.fail("getAggregateTagsAndCountByPeriodTest(): MONTH check: value shouldn't be null")

            aTagsAndCountsMonthStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getAggregateCountByTagAndPeriod(it1, DbRepository.Period.MONTH) }
            )
        }

        var aTagsAndCountsYearStr = "count of each aggregate divided by tag in this year: "
        aTagsAndCountsYear.forEach{

            if(it.key == null) Assert.fail("getAggregateTagsAndCountByPeriodTest(): YEAR check: key shouldn't be null")
            if(it.value == null) Assert.fail("getAggregateTagsAndCountByPeriodTest(): YEAR check: value shouldn't be null")
            
            aTagsAndCountsYearStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getAggregateCountByTagAndPeriod(it1, DbRepository.Period.YEAR) }
            )
        }

        Log.d(tag, aTagsAndCountsMonthStr)
        Log.d(tag, aTagsAndCountsYearStr)
    }

    @Test
    @Throws(Exception::class)
    fun getAggregateTagsAndExpensesByPeriodTest(): Unit = runBlocking{

        val aTagsAndExpensesMonth = dbRepository.getAggregateTagsAndExpensesByPeriod(DbRepository.Period.MONTH)
        val aTagsAndExpensesYear = dbRepository.getAggregateTagsAndExpensesByPeriod(DbRepository.Period.YEAR)

        var aTagsAndExpensesMonthStr = "sum of all expenses of all aggregate divided by tag in this month: "
        aTagsAndExpensesMonth.forEach{

            if(it.key == null) Assert.fail("getAggregateTagsAndExpensesByPeriodTest(): MONTH check: key shouldn't be null")
            if(it.value == null) Assert.fail("getAggregateTagsAndExpensesByPeriodTest(): MONTH check: value shouldn't be null")

            aTagsAndExpensesMonthStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getAggregateExpensesByTagAndPeriod(it1, DbRepository.Period.MONTH) }
            )
        }

        var aTagsAndExpensesYearStr = "sum of all expenses of all aggregate divided by tag in this year: "
        aTagsAndExpensesYear.forEach{

            if(it.key == null) Assert.fail("getAggregateTagsAndExpensesByPeriodTest(): YEAR check: key shouldn't be null")
            if(it.value == null) Assert.fail("getAggregateTagsAndExpensesByPeriodTest(): YEAR check: value shouldn't be null")

            aTagsAndExpensesYearStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getAggregateExpensesByTagAndPeriod(it1, DbRepository.Period.YEAR) }
            )
        }

        Log.d(tag, aTagsAndExpensesMonthStr)
        Log.d(tag, aTagsAndExpensesYearStr)

    }

    @Test
    @Throws(Exception::class)
    fun getElementTagsAndCountByPeriodTest(): Unit = runBlocking{

        val eTagsAndCountsMonth = dbRepository.getElementTagsAndCountByPeriod(DbRepository.Period.MONTH)
        val eTagsAndCountsYear = dbRepository.getElementTagsAndCountByPeriod(DbRepository.Period.YEAR)

        var eTagsAndCountsMonthStr = "count of each single element divided by tag in this month: "
        eTagsAndCountsMonth.forEach{

            if(it.key == null) Assert.fail("getElementTagsAndCountByPeriodTest(): MONTH check: key shouldn't be null")
            if(it.value == null) Assert.fail("getElementTagsAndCountByPeriodTest(): MONTH check: value shouldn't be null")

            eTagsAndCountsMonthStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getElementCountByTagAndPeriod(it1, DbRepository.Period.MONTH) }
            )
        }

        var eTagsAndCountsYearStr = "count of each single element divided by tag in this year: "
        eTagsAndCountsYear.forEach{

            if(it.key == null) Assert.fail("getElementTagsAndCountByPeriodTest(): YEAR check: key shouldn't be null")
            if(it.value == null) Assert.fail("getElementTagsAndCountByPeriodTest(): YEAR check: value shouldn't be null")

            eTagsAndCountsYearStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getElementCountByTagAndPeriod(it1, DbRepository.Period.YEAR) }
            )
        }

        Log.d(tag, eTagsAndCountsMonthStr)
        Log.d(tag, eTagsAndCountsYearStr)

    }

    @Test
    @Throws(Exception::class)
    fun getElementTagsAndExpensesByPeriodTest(): Unit = runBlocking{

        val eTagsAndExpensesMonth = dbRepository.getElementTagsAndExpensesByPeriod(DbRepository.Period.MONTH)
        val eTagsAndExpensesYear = dbRepository.getElementTagsAndExpensesByPeriod(DbRepository.Period.YEAR)

        var eTagsAndExpensesMonthStr = "sum of all expenses of all single elements divided by tag in this month: "
        eTagsAndExpensesMonth.forEach{

            if(it.key == null) Assert.fail("getElementTagsAndExpensesByPeriodTest(): MONTH check: key shouldn't be null")
            if(it.value == null) Assert.fail("getElementTagsAndExpensesByPeriodTest(): MONTH check: value shouldn't be null")

            eTagsAndExpensesMonthStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getElementExpensesByTagAndPeriod(it1, DbRepository.Period.MONTH) }
            )
        }

        var eTagsAndExpensesYearStr = "sum of all expenses of all single elements divided by tag in this year: "
        eTagsAndExpensesYear.forEach{

            if(it.key == null) Assert.fail("getElementTagsAndExpensesByPeriodTest(): YEAR check: key shouldn't be null")
            if(it.value == null) Assert.fail("getElementTagsAndExpensesByPeriodTest(): YEAR check: value shouldn't be null")

            eTagsAndExpensesYearStr += "\n " + it.key + ": " + it.value.toString()
            Assert.assertEquals(
                it.value,
                it.key?.let { it1 -> dbRepository.getElementExpensesByTagAndPeriod(it1, DbRepository.Period.YEAR) }
            )
        }

        Log.d(tag, eTagsAndExpensesMonthStr)
        Log.d(tag, eTagsAndExpensesYearStr)

    }
}