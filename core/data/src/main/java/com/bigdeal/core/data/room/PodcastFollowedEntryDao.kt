/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
