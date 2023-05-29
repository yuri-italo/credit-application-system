package dev.yuri.credit.application.system.service.impl

import dev.yuri.credit.application.system.entity.Credit
import dev.yuri.credit.application.system.entity.Customer
import dev.yuri.credit.application.system.exception.BusinessException
import dev.yuri.credit.application.system.repository.CreditRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Random

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should save credit`() {
        // given
        val credit = buildCredit()
        val customerId = credit.customer?.id!!
        every { customerService.findById(customerId) } returns credit.customer!!
        every { creditRepository.save(credit) } returns credit

        // when
        val savedCredit = creditService.save(credit)

        // then
        Assertions.assertThat(savedCredit).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(savedCredit).isSameAs(credit)
        verify(exactly = 1) { customerService.findById(credit.customer?.id!!) }
        verify(exactly = 1) { creditRepository.save(credit) }
    }

    @Test
    fun `should not create credit when invalid day first installment`() {
        // given
        val credit = buildCredit(dayOfFirstInstallment = LocalDate.now().plusMonths(4L))

        // when
        // then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { creditService.save(credit) }
                .withMessage("The day of the first installment must be within the next 3 months.")
        verify(exactly = 0) { creditRepository.save(credit) }
    }

    @Test
    fun `should return list of credits for a customer`() {
        // given
        val customerId = 1L
        val credits = listOf(buildCredit(), buildCredit(), buildCredit())
        every { creditRepository.findAllByCustomerId(customerId) } returns credits

        // when
        val returnedCredits = creditService.findAllByCustomer(customerId)

        // then
        Assertions.assertThat(returnedCredits).isNotNull
        Assertions.assertThat(returnedCredits).isNotEmpty
        Assertions.assertThat(returnedCredits).isSameAs(credits)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
    }

    @Test
    fun `should return credit for a valid customer and credit code`() {
        // given
        val customerId = Random().nextLong()
        val credit = buildCredit(customer = Customer(id = customerId))
        val creditCode = credit.creditCode
        every { creditRepository.findByCreditCode(creditCode) } returns credit

        // when
        val returnedCredit = creditService.findByCreditCode(customerId, creditCode)

        // then
        Assertions.assertThat(returnedCredit).isNotNull
        Assertions.assertThat(returnedCredit).isSameAs(credit)
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    companion object {
        private fun buildCredit(
                creditValue: BigDecimal = BigDecimal.valueOf(150.00),
                dayOfFirstInstallment: LocalDate = LocalDate.now().plusMonths(1L),
                numberOfInstallments: Int = 10,
                customer: Customer = CustomerServiceTest.buildCustomer()
        ): Credit = Credit (
                creditValue = creditValue,
                dayFirstInstallment = dayOfFirstInstallment,
                numberOfInstallments = numberOfInstallments,
                customer = customer
        )
    }
}
