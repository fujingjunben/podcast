package com.example.jetcaster.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetcaster.data.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedDao {

    @Query(
        """
            SELECT * FROM feed_entries
        """
    )
    abstract fun queryAll(): Flow<List<FeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(feedEntity: FeedEntity)

    @Delete
    abstract suspend fun delete(feedEntity: FeedEntity)
}