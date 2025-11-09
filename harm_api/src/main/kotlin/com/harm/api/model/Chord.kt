import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "chords")
data class Chord(
    @Id
    val id: String,
    val title: String,
    val rawCifraText: String,
    val processedCifraJson: String,
    val styleName: String,
    val key: Char,
    val bpm: Int,
    val timeSignature: String,
    @field:ManyToOne
    val user: User
)