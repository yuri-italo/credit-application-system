package dev.yuri.credit.application.system.controller

import dev.yuri.credit.application.system.dto.CreditView
import dev.yuri.credit.application.system.dto.CustomerDto
import dev.yuri.credit.application.system.dto.CustomerUpdateDto
import dev.yuri.credit.application.system.dto.CustomerView
import dev.yuri.credit.application.system.entity.Customer
import dev.yuri.credit.application.system.service.impl.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerResource(
        private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
        val customer: Customer = customerDto.toEntity()
        val savedCustomer = this.customerService.save(customer)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerView(savedCustomer))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
        val customer = this.customerService.findById(id)
        val customerView = CustomerView(customer)
        return ResponseEntity.ok(customerView)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    fun update(
            @RequestParam(value = "customerId") id: Long,
            @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer = this.customerService.findById(id)
        val customerToUpdate = customerUpdateDto.toEntity(customer)
        val updatedCustomer = this.customerService.save(customerToUpdate)
        val customerView = CustomerView(updatedCustomer)
        return ResponseEntity.ok(customerView)
    }
}
