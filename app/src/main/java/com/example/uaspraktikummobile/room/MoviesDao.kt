package com.example.uaspraktikummobile.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(movies: List<MoviesRoom>)

    @Update
    fun updateMovies(movies: MoviesRoom)

    @Query("SELECT * FROM movies WHERE id = :moviesId")
    fun getMoviesById(moviesId: String): LiveData<MoviesRoom>


    @get:Query("SELECT * FROM movies ORDER BY id ASC")
    val getAllMovies: LiveData<List<MoviesRoom>>

    @Query("DELETE FROM Movies")
    fun deleteAllMovies()
}