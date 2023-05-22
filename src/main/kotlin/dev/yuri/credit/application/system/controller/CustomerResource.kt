package dev.yuri.credit.application.system.controller

import dev.yuri.credit.application.system.dto.CustomerDto
import dev.yuri.credit.application.system.entity.Customer
import dev.yuri.credit.application.system.service.impl.CustomerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/costumers")
class CustomerResource(private val customerService: CustomerService) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDto: CustomerDto): String {
        val customer: Customer = customerDto.toEntity()
        val savedCustomer = this.customerService.save(customer)
        return "Customer ${savedCustomer.email} saved!"
    }
}
