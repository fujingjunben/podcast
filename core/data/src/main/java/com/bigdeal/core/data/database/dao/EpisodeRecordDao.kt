package com.bigdeal.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bigdeal.core.data.EpisodeStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EpisodeRecordDao : BaseDao<EpisodeStateEntity> {


    @Query(
        """
            SELECT * FROM episodes_state_record WHERE episode_uri = :uri
        """
    )
    abstract fun queryRecordByUri(uri: String): Flow<EpisodeStateEntity>
}