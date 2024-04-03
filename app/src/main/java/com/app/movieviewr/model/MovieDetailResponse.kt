import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieDetailResponse(
    @field:SerializedName("adult")
    val adult: Boolean? = null,

    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @field:SerializedName("belongs_to_collection")
    val belongsToCollection: BelongsToCollection? = null,

    @field:SerializedName("budget")
    val budget: Int? = null,

    @field:SerializedName("genres")
    val genres: List<Genre>? = null,

    @field:SerializedName("homepage")
    val homepage: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("imdb_id")
    val imdbId: String? = null,

    @field:SerializedName("original_language")
    val originalLanguage: String? = null,

    @field:SerializedName("original_title")
    val originalTitle: String? = null,

    @field:SerializedName("overview")
    val overview: String? = null,

    @field:SerializedName("popularity")
    val popularity: Double? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,

    @field:SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>? = null,

    @field:SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>? = null,

    @field:SerializedName("release_date")
    val releaseDate: String? = null,

    @field:SerializedName("revenue")
    val revenue: Long? = null,

    @field:SerializedName("runtime")
    val runtime: Int? = null,

    @field:SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("tagline")
    val tagline: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("video")
    val video: Boolean? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null
) : Parcelable

@Parcelize
data class BelongsToCollection(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,

    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null
) : Parcelable

@Parcelize
data class Genre(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    var isChecked: Boolean = false
) : Parcelable

@Parcelize
data class ProductionCompany(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("logo_path")
    val logoPath: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("origin_country")
    val originCountry: String? = null
) : Parcelable

@Parcelize
data class ProductionCountry(
    @field:SerializedName("iso_3166_1")
    val iso31661: String? = null,

    @field:SerializedName("name")
    val name: String? = null
) : Parcelable

@Parcelize
data class SpokenLanguage(
    @field:SerializedName("english_name")
    val englishName: String? = null,

    @field:SerializedName("iso_639_1")
    val iso6391: String? = null,

    @field:SerializedName("name")
    val name: String? = null
) : Parcelable
