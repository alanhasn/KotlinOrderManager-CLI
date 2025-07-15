package data

// IMPORTS
import data.models.Customer
import data.models.Order
import data.models.Product
import data.models.User
import interfaces.OrderManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// Store Manager for handling orders and products and coroutines simulate network delay
class StoreManager : OrderManager {
    private val products = mutableListOf<Product>() // List of products
    private val orders = mutableListOf<Order>() // List of orders
    private var orderCounter = 1 // Counter for order IDs

    init {
        // Initialize with some products
        products.addAll(listOf(
            Product(
                id = 1,
                name = "Laptop",
                description = "Laptop lenovo think pad",
                price = 99.0,
                inStock = 1
            ),
            Product(
                id = 2, name = "Mouse",
                description = "Better mouse in the world",
                price = 100.0,
                inStock = 10
            ),
            Product(
                id = 3,
                name = "Keyboard",
                description = "Better keyboard in the world",
                price = 59.99,
                inStock = 8
            ),
            Product(
                id = 4,
                name = "Monitor",
                description = "Better monitor in the world",
                price = 199.99,
                inStock = 23
            )
        ))
    }

    // Fetch products from the database simulating network delay
    override suspend fun fetchProducts(): List<Product> {
        // Simulate network delay
        delay(1000)
        return products.toList()
    }

    // place order simulating network delay
    override suspend fun placeOrder(user: User, product: Product): Result<Order> =
        withContext(Dispatchers.IO) { // Run on a background thread IO
            try {
                delay(500) // Simulate processing time

                if (product.inStock <= 0) {
                    return@withContext Result.failure(
                        Exception("This product is out of stock")
                    )
                }

                val order = Order(
                    id = orderCounter++,
                    product = product,
                    user = user
                )

                // Update product stock
                product.inStock--
                orders.add(order)

                if (user is Customer) {
                    user.orders.add(order)
                }


                Result.success(order)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getProductById(id: Int): Product? {
        delay(1000)
        println("Getting product by id: $id")
        return products.find { it.id == id }
    }

    // New: return a copy of all orders (admin functionality)
    override suspend fun getAllOrders(): List<Order> {
        // minimal delay to simulate I/O
        delay(300)
        return orders.toList() // return immutable copy
    }
}