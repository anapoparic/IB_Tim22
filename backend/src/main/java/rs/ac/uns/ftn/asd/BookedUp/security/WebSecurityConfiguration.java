package rs.ac.uns.ftn.asd.BookedUp.security;


import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.GROUPS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {
    @Autowired
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/register*").permitAll()
                .requestMatchers("/api/register/**").permitAll()
                .requestMatchers("/api/logout").permitAll()
                .requestMatchers("/api/accommodations/search-filter").permitAll()
                .requestMatchers("//api/accommodations/mostPopular").permitAll()
                .requestMatchers("/api/notifications/*").permitAll()
                .requestMatchers("/api/guests/*").permitAll()
                .requestMatchers("/api/accommodations/*").permitAll()
                .requestMatchers("/api/hosts/*").permitAll()
                .requestMatchers("/api/reservations/*").permitAll()
                .requestMatchers("/api/reservations/host/**").permitAll()
                .requestMatchers("/api/reservations/host/*").permitAll()
                .requestMatchers("/api/reservations/host/**").permitAll()
                .requestMatchers("/api/reservations/host/***").permitAll()
                .requestMatchers("/api/reviews/*").permitAll()
                .requestMatchers("/api/reviews/**").permitAll()
                .requestMatchers("/api/reviews/***").permitAll()
                .requestMatchers("/api/users/reported-users").permitAll()
                .requestMatchers("/api/users/*").permitAll()
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/api/user-reports/*").permitAll()
                .requestMatchers("/api/reviews/***").permitAll()
                .requestMatchers("/socket/*").permitAll()
                .requestMatchers("/api/certificate/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                        "/**.html", "/**.css", "/**.js", "/**.png", "/**.jpg", "/**.jpeg", "/images/**").anonymous()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated());

//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        http.oauth2ResourceServer((oauth2) -> oauth2.jwt(token -> token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));
//        http.oauth2Login(Customizer.withDefaults()).logout((logout) -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/3"));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}