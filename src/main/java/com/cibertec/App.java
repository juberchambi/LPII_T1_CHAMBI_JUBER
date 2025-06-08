package com.cibertec;

import java.util.Scanner;

import com.cibertec.model.Cliente;
import com.cibertec.model.Pelicula;
import com.cibertec.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.h2.tools.Server;

public class App {

    private static Server h2Server;

    private static Server iniciarServidorH2() {
        try {
            Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("H2 Web Console disponible en: http://localhost:8082");
            System.out.println("JDBC URL: jdbc:h2:mem:LPII_T1_CHAMBI_JUBER");
            System.out.println("Usuario: sa | Contraseña: (vacía)");
            return webServer;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al iniciar H2 Console");
            e.printStackTrace();
            return null;
        }
    }

    private static void pausar(Scanner scanner) {
        System.out.print("Presiona ENTER para continuar...");
        scanner.nextLine();
    }

    private static void detenerServidorH2() {
        if (h2Server != null) {
            h2Server.stop();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            h2Server = iniciarServidorH2();

            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            EntityTransaction tx = em.getTransaction();

            // CREAR
     
            tx.begin();

            Cliente cliente = new Cliente("carlos quispe", "carlos@example.com");
            Pelicula pelicula = new Pelicula("son como niños", "Comedia", 15);
            em.persist(cliente);
            em.persist(pelicula);
            

            Cliente cliente2 = new Cliente("Ana Torres", "ana@example.com");
            Pelicula pelicula2 = new Pelicula("Rápidos y Furiosos", "Acción", 18);
            em.persist(cliente2);
            em.persist(pelicula2);

            // Tercer cliente y película
            Cliente cliente3 = new Cliente("Luis Gómez", "luis@example.com");
            Pelicula pelicula3 = new Pelicula("Titanic", "Romance", 12);
            em.persist(cliente3);
            em.persist(pelicula3);
            
            tx.commit();

            System.out.println("Cliente y Película creados.");
            pausar(scanner);

           










            em.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JPAUtil.shutdown();
            detenerServidorH2();
            System.out.println(">>> APLICACIÓN FINALIZADA <<<");
            scanner.close();
        }
    }
}
