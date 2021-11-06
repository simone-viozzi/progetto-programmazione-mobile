package com.example.receiptApp.db.element

import androidx.room.*
import com.example.receiptApp.db.aggregate.BaseAggregatesDao
import com.example.receiptApp.db.tag.Tag
import com.example.receiptApp.db.tag.TagsDao

/**
 * Elements dao
 *
 * @constructor Create empty Elements dao
 */

@Dao
interface ElementsDao : BaseElementsDao, BaseAggregatesDao, TagsDao {

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert queries

    @Transaction
    suspend fun _insertElementWithTag(element: Element): Long{

        val resultTag = getElementTagByName(element.elem_tag)
        if(resultTag != null){
            element.elem_tag_id = resultTag.tag_id
        }else{
            val newTagId = _insertTag(Tag(tag_name = element.elem_tag, aggregate = false))
            element.elem_tag_id = newTagId
        }
        return _insertElement(element)
    }

    @Transaction
    suspend fun _insertElementsListWithTag(elementList: List<Element>): List<Long>{

        var resultList = mutableListOf<Long>()
        elementList.forEach {
            resultList.add(_insertElementWithTag(it))
        }
        return resultList
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update queries

    @Transaction
    suspend fun _updateElementTag(element: Element): Long?{

        // if the keys are null the results are null, jump the query process
        val new_tag = if(element.elem_tag == null) null else getElementTagByName(element.elem_tag)
        val old_tag = if(element.elem_tag_id == null) null else getElementTagById(element.elem_tag_id)
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
            if(element.elem_tag != null){
                // and the new tag name passed isn't null it will be created
                new_tag_id = _insertTag(Tag(tag_name = element.elem_tag, aggregate = false))
            }
        }

        // if the tag isn't the same delete the old one
        if(old_tag != null) {
            // if the old tag isn't null check if is bind only to this aggregate
            if (_countAllElementsByTagId(old_tag.tag_id) <= 1) {
                // se il tag ha solo un elemento connesso lo cancello
                // in tal caso però devo prima effettuare l'aggiornamento dell'elemento
                // per evitre che l' elem_tag_id punti ad un tag inesistente contraddicendo
                // la relazione della chiave.
                element.elem_tag_id = new_tag_id
                _updateElement(element)
                _deleteTag(old_tag)
            }
        }

        return new_tag_id
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // delete elements queries

    @Transaction
    suspend fun _deleteElementWithTag(element: Element): Int{

        var deleteTag = false
        var resultTag: Tag? = null

        if(element.elem_tag != null){
            resultTag = getElementTagByName(element.elem_tag!!)
            if(resultTag != null){
                if(_countAllElementsByTagId(resultTag.tag_id) <= 1){
                    // se il numero di elementi associti al tag collegato
                    // all'elemento da eliminare è 1 allora lo elimino
                    deleteTag = true
                }
            }
        }

        val deleteResult = _deleteElement(element)

        // tag should be deletted after element due to key relation
        if(deleteTag) resultTag?.let { _deleteTag(it) }

        return deleteResult
    }

    /**
     * delete elements list with tag
     *
     * Attenzione utilizzare questa funzione esclusivamente per l'eliminazione
     * di un aggregato e la sua intera lista di elementi, se devono esser
     * eliminati solo una parte degli elementi di un aggregato utilizzare @deleteElement(element: Element): Int
     *
     * @param elementsList
     */
    @Transaction
    suspend fun _deleteElementsListWithTag(elementsList: List<Element>){

        var tagIdsList = mutableListOf<Long?>()
        var tagToDelete = mutableListOf<Tag>()

        // scorre la lista di elementi da eliminare
        elementsList.forEach {
            tagIdsList.add(it.elem_tag_id) // raccoglie gli id dei tag negli elementi
        }

        // effettua il conteggio del numero di id uguali e restituisce un map<id, id_count>
        val tagIdsCountMap = tagIdsList.filterNotNull().groupingBy { it }.eachCount()

        if(tagIdsCountMap.isNotEmpty()) {
            // score i tag_id unici nella lista verifica
            tagIdsList.filterNotNull().distinct().forEach {
                val tagIdCount = tagIdsCountMap[it]
                if(tagIdCount != null) {
                    if (tagIdCount >= _countAllElementsByTagId(it)) {
                        // se tutti gli elementi con il tag che ha l'id sotto esame
                        // appartengono alla lista allra il tag viene cancellato
                        getElementTagById(it)?.let { it1 -> tagToDelete.add(it1) }
                    }
                }
            }
        }

        _deleteElementsList(elementsList)

        // tag should be deletted after element due to key relation
        _deleteTagsList(tagToDelete)
    }

    @Transaction
    suspend fun _deleteElementsWithTagByAggregateId(aggregateId: Long?){
        val elementsList = _getElementsByAggregateId(aggregateId)
        _deleteElementsListWithTag(elementsList)
    }

    @Query("DELETE FROM element")
    suspend fun _deleteAllElements(): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get count queries aggregates



    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get queries elements



}