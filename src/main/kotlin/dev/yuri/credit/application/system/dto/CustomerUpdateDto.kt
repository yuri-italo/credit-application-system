package dev.yuri.credit.application.system.dto

import dev.yuri.credit.application.system.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto(
        @field:NotEmpty(message = "First name is required.")
        val firstName: String,
        @field:NotEmpty(message = "Last name is required.")
        val lastName: String,
        @field:NotNull(message = "Income is required.")
        val income: BigDecimal,
        @field:NotEmpty(message = "Zipcode is required.")
        val zipCode: String,
        @field:NotEmpty(message = "Street is required.")
        val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street

        return customer
    }
}
