/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import Entity.Password;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class PasswordJpaController implements Serializable {

    public PasswordJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Password password) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = password.getUsuario();
        if (usuarioOrphanCheck != null) {
            Password oldPasswordOfUsuario = usuarioOrphanCheck.getPassword();
            if (oldPasswordOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type Password whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario = password.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getNumerodecuenta());
                password.setUsuario(usuario);
            }
            em.persist(password);
            if (usuario != null) {
                usuario.setPassword(password);
                usuario = em.merge(usuario);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPassword(password.getNumerodecuenta()) != null) {
                throw new PreexistingEntityException("Password " + password + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Password password) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Password persistentPassword = em.find(Password.class, password.getNumerodecuenta());
            Usuario usuarioOld = persistentPassword.getUsuario();
            Usuario usuarioNew = password.getUsuario();
            List<String> illegalOrphanMessages = null;
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                Password oldPasswordOfUsuario = usuarioNew.getPassword();
                if (oldPasswordOfUsuario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type Password whose usuario column cannot be null. Please make another selection for the usuario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getNumerodecuenta());
                password.setUsuario(usuarioNew);
            }
            password = em.merge(password);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setPassword(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setPassword(password);
                usuarioNew = em.merge(usuarioNew);
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
                String id = password.getNumerodecuenta();
                if (findPassword(id) == null) {
                    throw new NonexistentEntityException("The password with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Password password;
            try {
                password = em.getReference(Password.class, id);
                password.getNumerodecuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The password with id " + id + " no longer exists.", enfe);
            }
            Usuario usuario = password.getUsuario();
            if (usuario != null) {
                usuario.setPassword(null);
                usuario = em.merge(usuario);
            }
            em.remove(password);
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

    public List<Password> findPasswordEntities() {
        return findPasswordEntities(true, -1, -1);
    }

    public List<Password> findPasswordEntities(int maxResults, int firstResult) {
        return findPasswordEntities(false, maxResults, firstResult);
    }

    private List<Password> findPasswordEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Password.class));
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

    public Password findPassword(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Password.class, id);
        } finally {
            em.close();
        }
    }

    public int getPasswordCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Password> rt = cq.from(Password.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
