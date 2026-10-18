package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.SalonRepository
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.ClientVip
import com.example.model.FinancialSummary
import com.example.model.PortfolioItem
import com.example.model.Product
import com.example.model.SalonService
import com.example.model.ServiceCategory
import com.example.model.Stylist
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String, val subtitle: String) {
    HOME("Maison de Beauté", "Experiência de Luxo & Visagismo"),
    ADMIN("Gestão Executiva", "Painel Administrativo & Métricas"),
    APPOINTMENTS("Agenda VIP", "Controle de Atendimentos"),
    SERVICES("Menu de Alta Costura", "Serviços e Tratamentos"),
    STYLISTS("Mestres da Beleza", "Equipe de Especialistas"),
    BOUTIQUE("Boutique Exclusiva", "Cosméticos & Home Care"),
    NEW_BOOKING("Novo Agendamento", "Reserve sua Experiência VIP"),
    PROFILE("Perfil VIP & Histórico", "Gerenciamento de Conta & Avaliações")
}

data class SalonUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedCategory: ServiceCategory = ServiceCategory.TODOS,
    val selectedPeriod: String = "Hoje",
    val searchQuery: String = "",
    val isBookingDialogOpen: Boolean = false,
    val preselectedService: SalonService? = null,
    val preselectedStylist: Stylist? = null,
    val userNotification: String? = null,
    val adminFilterStatus: AppointmentStatus? = null
)

class SalonViewModel(
    private val repository: SalonRepository
) : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _uiState = MutableStateFlow(SalonUiState())
    val uiState: StateFlow<SalonUiState> = _uiState.asStateFlow()

    val services: StateFlow<List<SalonService>> = repository.services
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stylists: StateFlow<List<Stylist>> = repository.stylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<Appointment>> = repository.appointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientsVip: StateFlow<List<ClientVip>> = repository.clientsVip
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolio: StateFlow<List<PortfolioItem>> = repository.portfolio
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialSummary: StateFlow<FinancialSummary> = repository.financialSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.financialSummary.value)

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInWithGoogle(context: Context) {
        _isSigningIn.value = true
        _authError.value = null
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder(
                    context.getString(R.string.default_web_client_id)
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCred)
                    _authError.value = null
                } else {
                    _authError.value = "Tipo de credencial não suportado."
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("SalonAuth", "Login cancelado pelo usuário", e)
                _authError.value = "Login cancelado. Selecione uma conta Google para prosseguir."
            } catch (e: Exception) {
                Log.e("SalonAuth", "Falha na autenticação Google", e)
                _authError.value = e.localizedMessage ?: "Erro ao autenticar com Google."
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun setCategoryFilter(category: ServiceCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun clearSearchQuery() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
    }

    fun setPeriodFilter(period: String) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
    }

    fun setAdminStatusFilter(status: AppointmentStatus?) {
        _uiState.value = _uiState.value.copy(adminFilterStatus = status)
    }

    fun openBookingDialog(service: SalonService? = null, stylist: Stylist? = null) {
        _uiState.value = _uiState.value.copy(
            isBookingDialogOpen = true,
            preselectedService = service,
            preselectedStylist = stylist
        )
    }

    fun setPreselectedBooking(service: SalonService? = null, stylist: Stylist? = null) {
        _uiState.value = _uiState.value.copy(
            preselectedService = service,
            preselectedStylist = stylist
        )
    }

    fun closeBookingDialog() {
        _uiState.value = _uiState.value.copy(
            isBookingDialogOpen = false,
            preselectedService = null,
            preselectedStylist = null
        )
    }

    fun confirmBooking(
        clientName: String,
        clientPhone: String,
        isVip: Boolean,
        service: SalonService,
        stylist: Stylist,
        time: String,
        notes: String,
        date: String = "Hoje"
    ): Appointment {
        val apt = repository.bookAppointment(
            clientName = clientName,
            clientPhone = clientPhone,
            isVip = isVip,
            service = service,
            stylist = stylist,
            time = time,
            notes = notes,
            date = date
        )
        _uiState.value = _uiState.value.copy(
            isBookingDialogOpen = false,
            preselectedService = null,
            preselectedStylist = null,
            userNotification = "Agendamento VIP registrado na nuvem para ${apt.clientName} às ${apt.time}!"
        )
        return apt
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        repository.updateAppointmentStatus(appointmentId, newStatus)
        _uiState.value = _uiState.value.copy(
            userNotification = "Status atualizado no Firestore: ${newStatus.label}"
        )
    }

    fun adjustStock(productId: String, delta: Int) {
        repository.adjustProductStock(productId, delta)
        _uiState.value = _uiState.value.copy(
            userNotification = "Estoque atualizado com sucesso"
        )
    }

    fun submitReview(appointmentId: String, rating: Float, comment: String) {
        repository.submitReview(appointmentId, rating, comment)
        _uiState.value = _uiState.value.copy(
            userNotification = "Avaliação de $rating estrelas salva com sucesso no Firestore!"
        )
    }

    fun updateClientProfile(name: String, phone: String, email: String) {
        repository.updateClientProfile(name, phone, email)
        _uiState.value = _uiState.value.copy(
            userNotification = "Perfil atualizado com sucesso no Firestore!"
        )
    }

    private val _showIntroAnimation = MutableStateFlow(true)
    val showIntroAnimation: StateFlow<Boolean> = _showIntroAnimation.asStateFlow()

    private val _appLogoUri = MutableStateFlow<String?>(null)
    val appLogoUri: StateFlow<String?> = _appLogoUri.asStateFlow()

    fun setShowIntroAnimation(enabled: Boolean) {
        _showIntroAnimation.value = enabled
    }

    fun setAppLogoUri(uri: String?) {
        _appLogoUri.value = uri
        _uiState.value = _uiState.value.copy(userNotification = "Logotipo e aparência atualizados com sucesso!")
    }

    fun addPortfolioItem(title: String, category: String, mediaUri: String, mediaType: String, stylistName: String, description: String) {
        repository.addPortfolioItem(title, category, mediaUri, mediaType, stylistName, description)
        _uiState.value = _uiState.value.copy(
            userNotification = if (mediaType == "VIDEO") "Vídeo adicionado ao portfólio com sucesso!" else "Foto adicionada ao portfólio com sucesso!"
        )
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(userNotification = null)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                SalonViewModel(SalonRepository(app))
            }
        }
    }
}
