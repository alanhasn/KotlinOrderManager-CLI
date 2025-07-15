// views/ConsoleUI.kt
package views

import data.StoreManager
import data.models.Admin
import data.models.Customer
import data.models.Product
import data.models.User
import interfaces.OrderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ConsoleUI(private val orderManager: OrderManager = StoreManager()) {
    private lateinit var currentUser: User

    fun start() {
        println("=== Shop Console ===")

        // simple login prompt
        print("Enter username (admin for admin access): ")
        val username = readLine()?.trim()?.lowercase() ?: "guest"
        val adminLoggedIn = username == "admin"
        currentUser = if (adminLoggedIn) Admin(username) else Customer(username)

        runBlocking {
            try {
                if (currentUser is Admin) {
                    adminMenu()
                } else {
                    customerMenu()
                }
            } catch (e: Exception) {
                handleFatalError(e)
            } finally {
                println("Cleaning up...")
                println("🔥✋✋❤️❤️👋👋👋")
            }
        }
    }

    // Menu for customer users
    private suspend fun customerMenu() {
        while (true) {
            println("\n=== Main Menu ===")
            println("1. View Products")
            println("2. Place Order")
            println("3. My Orders")
            println("4. Get Product by id")
            println("5. Exit")
            print("> ")

            when (readLine()) {
                "1" -> showProducts()
                "2" -> placeOrder()
                "3" -> showUserOrders()
                "4" -> getProductById()
                "5" -> {
                    println("Goodbye!")
                    break
                }
                else -> println("Invalid option")
            }
        }
    }

    // Menu for admin users
    private suspend fun adminMenu() {
        while (true) {
            println("\n=== Admin Menu ===")
            println("1. View Products")
            println("2. View All Orders")
            println("3. Get Product by id")
            println("4. Exit")
            print("> ")

            when (readLine()) {
                "1" -> showProducts()
                "2" -> showAllOrders()
                "3" -> getProductById()
                "4" -> {
                    println("Goodbye!")
                    break
                }
                else -> println("Invalid option")
            }
        }
    }

    private suspend fun showProducts() {
        println("\n=== Products ===")
        val productDeferred: Deferred<List<Product>> = CoroutineScope(Dispatchers.IO).async {
            orderManager.fetchProducts()
        }
        val products = productDeferred.await()

        products.forEachIndexed { i, product ->
            println("${i + 1}. ${product.name} - ${"%.2f".format(product.price)} (${product.inStock} left)")
        }
    }

    private suspend fun placeOrder() {
        showProducts()
        print("\nEnter product number (0 to cancel): ")
        val productNum = readLine()?.toIntOrNull()
        if (productNum == 0) return

        val products = orderManager.fetchProducts()
        if (productNum !in 1..products.size) {
            println("Invalid product number")
            return
        }

        val product = products[productNum!! - 1]
        println("Ordering: ${product.name}")

        orderManager.placeOrder(currentUser, product).onSuccess {
            println("Ordered: ${it.product.name}")
        }.onFailure {
            println("Error: ${it.message}")
        }
    }

    private suspend fun getProductById() {
        print("\nEnter product id: ")
        val id = readLine()?.toIntOrNull()
        if (id != null) {
            val product = orderManager.getProductById(id)
            if (product != null) {
                println("Product found: ${product.name}")
            } else {
                println("Product not found")
            }
        } else {
            println("Invalid product id")
        }
    }

    private fun handleFatalError(error: Throwable) {
        println("\nA critical error occurred:")
        println("${error.message}")
        println("The application needs to close.")
        error.printStackTrace()
    }

    private suspend fun showAllOrders() {
        println("\n=== All Orders ===")
        val orders = orderManager.getAllOrders()
        if (orders.isEmpty()) {
            println("No orders yet.")
            return
        }
        orders.forEach { o ->
            println("#${o.id} - ${o.product.name} for ${o.user.username} @ ${"%.2f".format(o.product.price)}")
        }
    }

    private fun showUserOrders() {
        if (currentUser is Customer) {
            val orders = (currentUser as Customer).orders
            if (orders.isEmpty()) {
                println("You have no orders yet.")
                return
            }
            println("\n=== My Orders ===")
            orders.forEach { o ->
                println("#${o.id} - ${o.product.name} @ ${"%.2f".format(o.product.price)}")
            }
        } else {
            println("Only customers can view their own orders.")
        }
    }
}