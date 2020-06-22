package work.inabajun.ranabauts.domain.command

import org.springframework.http.HttpStatus

/**
 * Command response for HTTP request
 */
data class Response(val status: HttpStatus)
