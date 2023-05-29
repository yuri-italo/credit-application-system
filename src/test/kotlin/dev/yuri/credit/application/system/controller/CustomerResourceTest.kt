package dev.yuri.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.yuri.credit.application.system.dto.CustomerDto
import dev.yuri.credit.application.system.dto.CustomerUpdateDto
import dev.yuri.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        // given
        val customerDto = builderCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Shelton"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Mello"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("146.487.820-03"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.email")
                .value("sheltonmello@email.com"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("33333-333"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Main Street"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create a customer with same cpf and return 409 status`() {
        // given
        customerRepository.save(builderCustomerDto().toEntity())
        val customerDto = builderCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isConflict)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Data Access Error"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("org.springframework.dao.DataIntegrityViolationException"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create a customer with firstName empty and return status 400`() {
        // given
        val customerDto = builderCustomerDto(firstName = "")
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Validation Error"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("org.springframework.web.bind.MethodArgumentNotValidException"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        // given
        val customer = customerRepository.save(builderCustomerDto().toEntity())

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Shelton"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Mello"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("146.487.820-03"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.email")
                .value("sheltonmello@email.com"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("33333-333"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Main Street"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find costumer with invalid id and return 400 status`() {
        // given
        val id = 2L

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("$URL/${id}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Business Error"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("dev.yuri.credit.application.system.exception.BusinessException"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id and return 204 status`() {
        // given
        val customer = customerRepository.save(builderCustomerDto().toEntity())

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent)
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer by invalid id and return 400 status`() {
        // given
        val id = 2L

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${id}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Business Error"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.timeStamp").exists())
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("dev.yuri.credit.application.system.exception.BusinessException"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    private fun builderCustomerDto(
            firstName: String = "Shelton",
            lastName: String = "Mello",
            cpf: String = "146.487.820-03",
            email: String = "sheltonmello@email.com",
            income: BigDecimal = BigDecimal.valueOf(2000.0),
            password: String = "123456",
            zipCode: String = "33333-333",
            street: String = "Main Street",
    ) = CustomerDto(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            income = income,
            password = password,
            zipCode = zipCode,
            street = street
    )

    private fun builderCustomerUpdateDto(
            firstName: String = "Helton",
            lastName: String = "Melo",
            income: BigDecimal = BigDecimal.valueOf(5000.0),
            zipCode: String = "22222-222",
            street: String = "Second Street"
    ): CustomerUpdateDto = CustomerUpdateDto(
            firstName = firstName,
            lastName = lastName,
            income = income,
            zipCode = zipCode,
            street = street
    )
}
