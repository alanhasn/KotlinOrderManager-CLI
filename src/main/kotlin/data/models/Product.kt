package data.models


// Product model
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    var inStock: Int = 10
)