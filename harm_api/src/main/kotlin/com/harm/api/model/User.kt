@Entity
@Table(name=users)
data class User(
    @Id
    @GeneratedValue
    val id: String,
    val username: String,
    @Column
    val email: String,
    val passwordHash: String
    @OneToMany
    val chords: Chord
)