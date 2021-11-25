package com.example.receiptApp.db.aggregate

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.receiptApp.db.element.Element
import com.example.receiptApp.db.element.ElementsDao
import com.example.receiptApp.db.tag.Tag
import com.example.receiptApp.db.tag.TagsDao
import java.util.*

/**
 * Aggregates dao
 *
 * Clear explanation of relation between Entity and how to use it
 * link: https://www.tutorialguruji.com/android/how-to-insert-entities-with-a-one-to-many-relationship-in-room/
 *
 *  Utilization policy:
 *  - The aggregates must be ever inserted with their list of elements
 *  - The deletion of one aggregate should be ever done with the managed method
 *
 *  NOTE: private methods seems can't be created so methods with this
 *        form "_methodName()" shouldn't be touched, otherway it isn't
 *        granted the correct behaviour of the db.
 *
 * @constructor Create empty Aggregates dao
 */

@Dao
interface AggregatesDao : BaseAggregatesDao, ElementsDao, TagsDao {

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    @Transaction
    suspend fun _insertAggregateWithTag(aggregate: Aggregate): Long{

        val resultTag = getAggregateTagByName(aggregate.tag)
        if(resultTag != null){
            // se il tag specificato per l'aggregato è diverso da null allora è già contenuto nel db
            aggregate.tag_id = resultTag.tag_id

        }else{
            // se il tag specificato per l'aggregato non è presente nel db lo aggiungo
            val newTagId = _insertTag(Tag(tag_name = aggregate.tag, aggregate = true))
            aggregate.tag_id = newTagId
        }
        // in fine aggiungo l'aggregato
        return _insertAggregate(aggregate)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    /**
     * _update aggregate tag
     *
     * Private function used only inside updateAggregate()
     * this function require as argument an instance of Aggregate that
     * contain the field tag and tag_id not null, with values that dosen't match
     * so the method apply the procedure for detaching the old tag refered by the tag_id field
     * and attach or create the new tag specified by the tag field of the Aggregate instance
     *
     * @param aggregate an aggregate already present inside the database
     * @return the id of the new tag for the aggregate passed as argument
     */
    @Transaction
    suspend fun _updateAggregateTag(aggregate: Aggregate): Long?{

        // if the keys are null the results are null, jump the query process
        val new_tag = if(aggregate.tag == null) null else getAggregateTagByName(aggregate.tag)
        val old_tag = if(aggregate.tag_id == null) null else getAggregateTagById(aggregate.tag_id)
        var new_tag_id: Long? = null

        // verify that the tags aren't the same
        if (new_tag != null &&
            old_tag != null &&
            new_tag.tag_id == old_tag.tag_id
            ) return old_tag.tag_id

        // verify if the new tag should be created
        if (new_tag != null) {
            // if new tag isn't null the tag already exist
            new_tag_id = new_tag.tag_id
        } else {
            // if new tag is null
            if(aggregate.tag != null){
                // and the new tag name passed isn't null it will be created
                new_tag_id = _insertTag(Tag(tag_name = aggregate.tag, aggregate = true))
            }
        }

        // if the tag isn't the same delete the old one
        if(old_tag != null) {
            // if the old tag isn't null check if is bind only to this aggregate
            if (_countAllAggregatesByTagId(old_tag.tag_id) <= 1) {
                // se il tag ha solo un aggregato connesso lo cancello
                // in tal caso però devo prima effettuare l'aggiornamento dell'aggregato
                // per evitre che il tag_id punti ad un tag inesistente contraddicendo
                // la relazione della chiave.
                aggregate.tag_id = new_tag_id
                _updateAggregate(aggregate)
                _deleteTag(old_tag)
            }
        }

        return new_tag_id
    }

    @Transaction
    suspend fun _updateAggregateTotalCostById(aggregateId: Long, totalCost: Float){

        var aggregate = _getAggregateById(aggregateId)
        aggregate.total_cost = totalCost
        _updateAggregate(aggregate)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Delete queries

    // delete aggregates with elements queries

    @Transaction
    suspend fun _deleteAggregateWithTag(aggregate: Aggregate){

        if(aggregate.tag != null) {
            val resultTag = getAggregateTagByName(aggregate.tag!!)
            if (resultTag != null) {
                // se il tag specificato per l'aggregato è diverso da null
                // allora verifico se è collegato solo a questo agregato
                if (_countAllAggregatesByTagId(resultTag.tag_id) <= 1) {
                    // se il tag ha solo un aggregato connesso lo cancello
                    _deleteTag(resultTag)
                }
            }
        }
        _deleteAggregate(aggregate)
    }

    @Transaction
    suspend fun _deleteAgregateWithTagById(aggregate_id: Long){
        val aggregate = _getAggregateById(aggregate_id)
        _deleteAggregateWithTag(aggregate)
    }

    @Transaction
    suspend fun _deleteAggregateWithElements(aggregate: Aggregate) {
        _deleteElementsWithTagByAggregateId(aggregate.id)
        _deleteAggregateWithTag(aggregate)
    }

    @Transaction
    suspend fun _deleteAggregateWithElementsById(id: Long) {
        _deleteElementsWithTagByAggregateId(id)
        _deleteAgregateWithTagById(id)
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates

    @Query("SELECT COUNT(*) FROM aggregate WHERE aggregate.tag_id = :tag_id")
    suspend fun _countAllAggregatesByTagId(tag_id: Long?): Long

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries aggregates with elements

    @Transaction
    suspend fun _getLastAggregateWithElements(): Map<Aggregate, List<Element>> {
        val lastAggregate: Aggregate = _getLastAggregate()
        val elementsList: List<Element> = _getElementsByAggregateId(lastAggregate.id)
        return mapOf(lastAggregate to elementsList)
    }

    @Transaction
    suspend fun _getAggregateWithElementsById(id: Long): Map<Aggregate, List<Element>> {
        val aggregate: Aggregate = _getAggregateById(id)
        val elementsList: List<Element> = _getElementsByAggregateId(aggregate.id)
        return mapOf(aggregate to elementsList)
    }

    @Transaction
    suspend fun _getAggregateWithElementsByDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = _getAggregatesByDate(date) ?: listOf()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = _getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun _getAllAggregatesWithElements(): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate>? = _getAllAggregates()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        if(aggregatesList != null) {
            for (aggregate in aggregatesList) {
                val listOfElements = _getElementsByAggregateId(aggregate.id)
                resultMap[aggregate] = listOfElements
            }
        }
        return resultMap
    }

    @Transaction
    suspend fun _getAggregateWithElementsUntilDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = _getAggregatesByDate(date) ?: listOf()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = _getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun _getAggregateWithElementsAfterDate(date: Date): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = _getAggregatesAfterDate(date) ?: listOf()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = _getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun _getAggregateWithElementsBetweenDate(
        start_date: Date,
        end_date: Date
    ): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = _getAggregatesBetweenDate(start_date, end_date) ?: listOf()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = _getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    @Transaction
    suspend fun _getAggregateWithElementsByTag(tag_id: Long): Map<Aggregate, List<Element>> {
        val aggregatesList: List<Aggregate> = _getAggregatesByTag(tag_id) ?: listOf()
        val resultMap = mutableMapOf<Aggregate, List<Element>>()
        for (aggregate in aggregatesList) {
            val listOfElements = _getElementsByAggregateId(aggregate.id)
            resultMap[aggregate] = listOfElements
        }
        return resultMap
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // query helpers

    @Transaction
    suspend fun _addTagNameToAggregatesList(listWithoutTags: List<Aggregate>): List<Aggregate>{
        var resultWithTags = mutableListOf<Aggregate>()
        listWithoutTags.forEach {
            resultWithTags.add(_addTagNameToAggregate(it))
        }
        return resultWithTags
    }

    @Transaction
    suspend fun _addTagNameToAggregatesListWithElements(mapWithoutTags: Map<Aggregate, List<Element>>): Map<Aggregate, List<Element>>{
        var resultWithTags = mutableMapOf<Aggregate, List<Element>>()
        for((key, value) in mapWithoutTags){
            val aggregateWithTags = _addTagNameToAggregate(key)
            val elementsListWithTags = _addTagNameToElementsList(value, aggregateWithTags.tag)
            resultWithTags[aggregateWithTags] = elementsListWithTags
        }
        return resultWithTags
    }

}