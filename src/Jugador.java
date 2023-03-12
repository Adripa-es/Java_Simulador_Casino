
import java.util.Random;

public class Jugador implements Runnable {
    private final Object lock;
    private final Random random;
    private final String Estrategia;
    private int saldoJugador = 1000;

    public Jugador(Object lock, String Estrategia) {
        this.lock = lock;
        this.random = new Random();
        this.Estrategia = Estrategia;
    }

    @Override
    public void run() {
        int numeroAleatorio;
        int valorApuesta = 10;

        // Obtiene el nombre del hilo
        String nombreHilo = Thread.currentThread().getName();

        // aumentamos la cantidad de jugadores que estan jugando
        Banca.setJugadoresVivos(1);

        // mientras haya saldo y la banca tenga dinero
        while (saldoJugador > valorApuesta && Banca.getbancaConDinero()) {
            // servirá para luego saber si ha perdido o ganado dinero
            // y saber de qué color poner sus resultados
            int saldoTemporal = saldoJugador;
            // se almacenaran los resultados de cada jugador tras hacer sus apuestas y
            // conocer el resultado

            // Espera a ser despertado por el hilo Croupier
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Genera un número aleatorio entre 1 y 36
            numeroAleatorio = random.nextInt(36) + 1;

            // se restan 10 Euros para hacer la apuesta
            saldoJugador -= valorApuesta;

            String tipoApuesta = "";
            String resultados = "";
            // sin importar la estrategia, si la ruleta saca un 0 entonces todos pierden
            if (Banca.getNumeroRuleta() == 0) {
                apuestaPerdida(valorApuesta);

            } else {
                // si el jugador apuesta a un nº concreto o juega a martingala
                if (Estrategia.equals("NumeroConcreto") || Estrategia.equals("Martingala")) {
                    if (Banca.getNumeroRuleta() == numeroAleatorio) {
                        apuestaGanada(360, valorApuesta);
                    } else if (Banca.getNumeroRuleta() != numeroAleatorio) {
                        apuestaPerdida(valorApuesta);
                        // si pierde usando el martingala,
                        // entonces el valor de la siguiente apuesta aumenta
                        if (Estrategia.equals("Martingala")) {
                            valorApuesta = valorApuesta * 2;
                        }

                    }
                }

                // si el jugador apuesta a pares/impares
                else if (Estrategia.equals("Pares/Impares")) {
                    // se aprovecha el numero generado por el jugador para definir si apuesta a
                    // pares o impares
                    // si el numero de la ruleta es par y la apuesta tambien o si ambos son impares
                    // entonces gana el jugador
                    if ((Banca.getNumeroRuleta() % 2 == 0 && numeroAleatorio % 2 == 0)
                            || (Banca.getNumeroRuleta() % 2 != 0 && numeroAleatorio % 2 != 0)) {
                        apuestaGanada(valorApuesta * 2, valorApuesta);
                    } else {
                        apuestaPerdida(valorApuesta);
                    }
                    if (numeroAleatorio % 2 == 0) {
                        tipoApuesta = "Par";
                    } else {
                        tipoApuesta = "Impar";
                    }

                }

            }

            resultados = crearResumenResultados(Estrategia, nombreHilo, saldoJugador, numeroAleatorio, tipoApuesta, saldoTemporal, valorApuesta);

            System.out.println(resultados);
        }
        if (saldoJugador < valorApuesta) {
            // el texto aparecerá en rojo y en negrita
            System.out.println("\t\t\t\033[35m \033[1m--->" + nombreHilo
                    + " se retira, sin saldo suficiente para apostar.<---\033[0m");
        }

        // restamos uno al numero de jugadores vivos
        Banca.setJugadoresVivos(-1);
    }

    // Función que se encarga de actualizar el saldo del jugador, de la banca, y
    // devolver el
    // resultado de la apuesta
    private void apuestaGanada(int ganancias, int valorApuesta) {
        // avisamos a la banca que tiene que pagar lo correspondiente(ganancias)
        saldoJugador += Banca.setPierdeBanca(ganancias, valorApuesta);
    }

    // Función que se encarga de actualizar el saldo del jugador, de la banca, y
    // devolver el
    // resultado de la apuesta
    private void apuestaPerdida(int valorApuesta) {
        Banca.setGanaBanca(valorApuesta);
    }

    // se introducirá toda la información de la ronda para crear el mensaje resumen
    // de la ronda del jugador
    private String crearResumenResultados(String estrategia, String nombreHilo, int saldo, int numeroAleatorio,
            String tipoApuesta, int saldoTemporal, int valorApuesta) {

        String resultados = "\t|\t" + Estrategia + " \t|\t" + nombreHilo + "\t|\t" + saldo + "\tEuros";
        String resultadoApuesta = "";
        // significa que no fue una apuesta de pares o impares
        if (tipoApuesta.equals("")) {
            resultados += "\t|\t" + numeroAleatorio;
        } else {
            resultados += "\t|\t" + tipoApuesta;
        }

        // si ganó pero la banca no tenia suficiente dinero para el premio
        if (saldoTemporal == saldo) {
            resultados = "\033[33m" + resultados;
            resultadoApuesta = "Banca sin dinero, apuesta devuelta";
        }
        // si el jugador perdió dinero, sus resultados saldrán en rojo
        else if (saldoTemporal > saldo) {
            resultados = "\033[31m" + resultados;
            resultadoApuesta = "Perdida -" + valorApuesta + " Euros";
        }
        // si ganó dinero, sus resultados serán en verde
        else if (saldoTemporal < saldo) {
            resultados = "\033[32m" + resultados;
            resultadoApuesta = "Ganada  +" + (saldo - saldoTemporal) + " Euros";
        }

        return resultados +=  "\t|\t" + resultadoApuesta + "\t|\033[0m";
    }

}
