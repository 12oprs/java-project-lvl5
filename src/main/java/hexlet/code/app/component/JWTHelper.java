package hexlet.code.app.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;

import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import io.jsonwebtoken.SignatureAlgorithm;

@Component
public final class JWTHelper {

    private final String secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTHelper(@Value("${jwt.issuer:spring_blog}") final String issuer,
                     @Value("${jwt.expiration-sec:86400}") final Long expirationSec,
                     @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSec,
                     @Value("${jwt.secret:secret}") final String secret) {
        this.issuer = issuer;
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.clock = DefaultClock.INSTANCE;

        final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.secretKey = Encoders.BASE64.encode(key.getEncoded());

    }

    public String expiring(final Map<String, Object> attributes) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), HS256)
                .setClaims(getClaims(attributes, expirationSec))
                .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setClock(clock)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getClaims(final Map<String, Object> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);
        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * 1000));
        }
        return claims;
    }

}
