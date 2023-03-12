
public class Banca {
    private static int SaldoBanca;
    private static int NumeroRuleta;
    private static int JugadoresVivos = 0;
    private static boolean bancaConDinero = true;

    public static void setReiniciarBanca() {
        SaldoBanca = 50000;
    }

    // cuando el jugador gana se retira la parte correspondiente a la banca
    public static synchronized int setPierdeBanca(int Perdida, int valorApuesta) {

        //si lo que pierde la banca es mayor que el saldo que esta tiene
        //entonces dejamos avisado que la banca ya no tiene suficiente dinero
        //y devuelve esta funcion el dinero apostado por el jugador
        if (SaldoBanca < Perdida) {
            bancaConDinero = false;
            return valorApuesta;
        } else {
            SaldoBanca -= Perdida;
            return Perdida;
        }
    }

    // cuando el jugador pierde dinero, es añadido a la banca
    public static synchronized void setGanaBanca(int Ganancia) {
        SaldoBanca += Ganancia;
    }

    // obtener el saldo total de la banca
    public static int getSaldoBanca() {
        return SaldoBanca;
    }

    // para que el Croupier introduzca el numero de la ruleta
    public static void setNumeroRuleta(int NumeroRuleta) {
        Banca.NumeroRuleta = NumeroRuleta;
    }

    // obtener el numero de la ruleta
    public static int getNumeroRuleta() {
        return NumeroRuleta;
    }

    // servirá como contador de jugadores vivos
    public static synchronized void setJugadoresVivos(int JugadoresVivos) {
        Banca.JugadoresVivos += JugadoresVivos;
    }

    // obtener el total de jugadores vivos
    public static int getJugadoresVivos() {
        return JugadoresVivos;
    }

    public static boolean getbancaConDinero(){
        return bancaConDinero;
    }

}
