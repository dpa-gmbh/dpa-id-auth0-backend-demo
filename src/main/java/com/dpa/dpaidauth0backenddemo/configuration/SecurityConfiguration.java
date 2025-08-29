package com.dpa.dpaidauth0backenddemo.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
  @Value("${spring.security.oauth2.client.provider.dpaid-devel.issuer-uri}")
  private String issuerUri;

  @Value("${spring.security.oauth2.audience}")
  private String audience;

  @Value("${spring.security.oauth2.secondAudience}")
  private String secondAudience;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(authorize ->
            authorize.requestMatchers("/v3/**", "/swagger-ui/**","/actuator/health","/error","/authorization/v1/rwa/code/validate").permitAll()
                .anyRequest().authenticated())
        .oauth2ResourceServer((oauth2) -> oauth2.authenticationManagerResolver(JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers(issuerUri)))
        .httpBasic(Customizer.withDefaults())
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        // SECURITY HEADERS
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                    .httpStrictTransportSecurity(sec -> sec.maxAgeInSeconds(31536000)
                            .preload(true)
                            .includeSubDomains(true))
                    .contentTypeOptions(Customizer.withDefaults())
                    .referrerPolicy(policy -> policy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .addHeaderWriter(new StaticHeadersWriter("X-XSS-Protection", "0")))
        .build();

  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean(name = "dpaIdDecoder")
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
    OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience,secondAudience);
    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

    jwtDecoder.setJwtValidator(withAudience);
    return jwtDecoder;
  }

  public static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String audience;
    private final String secondAudience;

      public AudienceValidator(String audience, String secondAudience) {
          this.audience = audience;
          this.secondAudience = secondAudience;
      }

    OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);
      public OAuth2TokenValidatorResult validate(Jwt jwt) {
       if (jwt.getAudience().contains(this.audience) || jwt.getAudience().contains(this.secondAudience)) {
          return OAuth2TokenValidatorResult.success();
        } else {
          return OAuth2TokenValidatorResult.failure(error);
        }
    }
  }
}
