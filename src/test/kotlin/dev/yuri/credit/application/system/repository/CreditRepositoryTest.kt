package dev.yuri.credit.application.system.repository

import dev.yuri.credit.application.system.entity.Address
import dev.yuri.credit.application.system.entity.Credit
import dev.yuri.credit.application.system.entity.Customer
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    @BeforeEach fun setup() {
        customer = testEntityManager.persist(buildCustomer())
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))
    }

    private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(500.0),
            dayFirstInstallment: LocalDate = LocalDate.of(2023, Month.APRIL, 22),
            numberOfInstallments: Int = 5,
            customer: Customer
    ): Credit = Credit(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customer = customer
    )

    private fun buildCustomer(
            firstName: String = "Carlos",
            lastName: String = "Silva",
            cpf: String = "146.487.820-03",
            email: String = "carlos@email.com",
            password: String = "123456",
            zipCode: String = "33333-333",
            street: String = "Main Street",
            income: BigDecimal = BigDecimal.valueOf(1500.0),
    ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                    zipCode = zipCode,
                    street = street,
            ),
            income = income,
    )
}
