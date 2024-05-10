package com.bigdeal.core.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bigdeal.core.data.FeedEntity
import com.bigdeal.core.data.database.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedDao : BaseDao<FeedEntity> {

    @Query(
        """
            SELECT * FROM feed_entries
        """
    )
    abstract fun queryAll(): Flow<List<FeedEntity>>
}