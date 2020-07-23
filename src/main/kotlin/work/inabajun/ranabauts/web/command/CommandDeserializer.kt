package work.inabajun.ranabauts.web.command

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.net.URL
import java.util.Collections
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import work.inabajun.ranabauts.domain.command.Command
import work.inabajun.ranabauts.domain.command.CommandType
import work.inabajun.ranabauts.domain.command.HTTPCommand

/**
 * Jackson deserializer for command
 */
class CommandDeserializer(vc: Class<Command>?) : StdDeserializer<Command>(vc) {

    constructor() : this(null)

    /**
     * Deserialize Command each implementations
     *
     * @throws IllegalCommandException command object is illegal
     */
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Command {
        val tree: JsonNode = p!!.codec.readTree(p)
        return parseCommand(tree)
    }

    private fun parseCommand(command: JsonNode): Command {
        val type = parseType(command)
        if (type == CommandType.HTTP) {
            val uri = parseURI(command)
            val commands = parseCommands(command)
            return HTTPCommand(uri, commands)
        } else {
            throw IllegalCommandException("This type:${type.name} is not implemented.")
        }
    }

    private fun parseType(command: JsonNode): CommandType {
        if (!command.has("type")) {
            throw IllegalCommandException("Command needs 'type' field.")
        }

        val type = command.get("type")

        if (!type.isTextual) {
            throw IllegalCommandException("'type' field required string value.")
        }

        try {
            return CommandType.valueOf(type.textValue())
        } catch (e: IllegalArgumentException) {
            throw IllegalCommandException("this 'type':${type.textValue()} is not implemented.", e)
        }
    }

    private fun parseURI(command: JsonNode): URL {
        if (!command.has("uri")) {
            throw IllegalCommandException("Command needs 'uri' field.")
        }

        val uri = command.get("uri")
        if (!uri.isTextual) {
            throw IllegalCommandException("'type' field required string value.")
        }

        try {
            return URL(uri.textValue())
        } catch (e: IllegalArgumentException) {
            throw IllegalCommandException("URI:${uri.textValue()} is invalid.", e)
        }
    }

    private fun parseCommands(command: JsonNode): List<Command> {
        if (!command.has("commands")) {
            return Collections.emptyList()
        }

        val commands = command.get("commands")
        if (!commands.isArray) {
            throw IllegalCommandException("'commands' field required array value. Each value is command object.")
        }

        return commands.map { c -> parseCommand(c) }
    }
}

/**
 * Command JSON is invalid.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class IllegalCommandException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String?) : this(message, null)
}
