package br.edu.ifpb.gps.farnel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return (request, response, authentication) -> {
			boolean isParceiro = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_PARCEIRO"));
			boolean isEntregador = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ENTREGADOR"));

			if (isParceiro) {
				response.sendRedirect(request.getContextPath() + "/parceiro");
			} else if (isEntregador) {
				response.sendRedirect(request.getContextPath() + "/entregas");
			} else {
				response.sendRedirect(request.getContextPath() + "/catalogo");
			}
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/catalogo/**",
					"/pratos/imagem/**",
					"/login",
					"/cadastro/**",
					"/css/**",
					"/js/**",
					"/img/**",
					"/favicon.ico"
				).permitAll()
				.requestMatchers("/parceiro/**").hasRole("PARCEIRO")
				.requestMatchers("/entregas/**").hasRole("ENTREGADOR")
				.requestMatchers("/minhas-marmitas/**", "/checkout/**", "/carrinho/**", "/perfil/**").hasRole("CONSUMIDOR")
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.successHandler(customAuthenticationSuccessHandler())
				.failureUrl("/login?error=true")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout=true")
				.permitAll()
			);

		return http.build();
	}

}
