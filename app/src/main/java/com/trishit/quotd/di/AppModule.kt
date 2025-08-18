package com.trishit.quotd.di

import android.content.Context
import androidx.room.Room
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
import com.trishit.quotd.data.QuoteApi
import com.trishit.quotd.data.QuoteRepository
import com.trishit.quotd.data.local.FavouriteDao
import com.trishit.quotd.data.local.QuoteDatabase

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
        Room.databaseBuilder(context, QuoteDatabase::class.java, "quotes.db").build()

    @Provides
    fun provideFavouriteDao(db: QuoteDatabase): FavouriteDao = db.favouriteDao()

    @Provides
    @Singleton
    fun provideQuoteRepository(api: QuoteApi, dao: FavouriteDao): QuoteRepository =
        QuoteRepository(api, dao)
}