package dev.yuri.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.yuri.credit.application.system.dto.CreditDto
import dev.yuri.credit.application.system.dto.CustomerDto
import dev.yuri.credit.application.system.repository.CreditRepository
import dev.yuri.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalDate
import java.util.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired private lateinit var creditRepository: CreditRepository
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/credits"
    }

    @BeforeEach fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should create credit and return 201 status`() {
        // given
        val customer = customerRepository.save(customerDtoBuilder().toEntity())
        val creditDto = creditDtoBuilder(customerId = customer.id!!)
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("1000.0"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments")
                .value(5))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.incomeCostumer").value(customer.income))
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create credit with invalid day first of installment and return 400 status`() {
        // given
        val creditDto = creditDtoBuilder(dayFirstOfInstallment = LocalDate.now().plusMonths(4L))
        customerRepository.save(customerDtoBuilder().toEntity())
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
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
    fun `should not create credit with invalid customer id and return 400 status`() {
        // given
        val creditDto = creditDtoBuilder(customerId = Random().nextLong())
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
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
    fun `should return a list of all credits by customer id and return 200 status`() {
        // given
        val customer = customerRepository.save(customerDtoBuilder().toEntity())
        val customerCredit = creditRepository.save(creditDtoBuilder(customerId = customer.id!!).toEntity())
        val customerCredit2 = creditRepository.save(
            creditDtoBuilder(customerId = customer.id!!,
            creditValue = BigDecimal.valueOf(5000),
            numberOfInstallments = 7).toEntity()
        )
        val creditCode = customerCredit.creditCode.toString()
        val creditCode2 = customerCredit2.creditCode.toString()

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.get("$URL?customerId=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[0].creditCode").value(creditCode))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[0].creditValue")
                .value("1000.0"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[0].numberOfInstallments")
                .value(5))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[1].creditCode").value(creditCode2))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[1].creditValue")
                .value("5000.0"))
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.[1].numberOfInstallments")
                .value(7))
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return an empty list of credits if customer id does not exist and return 200 status`() {
        // given
        val nonExistingCustomerId = 2L

        // when
        val resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("$URL?customerId=${nonExistingCustomerId}")
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0))
        resultActions.andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun findByCreditCode() {
    }

    private fun creditDtoBuilder(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusMonths(2L),
        numberOfInstallments: Int = 5,
        customerId: Long = 1L
    ) = CreditDto(
            creditValue = creditValue,
            dayFirstOfInstallment = dayFirstOfInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId
    )

    private fun customerDtoBuilder(
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
}
