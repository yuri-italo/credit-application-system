package dev.yuri.credit.application.system.controller

import dev.yuri.credit.application.system.dto.CreditDto
import dev.yuri.credit.application.system.dto.CreditViewList
import dev.yuri.credit.application.system.service.impl.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/credits")
class CreditResource(
        private val creditService: CreditService
) {
    @PostMapping
    fun save(@RequestBody creditDto: CreditDto): String {
        val credit = this.creditService.save(creditDto.toEntity())
        return "Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!"
    }

    @GetMapping()
    fun findAllByCustomerId(@RequestParam customerId: Long): List<CreditViewList> {
        return this.creditService.findAllByCustomer(customerId).stream()
                .map { credit -> CreditViewList(credit) }
                .toList()
    }
}
