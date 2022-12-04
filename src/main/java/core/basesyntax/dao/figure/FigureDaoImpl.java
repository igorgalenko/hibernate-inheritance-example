package core.basesyntax.dao.figure;

import core.basesyntax.dao.AbstractDao;
import core.basesyntax.model.figure.Figure;
import core.basesyntax.model.zoo.Animal;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

public class FigureDaoImpl<T extends Figure> extends AbstractDao implements FigureDao<T> {
    public FigureDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public T save(T figure) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(figure);
            transaction.commit();
            return figure;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save figure to DB " + figure, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<T> findByColor(String color, Class<T> clazz) {
        String simpleName = clazz.getSimpleName();
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(""
                    + "FROM " + simpleName + " "
                    + "WHERE color = :color ", clazz);
            query.setParameter("color", color);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get " + simpleName + " by color " + color, e);
        }
    }
}
