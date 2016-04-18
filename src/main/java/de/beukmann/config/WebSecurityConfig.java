package de.beukmann.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import de.beukmann.security.CsrfHeaderFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ldap.user:}")
	private String LDAP_USER;
	


	@Value("${ldap.url:}")
	private String URL;
	
	@Value("${ldap.password:}")
	private String LDAP_PASSWORD;

	@Value("${ldap.url:}")
	private String LDAP_URL;

	@Value("${ldap.userbase:}")
	private String LDAP_USERBASE;

	@Value("${ldap.userfilter:}")
	private String LDAP_USERFILTER;

	@Value("${ldap.groupbase:}")
	private String LDAP_GROUPBASE;

	@Value("${ldap.groupfilter:}")
	private String LDAP_GROUPFILTER;
	
	@Value("${ldap.roleprefix:")
	private String LDAP_ROLE_PREFIX;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().fullyAuthenticated().and().httpBasic().and().csrf()
				.csrfTokenRepository(csrfTokenRepository()).and()
				.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);

	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		if (LDAP_URL == null || "".equals(LDAP_URL)){
			auth.inMemoryAuthentication().withUser("user").password("password").roles("USER", "ADMIN")
			                       .and().withUser("stefan").password("geheim").roles("USER");
		} else  {
			DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(LDAP_URL);
			contextSource.setUserDn(LDAP_USER);
			contextSource.setPassword(LDAP_PASSWORD);
			contextSource.setReferral("follow");
			contextSource.afterPropertiesSet();
	
			LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthenticationProviderConfigurer = auth
					.ldapAuthentication();
			ldapAuthenticationProviderConfigurer.userSearchFilter(LDAP_USERFILTER).userSearchBase(LDAP_USERBASE)
					.groupSearchBase(LDAP_GROUPBASE).groupSearchFilter(LDAP_GROUPFILTER).contextSource(contextSource);
		}
	}

}