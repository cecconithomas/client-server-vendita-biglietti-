package com.example;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 3000;

        try (
            Socket socket = new Socket(host, porta);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("SERVER -> " + in.readLine());

            System.out.print("Inserisci il tuo username per il LOGIN: ");
            String user = tastiera.readLine();
            out.println("LOGIN " + user);
            System.out.println("SERVER -> " + in.readLine());

            System.out.println("\nComandi disponibili: N (disponibilità), BUY (acquista), QUIT (esci)");

            while (true) {
                System.out.print("\n> ");
                String comando = tastiera.readLine();
                if (comando == null) break;

                out.println(comando);

                if (comando.equalsIgnoreCase("BUY")) {
                    System.out.print("Inserisci Tipo e Quantità (es. Gold 2): ");
                    String dettaglio = tastiera.readLine();
                    out.println(dettaglio);
                }

                String risposta = in.readLine();
                if (risposta == null) break;
                System.out.println("SERVER -> " + risposta);

                if (comando.equalsIgnoreCase("QUIT") || risposta.equals("BYE")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Errore di connessione al server.");
        }
    }
}