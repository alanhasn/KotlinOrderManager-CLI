// interfaces/OrderManager.kt
package interfaces

import data.models.Order
import data.models.Product
import data.models.User

interface OrderManager {
    suspend fun fetchProducts(): List<Product>
    suspend fun placeOrder(user: User, product: Product): Result<Order>
    suspend fun getProductById(id: Int): Product?
    suspend fun deleteProduct(id: Int): Boolean
    suspend fun updateProductStock(id: Int, newStock: Int): Boolean
    // Retrieve all orders (admin functionality)
    suspend fun getAllOrders(): List<Order>
}