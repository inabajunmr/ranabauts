package work.inabajun.ranabauts.web.command

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.Collections
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import work.inabajun.ranabauts.domain.command.Command
import work.inabajun.ranabauts.domain.command.CommandType
import work.inabajun.ranabauts.domain.command.HTTPCommand

internal class CommandDeserializerTest {

    private val sut = getMapperWithCommandDeserializer()

    private fun getMapperWithCommandDeserializer(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Command::class.java, CommandDeserializer())
        mapper.registerModule(module)
        return mapper
    }

    @Test
    fun deserialize_HTTPCommandWithEmptyCommands() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com",
               "commands":[],
               "response": {
                   "status":200
               }
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, HttpStatus.OK, Collections.emptyList(), "http://example.com")
    }

    @Test
    fun deserialize_HTTPCommandWithSingleCommands() {
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
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, HttpStatus.CREATED, null, "http://example.com/p")
        assertHTTPCommand(actual.commands[0], HttpStatus.OK, Collections.emptyList(), "http://example.com/c")
        assertThat(actual.commands.size).isEqualTo(1)
    }

    @Test
    fun deserialize_HTTPCommandWithMultipleCommands() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com/p1",
               "commands":[
                    {
                        "type":"HTTP",
                        "uri":"http://example.com/c1",
                        "commands":[],
                        "response":{
                            "status":200
                        }
                    },
                    {
                        "type":"HTTP",
                        "uri":"http://example.com/c2",
                        "commands":[],
                        "response":{
                            "status":201
                        }
                    }
                ],
               "response":{
                   "status":202
               }
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, HttpStatus.ACCEPTED, null, "http://example.com/p1")
        assertHTTPCommand(actual.commands[0], HttpStatus.OK, Collections.emptyList(), "http://example.com/c1")
        assertHTTPCommand(actual.commands[1], HttpStatus.CREATED, Collections.emptyList(), "http://example.com/c2")
        assertThat(actual.commands.size).isEqualTo(2)
    }

    @Test
    fun deserialize_HTTPCommandWithNestedCommands() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com/p",
               "commands":[
                    {
                        "type":"HTTP",
                        "uri":"http://example.com/c",
                        "commands":[
                            {
                                "type":"HTTP",
                                "uri":"http://example.com/cc",
                                "commands":[],
                                "response":{
                                    "status":202
                                }
                            }                            
                        ],
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
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, HttpStatus.CREATED, null, "http://example.com/p")
        assertHTTPCommand(actual.commands[0], HttpStatus.OK, null, "http://example.com/c")
        assertHTTPCommand(actual.commands[0].commands[0], HttpStatus.ACCEPTED, Collections.emptyList(), "http://example.com/cc")
        assertThat(actual.commands.size).isEqualTo(1)
        assertThat(actual.commands[0].commands.size).isEqualTo(1)
    }

    @Test
    fun deserialize_HTTPCommandWithNullCommands() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com",
               "response": {
                   "status":200
               }
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, HttpStatus.OK, Collections.emptyList(), "http://example.com")
    }

    @Test
    fun deserialize_IllegalTypeCommand() {
        // setup
        val input = """
            {
               "type":"UNKNOWN",
               "uri":"http://example.com",
               "response": {
                   "status":200
               }
            }
        """

        // exercise

        val actual = catchThrowable { sut.readValue<Command>(input) }

        // verify
        assertThat(actual).isInstanceOf(IllegalCommandException::class.java)
    }

    @Test
    fun deserialize_IllegalResponse() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com",
               "response": {
                   "status":1000
               }
            }
        """

        // exercise
        val actual = catchThrowable { sut.readValue<Command>(input) }

        // verify
        assertThat(actual).isInstanceOf(IllegalCommandException::class.java)
    }

    private fun assertHTTPCommand(actual: Command, status: HttpStatus, commands: List<Command>?, uri: String) {
        // verify
        assertThat(actual.response.status).isEqualTo(status)
        assertThat(actual.type).isEqualTo(CommandType.HTTP)
        if (commands != null) {
            assertThat(actual.commands).isEqualTo(commands)
        }

        if (actual is HTTPCommand) {
            assertThat(actual.uri.toString()).isEqualTo(uri)
        } else {
            fail("actual is not HTTPCommand.")
        }
    }
}
