package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static Map<String, Integer> disponibilita = new HashMap<>();
    public static Set<String> usernamesConnessi = new HashSet<>();

    public static void main(String[] args) throws IOException {
        disponibilita.put("Gold", 10);
        disponibilita.put("Pit", 30);
        disponibilita.put("Parterre", 60);

        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("Server avviato sulla porta 3000...");

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("WELCOME");
                String username = null;
                String linea;

                while ((linea = in.readLine()) != null) {
                    linea = linea.trim();

                    if (username == null) {
                        if (linea.startsWith("LOGIN ")) {
                            String tentativo = linea.substring(6).trim();
                            synchronized (usernamesConnessi) {
                                if (usernamesConnessi.contains(tentativo)) {
                                    out.println("ERR USERINUSE");
                                } else {
                                    username = tentativo;
                                    usernamesConnessi.add(username);
                                    out.println("OK");
                                }
                            }
                        } else if (linea.equals("QUIT")) {
                            out.println("BYE");
                            break;
                        } else {
                            out.println("ERR LOGINREQUIRED");
                        }
                    } else {
                        if (linea.equals("N")) {
                            synchronized (disponibilita) {
                                out.println("AVAIL Gold:" + disponibilita.get("Gold") + " Pit:" + disponibilita.get("Pit") + " Parterre:" + disponibilita.get("Parterre"));
                            }
                        } else if (linea.equals("BUY")) {
                            String rigaDati = in.readLine().trim();
                            String[] parti = rigaDati.split(" ");
                            String tipo = parti[0];
                            int qta = Integer.parseInt(parti[1]);

                            synchronized (disponibilita) {
                                if (disponibilita.containsKey(tipo) && disponibilita.get(tipo) >= qta) {
                                    disponibilita.put(tipo, disponibilita.get(tipo) - qta);
                                    out.println("OK");
                                } else {
                                    out.println("KO");
                                }
                            }
                        } else if (linea.equals("QUIT")) {
                            out.println("BYE");
                            break;
                        } else {
                            out.println("ERR UNKNOWNCMD");
                        }
                    }
                }

                if (username != null) {
                    synchronized (usernamesConnessi) {
                        usernamesConnessi.remove(username);
                    }
                }
                socket.close();

            } catch (Exception e) {
                System.out.println("Client disconnesso.");
            }
        }
    }
}
