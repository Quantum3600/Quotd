package com.trishit.quotd.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.trishit.quotd.data.QuoteApi
import com.trishit.quotd.data.QuoteRepository
import com.trishit.quotd.data.UserPreferencesRepository
import com.trishit.quotd.data.local.CachedQuoteDao
import com.trishit.quotd.data.local.FavouriteDao
import com.trishit.quotd.data.local.QuoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://zenquotes.io/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideQuoteApi(retrofit: Retrofit): QuoteApi =
        retrofit.create(QuoteApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuoteDatabase =
        Room.databaseBuilder(context, QuoteDatabase::class.java, "quotes.db")
            .fallbackToDestructiveMigration(false) // Handle version changes by recreating the database
            .build()

    @Provides
    fun provideFavouriteDao(db: QuoteDatabase): FavouriteDao = db.favouriteDao()

    @Provides
    fun provideCachedQuoteDao(db: QuoteDatabase): CachedQuoteDao = db.cachedQuoteDao()

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return UserPreferencesRepository(dataStore)
    }
    
    @Provides
    @Singleton
    fun provideQuoteRepository(
        api: QuoteApi,
        favouriteDao: FavouriteDao,
        cachedQuoteDao: CachedQuoteDao,
        userPreferencesRepository: UserPreferencesRepository
    ): QuoteRepository = QuoteRepository(api, favouriteDao, cachedQuoteDao, userPreferencesRepository)
}