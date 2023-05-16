package com.example.gestionpracticasfinal.modelos;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

//metodo para guardar las practicas de una empresa
public class Practica {
    //atributos de la clase
    private int id_alumno;
    private int id_empresa;
    private LocalTime hEntrada;
    private LocalTime hSalida;
    private LocalDate fInicio;
    private LocalDate fFin;

    //constructor vacio
    public Practica() {
    }

    //constructor con todos los atributos
    public Practica(int id_alumno, int id_empresa, LocalTime hEntrada, LocalTime hSalida, LocalDate fInicio, LocalDate fFin)
            throws Exception {
        this.id_alumno = id_alumno;
        this.id_empresa = id_empresa;
        sethEntrada(hEntrada);
        sethSalida(hSalida);
        this.fInicio = fInicio;
        this.fFin = fFin;
    }

    //metodo que calcula la fecha de finalizacion de las practicas
    public void calculaFfin() throws Exception {
        LocalDate fFin;
        //primero recoge la fecha de inicio
        fFin = LocalDate.of(fInicio.getYear(), fInicio.getMonth(), fInicio.getDayOfMonth());
        //duracion total de las practicas 400 horas, 60 minutos cada hora
        int duracionPracticas = 400 * 60;
        //la cantidad de minutos diarios que hace el alumno en su jornada de trabajo se obtiene con la diferencia
        //entre hora entrada y salida
        int minutosDia = (int) ChronoUnit.MINUTES.between(hEntrada, hSalida);

        //si los minutos son menores a 1 o superiores a 510 (8 horas y media) suelta una excepcion
        if (minutosDia < 1 || minutosDia > 510) {
            throw new Exception("Horario no valido, la hora de inicio debe ser antes de la hora de fin, no superior" +
                    " a 8:30 diarias");
        }

        //bucle para calcular la fecha
        do {
            //mientras los minutos restantes de practicas sean superiores a 1 semana de minutos del alumno
            //se restaran los minutos semana a semana
            if (duracionPracticas > minutosDia * 5) {
                duracionPracticas = duracionPracticas - (minutosDia * 5);
                //se añade 1 semana a finalizacion en cada vuelta
                fFin = fFin.plusWeeks(1);
            } else {
                //una vez le quede menos de 1 semana de trabajo restara cada dia que no sean sabado o domingo
                if (fFin.getDayOfWeek() != DayOfWeek.SATURDAY && fFin.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    duracionPracticas = duracionPracticas - minutosDia;
                }
                //añade 1 dia por cada vuelta
                fFin = fFin.plusDays(1);
            }
            //una vez la duracion de las practicas llegue a su fin se termina el bucle
        }while (duracionPracticas > 0);

        //se asigna la fecha al atributo fFin con 1 dia menos del total, ya que al restar los dias finales añade
        //1 siempre y termina con 1 de sobra
        this.fFin = fFin.minusDays(1);
    }

    //getters y setters de la clase
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

    //setter de hora de entrada, no permite horas menores a 5am o superiores a 7pm, tira una excepcion si es asi
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

    //setter de hora salida, no permite superiores a 10pm ni inferiores a 8am
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

    //setter fecha de inicio no permite que un alumno comience sus practicas un dia del fin de semana
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
