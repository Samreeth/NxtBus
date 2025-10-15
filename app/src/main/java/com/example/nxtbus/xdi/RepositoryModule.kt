package com.example.nxtbus.xdi


import com.example.nxtbus.data.b_repository.BookingRepository
import com.example.nxtbus.data.b_repository.BusRepository
import com.example.nxtbus.domain.repository.IBookingRepository
import com.example.nxtbus.domain.repository.IBusRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBusRepository(
        busRepository: BusRepository
    ): IBusRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepository: BookingRepository
    ): IBookingRepository
}
