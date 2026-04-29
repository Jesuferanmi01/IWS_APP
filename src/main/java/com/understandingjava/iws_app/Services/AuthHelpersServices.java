package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Execeptions.CustomException;
import com.understandingjava.iws_app.Models.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.understandingjava.iws_app.Models.Users.UserType.MERCHANT;

@Service
public class AuthHelpersServices {


      @Value("${app.jwt.secret}")
      private String jwtSecret;

      @Value("${app.jwt.expiration}")
      private long jwtExpiration;

      private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


      public  String enCodePassword(String password) {
            try{
                  return passwordEncoder.encode(password);
            }
            catch (Exception ex){
                  throw new CustomException.SomethingWentWrongException("Password encoding failed. Please try again.", ex);
            }
      }

      public  boolean passwordMatch(String password, String encodedPassword){
            return passwordEncoder.matches(password,encodedPassword);
      }


      public  String generateUserCode(Users.UserType userType , Integer totalCount){
            //var totalCount = userRepository.countAllUsers();
            String usercode;
            if(userType == Users.UserType.USER){
                  usercode = "USER-"+totalCount;
            }else if (userType == MERCHANT){
                  usercode = "MECT-"+ totalCount;
            }
            else throw new CustomException.ValidationException("invalid Usertype");
            return  usercode;
      }

      private Key signingKey() {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
      }

      public String generateToken(Users user) {
            //log.debug("[AUTH HELPER] Generating token for userCode={}", user.getUserCode());
            try {
                  Map<String, Object> claims = new HashMap<>();
                  claims.put("name",     user.getFirstName() + " " + user.getLastName());
                  claims.put("role",     user.getUserType());
                  claims.put("userCode", user.getUserCode());
                  claims.put("email",    user.getEmail());

                  return Jwts.builder()
                          .setClaims(claims)
                          .setSubject(user.getUserCode())
                          .setIssuedAt(new Date())
                          .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                          .signWith(signingKey(), SignatureAlgorithm.HS256)
                          .compact();

            } catch (Exception ex) {
                  //log.error("[AUTH HELPER] Token generation failed for userCode={}", user.getUserCode(), ex);
                  throw new CustomException.SomethingWentWrongException("Token generation failed. Please try again.", ex);
            }
      }

      public String extractUserCode(String token) {
            return extractClaims(token).getSubject();
      }

      public Claims extractClaims(String token) {
            return Jwts.parser()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
      }

      public boolean isTokenExpired(String token) {
            return extractClaims(token).getExpiration().before(new Date());
      }
//    public String generatetoken(Users user)

//    public Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }

      public String extractSubject(String token) {
            return extractClaims(token).getSubject();
      }
}
