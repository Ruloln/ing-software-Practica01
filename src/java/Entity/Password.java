/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "Password")
@NamedQueries({
    @NamedQuery(name = "Password.findAll", query = "SELECT p FROM Password p")})
public class Password implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "Numero de cuenta")
    private String numerodecuenta;
    @Size(max = 30)
    @Column(name = "pass")
    private String pass;
    @JoinColumn(name = "Numero de cuenta", referencedColumnName = "Numero de cuenta", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public Password() {
    }

    public Password(String numerodecuenta) {
        this.numerodecuenta = numerodecuenta;
    }

    public String getNumerodecuenta() {
        return numerodecuenta;
    }

    public void setNumerodecuenta(String numerodecuenta) {
        this.numerodecuenta = numerodecuenta;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Password)) {
            return false;
        }
        Password other = (Password) object;
        if ((this.numerodecuenta == null && other.numerodecuenta != null) || (this.numerodecuenta != null && !this.numerodecuenta.equals(other.numerodecuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Password[ numerodecuenta=" + numerodecuenta + " ]";
    }
    
}
