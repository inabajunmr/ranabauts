package work.inabajun.ranabauts.domain.command

import java.util.function.Consumer

/**
 * Command is series of commands and response for application.
 *
 * Application that accepts this command executes series of commands and return response.
 * This command will be accepted via HTTP request.
 */
abstract class Command(response: Response, commands: List<Command>, commandType: CommandType) {

    /**
     * HTTP Response for an application that accepts this command
     */
    val response: Response = response

    /**
     * Command List to execute by an application that accepts this command
     */
    val commands: List<Command> = commands

    val type: CommandType = commandType

    /**
     * execute this command
     */
    protected abstract fun execute()

    /**
     *
     * @return
     */
    fun executeCommands(): Response {
        commands?.forEach(Consumer { c: Command -> c.execute() })
        return response
    }
}
