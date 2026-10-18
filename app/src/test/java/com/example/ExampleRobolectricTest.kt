package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SalonRepository
import com.example.model.AppointmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Maison Beauté", appName)
  }

  @Test
  fun `test salon repository booking and status update`() {
    val repository = SalonRepository()
    val services = repository.services.value
    val stylists = repository.stylists.value
    assertTrue(services.isNotEmpty())
    assertTrue(stylists.isNotEmpty())

    val apt = repository.bookAppointment(
      clientName = "Gisele Bündchen",
      clientPhone = "(11) 99999-8888",
      isVip = true,
      service = services.first(),
      stylist = stylists.first(),
      time = "16:00",
      notes = "Atendimento VIP"
    )

    assertNotNull(apt)
    assertEquals("Gisele Bündchen", apt.clientName)
    assertEquals(AppointmentStatus.CONFIRMADO, apt.status)

    repository.updateAppointmentStatus(apt.id, AppointmentStatus.EM_ATENDIMENTO)
    val updatedApt = repository.appointments.value.first { it.id == apt.id }
    assertEquals(AppointmentStatus.EM_ATENDIMENTO, updatedApt.status)
  }

  @Test
  fun `test stock adjustment in repository`() {
    val repository = SalonRepository()
    val product = repository.products.value.first()
    val initialStock = product.stockQuantity

    repository.adjustProductStock(product.id, -1)
    val updated = repository.products.value.first { it.id == product.id }
    assertEquals(initialStock - 1, updated.stockQuantity)
  }

  @Test
  fun `test new booking route and screen enum mapping`() {
    assertEquals("new_booking", SalonRoutes.NEW_BOOKING)
    assertEquals(SalonRoutes.NEW_BOOKING, com.example.ui.viewmodel.AppScreen.NEW_BOOKING.toRoute())
    assertEquals(com.example.ui.viewmodel.AppScreen.NEW_BOOKING, routeToAppScreen(SalonRoutes.NEW_BOOKING))
  }

  @Test
  fun `test custom date booking in repository`() {
    val repository = SalonRepository()
    val service = repository.services.value.first()
    val stylist = repository.stylists.value.first()

    val appointment = repository.bookAppointment(
      clientName = "Dra. Mariana Vasconcellos",
      clientPhone = "(11) 98765-4321",
      isVip = true,
      service = service,
      stylist = stylist,
      time = "14:15",
      notes = "Cortesia: Champagne Moët & Chandon",
      date = "Segunda (20/OUT)"
    )

    assertEquals("Dra. Mariana Vasconcellos", appointment.clientName)
    assertEquals("Segunda (20/OUT)", appointment.date)
    assertEquals("14:15", appointment.time)
    assertEquals(AppointmentStatus.CONFIRMADO, appointment.status)
    assertTrue(repository.appointments.value.any { it.id == appointment.id })
  }
}

