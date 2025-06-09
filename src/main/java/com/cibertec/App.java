package com.cibertec;

import com.cibertec.model.*;
import com.cibertec.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EntityManager manager = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaccion = manager.getTransaction();

        try {
            // Agregar datos si no hay registros existentes
            transaccion.begin();

            long clientesRegistrados = manager.createQuery("SELECT COUNT(c) FROM Cliente c", Long.class).getSingleResult();
            if (clientesRegistrados == 0) {
                manager.persist(new Cliente("Carlos Quispe", "Carlos.Quispe@mail.com"));
                manager.persist(new Cliente("Karina Kalmet", "Karina.kal@mail.com"));
                manager.persist(new Cliente("Rocio Zambrano", "Rocio.Zam@mail.com"));
            }

            long peliculasRegistradas = manager.createQuery("SELECT COUNT(p) FROM Pelicula p", Long.class).getSingleResult();
            if (peliculasRegistradas == 0) {
                manager.persist(new Pelicula("Corazon Valiente", "Drama", 10));
                manager.persist(new Pelicula("Son como niños", "Familiar", 8));
                manager.persist(new Pelicula("El pianista", "Drama", 6));
            }

            transaccion.commit();

            // Mostrar clientes
            List<Cliente> listaClientes = manager.createQuery("FROM Cliente", Cliente.class).getResultList();
            System.out.println("== Registro exitoso ==");
            for (int i = 0; i < listaClientes.size(); i++) {
                System.out.printf("%d) %s\n", i + 1, listaClientes.get(i).getNombre());
            }

            System.out.print("Ingrese N° de cliente: ");
            int posCliente = Integer.parseInt(sc.nextLine()) - 1;

            if (posCliente < 0 || posCliente >= listaClientes.size()) {
                System.out.println("Cliente no existe.");
                return;
            }

            Cliente clienteElegido = listaClientes.get(posCliente);

            // Mostrar películas
            List<Pelicula> listaPeliculas = manager.createQuery("FROM Pelicula", Pelicula.class).getResultList();
            System.out.println("\n=== Disponibilidad de Películas ===");
            for (int i = 0; i < listaPeliculas.size(); i++) {
                Pelicula peli = listaPeliculas.get(i);
                System.out.printf("%d) %s - %s | Stock: %d\n", i + 1, peli.getTitulo(), peli.getGenero(), peli.getStock());
            }

            System.out.print("Seleccione una película: ");
            int posPelicula = Integer.parseInt(sc.nextLine()) - 1;

            if (posPelicula < 0 || posPelicula >= listaPeliculas.size()) {
                System.out.println("Película no válida.");
                return;
            }

            Pelicula peliElegida = listaPeliculas.get(posPelicula);

            // Solicitar cantidad
            System.out.print("Ingrese cuántas unidades desea alquilar: ");
            int unidades = Integer.parseInt(sc.nextLine());

            if (unidades <= 0) {
                System.out.println("La cantidad debe ser mayor a cero.");
                return;
            }

            if (unidades > peliElegida.getStock()) {
                System.out.println("No hay suficiente stock disponible. Stock actual: " + peliElegida.getStock());
                return;
            }

            // Registrar el alquiler
            transaccion.begin();

            Alquiler nuevo = new Alquiler();
            nuevo.setCliente(clienteElegido);
            nuevo.setFecha(LocalDate.now());
            nuevo.setEstado(EstadoAlquiler.ACTIVO);
            nuevo.setTotal(unidades);
            manager.persist(nuevo);

            manager.flush(); // Genera el ID del alquiler antes de asociarlo con el detalle

            DetalleAlquiler detalle = new DetalleAlquiler();
            detalle.setAlquiler(nuevo);
            detalle.setPelicula(peliElegida);
            detalle.setCantidad(unidades);
            detalle.setId(new DetalleAlquilerId(nuevo.getId(), peliElegida.getIdPelicula()));
            manager.persist(detalle);

            peliElegida.setStock(peliElegida.getStock() - unidades);
            manager.merge(peliElegida);

            transaccion.commit();

            System.out.println("\nRegistro exitoso del alquiler");
            System.out.println("Cliente: " + clienteElegido.getNombre());
            System.out.println("Película alquilada: " + peliElegida.getTitulo());
            System.out.println("Cantidad: " + unidades);
            System.out.println("Stock actualizado: " + peliElegida.getStock());

        } catch (Exception e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            System.out.println("Ocurrió un error durante el proceso.");
            e.printStackTrace();
        } finally {
            manager.close();
            JPAUtil.shutdown();
            sc.close();
            System.out.println("\nFin de la ejecución del sistema.");
        }
    }
}
