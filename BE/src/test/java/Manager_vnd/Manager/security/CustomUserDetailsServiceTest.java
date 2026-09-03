package Manager_vnd.Manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("loadUserByUsername trả về UserDetails từ DB")
    void loadUserByUsername_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@local.dev");
        user.setPassword("$2a$hash");
        user.setActive(true);
        when(userRepository.findByEmail("admin@local.dev")).thenReturn(Optional.of(user));

        var details = customUserDetailsService.loadUserByUsername("admin@local.dev");

        assertEquals("admin@local.dev", details.getUsername());
        assertTrue(details instanceof CustomUserDetails);
        assertEquals(1L, ((CustomUserDetails) details).getUser().getId());
    }

    @Test
    @DisplayName("Email không tồn tại")
    void loadUserByUsername_notFound() {
        when(userRepository.findByEmail("x@y.z")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("x@y.z"));
    }
}
