package Manager_vnd.Manager.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.user.User;
import Manager_vnd.Manager.feature.user.UserRepository;

@Component
public class ActorResolver {

    private final UserRepository userRepository;

    public ActorResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return userRepository.findByEmail(jwt.getSubject())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", jwt.getSubject()));
        }
        throw new ResourceNotFoundException("User", "session", "current");
    }
}
