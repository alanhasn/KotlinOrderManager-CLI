// interfaces/Loggable.kt
package interfaces

interface Loggable {
    fun login(username: String, password: String): Boolean
}