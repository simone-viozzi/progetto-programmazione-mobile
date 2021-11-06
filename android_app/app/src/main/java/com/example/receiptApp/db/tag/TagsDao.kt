package com.example.receiptApp.db.tag

import androidx.room.*
import com.example.receiptApp.db.aggregate.Aggregate


@Dao
interface TagsDao {
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Insert tag queries

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertTag(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertTagList(tags: List<Tag>): List<Long>

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Update tag queries

    @Update
    suspend fun _updateTag(tag: Tag): Int

    @Update
    suspend fun _updateTagsList(tags: List<Tag>): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // delete tag queries

    @Delete
    suspend fun _deleteTag(tag: Tag)

    @Delete
    suspend fun _deleteTagsList(tags: List<Tag>)

    @Query("DELETE FROM tag WHERE tag.tag_id = :id")
    suspend fun _deleteTagById(id: Long)

    @Query("DELETE FROM tag")
    suspend fun _deleteAllTags()

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get tag count queries

    @Query("SELECT COUNT(*) FROM tag WHERE tag.aggregate = 1")
    suspend fun getAggregateTagsCount(): Int

    @Query("SELECT COUNT(*) FROM tag WHERE tag.aggregate = 0")
    suspend fun getElementTagsCount(): Int

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // Get tag queries

    @Query("SELECT * FROM tag WHERE tag.tag_id = :id AND tag.aggregate = 1 LIMIT 1")
    suspend fun getAggregateTagById(id: Long?): Tag?

    @Query("SELECT * FROM tag WHERE tag.tag_name = :name AND tag.aggregate = 1 LIMIT 1")
    suspend fun getAggregateTagByName(name: String?): Tag?

    @Query("SELECT * FROM tag WHERE tag.tag_id = :id AND tag.aggregate = 0 LIMIT 1")
    suspend fun getElementTagById(id: Long?): Tag?

    @Query("SELECT * FROM tag WHERE tag.tag_name = :name AND tag.aggregate = 0 LIMIT 1")
    suspend fun getElementTagByName(name: String?): Tag?

    @Query("SELECT * FROM tag WHERE tag.aggregate = 1")
    suspend fun getAggregateTags(): List<Tag>?

    @Query("SELECT * FROM tag WHERE tag.aggregate = 0")
    suspend fun getElementTags(): List<Tag>?
}