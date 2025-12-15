package ru.grigorii.calendar_reminder_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class CalendarReminderSystemApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CalendarReminderSystemApplication.class, args);
    }

}
