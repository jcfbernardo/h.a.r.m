@Entity
@Table(name = "chords")
data class Chord(
    val id: String,
    val title: String,
    val rawCifraText: String,
    val processedCifraJson: String,
    val styleName: String,
    val key: Char,
    val bpm: Int,
    val timeSignature: String,
    @ManyToOne
    val user: User
)