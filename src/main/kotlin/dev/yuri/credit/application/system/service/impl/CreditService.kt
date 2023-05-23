package dev.yuri.credit.application.system.service.impl

import dev.yuri.credit.application.system.entity.Credit
import dev.yuri.credit.application.system.exception.BusinessException
import dev.yuri.credit.application.system.repository.CreditRepository
import dev.yuri.credit.application.system.service.ICreditService
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
        private val creditRepository: CreditRepository,
        private val customerService: CustomerService
) : ICreditService {
    override fun save(credit: Credit): Credit {
        this.isAValidDayOfFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }

        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = this.creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = (this.creditRepository.findByCreditCode(creditCode)
                ?: throw BusinessException("Credit code $creditCode not found"))

        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact the admin")
    }

    private fun isAValidDayOfFirstInstallment(firstInstallment: LocalDate) : Boolean {
        return if (firstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("The day of the first installment must be within the next 3 months.")
    }
}
