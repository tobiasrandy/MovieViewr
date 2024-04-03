import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieGenresResponse(
    @field:SerializedName("genres")
    val genres: MutableList<Genre>? = null
) : Parcelable
