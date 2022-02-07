package uz.napa.clinic.service.iml;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.repository.UserRepository;
import uz.napa.clinic.security.CustomUserDetails;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> byUsername = userRepository.findByPhoneNumber(s);
        return byUsername.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found "));
    }

    public UserDetails loadUserByUserId(UUID id) {
        Optional<User> userOptional = userRepository.findByIdAndDeletedFalse(id);
        return userOptional.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User id no validate"));
    }
//    @Override
//    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        return null;
//    }
//    public UserDetails loadUserByUserId(UUID id) {
//        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User id no validate"));
//    }
}
