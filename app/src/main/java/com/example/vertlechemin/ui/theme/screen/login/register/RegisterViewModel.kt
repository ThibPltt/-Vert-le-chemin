package com.example.vertlechemin.ui.theme.screen.login.register


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

// États possibles pour l'inscription
sealed class RegisterState {
    object Initial : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    private val _displayNameError = MutableStateFlow<String?>(null)
    val displayNameError: StateFlow<String?> = _displayNameError.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validatePassword(newPassword)
        // Vérifier également la confirmation si elle a déjà une valeur
        if (_confirmPassword.value.isNotEmpty()) {
            validateConfirmPassword(_confirmPassword.value)
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        validateConfirmPassword(newConfirmPassword)
    }

    fun onDisplayNameChange(newDisplayName: String) {
        _displayName.value = newDisplayName
        validateDisplayName(newDisplayName)
    }

    private fun validateEmail(email: String) {
        _emailError.value = ValidationUtils.getEmailErrorMessage(email)
    }

    private fun validatePassword(password: String) {
        _passwordError.value = ValidationUtils.getPasswordErrorMessage(password)
    }

    private fun validateConfirmPassword(confirmPassword: String) {
        _confirmPasswordError.value = when {
            confirmPassword.isEmpty() -> "Veuillez confirmer votre mot de passe"
            confirmPassword != _password.value -> "Les mots de passe ne correspondent pas"
            else -> null
        }
    }

    private fun validateDisplayName(displayName: String) {
        _displayNameError.value = when {
            displayName.isEmpty() -> "Le nom d'utilisateur ne peut pas être vide"
            displayName.length < 3 -> "Le nom d'utilisateur doit contenir au moins 3 caractères"
            else -> null
        }
    }

    fun register() {
        // Valider tous les champs
        validateEmail(_email.value)
        validatePassword(_password.value)
        validateConfirmPassword(_confirmPassword.value)
        validateDisplayName(_displayName.value)

        // Vérifier s'il y a des erreurs
        if (_emailError.value != null || _passwordError.value != null ||
            _confirmPasswordError.value != null || _displayNameError.value != null) {
            return
        }

        // Tout est valide, on procède à l'inscription
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            when (val result = userRepository.register(_email.value, _password.value, _displayName.value)) {
                is AuthResult.Success -> {
                    _registerState.value = RegisterState.Success
                }
                is AuthResult.Error -> {
                    _registerState.value = RegisterState.Error(result.message)
                }
                AuthResult.Loading -> {
                    _registerState.value = RegisterState.Loading
                }
            }
        }
    }

    // Reset de l'état après navigation
    fun resetState() {
        _registerState.value = RegisterState.Initial
    }
}