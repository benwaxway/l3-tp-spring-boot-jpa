package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class UserRepository implements CRUDRepository<String, User> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public User get(String id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void delete(User entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<User> all() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }
    /**
     * Trouve tous les utilisateurs ayant plus de l'age passé
     * @param age l'age minimum de l'utilisateur
     * @return
     */
    public List<User> findAllOlderThan(int age) {
        return entityManager.createQuery("from User u where u.birth < :date", User.class)
                .setParameter("date", Date.from(ZonedDateTime.now().minus(age, ChronoUnit.YEARS).toInstant()))
                .getResultList();
    }

}
