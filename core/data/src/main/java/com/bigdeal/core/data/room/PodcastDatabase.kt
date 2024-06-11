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
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun podcastsDao(): PodcastsDao
    abstract fun episodesDao(): EpisodesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun podcastCategoryEntryDao(): PodcastCategoryEntryDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
    abstract fun podcastFollowedEntryDao(): PodcastFollowedEntryDao

    abstract fun feedDao(): FeedDao

    abstract fun episodeRecordDao(): EpisodeRecordDao
}
