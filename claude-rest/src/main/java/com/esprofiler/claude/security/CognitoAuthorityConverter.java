package com.esprofiler.claude.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CognitoAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Extract the groups from the JWT claims
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");

        // Convert groups to GrantedAuthorities, prefixing with CGROUP_
        Stream<GrantedAuthority> groupAuthorities = groups != null ? groups.stream()
                .map(group -> new SimpleGrantedAuthority("CGROUP_" + group)) : Stream.empty();

        // Extract the scopes from the JWT claims. Scopes are a space-separated string.
        String scopesString = jwt.getClaimAsString("scope");
        List<String> scopes = scopesString != null ? List.of(scopesString.split(" ")) : List.of();

        // Convert scopes to GrantedAuthorities, prefixing with SCOPE_
        Stream<GrantedAuthority> scopeAuthorities = scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope));

        // Combine group and scope authorities into a single list
        return Stream.concat(groupAuthorities, scopeAuthorities).collect(Collectors.toList());
    }
}

