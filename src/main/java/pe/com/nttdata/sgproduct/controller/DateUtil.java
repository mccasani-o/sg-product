package pe.com.nttdata.sgproduct.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd";

    private DateUtil(){}

    public static LocalDate calcularDiaMovimiento() {
        LocalDate hoy = LocalDate.now();
        YearMonth fechaSiguiente = YearMonth.from(hoy).plusMonths(1); // Próximo mes
        return LocalDate.of(fechaSiguiente.getYear(), fechaSiguiente.getMonth(), 27); // Día 27
    }

    public static String localDateTimeToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return calcularDiaMovimiento().format(formatter);
    }




}
