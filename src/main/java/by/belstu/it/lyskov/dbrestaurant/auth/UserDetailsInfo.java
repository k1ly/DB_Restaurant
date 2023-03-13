package by.belstu.it.lyskov.dbrestaurant.auth;

import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsInfo implements UserDetails {

    private Long id;

    private String login;

    private String password;

    private String name;

    private String email;

    private String phone;

    private boolean blocked;

    private Role role;

    private Order order;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    public UserDetailsInfo(Long id, String login, String password,
                           String name, String email, String phone,
                           boolean blocked, Role role, Order order) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.blocked = blocked;
        this.order = order;
        this.role = role;
        accountNonExpired = true;
        accountNonLocked = true;
        credentialsNonExpired = true;
        enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
