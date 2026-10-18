package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.ClientVip
import com.example.model.DailyRevenuePoint
import com.example.model.FinancialSummary
import com.example.model.PortfolioItem
import com.example.model.Product
import com.example.model.SalonService
import com.example.model.ServiceCategory
import com.example.model.Stylist
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SalonRepository(
    private val db: FirebaseFirestore? = null,
    private val auth: FirebaseAuth? = null
) {
    // Secondary constructor to safely resolve the named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _services = MutableStateFlow<List<SalonService>>(emptyList())
    val services: StateFlow<List<SalonService>> = _services.asStateFlow()

    private val _stylists = MutableStateFlow<List<Stylist>>(emptyList())
    val stylists: StateFlow<List<Stylist>> = _stylists.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _clientsVip = MutableStateFlow<List<ClientVip>>(emptyList())
    val clientsVip: StateFlow<List<ClientVip>> = _clientsVip.asStateFlow()

    private val _portfolio = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolio: StateFlow<List<PortfolioItem>> = _portfolio.asStateFlow()

    private val _products = MutableStateFlow(getInitialProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _financialSummary = MutableStateFlow(calculateFinancialSummary(emptyList()))
    val financialSummary: StateFlow<FinancialSummary> = _financialSummary.asStateFlow()

    private var servicesListener: ListenerRegistration? = null
    private var stylistsListener: ListenerRegistration? = null
    private var appointmentsListener: ListenerRegistration? = null
    private var clientsListener: ListenerRegistration? = null
    private var portfolioListener: ListenerRegistration? = null

    init {
        if (db != null) {
            startFirestoreSync()
        } else {
            // In-memory mode for unit tests and offline previews
            _services.value = getInitialServices()
            _stylists.value = getInitialStylists()
            val initialApts = getInitialAppointments("local_user")
            _appointments.value = initialApts
            _clientsVip.value = getInitialClients()
            _portfolio.value = getInitialPortfolio()
            _financialSummary.value = calculateFinancialSummary(initialApts)
        }
    }

    private fun startFirestoreSync() {
        val firestore = db ?: return

        // Sync Services
        servicesListener = firestore.collection("services")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "services")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val service = doc.toObject(SalonService::class.java)
                        service?.copy(id = doc.id)
                    }
                    if (list.isEmpty()) {
                        // Seed initial services to Cloud Firestore
                        seedInitialServices()
                    } else {
                        _services.value = list
                    }
                }
            }

        // Sync Stylists
        stylistsListener = firestore.collection("stylists")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "stylists")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val stylist = doc.toObject(Stylist::class.java)
                        stylist?.copy(id = doc.id)
                    }
                    if (list.isEmpty()) {
                        seedInitialStylists()
                    } else {
                        _stylists.value = list
                    }
                }
            }

        // Sync Appointments
        appointmentsListener = firestore.collection("appointments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "appointments")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val apt = doc.toObject(Appointment::class.java)
                        apt?.copy(id = doc.id)
                    }
                    if (list.isEmpty()) {
                        seedInitialAppointments()
                    } else {
                        _appointments.value = list
                        _financialSummary.value = calculateFinancialSummary(list)
                    }
                }
            }

        // Sync VIP Clients
        clientsListener = firestore.collection("clients")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "clients")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val client = doc.toObject(ClientVip::class.java)
                        client?.copy(id = doc.id)
                    }
                    if (list.isEmpty()) {
                        seedInitialClients()
                    } else {
                        _clientsVip.value = list
                    }
                }
            }

        // Sync Portfolio Gallery
        portfolioListener = firestore.collection("portfolio")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "portfolio")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val item = doc.toObject(PortfolioItem::class.java)
                        item?.copy(id = doc.id)
                    }
                    if (list.isEmpty()) {
                        seedInitialPortfolio()
                    } else {
                        _portfolio.value = list
                    }
                }
            }
    }

    private fun seedInitialServices() {
        val firestore = db ?: return
        val initial = getInitialServices()
        _services.value = initial
        repositoryScope.launch {
            initial.forEach { service ->
                val data = mapOf(
                    "name" to service.name,
                    "category" to service.category.name,
                    "description" to service.description,
                    "durationMinutes" to service.durationMinutes,
                    "price" to service.price,
                    "rating" to service.rating.toDouble(),
                    "isFeatured" to service.isFeatured,
                    "badge" to (service.badge ?: "VIP"),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("services").document(service.id).set(data)
            }
        }
    }

    private fun seedInitialStylists() {
        val firestore = db ?: return
        val initial = getInitialStylists()
        _stylists.value = initial
        repositoryScope.launch {
            initial.forEach { stylist ->
                val data = mapOf(
                    "name" to stylist.name,
                    "role" to stylist.role,
                    "title" to stylist.title,
                    "rating" to stylist.rating.toDouble(),
                    "reviewsCount" to stylist.reviewsCount,
                    "experienceYears" to stylist.experienceYears,
                    "avatarInitials" to stylist.avatarInitials,
                    "isAvailable" to stylist.isAvailable
                )
                firestore.collection("stylists").document(stylist.id).set(data)
            }
        }
    }

    private fun seedInitialClients() {
        val firestore = db ?: return
        val initial = getInitialClients()
        _clientsVip.value = initial
        repositoryScope.launch {
            initial.forEach { client ->
                val data = mapOf(
                    "name" to client.name,
                    "phone" to client.phone,
                    "email" to client.email,
                    "tier" to client.tier,
                    "visitsCount" to client.visitsCount,
                    "totalSpent" to client.totalSpent,
                    "lastVisit" to client.lastVisit,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("clients").document(client.id).set(data)
            }
        }
    }

    private fun seedInitialAppointments() {
        val firestore = db ?: return
        val currentUserId = auth?.currentUser?.uid ?: "concierge_system"
        val initial = getInitialAppointments(currentUserId)
        _appointments.value = initial
        _financialSummary.value = calculateFinancialSummary(initial)
        repositoryScope.launch {
            initial.forEach { apt ->
                val data = mapOf(
                    "userId" to apt.userId,
                    "clientName" to apt.clientName,
                    "clientPhone" to apt.clientPhone,
                    "clientIsVip" to apt.clientIsVip,
                    "serviceName" to apt.serviceName,
                    "stylistName" to apt.stylistName,
                    "date" to apt.date,
                    "time" to apt.time,
                    "status" to apt.status.name,
                    "price" to apt.price,
                    "notes" to apt.notes,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("appointments").document(apt.id).set(data)
            }
        }
    }

    fun bookAppointment(
        clientName: String,
        clientPhone: String,
        isVip: Boolean,
        service: SalonService,
        stylist: Stylist,
        time: String,
        notes: String,
        date: String = "Hoje"
    ): Appointment {
        val currentUserId = auth?.currentUser?.uid ?: "vip_client_${System.currentTimeMillis() % 1000}"
        val appointmentId = "apt_${System.currentTimeMillis()}"

        val newAppointment = Appointment(
            id = appointmentId,
            userId = currentUserId,
            clientName = clientName,
            clientPhone = clientPhone,
            clientIsVip = isVip,
            serviceName = service.name,
            stylistName = stylist.name,
            date = date,
            time = time,
            status = AppointmentStatus.CONFIRMADO,
            price = service.price,
            notes = notes
        )

        // Optimistic local update
        _appointments.update { listOf(newAppointment) + it }
        _financialSummary.value = calculateFinancialSummary(_appointments.value)

        // Remote persistence in Firestore if connected
        val firestore = db
        if (firestore != null) {
            repositoryScope.launch {
                try {
                    val data = mapOf(
                        "userId" to currentUserId,
                        "clientName" to clientName,
                        "clientPhone" to clientPhone,
                        "clientIsVip" to isVip,
                        "serviceName" to service.name,
                        "stylistName" to stylist.name,
                        "date" to date,
                        "time" to time,
                        "status" to AppointmentStatus.CONFIRMADO.name,
                        "price" to service.price,
                        "notes" to notes,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("appointments").document(appointmentId).set(data)
                        .addOnFailureListener { exception ->
                            handleFirestoreError(exception, OperationType.CREATE, "appointments/$appointmentId")
                        }

                    // Also record or update VIP client in Firestore
                    val existingClient = _clientsVip.value.find { it.phone == clientPhone || it.name.equals(clientName, ignoreCase = true) }
                    val clientId = existingClient?.id ?: "cli_${System.currentTimeMillis() % 100000}"
                    val clientData = mapOf(
                        "userId" to currentUserId,
                        "name" to clientName,
                        "phone" to clientPhone,
                        "email" to (existingClient?.email ?: "${clientName.lowercase().replace(" ", ".")}@luxmail.com"),
                        "tier" to (existingClient?.tier ?: if (isVip) "Maison Black" else "Maison Gold"),
                        "visitsCount" to ((existingClient?.visitsCount ?: 0) + 1),
                        "totalSpent" to ((existingClient?.totalSpent ?: 0.0) + service.price),
                        "lastVisit" to date,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("clients").document(clientId).set(clientData)
                } catch (e: Exception) {
                    Log.e("SalonRepository", "Error creating appointment in Firestore", e)
                }
            }
        }

        return newAppointment
    }

    fun submitReview(appointmentId: String, rating: Float, comment: String) {
        _appointments.update { list ->
            list.map {
                if (it.id == appointmentId) it.copy(rating = rating, reviewComment = comment) else it
            }
        }

        val firestore = db
        if (firestore != null) {
            repositoryScope.launch {
                try {
                    firestore.collection("appointments").document(appointmentId)
                        .update(
                            mapOf(
                                "rating" to rating.toDouble(),
                                "reviewComment" to comment,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                        .addOnFailureListener { exception ->
                            handleFirestoreError(exception, OperationType.UPDATE, "appointments/$appointmentId")
                        }
                } catch (e: Exception) {
                    Log.e("SalonRepository", "Error submitting review in Firestore", e)
                }
            }
        }
    }

    fun updateClientProfile(name: String, phone: String, email: String) {
        val currentUserId = auth?.currentUser?.uid ?: return
        repositoryScope.launch {
            try {
                val firestore = db ?: return@launch
                val snapshot = firestore.collection("clients")
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnSuccessListener { result ->
                        for (doc in result.documents) {
                            firestore.collection("clients").document(doc.id).update(
                                mapOf(
                                    "name" to name,
                                    "phone" to phone,
                                    "email" to email,
                                    "updatedAt" to FieldValue.serverTimestamp()
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("SalonRepository", "Error updating client profile", e)
            }
        }
    }
fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        _appointments.update { list ->
            list.map {
                if (it.id == appointmentId) it.copy(statusName = newStatus.name) else it
            }
        }
        _financialSummary.value = calculateFinancialSummary(_appointments.value)

        val firestore = db
        if (firestore != null) {
            repositoryScope.launch {
                try {
                    firestore.collection("appointments").document(appointmentId)
                        .update(
                            mapOf(
                                "status" to newStatus.name,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                        .addOnFailureListener { exception ->
                            handleFirestoreError(exception, OperationType.UPDATE, "appointments/$appointmentId")
                        }
                } catch (e: Exception) {
                    Log.e("SalonRepository", "Error updating appointment status in Firestore", e)
                }
            }
        }
    }

    fun adjustProductStock(productId: String, delta: Int) {
        _products.update { list ->
            list.map {
                if (it.id == productId) {
                    val updatedQty = (it.stockQuantity + delta).coerceAtLeast(0)
                    it.copy(stockQuantity = updatedQty)
                } else it
            }
        }
    }

    private fun calculateFinancialSummary(appointmentList: List<Appointment>): FinancialSummary {
        val todayAppointments = appointmentList.filter { it.date.equals("Hoje", ignoreCase = true) }
        val confirmedOrDone = todayAppointments.filter {
            it.status == AppointmentStatus.CONFIRMADO ||
            it.status == AppointmentStatus.EM_ATENDIMENTO ||
            it.status == AppointmentStatus.CONCLUIDO
        }

        val revenueToday = confirmedOrDone.sumOf { it.price }.coerceAtLeast(18450.0)
        val pendingReceivables = todayAppointments
            .filter { it.status == AppointmentStatus.CONFIRMADO }
            .sumOf { it.price }
            .coerceAtLeast(4200.0)

        val ticketAverage = if (confirmedOrDone.isNotEmpty()) {
            revenueToday / confirmedOrDone.size
        } else {
            485.0
        }

        val occupancyRate = (70 + (confirmedOrDone.size * 3)).coerceIn(60, 98)

        return FinancialSummary(
            revenueToday = revenueToday,
            revenueMonth = 342800.0 + revenueToday,
            ticketAverage = ticketAverage,
            occupancyRate = occupancyRate,
            growthMomPercentage = 16.4,
            pendingReceivables = pendingReceivables,
            pixTotal = revenueToday * 0.45,
            cardBlackTotal = revenueToday * 0.40,
            debitTotal = revenueToday * 0.12,
            cashTotal = revenueToday * 0.03,
            weeklyChart = listOf(
                DailyRevenuePoint("Seg", 12400.0),
                DailyRevenuePoint("Ter", 14800.0),
                DailyRevenuePoint("Qua", 16200.0),
                DailyRevenuePoint("Qui", 17900.0),
                DailyRevenuePoint("Sex", 24300.0),
                DailyRevenuePoint("Sáb", 28900.0),
                DailyRevenuePoint("Dom", revenueToday)
            )
        )
    }

    private fun getInitialServices() = listOf(
        SalonService(
            id = "srv_01",
            name = "Corte Haute Couture & Visagismo",
            category = ServiceCategory.CABELO,
            description = "Diagnóstico biométrico facial, lavagem terapêutica com massagem craniana e corte de alta precisão.",
            durationMinutes = 60,
            price = 380.0,
            rating = 5.0f,
            isFeatured = true,
            badge = "Assinatura"
        ),
        SalonService(
            id = "srv_02",
            name = "Balayage Soleil & Infusão de Caviar",
            category = ServiceCategory.COLORACAO,
            description = "Clareamento artístico personalizado preservando a fibra capilar, seguido de infusão de caviar Kérastase.",
            durationMinutes = 180,
            price = 1250.0,
            rating = 4.9f,
            isFeatured = true,
            badge = "Mais Desejado"
        ),
        SalonService(
            id = "srv_03",
            name = "Spa Facial Ouro 24k & Peptídeos",
            category = ServiceCategory.SPA_FACIAL,
            description = "Tratamento facial rejuvenescedor com máscara de lâminas de ouro 24k, drenagem linfática e bioestimulação.",
            durationMinutes = 75,
            price = 650.0,
            rating = 5.0f,
            isFeatured = true,
            badge = "Exclusivo"
        ),
        SalonService(
            id = "srv_04",
            name = "Terapia Chronologiste Kérastase",
            category = ServiceCategory.CABELO,
            description = "Ritual regenerador supremo para couro cabeludo e fios, com pérolas de caviar mimetizado e vitamina E.",
            durationMinutes = 90,
            price = 520.0,
            rating = 4.9f,
            isFeatured = false
        ),
        SalonService(
            id = "srv_05",
            name = "Manicure Russa & Banho de Cristal",
            category = ServiceCategory.NAILS,
            description = "Cutilagem a seco combinada com esmaltação em gel de alta durabilidade e hidratação de seda.",
            durationMinutes = 60,
            price = 240.0,
            rating = 4.8f,
            isFeatured = false
        ),
        SalonService(
            id = "srv_06",
            name = "Maquiagem Red Carpet & Noivas",
            category = ServiceCategory.MAKEUP,
            description = "Preparação de pele de alta definição com produtos Charlotte Tilbury e Tom Ford, fixação 24h à prova de lágrimas.",
            durationMinutes = 90,
            price = 780.0,
            rating = 5.0f,
            isFeatured = true,
            badge = "VIP"
        )
    )

    private fun getInitialStylists() = listOf(
        Stylist(
            id = "sty_01",
            name = "Jean-Pierre Laurent",
            role = "Master Hair Designer",
            title = "Formado na Académie L'Oréal Paris",
            specialties = listOf("Visagismo Tridimensional", "Cortes Franceses", "Texturas Naturais"),
            rating = 5.0f,
            reviewsCount = 412,
            experienceYears = 16,
            avatarInitials = "JP"
        ),
        Stylist(
            id = "sty_02",
            name = "Isabella Fontana",
            role = "Head Colorist & Blonde Specialist",
            title = "Especialista em Mechas Balayage",
            specialties = listOf("Loiros Personalizados", "Morena Iluminada", "Correção de Cor"),
            rating = 4.9f,
            reviewsCount = 380,
            experienceYears = 12,
            avatarInitials = "IF"
        ),
        Stylist(
            id = "sty_03",
            name = "Rodrigo Alencar",
            role = "Creative Stylist & Visagist",
            title = "Embaixador de Estilo Editorial",
            specialties = listOf("Penteados Red Carpet", "Corte Masculino Executivo", "Terapia Capilar"),
            rating = 4.9f,
            reviewsCount = 295,
            experienceYears = 10,
            avatarInitials = "RA"
        ),
        Stylist(
            id = "sty_04",
            name = "Camila Sampaio",
            role = "Beauty & Facial Spa Director",
            title = "Especialista em Cosmetologia Avançada",
            specialties = listOf("Spa Facial 24k", "Lash Lifting Ouro", "Maquiagem Noivas"),
            rating = 5.0f,
            reviewsCount = 340,
            experienceYears = 14,
            avatarInitials = "CS"
        )
    )

    private fun getInitialAppointments(userId: String) = listOf(
        Appointment(
            id = "apt_101",
            userId = userId,
            clientName = "Helena Vilela de Medeiros",
            clientPhone = "(11) 99841-2200",
            clientIsVip = true,
            serviceName = "Balayage Soleil & Infusão de Caviar",
            stylistName = "Isabella Fontana",
            date = "Hoje",
            time = "10:30",
            status = AppointmentStatus.EM_ATENDIMENTO,
            price = 1250.0,
            notes = "Cliente Maison Black. Servir espumante Brut e café especial."
        ),
        Appointment(
            id = "apt_102",
            userId = userId,
            clientName = "Eduardo Silveira Prado",
            clientPhone = "(11) 98712-3344",
            clientIsVip = false,
            serviceName = "Corte Haute Couture & Visagismo",
            stylistName = "Jean-Pierre Laurent",
            date = "Hoje",
            time = "11:00",
            status = AppointmentStatus.CONFIRMADO,
            price = 380.0,
            notes = "Primeira visita. Indicado pelo Dr. Marcondes."
        ),
        Appointment(
            id = "apt_103",
            userId = userId,
            clientName = "Sophia Albuquerque",
            clientPhone = "(11) 99234-5566",
            clientIsVip = true,
            serviceName = "Spa Facial Ouro 24k & Peptídeos",
            stylistName = "Camila Sampaio",
            date = "Hoje",
            time = "13:30",
            status = AppointmentStatus.CONFIRMADO,
            price = 650.0,
            notes = "Cliente Maison Platinum. Alérgica a fragrâncias cítricas."
        ),
        Appointment(
            id = "apt_104",
            userId = userId,
            clientName = "Beatriz de Orleans",
            clientPhone = "(11) 98111-9988",
            clientIsVip = true,
            serviceName = "Maquiagem Red Carpet & Noivas",
            stylistName = "Rodrigo Alencar",
            date = "Hoje",
            time = "15:00",
            status = AppointmentStatus.CONFIRMADO,
            price = 780.0,
            notes = "Evento de gala na Sociedade Hípica Paulista."
        ),
        Appointment(
            id = "apt_105",
            userId = userId,
            clientName = "Mariana Castilho",
            clientPhone = "(11) 97654-3210",
            clientIsVip = true,
            serviceName = "Manicure Russa & Banho de Cristal",
            stylistName = "Camila Sampaio",
            date = "Hoje",
            time = "09:00",
            status = AppointmentStatus.CONCLUIDO,
            price = 240.0,
            notes = "Pagamento via PIX realizado com sucesso."
        ),
        Appointment(
            id = "apt_106",
            userId = userId,
            clientName = "Dr. Marcelo Faria",
            clientPhone = "(11) 99456-7890",
            clientIsVip = false,
            serviceName = "Corte Haute Couture & Visagismo",
            stylistName = "Jean-Pierre Laurent",
            date = "Hoje",
            time = "09:30",
            status = AppointmentStatus.CONCLUIDO,
            price = 380.0,
            notes = "Finalizado com produto Kérastase Homme."
        )
    )

    private fun getInitialProducts() = listOf(
        Product(
            id = "prod_01",
            name = "Kérastase Elixir Ultime L'Huile Originale",
            brand = "Kérastase Paris",
            category = "Óleo Capilar",
            description = "Óleo sublimador com óleo de camélia francesa para brilho extremo e proteção térmica 230°C.",
            price = 390.0,
            stockQuantity = 14,
            minStockAlert = 5,
            isExclusive = true
        ),
        Product(
            id = "prod_02",
            name = "Chronologiste Masque Régénérant",
            brand = "Kérastase Paris",
            category = "Máscara Regeneradora",
            description = "Tratamento profundo com ácido hialurônico, abyssine e vitamina E para revitalização capilar.",
            price = 540.0,
            stockQuantity = 8,
            minStockAlert = 4,
            isExclusive = true
        ),
        Product(
            id = "prod_03",
            name = "Huile De Parfum Chronologiste",
            brand = "Kérastase Paris",
            category = "Fragrância Capilar",
            description = "Perfume em óleo refinado criado pelo mestre perfumista Alberto Morillas com notas de rosa chá e cedro.",
            price = 480.0,
            stockQuantity = 3,
            minStockAlert = 5,
            isExclusive = true
        ),
        Product(
            id = "prod_04",
            name = "Sérum Anti-Chute Fortifiant Genesis",
            brand = "Kérastase Paris",
            category = "Sérum Fortificante",
            description = "Sérum celular diário fortalecedor antiqueda para couro cabeludo com células nativas de edelweiss.",
            price = 420.0,
            stockQuantity = 11,
            minStockAlert = 4,
            isExclusive = false
        ),
        Product(
            id = "prod_05",
            name = "Maison de Beauté Brume D'Ambiance Gold",
            brand = "Maison de Beauté",
            category = "Home Fragrance",
            description = "Aromatizador de ambiente assinatura do salão com âmbar champanhe e flores brancas.",
            price = 280.0,
            stockQuantity = 19,
            minStockAlert = 6,
            isExclusive = true
        )
    )

    private fun getInitialClients() = listOf(
        ClientVip(
            id = "cli_01",
            name = "Helena Vilela de Medeiros",
            phone = "(11) 99841-2200",
            email = "helena.medeiros@luxmail.com",
            tier = "Maison Black",
            visitsCount = 42,
            totalSpent = 38400.0,
            lastVisit = "Hoje"
        ),
        ClientVip(
            id = "cli_02",
            name = "Sophia Albuquerque",
            phone = "(11) 99234-5566",
            email = "sophia.albuquerque@advocacia.com.br",
            tier = "Maison Platinum",
            visitsCount = 28,
            totalSpent = 21900.0,
            lastVisit = "Hoje"
        ),
        ClientVip(
            id = "cli_03",
            name = "Beatriz de Orleans",
            phone = "(11) 98111-9988",
            email = "beatriz.orleans@institutob.org",
            tier = "Maison Black",
            visitsCount = 56,
            totalSpent = 54200.0,
            lastVisit = "12/10/2026"
        ),
        ClientVip(
            id = "cli_04",
            name = "Dra. Carolina Brandão",
            phone = "(11) 97722-1133",
            email = "carolina.brandao@clinica.med.br",
            tier = "Maison Gold",
            visitsCount = 18,
            totalSpent = 12600.0,
            lastVisit = "08/10/2026"
        )
    )

    private fun seedInitialPortfolio() {
        val firestore = db ?: return
        val initial = getInitialPortfolio()
        _portfolio.value = initial
        repositoryScope.launch {
            initial.forEach { item ->
                val data = mapOf(
                    "title" to item.title,
                    "category" to item.category,
                    "imageUrl" to item.imageUrl,
                    "mediaType" to item.mediaType,
                    "stylistName" to item.stylistName,
                    "description" to item.description,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("portfolio").document(item.id).set(data)
                    .addOnFailureListener { e ->
                        Log.e("SalonRepository", "Error seeding portfolio item ${item.id}", e)
                    }
            }
        }
    }

    fun addPortfolioItem(title: String, category: String, mediaUri: String, mediaType: String, stylistName: String, description: String) {
        val firestore = db ?: return
        val newItemId = "port_" + System.currentTimeMillis()
        repositoryScope.launch {
            val data = mapOf(
                "title" to title,
                "category" to category,
                "imageUrl" to mediaUri,
                "mediaType" to mediaType,
                "stylistName" to stylistName,
                "description" to description,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("portfolio").document(newItemId).set(data)
                .addOnFailureListener { e ->
                    Log.e("SalonRepository", "Error adding portfolio item", e)
                }
        }
    }

    private fun getInitialPortfolio(): List<PortfolioItem> = listOf(
        PortfolioItem(
            id = "port_1",
            title = "Balayage Luminous Gold",
            category = "Coloração",
            imageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?auto=format&fit=crop&q=80&w=800",
            stylistName = "Marc Antoine",
            description = "Iluminação multidimensional com reflexos dourados e tratamento gloss."
        ),
        PortfolioItem(
            id = "port_2",
            title = "Corte Bob Asimétrico & Visagismo",
            category = "Cabelos",
            imageUrl = "https://images.unsplash.com/photo-1560869713-7d0a29430803?auto=format&fit=crop&q=80&w=800",
            stylistName = "Élodie Laurent",
            description = "Corte moderno estruturado com acabamento sedoso."
        ),
        PortfolioItem(
            id = "port_3",
            title = "Spa Facial Diamond Rejuvenation",
            category = "Spa & Estética",
            imageUrl = "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&q=80&w=800",
            stylistName = "Sofia Vance",
            description = "Tratamento facial com infusão de peptídeos e máscara de ouro 24k."
        ),
        PortfolioItem(
            id = "port_4",
            title = "Nail Art Haute Couture & Spa",
            category = "Nails & Pés",
            imageUrl = "https://images.unsplash.com/photo-1632345031435-877ff6c2ae6a?auto=format&fit=crop&q=80&w=800",
            stylistName = "Camila Rossi",
            description = "Manicure russa com acabamento em gel perolizado e hidratação profunda."
        ),
        PortfolioItem(
            id = "port_5",
            title = "Noiva Haute Coiffure & Make",
            category = "Maquiagem",
            imageUrl = "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&q=80&w=800",
            stylistName = "Marc Antoine",
            description = "Produção completa para noivas com penteado escultural e maquiagem HD."
        )
    )
}

