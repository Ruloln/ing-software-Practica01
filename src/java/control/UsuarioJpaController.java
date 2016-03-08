/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entity.Password;
import Entity.Usuario;
import control.exceptions.IllegalOrphanException;
import control.exceptions.NonexistentEntityException;
import control.exceptions.PreexistingEntityException;
import control.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author raul__000
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Password password = usuario.getPassword();
            if (password != null) {
                password = em.getReference(password.getClass(), password.getNumerodecuenta());
                usuario.setPassword(password);
            }
            em.persist(usuario);
            if (password != null) {
                Usuario oldUsuarioOfPassword = password.getUsuario();
                if (oldUsuarioOfPassword != null) {
                    oldUsuarioOfPassword.setPassword(null);
                    oldUsuarioOfPassword = em.merge(oldUsuarioOfPassword);
                }
                password.setUsuario(usuario);
                password = em.merge(password);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsuario(usuario.getNumerodecuenta()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNumerodecuenta());
            Password passwordOld = persistentUsuario.getPassword();
            Password passwordNew = usuario.getPassword();
            List<String> illegalOrphanMessages = null;
            if (passwordOld != null && !passwordOld.equals(passwordNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Password " + passwordOld + " since its usuario field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (passwordNew != null) {
                passwordNew = em.getReference(passwordNew.getClass(), passwordNew.getNumerodecuenta());
                usuario.setPassword(passwordNew);
            }
            usuario = em.merge(usuario);
            if (passwordNew != null && !passwordNew.equals(passwordOld)) {
                Usuario oldUsuarioOfPassword = passwordNew.getUsuario();
                if (oldUsuarioOfPassword != null) {
                    oldUsuarioOfPassword.setPassword(null);
                    oldUsuarioOfPassword = em.merge(oldUsuarioOfPassword);
                }
                passwordNew.setUsuario(usuario);
                passwordNew = em.merge(passwordNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getNumerodecuenta();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getNumerodecuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Password passwordOrphanCheck = usuario.getPassword();
            if (passwordOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Password " + passwordOrphanCheck + " in its password field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
