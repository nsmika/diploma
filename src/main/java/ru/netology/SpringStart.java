package ru.netology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootApplication
public class SpringStart {
    static Calendar calendar = new GregorianCalendar();
    public static void main(String[] args) {
        System.out.printf("Сервер запущен: %s", calendar.getTime());
        SpringApplication.run(SpringStart.class, args);
    }
}