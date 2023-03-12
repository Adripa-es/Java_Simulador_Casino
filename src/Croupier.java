
import java.util.Random;

public class Croupier implements Runnable {
    private final Random random;
    private final Object lock;

    public Croupier(Object lock) {
        this.lock = lock;
        this.random = new Random();
    }

    public void run() {
        int numeroRuleta, contadorRondas = 0;

        while (Banca.getJugadoresVivos() > 0 && Banca.getbancaConDinero()) {

            // Duerme durante 3 segundos
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Genera un número aleatorio entre 0 y 36
            numeroRuleta = random.nextInt(36) + 1;

            Banca.setNumeroRuleta(numeroRuleta);
            contadorRondas++;

            if (!Banca.getbancaConDinero()) {
                System.out.println("\n\n\t\t\t\033[35m \033[1m---> La banca no tiene dinero suficiente, echando a los jugadores del casino...<---\033[0m");
            } else {
                // Despierta a todos los hilos que estén esperando en el objeto lock
                synchronized (lock) {
                    System.out.println(
                            "\033[36m|===============================================================================================================================================|\033[0m");
                    System.out.println(
                            "\033[1m\033[32m\t\t|>>==Saldo banca: " + Banca.getSaldoBanca() + "=====Numero Ruleta: "
                                    + numeroRuleta + "=====Total Jugadores Vivos: " + Banca.getJugadoresVivos()
                                    + "=====Ronda Actual: " + contadorRondas + "==<<|\033[0m");
                    System.out.println(
                            "\033[1m\033[36m|=======|========ESTRATEGIA=====|========JUGADOR========|=========SALDO=========|==APUESTA EN===|===========RESULTADO===========|===============|\033[0m");

                    lock.notifyAll();
                }
            }

        }

    }

}


