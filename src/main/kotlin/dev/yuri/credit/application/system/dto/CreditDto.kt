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
        @field:NotNull(message = "Credit value is required.")
        val creditValue: BigDecimal,
        @field:Future(message = "The day of the first installment must be in the future.")
        val dayFirstOfInstallment: LocalDate,
        @field:Min(value = 1, message = "Number of installments must be equal to or greater than 1.")
        @field:Max(value = 48, message = "Number of installments must be equal to or smaller than 48.")
        val numberOfInstallments: Int,
        @field:NotNull(message = "Customer ID is required.")
        val customerId: Long
) {
    fun toEntity(): Credit = Credit(
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstOfInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)
    )

}
