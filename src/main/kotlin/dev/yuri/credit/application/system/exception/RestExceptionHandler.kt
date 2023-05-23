package dev.yuri.credit.application.system.exception

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String, String?> = HashMap()
        e.bindingResult.allErrors.stream().forEach {
            it ->
            val fieldName: String = (it as FieldError).field
            val messageError: String? = it.defaultMessage
            errors[fieldName] = messageError
        }

        return ResponseEntity(
                ExceptionDetails(
                        title = "Validation Error",
                        timeStamp = LocalDateTime.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        exception = e.javaClass.name,
                        details = errors
                ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(e: DataAccessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
                ExceptionDetails(
                        title = "Data Access Error",
                        timeStamp = LocalDateTime.now(),
                        status = HttpStatus.CONFLICT.value(),
                        exception = e.javaClass.name,
                        details = mutableMapOf(e.cause.toString() to e.message)
                ), HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
                ExceptionDetails(
                        title = "Business Error",
                        timeStamp = LocalDateTime.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        exception = e.javaClass.name,
                        details = mutableMapOf(e.cause.toString() to e.message)
                ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
                ExceptionDetails(
                        title = "Invalid Argument Error",
                        timeStamp = LocalDateTime.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        exception = e.javaClass.name,
                        details = mutableMapOf(e.cause.toString() to e.message)
                ), HttpStatus.CONFLICT
        )
    }
}
