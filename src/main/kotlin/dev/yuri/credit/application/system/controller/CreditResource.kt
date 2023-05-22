package dev.yuri.credit.application.system.controller

import dev.yuri.credit.application.system.dto.CreditDto
import dev.yuri.credit.application.system.dto.CreditView
import dev.yuri.credit.application.system.dto.CreditViewList
import dev.yuri.credit.application.system.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/credits")
class CreditResource(
        private val creditService: CreditService
) {
    @PostMapping
    fun save(@RequestBody creditDto: CreditDto): ResponseEntity<String> {
        val credit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam customerId: Long): ResponseEntity<List<CreditViewList>> {
        val creditViewList = this.creditService.findAllByCustomer(customerId).stream()
                .map { credit -> CreditViewList(credit) }
                .toList()
        return ResponseEntity.ok(creditViewList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
            @RequestParam(value = "customerId") customerId: Long,
            @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit = this.creditService.findByCreditCode(customerId,creditCode)
        val creditView = CreditView(credit)
        return ResponseEntity.ok(creditView)
    }
}
