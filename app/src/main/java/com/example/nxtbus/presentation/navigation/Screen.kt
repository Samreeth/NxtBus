package com.example.nxtbus.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SearchResults : Screen("search_results/{fromCity}/{toCity}/{date}") {
        fun createRoute(fromCity: String, toCity: String, date: String) =
            "search_results/$fromCity/$toCity/${date.replace("/", "-")}"
    }
    object SeatSelection : Screen("seat_selection/{busId}/{fromCity}/{toCity}/{date}") {
        fun createRoute(busId: String, fromCity: String, toCity: String, date: String) =
            "seat_selection/$busId/$fromCity/$toCity/${date.replace("/", "-")}"
    }
    object PassengerDetails : Screen("passenger_details/{busId}/{selectedSeats}/{totalAmount}/{fromCity}/{toCity}/{date}") {
        fun createRoute(busId: String, selectedSeats: String, totalAmount: Int, fromCity: String, toCity: String, date: String) =
            "passenger_details/$busId/$selectedSeats/$totalAmount/$fromCity/$toCity/${date.replace("/", "-")}"
    }
    object BookingConfirmation : Screen("booking_confirmation/{pnr}") {
        fun createRoute(pnr: String) = "booking_confirmation/$pnr"
    }
    object TicketCancelled : Screen("ticket_cancelled/{pnr}") {
        fun createRoute(pnr: String) = "ticket_cancelled/$pnr"
    }
    object MyTickets : Screen("my_tickets")
    object Profile : Screen("profile")
}