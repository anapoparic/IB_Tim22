package rs.ac.uns.ftn.asd.BookedUp.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        return new JwtAuthenticationToken(
                source,
                Stream.concat(
                                new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                                extractResourceRoles(source).stream())
                        .collect(toSet()));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        var resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess instanceof Map) {
            var resourceAccessMap = (Map<String, Object>) resourceAccess;

            if (resourceAccessMap.containsKey("backend")) {
                var backendRolesMap = (Map<String, Object>) resourceAccessMap.get("backend");
                var roles = (List<String>) backendRolesMap.get("roles");

                if (roles != null) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                            .collect(Collectors.toSet());
                }
            }
        }

        return Collections.emptySet();
    }


}