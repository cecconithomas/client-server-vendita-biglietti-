package com.example;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 3000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("SERVER -> " + in.readLine());

        while (true) {
            System.out.print("Inserisci il tuo username per il LOGIN: ");
            String user = tastiera.readLine();
            out.println("LOGIN " + user);
            
            String risposta = in.readLine();
            System.out.println("SERVER -> " + risposta);
            if ("OK".equals(risposta)) break;
            System.out.println("Login fallito. Riprova.");
        }

        System.out.println("\nComandi disponibili: N (disponibilità), BUY (acquista), QUIT (esci)");

        String comando;
        while (true) {
            System.out.print("\nInserisci comando: ");
            comando = tastiera.readLine();
            if (comando == null) break;
            comando = comando.trim();
            if (comando.isEmpty()) continue;

            if (comando.equalsIgnoreCase("BUY")) {
                out.println("BUY");
                System.out.print("Inserisci Tipo e Quantità (es. Gold 2): ");
                out.println(tastiera.readLine());
            } else {
                out.println(comando);
            }

            String risposta = in.readLine();
            if (risposta == null) break;
            System.out.println("SERVER -> " + risposta);

            if (comando.equalsIgnoreCase("QUIT") || "BYE".equals(risposta)) break;
        }

        tastiera.close();
        out.close();
        in.close();
        socket.close();
    }
}
