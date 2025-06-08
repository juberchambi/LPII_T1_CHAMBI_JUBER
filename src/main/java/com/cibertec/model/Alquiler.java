package com.cibertec.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "alquileres")
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @OneToMany(mappedBy = "alquiler", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAlquiler> detalles = new ArrayList<>();

    public Alquiler() {}

    public Alquiler(Cliente cliente, Date fecha) {
        this.cliente = cliente;
        this.fecha = fecha;
    }

    public Alquiler(Cliente cliente, Date fecha, List<DetalleAlquiler> detalles) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.detalles = detalles;
        detalles.forEach(d -> d.setAlquiler(this)); 
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<DetalleAlquiler> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleAlquiler> detalles) {
        this.detalles = detalles;
    }

    public void addDetalle(DetalleAlquiler detalle) {
        detalles.add(detalle);
        detalle.setAlquiler(this);
    }

    public void removeDetalle(DetalleAlquiler detalle) {
        detalles.remove(detalle);
        detalle.setAlquiler(null);
    }

    @Override
    public String toString() {
        return "Alquiler{id=" + id + ", cliente=" + cliente + ", fecha=" + fecha + "}";
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
