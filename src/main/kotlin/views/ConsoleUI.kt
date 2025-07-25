package views

import data.StoreManager
import data.models.Admin
import data.models.Customer
import data.models.User
import interfaces.OrderManager
import kotlinx.coroutines.*
import utils.AnsiColor
import kotlin.system.exitProcess

class ConsoleUI(private val orderManager: OrderManager = StoreManager()) {
    private lateinit var currentUser: User

    fun start() {
        printHeader("Welcome to Shop Console")
        print("${AnsiColor.PURPLE}👤 Enter username (admin for admin access): ${AnsiColor.RESET}")
        val username = readlnOrNull()?.trim()?.lowercase() ?: "guest"
        val isAdmin = username == "admin"
        currentUser = if (isAdmin) Admin(username) else Customer(username)

        runBlocking {
            try {
                if (isAdmin) adminMenu() else customerMenu()
            } catch (e: Exception) {
                handleFatalError(e)
            } finally {
                println("${AnsiColor.YELLOW}👋 Cleaning up... Goodbye!${AnsiColor.RESET}")
            }
        }
    }

    private fun printHeader(title: String) {
        println("${AnsiColor.BRIGHT_BLUE}\n╔════════════════════════════════════╗")
        println("║${AnsiColor.RESET} ${AnsiColor.BOLD}${AnsiColor.CYAN}$title${AnsiColor.RESET}")
        println("${AnsiColor.BRIGHT_BLUE}╚════════════════════════════════════╝${AnsiColor.RESET}")
    }

    private suspend fun customerMenu() {
        while (true) {
            printHeader("Customer Menu")
            println(
                """
            ${AnsiColor.BRIGHT_CYAN}1️⃣  View Products
            2️⃣  Place Order
            3️⃣  My Orders
            4️⃣  Get Product by ID
            5️⃣  Logout${AnsiColor.RESET}
        """.trimIndent()
            )
            print("${AnsiColor.PURPLE}👉 Choose an option: ${AnsiColor.RESET}")

            when (readlnOrNull()?.toIntOrNull()) {
                1 -> showProducts()
                2 -> placeOrder()
                3 -> showUserOrders()
                4 -> getProductById()
                5 -> {
                    println("${AnsiColor.GREEN}👋 You have been logged out.${AnsiColor.RESET}")
                    start()
                    break
                }
                else -> println("${AnsiColor.RED}Invalid option. Try again.${AnsiColor.RESET}")
            }
        }
    }

    private suspend fun adminMenu() {
        while (true) {
            printHeader("🛠️ Admin Panel")
            println(
                """
            ${AnsiColor.BRIGHT_CYAN}1️⃣  View Products
            2️⃣  View All Orders
            3️⃣  Get Product by ID
            4️⃣  Delete Product by ID
            5️⃣  Logout
            6️⃣  Edit Product Stock
            7️⃣  Reports${AnsiColor.RESET}
        """.trimIndent()
            )
            print("${AnsiColor.PURPLE}👉 Choose an option: ${AnsiColor.RESET}")

            when (readlnOrNull()?.toIntOrNull()) {
                1 -> showProducts()
                2 -> showAllOrders()
                3 -> getProductById()
                4 -> deleteProductById()
                5 -> {
                    println("${AnsiColor.GREEN}👋 Admin logged out.${AnsiColor.RESET}")
                    start()
                    break
                }
                6 -> updateProductStock()
                7 -> showSalesReport()
                else -> println("${AnsiColor.RED}Invalid option.${AnsiColor.RESET}")
            }
        }
    }

    private suspend fun showProducts() {
        printHeader("Available Products")
        val products = withContext(Dispatchers.IO) { orderManager.fetchProducts() }

        if (products.isEmpty()) {
            println("${AnsiColor.YELLOW}No products found.${AnsiColor.RESET}")
        } else {
            products.forEachIndexed { i, p ->
                println("${i + 1}. ${p.name} - ${"%.2f".format(p.price)} USD (${p.inStock} in stock)")
            }
        }
    }

    private suspend fun placeOrder() {
        showProducts()
        print("${AnsiColor.PURPLE}\nEnter product number (0 to cancel): ${AnsiColor.RESET}")
        val choice = readlnOrNull()?.toIntOrNull()

        if (choice == 0) return

        val products = orderManager.fetchProducts()
        if (choice == null || choice !in 1..products.size) {
            println("${AnsiColor.RED}Invalid product number.${AnsiColor.RESET}")
            return
        }

        val product = products[choice - 1]
        println("${AnsiColor.BLUE}Ordering: ${product.name}${AnsiColor.RESET}")

        orderManager.placeOrder(currentUser, product).onSuccess {
            println("${AnsiColor.GREEN}Order placed for ${it.product.name}${AnsiColor.RESET}")
        }.onFailure {
            println("${AnsiColor.RED}Failed to place order: ${it.message}${AnsiColor.RESET}")
        }
    }

    private suspend fun getProductById() {
        print("${AnsiColor.PURPLE}\nEnter Product ID: ${AnsiColor.RESET}")
        val id = readlnOrNull()?.toIntOrNull()

        if (id != null) {
            val product = orderManager.getProductById(id)
            if (product != null) {
                println("${AnsiColor.GREEN}Found: ${product.name}${AnsiColor.RESET}")
            } else {
                println("${AnsiColor.RED}Product not found.${AnsiColor.RESET}")
            }
        } else {
            println("${AnsiColor.RED}Invalid input.${AnsiColor.RESET}")
        }
    }

    private suspend fun showAllOrders() {
        printHeader("All Orders")
        val orders = orderManager.getAllOrders()
        if (orders.isEmpty()) {
            println("${AnsiColor.YELLOW}No orders placed yet.${AnsiColor.RESET}")
        } else {
            orders.forEach {
                println("#${it.id} - ${it.product.name} for ${it.user.username} @ ${"%.2f".format(it.product.price)}")
            }
        }
    }

    private fun showUserOrders() {
        if (currentUser is Customer) {
            val orders = (currentUser as Customer).orders
            if (orders.isEmpty()) {
                println("${AnsiColor.YELLOW}You have no orders yet.${AnsiColor.RESET}")
            } else {
                printHeader("My Orders")
                orders.forEach {
                    println("#${it.id} - ${it.product.name} @ ${"%.2f".format(it.product.price)}")
                }
            }
        } else {
            println("${AnsiColor.RED}Only customers can view their own orders.${AnsiColor.RESET}")
        }
    }

    private suspend fun deleteProductById() {
        showProducts()
        print("${AnsiColor.PURPLE}\nEnter Product ID to delete: ${AnsiColor.RESET}")
        val id = readlnOrNull()?.toIntOrNull()

        if (id == null) {
            println("${AnsiColor.RED}Invalid ID.${AnsiColor.RESET}")
            return
        }

        val product = orderManager.getProductById(id)
        if (product == null) {
            println("${AnsiColor.RED}Product not found.${AnsiColor.RESET}")
            return
        }

        val result = orderManager.deleteProduct(id)
        if (result) {
            println("${AnsiColor.GREEN}Product '${product.name}' deleted successfully.${AnsiColor.RESET}")
        } else {
            println("${AnsiColor.RED}Failed to delete product.${AnsiColor.RESET}")
        }
    }

    private suspend fun updateProductStock() {
        showProducts()

        print("${AnsiColor.PURPLE}\nEnter Product ID to edit stock: ${AnsiColor.RESET}")
        val id = readlnOrNull()?.toIntOrNull()

        if (id == null) {
            println("${AnsiColor.RED}Invalid ID.${AnsiColor.RESET}")
            return
        }

        val product = orderManager.getProductById(id)
        if (product == null) {
            println("${AnsiColor.RED}Product not found.${AnsiColor.RESET}")
            return
        }

        println("${AnsiColor.CYAN}Current stock for '${product.name}': ${product.inStock}${AnsiColor.RESET}")
        print("${AnsiColor.PURPLE}Enter new stock value: ${AnsiColor.RESET}")
        val newStock = readlnOrNull()?.toIntOrNull()

        if (newStock == null || newStock < 0) {
            println("${AnsiColor.RED}Invalid stock value. Must be 0 or more.${AnsiColor.RESET}")
            return
        }

        val result = orderManager.updateProductStock(id, newStock)
        if (result) {
            println("${AnsiColor.GREEN}Stock updated successfully to $newStock.${AnsiColor.RESET}")
        } else {
            println("${AnsiColor.RED}Failed to update stock.${AnsiColor.RESET}")
        }
    }

    private suspend fun showSalesReport() {
        printHeader("Sales Report")

        val orders = orderManager.getAllOrders()

        val totalSold = orders.size
        val totalRevenue = orders.sumOf { it.product.price }

        println("${AnsiColor.CYAN}Total Products Sold: ${AnsiColor.BOLD}$totalSold${AnsiColor.RESET}")
        println("${AnsiColor.CYAN}Total Revenue: ${AnsiColor.BOLD}%.2f USD${AnsiColor.RESET}".format(totalRevenue))
    }

    private fun handleFatalError(e: Throwable) {
        println("${AnsiColor.RED}\nCritical Error: ${e.message}${AnsiColor.RESET}")
        e.printStackTrace()
        exitProcess(1)
    }

}
