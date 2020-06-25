package work.inabajun.ranabauts.web.command

import java.net.URI
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CommandControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun testCommand() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com/p",
               "commands":[
                    {
                        "type":"HTTP",
                        "uri":"http://example.com/c",
                        "commands":[],
                        "response":{
                            "status":200
                        }
                    }
                ],
               "response":{
                   "status":201
               }
            }
        """

        // exercise
        val actual = restTemplate.exchange(RequestEntity.post(URI("/command"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(input), String::class.java)

        // verify
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CREATED)
    }
}
