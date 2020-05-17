package com.example.androidtest.utils

import io.reactivex.Scheduler

class SchedulerProvider(
    val io: Scheduler,
    val computation: Scheduler,
    val main: Scheduler
)