import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name="users")
data class User(
    @Id
    @GeneratedValue
     val id: String? = null,
     val username: String,
    @Column
     val email: String,
     val passwordHash: String,
    @OneToMany
     val chords: MutableList<Chord>? = null
)