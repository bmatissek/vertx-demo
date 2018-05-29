package vertxcoroutines

import kotlinx.coroutines.experimental.delay

object MovieService {

    private val movieList : MutableList<Movie> by lazy {
        mutableListOf(
            Movie(0, "The Shawshank Redemption (1994)"),
            Movie(1, "The Godfather (1972)"),
            Movie(2, "The Godfather: Part II (1974)"),
            Movie(3, "The Dark Knight (2008)"),
            Movie(4, "12 Angry Men (1957)"),
            Movie(5, "Schindler's List (1993)"),
            Movie(6, "The Lord of the Rings: The Return of the King (2003)"),
            Movie(7, "Pulp Fiction (1994)"),
            Movie(8, "The Good, the Bad and the Ugly (1966)"),
            Movie(9, "Fight Club (1999)")
        )
    }

    suspend fun getAllMovies() : MutableList<Movie> {
        delay(2000)
        return movieList
    }

    suspend fun getMovieById(id : Int) : Movie? {
        delay(1000)
        return movieList.getOrNull(id)
    }

    suspend fun createMovie(name : String) : MovieService {
        delay (1500)
        movieList.add(
            Movie(movieList.size, name)
        )
        return this
    }
}