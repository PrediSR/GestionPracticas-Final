package com.example.gestionpracticasfinal.modelos;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Practica {
    private int id_alumno;
    private int id_empresa;
    private LocalTime hEntrada;
    private LocalTime hSalida;
    private LocalDate fInicio;
    private LocalDate fFin;

    public Practica() {
    }

    public Practica(int id_alumno, int id_empresa, LocalTime hEntrada, LocalTime hSalida, LocalDate fInicio, LocalDate fFin)
            throws Exception {
        this.id_alumno = id_alumno;
        this.id_empresa = id_empresa;
        sethEntrada(hEntrada);
        sethSalida(hSalida);
        this.fInicio = fInicio;
        this.fFin = fFin;
    }

    public void calculaFfin() throws Exception {
        fFin = LocalDate.of(fInicio.getYear(), fInicio.getMonth(), fInicio.getDayOfMonth());
        int duracionPracticas = 400 * 60;
        int minutosDia = (int) ChronoUnit.MINUTES.between(hEntrada, hSalida);

        if (minutosDia < 0 || minutosDia > 510) {
            throw new Exception("Horario no valido, la hora de inicio debe ser antes de la hora de fin, no superior" +
                    " a 8:30 diarias");
        }

        do {
            if (duracionPracticas > minutosDia * 5) {
                duracionPracticas = duracionPracticas - (minutosDia * 5);
                fFin = fFin.plusWeeks(1);
            } else {
                if (fFin.getDayOfWeek() != DayOfWeek.SATURDAY && fFin.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    duracionPracticas = duracionPracticas - minutosDia;
                }
                fFin = fFin.plusDays(1);
            }
        }while (duracionPracticas > 0);
        fFin = fFin.minusDays(1);
    }

    public int getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(int id_alumno) {
        this.id_alumno = id_alumno;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public LocalTime gethEntrada() {
        return hEntrada;
    }

    public void sethEntrada(LocalTime hEntrada) throws Exception {
        if (hEntrada.isAfter(LocalTime.of(5, 0)) && hEntrada.isBefore(LocalTime.of(19, 0))) {
            this.hEntrada = hEntrada;
        } else {
            throw new Exception("hora de entrada no valida, no se permite que los alumnos entren antes de las 5:00 o" +
                    " despues de las 19:00");
        }
    }

    public LocalTime gethSalida() {
        return hSalida;
    }

    public void sethSalida(LocalTime hSalida) throws Exception {
        if (hSalida.isAfter(LocalTime.of(22, 0)) || hSalida.isBefore(LocalTime.of(8, 0))) {
            throw new Exception("hora de salida no valida, los alumnos no pueden hacer horario nocturno");
        } else {
            this.hSalida = hSalida;
        }
    }

    public LocalDate getfInicio() {
        return fInicio;
    }

    public void setfInicio(LocalDate fInicio) throws Exception {
        if (fInicio.getDayOfWeek() == DayOfWeek.SATURDAY || fInicio.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new Exception("Error, alumno empezando en fin de semana");
        } else {
            this.fInicio = fInicio;
        }
    }

    public LocalDate getfFin() {
        return fFin;
    }
}
