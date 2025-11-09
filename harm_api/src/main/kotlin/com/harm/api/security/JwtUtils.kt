import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtils(
    @Value("\${jwt.secret}") private var jwtSecret: String,
    @Value("\${jwt.expiration}") private val jwtExpirationMs: Int,
    private var key: SecretKey
) {
    @PostConstruct
    fun init() {
        this .key = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
    }
    fun generateToken(username: String): String? {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date((Date()).time + jwtExpirationMs))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (_: MalformedJwtException) {
            return false
        } catch (_: ExpiredJwtException) {
            return false
        } catch (_: UnsupportedJwtException) {
            return false
        } catch (_: IllegalArgumentException) {
           return false
        }
    }
}