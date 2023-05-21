package dev.yuri.credit.application.system.repository

import dev.yuri.credit.application.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
@Repository
interface CustomerRepository: JpaRepository<Customer, UUID> {
}
