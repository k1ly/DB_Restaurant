package by.belstu.it.lyskov.dbrestaurant.auth;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import by.belstu.it.lyskov.dbrestaurant.entity.User;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.RoleNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.repository.RoleRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserDetails userDetails;
        try {
            if (login.length() == 0) {
                Role role = roleRepository.findByName("GUEST").orElseThrow(() -> new RoleNotFoundException("Role with name \"GUEST\" doesn't exist!"));
                userDetails = new UserDetailsInfo(0L, login, "", "", null, null, false, role, null);
            } else {
                User user = userRepository.findByLogin(login)
                        .orElseThrow(() -> new UsernameNotFoundException("User with login \"" + login + "\" doesn't exist!"));
                Role role = roleRepository.findById(user.getRole().getId())
                        .orElseThrow(() -> new RoleNotFoundException("Role with id \"" + user.getRole().getId() + "\" doesn't exist!"));
                userDetails = new UserDetailsInfo(user.getId(), user.getLogin(), user.getPassword(),
                        user.getName(), user.getEmail(), user.getPhone(),
                        user.isBlocked(), role, user.getOrder());
            }
        } catch (RoleNotFoundException | RepositoryException e) {
            throw new UsernameNotFoundException("Failed to load user by username", e);
        }
        return userDetails;
    }
}
