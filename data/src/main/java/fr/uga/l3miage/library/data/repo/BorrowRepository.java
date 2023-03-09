package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class BorrowRepository implements CRUDRepository<String, Borrow> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Borrow save(Borrow entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Borrow get(String id) {
        return entityManager.find(Borrow.class, id);
    }

    @Override
    public void delete(Borrow entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Borrow> all() {
        return entityManager.createQuery("from Borrow", Borrow.class).getResultList();
    }

    /**
     * Trouver des emprunts en cours pour un emprunteur donné
     *
     * @param userId l'id de l'emprunteur
     * @return la liste des emprunts en cours
     */
    public List<Borrow> findInProgressByUser(String userId) {
        return entityManager.createQuery("from Borrow b join b.borrower bb where bb.id=:userId and b.finished=false", Borrow.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Compte le nombre total de livres emprunté par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countBorrowedBooksByUser(String userId) {
        return entityManager.createQuery("select count(bk) from Borrow b join b.borrower bb join b.books bk where bb.id=:userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult()
                .intValue();
    }

    /**
     * Compte le nombre total de livres non rendu par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countCurrentBorrowedBooksByUser(String userId) {
        return entityManager.createQuery("select count(bk) from Borrow b join b.borrower bb join b.books bk where bb.id=:userId and b.finished=false", Long.class)
                .setParameter("userId", userId)
                .getSingleResult()
                .intValue();
    }

    /**
     * Recherche tous les emprunt en retard trié
     *
     * @return la liste des emprunt en retard
     */
    public List<Borrow> foundAllLateBorrow() {
        return entityManager.createQuery("from Borrow b where b.finished=false and b.requestedReturn > now()", Borrow.class)
                .getResultList();
    }

    /**
     * Calcul les emprunts qui seront en retard entre maintenant et x jours.
     *
     * @param days le nombre de jour avant que l'emprunt soit en retard
     * @return les emprunt qui sont bientôt en retard
     */
    public List<Borrow> findAllBorrowThatWillLateWithin(int days) {
        return entityManager.createQuery("from Borrow b where b.finished=false and b.requestedReturn between now() and :date", Borrow.class)
                .setParameter("date", Date.from(ZonedDateTime.now().plus(days, ChronoUnit.DAYS).toInstant()))
                .getResultList();
    }

}
