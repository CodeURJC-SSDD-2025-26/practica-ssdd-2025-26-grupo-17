package es.dawgroup17.nexgym.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Un detalle importante en tus logs:
    // He visto esta advertencia (Warning) que deberías tener en cuenta para el
    // futuro:

    // Global AuthenticationManager configured with an AuthenticationProvider bean.
    // UserDetailsService beans will not be used by Spring Security...

    // Esto indica que has definido un AuthenticationProvider personalizado, y
    // Spring te avisa de que ignorará cualquier UserDetailsService que tengas
    // suelto por ahí. Si el login te funciona, no te preocupes; si no te deja
    // entrar, es que esa parte de la seguridad necesita un ajuste.

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/loginerror").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/payment").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/class/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/classes/**").permitAll()

                        // PRIVATE PAGES
                        .requestMatchers("/profile").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/session/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .requestMatchers("/newClass").hasAnyRole("ADMIN"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}