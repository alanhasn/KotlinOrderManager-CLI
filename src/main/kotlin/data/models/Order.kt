package data.models

import java.time.LocalDate

// Order Model
data class Order(
    val id: Int,
    val product:Product,
    val user: User,
    val date: LocalDate = LocalDate.now()
)
