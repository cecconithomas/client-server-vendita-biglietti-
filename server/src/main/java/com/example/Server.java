package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final Map<String, Integer> disponibilita = new HashMap<>();
    private static final Set<String> usernamesConnessi = new HashSet<>();

    public static void main(String[] args) {
        disponibilita.put("Gold", 10);
        disponibilita.put("Pit", 30);
        disponibilita.put("Parterre", 60);

        int porta = 3000;
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Server avviato sulla porta " + porta + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("WELCOME");

                boolean loggedIn = false;
                String linea;

                while ((linea = in.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;

                    String[] parti = linea.split(" ", 2);
                    String comando = parti[0].toUpperCase();

                    if (!loggedIn) {
                        if (comando.equals("LOGIN")) {
                            if (parti.length < 2 || parti[1].trim().isEmpty()) {
                                out.println("ERR SYNTAX");
                                continue;
                            }
                            String tentativoUser = parti[1].trim();
                            synchronized (usernamesConnessi) {
                                if (usernamesConnessi.contains(tentativoUser)) {
                                    out.println("ERR USERINUSE");
                                } else {
                                    username = tentativoUser;
                                    usernamesConnessi.add(username);
                                    loggedIn = true;
                                    out.println("OK");
                                }
                            }
                        } else if (comando.equals("QUIT")) {
                            out.println("BYE");
                            break;
                        } else {
                            out.println("ERR LOGINREQUIRED");
                        }
                        continue;
                    }

                    switch (comando) {
                        case "N":
                            synchronized (disponibilita) {
                                out.println("AVAIL Gold:" + disponibilita.get("Gold") + 
                                                  " Pit:" + disponibilita.get("Pit") + 
                                                  " Parterre:" + disponibilita.get("Parterre"));
                            }
                            break;

                        case "BUY":
                            String rigaAcquisto = in.readLine();
                            if (rigaAcquisto == null) break;
                            rigaAcquisto = rigaAcquisto.trim();
                            
                            String[] datiAcquisto = rigaAcquisto.split(" ");
                            if (datiAcquisto.length < 2) {
                                out.println("ERR SYNTAX");
                                break;
                            }

                            String tipo = datiAcquisto[0];
                            int quantita;
                            try {
                                quantita = Integer.parseInt(datiAcquisto[1]);
                            } catch (NumberFormatException e) {
                                out.println("ERR SYNTAX");
                                break;
                            }

                            synchronized (disponibilita) {
                                if (!disponibilita.containsKey(tipo)) {
                                    out.println("ERR UNKNOWNTYPE");
                                } else {
                                    int disp = disponibilita.get(tipo);
                                    if (quantita > 0 && disp >= quantita) {
                                        disponibilita.put(tipo, disp - quantita);
                                        out.println("OK");
                                    } else {
                                        out.println("KO");
                                    }
                                }
                            }
                            break;

                        case "QUIT":
                            out.println("BYE");
                            socket.close();
                            return;

                        default:
                            out.println("ERR UNKNOWNCMD");
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta con il client.");
            } finally {
                if (username != null) {
                    synchronized (usernamesConnessi) {
                        usernamesConnessi.remove(username);
                    }
                }
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}