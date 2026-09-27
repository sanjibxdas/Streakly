package com.streakly.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.streakly.BuildConfig
import com.streakly.data.local.StreaklyDatabase
import com.streakly.data.local.dao.AgentTaskDao
import com.streakly.data.local.dao.CachedHealthDataDao
import com.streakly.data.local.dao.ChecklistItemDao
import com.streakly.data.local.dao.GoalDao
import com.streakly.data.local.dao.HabitDao
import com.streakly.data.local.dao.HabitLogDao
import com.streakly.data.local.dao.JournalEntryDao
import com.streakly.data.local.dao.StreakCacheDao
import com.streakly.data.local.dao.WaterLogDao
import com.streakly.data.remote.nvidia.NvidiaNimClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StreaklyDatabase {
        return Room.databaseBuilder(
            context,
            StreaklyDatabase::class.java,
            StreaklyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWaterLogDao(db: StreaklyDatabase): WaterLogDao = db.waterLogDao()

    @Provides
    fun provideJournalEntryDao(db: StreaklyDatabase): JournalEntryDao = db.journalEntryDao()

    @Provides
    fun provideHabitDao(db: StreaklyDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideHabitLogDao(db: StreaklyDatabase): HabitLogDao = db.habitLogDao()

    @Provides
    fun provideStreakCacheDao(db: StreaklyDatabase): StreakCacheDao = db.streakCacheDao()

    @Provides
    fun provideCachedHealthDataDao(db: StreaklyDatabase): CachedHealthDataDao = db.cachedHealthDataDao()

    @Provides
    fun provideAgentTaskDao(db: StreaklyDatabase): AgentTaskDao = db.agentTaskDao()

    @Provides
    fun provideGoalDao(db: StreaklyDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideChecklistItemDao(db: StreaklyDatabase): ChecklistItemDao = db.checklistItemDao()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideNvidiaNimClient(okHttpClient: OkHttpClient, moshi: Moshi): NvidiaNimClient {
        return Retrofit.Builder()
            .baseUrl(NvidiaNimClient.DEFAULT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NvidiaNimClient::class.java)
    }
}
