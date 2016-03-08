/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author raul__000
 */
@Entity
@Table(name = "Usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "Numero de cuenta")
    private String numerodecuenta;
    @Size(max = 64)
    @Column(name = "Nombre")
    private String nombre;
    @Size(max = 30)
    @Column(name = "Correo")
    private String correo;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Password password;

    public Usuario() {
    }

    public Usuario(String numerodecuenta) {
        this.numerodecuenta = numerodecuenta;
    }

    public String getNumerodecuenta() {
        return numerodecuenta;
    }

    public void setNumerodecuenta(String numerodecuenta) {
        this.numerodecuenta = numerodecuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numerodecuenta != null ? numerodecuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.numerodecuenta == null && other.numerodecuenta != null) || (this.numerodecuenta != null && !this.numerodecuenta.equals(other.numerodecuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Usuario[ numerodecuenta=" + numerodecuenta + " ]";
    }
    
}
