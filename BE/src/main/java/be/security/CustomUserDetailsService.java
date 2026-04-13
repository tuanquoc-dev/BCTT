package be.security;

import be.entity.User;
import be.enums.UserStatus;
import be.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new DisabledException("Account is blocked");
        }

        // 🔥 force load permissions
        user.getRole().getPermissions().size();

        var authorities = user.getRole().getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getCode()))
                .collect(Collectors.toSet());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                true,
                authorities
        );
    }
}