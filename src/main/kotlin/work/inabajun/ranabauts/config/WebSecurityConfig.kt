package work.inabajun.ranabauts.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(val env: Environment) : WebSecurityConfigurerAdapter() {

    // for basic auth
    override fun configure(http: HttpSecurity) {
        http.csrf().disable() // only api
        if (env.containsProperty("spring.security.user.name")) {
            http.httpBasic()
            http.authorizeRequests()
                    .anyRequest().authenticated()
        }
    }
}
