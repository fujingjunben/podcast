package com.example.jetcaster.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jetcaster.data.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: DownloadEntity)

    @Delete
    abstract suspend fun delete(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: DownloadEntity)

    @Query("""
        SELECT * FROM DownloadEntities WHERE id = :id
        """)
    abstract fun queryWithId(id: Long): Flow<DownloadEntity>

    @Query("""
        SELECT * FROM DownloadEntities WHERE uri = :uri
    """)
    abstract fun queryWithEpisodeUri(uri: String): Flow<DownloadEntity>
}