

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {

        Banca.setReiniciarBanca();

        // Este objeto se usará como una especie de candado para sincronizar el acceso a
        // los hilos Jugador.
        Object lock = new Object();

        Random random = new Random();
        // se crearán entre 1 y 10 jugadores
        int numeroJugadores = random.nextInt(10) + 1;

        System.out.println("\n\n\t\tHan llegado al casino \033[33m\033[1m\033[4m" + numeroJugadores
                + " jugador/es\033[0m a apostar.\033[0m");
        System.out.println(
                "\033[31m\033[1m\033[4mOJO! La banca procesa los pagos poco a poco,"+
                "\nasi que puede que para uno no haya dinero pero para otro si,"+
                "\ndependerá de las apuestas procesadas hasta ese momento.\033[0m");

        crearJugadores(numeroJugadores, lock, Menu());

        crearCroupier(lock);

    }

    // Cada jugador hará una apuesta
    // Eligen un numero al azar
    // Seguirán asi hasta perder todo su dinero
    public static void crearJugadores(int totalJugadores, Object lock, String Estrategia) {
        Jugador jugador;
        Thread thread;
        // Creamos tantos hilos/jugadores como se hayan metido por parametros
        // todos harán las apuestas a la vez
        for (int i = 0; i < totalJugadores; i++) {
            if (Estrategia.equals("Random")) {
                jugador = new Jugador(lock, EstrategiaRandom());
            } else {
                jugador = new Jugador(lock, Estrategia);
            }

            thread = new Thread(jugador);
            thread.setName("Jugador " + (i + 1)); // Asigna el número de hilo al nombre del hilo
            thread.start();
        }

    }

    // Creamos e iniciamos el hilo croupier
    // se encargará de girar la ruleta y el resultado meterlo en la clase Banca
    // tambien despertará a todos aquellos que esperan con el objecto Lock
    public static void crearCroupier(Object lock) {
        Croupier ruleta = new Croupier(lock);
        new Thread(ruleta).start();
    }

    public static String Menu() {
        Scanner teclado = new Scanner(System.in);
        int Estrategia = 0;
        String strEstrategia = "";

        while (true) {
            System.out.println(
                    "Elige qué estrategia quieres que sigan los jugadores:\033[33m\033[1m" +
                            "\n\t1-Apostar a un numero en concreto(elegido al azar por los jugadores)." +
                            "\n\t2-Apostar a pares o impares." +
                            "\n\t3-Estrategia martingala." +
                            "\n\t4-Cada jugador con estrategias distintas.\033[0m");

            Estrategia = teclado.nextInt();

            switch (Estrategia) {
                case 1:
                    strEstrategia = "NumeroConcreto";
                    break;
                case 2:
                    strEstrategia = "Pares/Impares";
                    break;
                case 3:
                    strEstrategia = "Martingala";
                    break;
                case 4:
                    strEstrategia = "Random";
                    break;
            }

            // limpiamos la consola antes de que los jugadores inicien
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // si introdujo algun numero valido, se saldrá dle menu
            if (Estrategia > 0 && Estrategia < 5) {
                break;
            }

        }

        teclado.close();

        return strEstrategia;
    }

    public static String EstrategiaRandom() {
        // Crea una lista de strings
        List<String> list = Arrays.asList("NumeroConcreto", "Pares/Impares", "Martingala");

        // Crea un objeto Random
        Random random = new Random();

        // Genera un número aleatorio entre 0 y la longitud de la lista
        int index = random.nextInt(list.size());

        // Obtiene el elemento en la posición aleatoria de la lista
        String randomString = list.get(index);

        // Devuelve el elemento seleccionado al azar
        return randomString;
    }

}
