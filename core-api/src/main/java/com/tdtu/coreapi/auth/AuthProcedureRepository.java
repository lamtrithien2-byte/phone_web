package com.tdtu.coreapi.auth;

import com.tdtu.coreapi.auth.dto.LoginUserView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public LoginUserView loginByUserName(String userName) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_login");
        query.registerStoredProcedureParameter("p_user_name", String.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_user_name", userName);

        List<Object[]> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }

        Object[] row = result.get(0);
        return new LoginUserView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                (String) row[7],
                (String) row[8],
                null, // sp_auth_login does not return activation_expires
                toBoolean(row[9]),
                toBoolean(row[10]),
                toBoolean(row[11]),
                normalizeRole(row[12])
        );
    }

    public void activateAccount(String token) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_activate_account");
        query.registerStoredProcedureParameter("p_token", String.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_token", token);
        query.execute();
    }

    public void changeFirstPassword(Long userId, String passwordHash) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_change_first_password");
        query.registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("p_password_hash", String.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_user_id", userId);
        query.setParameter("p_password_hash", passwordHash);
        query.execute();
    }

    public void createResetToken(String email, String token, java.util.Date expires) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_create_reset_token");
        query.registerStoredProcedureParameter("p_email", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("p_token", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("p_expires", java.util.Date.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_email", email);
        query.setParameter("p_token", token);
        query.setParameter("p_expires", expires);
        query.execute();
    }

    public void resetPassword(String token, String passwordHash) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_reset_password");
        query.registerStoredProcedureParameter("p_token", String.class, jakarta.persistence.ParameterMode.IN);
        query.registerStoredProcedureParameter("p_password_hash", String.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_token", token);
        query.setParameter("p_password_hash", passwordHash);
        query.execute();
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    private String normalizeRole(Object value) {
        return value == null ? null : value.toString().toLowerCase();
    }
}
