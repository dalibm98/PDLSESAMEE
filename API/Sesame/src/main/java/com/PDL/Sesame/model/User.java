package com.PDL.Sesame.model;

import com.PDL.Sesame.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.ToString.Exclude;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    private String status ;

    private String description;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "destinataire")
    private List<Notification> notifications;
 @JsonIgnore
    @OneToMany(mappedBy = "auteur", fetch = FetchType.EAGER)
    private List<Question> questions;
    @JsonIgnore
    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Reponse> reponses;

    public byte[] getImage() {
        // return the byte array for the user's image
        return null;
    }
}
