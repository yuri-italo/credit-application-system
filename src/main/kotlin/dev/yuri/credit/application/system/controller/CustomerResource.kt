package dev.yuri.credit.application.system.controller

import dev.yuri.credit.application.system.dto.CustomerDto
import dev.yuri.credit.application.system.dto.CustomerUpdateDto
import dev.yuri.credit.application.system.dto.CustomerView
import dev.yuri.credit.application.system.entity.Customer
import dev.yuri.credit.application.system.service.impl.CustomerService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/costumers")
class CustomerResource(
        private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDto: CustomerDto): String {
        val customer: Customer = customerDto.toEntity()
        val savedCustomer = this.customerService.save(customer)
        return "Customer ${savedCustomer.email} saved!"
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): CustomerView {
        val customer = this.customerService.findById(id)
        return CustomerView(customer)
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    fun update(
            @RequestParam(value = "customerId") id: Long,
            @RequestBody customerUpdateDto: CustomerUpdateDto
    ): CustomerView {
        val customer = this.customerService.findById(id)
        val customerToUpdate = customerUpdateDto.toEntity(customer)
        val updatedCustomer = this.customerService.save(customerToUpdate)
        return CustomerView(updatedCustomer)
    }
}
