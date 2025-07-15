package data.models

import interfaces.Loggable

// abstract User class for Customer and Admin classes to inherit from
abstract class User(open val username: String) : Loggable {
    abstract val role: String // role of the user

    // login method to check if the username and password are correct for the user
    override fun login(username: String, password: String): Boolean {
        return username == username && password == password
    }
}

// Customer class
class Customer(override val username: String) : User(username) {
    override val role = "Customer"
    val orders = mutableListOf<Order>() // list of orders made by the customer
}

// Admin class
class Admin(override val username: String) : User(username) {
    override val role = "Admin"
}