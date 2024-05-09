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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bigdeal.core.data.Category
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStateEntity
import com.bigdeal.core.data.FeedEntity
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastCategoryEntry
import com.bigdeal.core.data.PodcastFollowedEntry
import com.bigdeal.core.data.database.dao.EpisodeRecordDao

/**
 * The [RoomDatabase] we use in this app.
 */
@Database(
    entities = [
        Podcast::class,
        EpisodeStateEntity::class,
        EpisodeEntity::class,
        PodcastCategoryEntry::class,
        Category::class,
        PodcastFollowedEntry::class,
        FeedEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class, PlayStateTypeConverters::class)
abstract class JetcasterDatabase : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
    abstract fun episodesDao(): EpisodesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao

    abstract fun feedDao(): FeedDao

    abstract fun episodeRecordDao(): EpisodeRecordDao
}
