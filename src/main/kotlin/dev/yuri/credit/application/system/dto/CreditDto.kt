package dev.yuri.credit.application.system.dto

import dev.yuri.credit.application.system.entity.Credit
import dev.yuri.credit.application.system.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
        @field:NotNull(message = "Credit value cannot be null")
        val creditValue: BigDecimal,
        @field:Future(message = "Day first of installment cannot be a past or present date")
        val dayFirstOfInstallment: LocalDate,
        @field:Min(value = 1L, message = "Number of installments must be equal or greater than 1")
        @field:Max(value = 12L, message = "Number of installments must be equal or smaller than 12")
        val numberOfInstallments: Int,
        @field:NotNull(message = "Customer ID cannot be null")
        val customerId: Long
) {
    fun toEntity(): Credit = Credit(
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)
    )

}
