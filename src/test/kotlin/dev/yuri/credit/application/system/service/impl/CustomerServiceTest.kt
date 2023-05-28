package dev.yuri.credit.application.system.service.impl

import dev.yuri.credit.application.system.entity.Address
import dev.yuri.credit.application.system.entity.Customer
import dev.yuri.credit.application.system.exception.BusinessException
import dev.yuri.credit.application.system.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should create customer`() {
        // given
        val customer = buildCustomer()
        every { customerRepository.save(any()) } returns customer

        // when
        val savedCustomer = customerService.save(customer)

        // then
        Assertions.assertThat(savedCustomer).isNotNull
        Assertions.assertThat(savedCustomer).isSameAs(customer)
        verify(exactly = 1) { customerRepository.save(customer) }
    }

    @Test
    fun `should find customer by id`() {
        // given
        val id = Random().nextLong()
        val customer = buildCustomer(id = id)
        every { customerRepository.findById(id) } returns Optional.of(customer)

        // when
        val customerFound = customerService.findById(id)

        // then
        Assertions.assertThat(customerFound).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(customerFound).isNotNull
        Assertions.assertThat(customerFound).isSameAs(customer)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should not find customer by invalid id and throw BusinessException`() {
        // given
        val id = Random().nextLong()
        every { customerRepository.findById(id) } returns Optional.empty()

        // when
        // then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { customerService.findById(id) }
                .withMessage("Id $id not found")
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should delete customer by id`() {
        // given
        val id = Random().nextLong()
        val customer = buildCustomer(id = id)
        every { customerRepository.findById(id) } returns Optional.of(customer)
        every { customerRepository.delete(customer) } just runs

        // when
        customerService.delete(id)

        // then
        verify(exactly = 1) { customerRepository.findById(id) }
        verify(exactly = 1) { customerRepository.delete(customer) }
    }

    private fun buildCustomer(
            firstName: String = "Carlos",
            lastName: String = "Maia",
            cpf: String = "146.487.820-03",
            email: String = "carlosmaia@email.com",
            password: String = "123456",
            zipCode: String = "33333-333",
            street: String = "Main Street",
            income: BigDecimal = BigDecimal.valueOf(10000.00),
            id: Long = 1L
    ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                    zipCode = zipCode,
                    street = street
            ),
            income = income,
            id = id
    )
}
