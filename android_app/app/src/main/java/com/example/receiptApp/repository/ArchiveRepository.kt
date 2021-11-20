package com.example.receiptApp.repository

import com.example.receiptApp.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.receiptApp.pages.archive.ArchiveDataModel
import java.text.SimpleDateFormat
import java.util.*


class ArchiveRepository(
    private val applicationContext: Context,
    private val dbRepository: DbRepository,
    private val attachmentRepository: AttachmentRepository
) {

    // ##########################################################################
    // GET METHODS

    suspend fun getAggregates(
        tag_name: String? = null,
        start: Date = Date(0), // by default take 1-1-1970 as start date as filter
        end: Date = Date() // by default take the call moment as end date as filter
    ): List<ArchiveDataModel.Aggregate>{

        // get aggregates from db
        val dbAggregateList = dbRepository.getAggregates(tag_name, start, end)

        // convert each aggregate in a list suitable for the archive adapter
        var archiveAggregateList = mutableListOf<ArchiveDataModel.Aggregate>()


        var idx = 0
        val format = SimpleDateFormat("dd/MM/yyyy") // output date format

        dbAggregateList?.forEach {
            archiveAggregateList.add(
                ArchiveDataModel.Aggregate(
                    id = idx++,
                    aggr_id= it.id!!,
                    tag = it.tag,
                    str_date = format.format(it.date),
                    thumbnail = it.attachment,
                    tot_cost = it.total_cost
                )
            )
        }

        return archiveAggregateList
    }

}