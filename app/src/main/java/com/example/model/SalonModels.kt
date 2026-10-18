package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppointmentStatus(val label: String) {
    CONFIRMADO("Confirmado"),
    EM_ATENDIMENTO("Em Atendimento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado")
}

enum class ServiceCategory(val label: String) {
    TODOS("Todos"),
    CABELO("Cabelos & Visagismo"),
    COLORACAO("Alta Coloração"),
    SPA_FACIAL("Spa & Estética"),
    NAILS("Nails & Spa Pés"),
    MAKEUP("Maquiagem & Noivas")
}

@IgnoreExtraProperties
data class SalonService(
    @DocumentId val id: String = "",
    val name: String = "",
    val categoryName: String = ServiceCategory.CABELO.name,
    val description: String = "",
    val durationMinutes: Int = 60,
    val price: Double = 0.0,
    val rating: Float = 5.0f,
    val isFeatured: Boolean = false,
    val badge: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val category: ServiceCategory
        get() = try {
            ServiceCategory.valueOf(categoryName)
        } catch (_: Exception) {
            ServiceCategory.CABELO
        }

    constructor(
        id: String,
        name: String,
        category: ServiceCategory,
        description: String,
        durationMinutes: Int,
        price: Double,
        rating: Float = 5.0f,
        isFeatured: Boolean = false,
        badge: String? = null
    ) : this(
        id = id,
        name = name,
        categoryName = category.name,
        description = description,
        durationMinutes = durationMinutes,
        price = price,
        rating = rating,
        isFeatured = isFeatured,
        badge = badge
    )
}

@IgnoreExtraProperties
data class Stylist(
    @DocumentId val id: String = "",
    val name: String = "",
    val role: String = "",
    val title: String = "",
    val specialties: List<String> = emptyList(),
    val rating: Float = 5.0f,
    val reviewsCount: Int = 0,
    val experienceYears: Int = 0,
    val avatarInitials: String = "",
    val isAvailable: Boolean = true
)

@IgnoreExtraProperties
data class Appointment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val clientIsVip: Boolean = false,
    val serviceName: String = "",
    val stylistName: String = "",
    val date: String = "Hoje",
    val time: String = "",
    val statusName: String = AppointmentStatus.CONFIRMADO.name,
    val price: Double = 0.0,
    val notes: String = "",
    val rating: Float = 0f,
    val reviewComment: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val status: AppointmentStatus
        get() = try {
            AppointmentStatus.valueOf(statusName)
        } catch (_: Exception) {
            AppointmentStatus.CONFIRMADO
        }

    constructor(
        id: String,
        clientName: String,
        clientPhone: String,
        clientIsVip: Boolean,
        serviceName: String,
        stylistName: String,
        date: String,
        time: String,
        status: AppointmentStatus,
        price: Double,
        notes: String = "",
        userId: String = "",
        rating: Float = 0f,
        reviewComment: String = ""
    ) : this(
        id = id,
        userId = userId,
        clientName = clientName,
        clientPhone = clientPhone,
        clientIsVip = clientIsVip,
        serviceName = serviceName,
        stylistName = stylistName,
        date = date,
        time = time,
        statusName = status.name,
        price = price,
        notes = notes,
        rating = rating,
        reviewComment = reviewComment
    )
}

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stockQuantity: Int = 0,
    val minStockAlert: Int = 0,
    val isExclusive: Boolean = true
)

@IgnoreExtraProperties
data class ClientVip(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val tier: String = "Maison Gold",
    val visitsCount: Int = 0,
    val totalSpent: Double = 0.0,
    val lastVisit: String = "Hoje",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class DailyRevenuePoint(
    val day: String,
    val revenue: Double
)

data class FinancialSummary(
    val revenueToday: Double,
    val revenueMonth: Double,
    val ticketAverage: Double,
    val occupancyRate: Int,
    val growthMomPercentage: Double,
    val pendingReceivables: Double,
    val pixTotal: Double,
    val cardBlackTotal: Double,
    val debitTotal: Double,
    val cashTotal: Double,
    val weeklyChart: List<DailyRevenuePoint>
)

@IgnoreExtraProperties
data class PortfolioItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Cabelos",
    val imageUrl: String = "",
    val mediaType: String = "IMAGE",
    val stylistName: String = "",
    val description: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

