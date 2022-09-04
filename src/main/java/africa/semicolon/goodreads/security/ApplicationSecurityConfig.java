package africa.semicolon.goodreads.security;

import africa.semicolon.goodreads.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class ApplicationSecurityConfig{
    private final UnAuthorizedEntryPoint  unAuthorizedEntryPoint;

    public ApplicationSecurityConfig(UnAuthorizedEntryPoint unAuthorizedEntryPoint) {
        this.unAuthorizedEntryPoint = unAuthorizedEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(authorize -> {
                    try {
                        authorize.antMatchers("/**/auth/**").permitAll()
                                .antMatchers("/customError").permitAll()
                                .antMatchers("/access-denied").permitAll()

                                .anyRequest().authenticated()
                                .and()
                                .exceptionHandling().authenticationEntryPoint(unAuthorizedEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler())
                                .and()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        http.addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter( exceptionHandlerFilterBean(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilterBean(){
        return new JwtAuthenticationFilter();
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilterBean(){
        return new ExceptionHandlerFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
