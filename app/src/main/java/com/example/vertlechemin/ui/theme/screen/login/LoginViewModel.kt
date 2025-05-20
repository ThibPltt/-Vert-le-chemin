package com.example.vertlechemin.ui.theme.screen.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vertlechemin.ui.theme.data.repository.AuthResult
import com.example.vertlechemin.ui.theme.data.repository.UserRepository
import com.example.vertlechemin.ui.theme.data.repository.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Typography.dagger

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validatePassword(newPassword)
    }

    private fun validateEmail(email: String) {
        _emailError.value = ValidationUtils.getEmailErrorMessage(email)
    }

    private fun validatePassword(password: String) {
        _passwordError.value = ValidationUtils.getPasswordErrorMessage(password)
    }

    fun login() {
        // Valider à nouveau avant de soumettre
        validateEmail(_email.value)
        validatePassword(_password.value)

        // Si erreurs, ne pas continuer
        if (_emailError.value != null || _passwordError.value != null) {
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            when (val result = userRepository.login(_email.value, _password.value)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginState.Success
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginState.Error(result.message)
                }
                AuthResult.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }

    // Reset de l'état après navigation
    fun resetState() {
        _loginState.value = LoginState.Initial
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}