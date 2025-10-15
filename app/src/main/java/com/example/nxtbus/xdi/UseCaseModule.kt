package com.example.nxtbus.xdi

import com.example.nxtbus.domain.repository.IBookingRepository
import com.example.nxtbus.domain.repository.IBusRepository
import com.example.nxtbus.domain.usecase.BookTicketUseCase
import com.example.nxtbus.domain.usecase.GetSeatsUseCase
import com.example.nxtbus.domain.usecase.SearchBusesUseCase
import com.example.nxtbus.domain.usecase.ValidatePassengerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSearchBusesUseCase(
        busRepository: IBusRepository
    ): SearchBusesUseCase {
        return SearchBusesUseCase(busRepository)
    }

    @Provides
    @Singleton
    fun provideGetSeatsUseCase(
        busRepository: IBusRepository
    ): GetSeatsUseCase {
        return GetSeatsUseCase(busRepository)
    }

    @Provides
    @Singleton
    fun provideBookTicketUseCase(
        bookingRepository: IBookingRepository
    ): BookTicketUseCase {
        return BookTicketUseCase(bookingRepository)
    }

    @Provides
    @Singleton
    fun provideValidatePassengerUseCase(): ValidatePassengerUseCase {
        return ValidatePassengerUseCase()
    }
}