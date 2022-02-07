package uz.napa.clinic;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import uz.napa.clinic.entity.User;
import uz.napa.clinic.security.CustomUserDetails;
import uz.napa.clinic.security.JwtTokenProvider;
import uz.napa.clinic.service.iml.CustomUserDetailsService;

import java.security.Principal;
import java.util.UUID;

@SpringBootApplication
public class ClinicApplication {
//    @Value("${rt-server.host}")
//    private String host;
//
//    @Value("${rt-server.port}")
//    private Integer port;
//
//    @Autowired
//    JwtTokenProvider tokenProvider;
//
//    @Autowired
//    CustomUserDetailsService userDetailsService;
//
//    @Bean
//    public SocketIOServer socketIOServer() {
//        Configuration config = new Configuration();
//        config.setHostname(host);
//        config.setPort(port);
//        config.setAuthorizationListener(data -> {
//            String token = data.getSingleUrlParam("token");
//            System.out.println(token);
//            System.out.println(data.getAddress());
//            System.out.println(data.getHttpHeaders());
//            System.out.println(data.getLocal());
//            System.out.println(data.getTime());
//            System.out.println(data.getUrl());
//            System.out.println(data.getUrlParams());
//            if (StringUtils.hasText(token) && token.substring(0, 5).equals("Tusiq")) {
//                token= token.substring(6);
//            }
//            String id = tokenProvider.getUserIdFromJWT(token);
//            UserDetails userDetails = userDetailsService.loadUserByUserId(UUID.fromString(id));
////            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println(userDetails.getAuthorities());
//            return true;
//        });
//        config.setAllowCustomRequests(true);
//        return new SocketIOServer(config);
//    }

    public static void main(String[] args) {
        SpringApplication.run(ClinicApplication.class, args);
    }
}
