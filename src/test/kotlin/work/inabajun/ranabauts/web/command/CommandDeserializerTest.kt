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
               "commands":[]
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, Collections.emptyList(), "http://example.com")
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
                        "commands":[]
                    }
                ]
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, null, "http://example.com/p")
        assertHTTPCommand(actual.commands[0], Collections.emptyList(), "http://example.com/c")
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
                        "commands":[]
                    },
                    {
                        "type":"HTTP",
                        "uri":"http://example.com/c2",
                        "commands":[]
                    }
                ]
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, null, "http://example.com/p1")
        assertHTTPCommand(actual.commands[0], Collections.emptyList(), "http://example.com/c1")
        assertHTTPCommand(actual.commands[1], Collections.emptyList(), "http://example.com/c2")
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
                                "commands":[]
                            }                            
                        ]
                    }
                ]
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, null, "http://example.com/p")
        assertHTTPCommand(actual.commands[0], null, "http://example.com/c")
        assertHTTPCommand(actual.commands[0].commands[0], Collections.emptyList(), "http://example.com/cc")
        assertThat(actual.commands.size).isEqualTo(1)
        assertThat(actual.commands[0].commands.size).isEqualTo(1)
    }

    @Test
    fun deserialize_HTTPCommandWithNullCommands() {
        // setup
        val input = """
            {
               "type":"HTTP",
               "uri":"http://example.com"
            }
        """

        // exercise
        val actual: Command = sut.readValue(input)

        // verify
        assertHTTPCommand(actual, Collections.emptyList(), "http://example.com")
    }

    @Test
    fun deserialize_IllegalTypeCommand() {
        // setup
        val input = """
            {
               "type":"UNKNOWN",
               "uri":"http://example.com"
            }
        """

        // exercise

        val actual = catchThrowable { sut.readValue<Command>(input) }

        // verify
        assertThat(actual).isInstanceOf(IllegalCommandException::class.java)
    }

    private fun assertHTTPCommand(actual: Command, commands: List<Command>?, uri: String) {
        // verify
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
