package com.dopae.simpletask.builder

import com.dopae.simpletask.exception.EmailNotReadyException
import com.dopae.simpletask.exception.PasswordNotMatchException
import com.dopae.simpletask.exception.PasswordNotReadyException
import com.dopae.simpletask.model.User

class UserBuilder {
    private var email: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""
    private val EMAIL_ADDRESS_PATTERN = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private val UPPERCASE_LETTER_PATTERN = Regex("[A-Z]")
    private val LOWERCASE_LETTER_PATTERN = Regex("[a-z]")
    private val SPECIAL_CHAR_PATTERN = Regex("\\W")

    fun setEmail(email: String): UserBuilder {
        this.email = email
        return this
    }

    fun setPassword(password: String): UserBuilder {
        this.password = password
        return this
    }

    fun setConfirmPassword(confirmPassword: String): UserBuilder {
        this.confirmPassword = confirmPassword
        return this
    }

    fun allReadyToGo(): UserPreBuilder {
        if (!email.matches(EMAIL_ADDRESS_PATTERN))
            throw EmailNotReadyException("Email Inválido")
        if (password.length < 8)
            throw PasswordNotReadyException("Senha muito curta")
        if (!password.any { it.isDigit() })
            throw PasswordNotReadyException("A senha deve ter pelo menos 1 numero")
        if (!UPPERCASE_LETTER_PATTERN.containsMatchIn(password))
            throw PasswordNotReadyException("A senha deve ter pelo menos 1 letra maiúscula")
        if (!LOWERCASE_LETTER_PATTERN.containsMatchIn(password))
            throw PasswordNotReadyException("A senha deve ter pelo menos 1 letra minúscula")
        if (!SPECIAL_CHAR_PATTERN.containsMatchIn(password))
            throw PasswordNotReadyException("A senha deve ter pelo menos 1 caractere especial")
        if (password != confirmPassword)
            throw PasswordNotMatchException("As senhas não coincidem")
        return UserPreBuilder()
    }

    inner class UserPreBuilder internal constructor() {
        fun build(): User {
            return User(email, password)
        }
    }

}