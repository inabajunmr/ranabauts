package work.inabajun.ranabauts.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `Ping API returns status code specified query parameter`() {
        // 200
        val entity200 = restTemplate.getForEntity<String>("/ping?status=200")
        assertThat(entity200.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity200.body).contains("ok")

        // 503
        val entity503 = restTemplate.getForEntity<String>("/ping?status=503")
        assertThat(entity503.statusCode).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
        assertThat(entity503.body).contains("ok")
    }
}
