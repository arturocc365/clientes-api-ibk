package com.ibk.clientesapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("clientes")
public class Cliente implements Persistable<String> {

    @Id
    private String id;

    @Column("nombre")
    private String nombre;

    @Column("apellido_paterno")
    private String apellidoPaterno;

    @Column("apellido_materno")
    private String apellidoMaterno;

    @Column("fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @Column("activo")
    private boolean activo;

    @Transient
    private boolean esNuevo;

    /** Constructor para CREAR (INSERT) */
    public static Cliente nuevo(String id, String nombre, String apellidoPaterno,
                                String apellidoMaterno, OffsetDateTime fechaCreacion, boolean activo) {
        return new Cliente(id, nombre, apellidoPaterno, apellidoMaterno, fechaCreacion, activo, true);
    }

    /** Constructor para ACTUALIZAR (UPDATE) */
    public static Cliente existente(String id, String nombre, String apellidoPaterno,
                                    String apellidoMaterno, OffsetDateTime fechaCreacion, boolean activo) {
        return new Cliente(id, nombre, apellidoPaterno, apellidoMaterno, fechaCreacion, activo, false);
    }

    /** Constructor general */
    public Cliente(String id, String nombre, String apellidoPaterno, String apellidoMaterno,
                   OffsetDateTime fechaCreacion, boolean activo, boolean esNuevo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaCreacion = fechaCreacion;
        this.activo = activo;
        this.esNuevo = esNuevo;
    }

    /** Constructor usado por Spring Data R2DBC al leer desde base de datos */
    @PersistenceCreator
    public Cliente(String id, String nombre, String apellidoPaterno, String apellidoMaterno,
                   OffsetDateTime fechaCreacion, boolean activo) {
        this(id, nombre, apellidoPaterno, apellidoMaterno, fechaCreacion, activo, false);
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean isNew() { return esNuevo; }

    public String id() { return id; }
    public String nombre() { return nombre; }
    public String apellidoPaterno() { return apellidoPaterno; }
    public String apellidoMaterno() { return apellidoMaterno; }
    public OffsetDateTime fechaCreacion() { return fechaCreacion; }
    public boolean activo() { return activo; }

    public String nombreCompleto() {
        String materno = apellidoMaterno == null ? "" : apellidoMaterno;
        return String.join(" ", nombre, apellidoPaterno, materno).trim().replaceAll("\\s+", " ");
    }
}
