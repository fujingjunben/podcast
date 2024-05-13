

package com.bigdeal.core.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bigdeal.core.data.PodcastFollowedEntry
import com.bigdeal.core.data.database.dao.BaseDao

@Dao
abstract class PodcastFollowedEntryDao : BaseDao<PodcastFollowedEntry> {
    @Query("DELETE FROM podcast_followed_entries WHERE podcast_id = :podcastId")
    abstract suspend fun deleteWithPodcastId(podcastId: String)

    @Query("SELECT COUNT(*) FROM podcast_followed_entries WHERE podcast_id = :podcastId")
    protected abstract suspend fun podcastFollowRowCount(podcastId: String): Int

    suspend fun isPodcastFollowed(podcastId: String): Boolean {
        return podcastFollowRowCount(podcastId) > 0
    }
}
