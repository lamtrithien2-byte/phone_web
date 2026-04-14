package com.tdtu.coreapi.user;

import com.tdtu.coreapi.user.dto.CreateStaffRequest;
import com.tdtu.coreapi.user.dto.UpdateProfileRequest;
import com.tdtu.coreapi.user.dto.UserView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserView getProfile(Long userId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_user_get_profile");
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.setParameter("p_user_id", userId);
        List<?> rows = query.getResultList();
        if (rows.isEmpty()) {
            return null;
        }
        return mapProfile((Object[]) rows.get(0));
    }

    public List<UserView> getStaffList() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_user_get_all_staff");
        return query.getResultList().stream()
                .map(row -> mapStaff((Object[]) row))
                .toList();
    }

    public Long createStaff(CreateStaffRequest request) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_auth_create_staff");
        query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_password_hash", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_full_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_address", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_activation_token", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_activation_expires", java.util.Date.class, ParameterMode.IN);

        query.setParameter("p_user_name", request.userName());
        query.setParameter("p_password_hash", request.password()); // Should be hashed in service
        query.setParameter("p_full_name", request.fullName());
        query.setParameter("p_email", request.email());
        query.setParameter("p_address", request.address());
        query.setParameter("p_phone_number", request.phoneNumber());
        query.setParameter("p_activation_token", null);
        query.setParameter("p_activation_expires", null);

        Object row = query.getSingleResult();
        return ((Number) row).longValue();
    }

    public UserView updateProfile(UpdateProfileRequest request) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_user_update_profile");
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_full_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_address", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_avatar", String.class, ParameterMode.IN);
        query.setParameter("p_user_id", request.userId());
        query.setParameter("p_full_name", request.fullName());
        query.setParameter("p_email", request.email());
        query.setParameter("p_address", request.address());
        query.setParameter("p_phone_number", request.phoneNumber());
        query.setParameter("p_avatar", request.avatar());
        query.execute();
        return getProfile(request.userId());
    }

    public void toggleStatus(Long userId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_user_toggle_lock");
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.setParameter("p_user_id", userId);
        query.execute();
    }

    private UserView mapProfile(Object[] row) {
        return new UserView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                row.length > 7 ? toBoolean(row[7]) : false,
                row.length > 8 ? toBoolean(row[8]) : false,
                row.length > 9 ? toBoolean(row[9]) : false,
                row.length > 10 && row[10] != null ? row[10].toString().toLowerCase() : null
        );
    }

    private UserView mapStaff(Object[] row) {
        return new UserView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                false,
                row.length > 7 ? toBoolean(row[7]) : false,
                row.length > 8 ? toBoolean(row[8]) : false,
                "staff"
        );
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
}
