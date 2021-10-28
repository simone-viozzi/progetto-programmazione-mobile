package com.example.receiptApp.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.example.receiptApp.db.aggregate.Aggregate
import com.example.receiptApp.db.element.Element


data class AggregateWithElements(
    @Embedded val aggregate: Aggregate,
    @Relation(
        parentColumn = "id",
        entityColumn = "aggregate_id"
    )
    val elements: List<Element>
)
