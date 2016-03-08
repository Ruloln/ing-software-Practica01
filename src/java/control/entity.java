/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author raul__000
 */
public final class entity {
    private static EntityManagerFactory emf= Persistence.createEntityManagerFactory("Practica01PU");
}
