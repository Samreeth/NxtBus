package com.example.nxtbus.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.example.nxtbus.presentation.screens.e_confirmation.BookingConfirmationScreen
import com.example.nxtbus.presentation.screens.a_home.HomeScreen
import com.example.nxtbus.presentation.screens.d_passenger.PassengerDetailsScreen
import com.example.nxtbus.presentation.screens.g_profile.ProfileScreen
import com.example.nxtbus.presentation.screens.b_search.SearchResultsScreen
import com.example.nxtbus.presentation.screens.c_seats.SeatSelectionScreen
import com.example.nxtbus.presentation.screens.f_tickets.MyTicketsScreen
import com.example.nxtbus.presentation.screens.e_confirmation.TicketCancelledScreen

@Composable
fun NxtBusNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Search Results Screen
        composable(
            route = "search_results/{fromCity}/{toCity}/{date}",
            arguments = listOf(
                navArgument("fromCity") { type = NavType.StringType },
                navArgument("toCity") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            SearchResultsScreen(navController = navController)
        }

        // Seat Selection Screen
        composable(
            route = "seat_selection/{busId}/{fromCity}/{toCity}/{date}",
            arguments = listOf(
                navArgument("busId") { type = NavType.StringType },
                navArgument("fromCity") { type = NavType.StringType },
                navArgument("toCity") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            SeatSelectionScreen(navController = navController)
        }

        // Passenger Details Screen
        composable(
            route = "passenger_details/{busId}/{selectedSeats}/{totalAmount}/{fromCity}/{toCity}/{date}",
            arguments = listOf(
                navArgument("busId") { type = NavType.StringType },
                navArgument("selectedSeats") { type = NavType.StringType },
                navArgument("totalAmount") { type = NavType.StringType },
                navArgument("fromCity") { type = NavType.StringType },
                navArgument("toCity") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            PassengerDetailsScreen(navController = navController)
        }

        // Booking Confirmation Screen
        composable(
            route = "booking_confirmation/{pnr}",
            arguments = listOf(
                navArgument("pnr") { type = NavType.StringType }
            )
        ) {
            BookingConfirmationScreen(navController = navController)
        }

        // My Tickets Screen
        composable(route = Screen.MyTickets.route) {
            MyTicketsScreen(navController = navController)
        }

        // Profile Screen
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // Ticket Cancelled Screen
        composable(
            route = "ticket_cancelled/{pnr}",
            arguments = listOf(
                navArgument("pnr") { type = NavType.StringType }
            )
        ) {
            TicketCancelledScreen(navController = navController)
        }
    }
}