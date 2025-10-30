package com.example;
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Esperando al jugador 2...");
        Socket socket = serverSocket.accept();
        System.out.println("Jugador 2 conectado.");

        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        char[][] tablero = crearTablero();
        colocarBarco(tablero);

        boolean juegoTerminado = false;

        while (!juegoTerminado) {
            // Tu turno
            System.out.print("Tu disparo (ej: A1): ");
            String disparo = teclado.readLine();
            salida.println(disparo);

            String resultado = entrada.readLine();
            System.out.println("Jugador 2 dice: " + resultado);
            if (resultado.equals("HUNDIDO")) {
                System.out.println("¡Ganaste!");
                juegoTerminado = true;
                break;
            }

            // Turno del oponente
            System.out.println("Esperando disparo del jugador 2...");
            String disparoOponente = entrada.readLine();
            System.out.println("Jugador 2 dispara a: " + disparoOponente);
            String respuesta = procesarDisparo(tablero, disparoOponente);
            salida.println(respuesta);
            if (respuesta.equals("HUNDIDO")) {
                System.out.println("¡Tu barco fue hundido! Fin del juego.");
                juegoTerminado = true;
            }
        }

        socket.close();
        serverSocket.close();
    }

    private static char[][] crearTablero() {
        char[][] t = new char[3][3];
        for (char[] fila : t) Arrays.fill(fila, '-');
        return t;
    }

    private static void colocarBarco(char[][] tablero) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Coloca tu barco (ej: A1): ");
        String coord = sc.nextLine().toUpperCase();
        int fila = coord.charAt(0) - 'A';
        int col = coord.charAt(1) - '1';
        tablero[fila][col] = 'B';
    }

    private static String procesarDisparo(char[][] tablero, String disparo) {
        int fila = disparo.charAt(0) - 'A';
        int col = disparo.charAt(1) - '1';
        if (tablero[fila][col] == 'B') {
            tablero[fila][col] = 'X';
            return "HUNDIDO";
        } else {
            return "AGUA";
        }
    }
}
