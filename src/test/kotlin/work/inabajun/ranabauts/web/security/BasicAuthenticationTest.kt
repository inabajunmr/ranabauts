package work.inabajun.ranabauts.web.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.security.user.name = user", "spring.security.user.password=passwd"])
class BasicAuthenticationTest(@Autowired val template: TestRestTemplate) {

    @Test
    fun basicAuthenticationTest() {

        // 401
        val entity401 = template.withBasicAuth("user", "wrong").getForEntity<String>("/ping?status=200")
        assertThat(entity401.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)

        // 200
        val entity200 = template.withBasicAuth("user", "passwd").getForEntity<String>("/ping?status=200")
        assertThat(entity200.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity200.body).contains("ok")
    }
}
