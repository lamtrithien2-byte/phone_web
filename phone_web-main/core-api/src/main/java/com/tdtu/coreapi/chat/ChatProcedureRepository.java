package com.tdtu.coreapi.chat;

import com.tdtu.coreapi.chat.dto.ChatMessageView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long createSession(String sessionCode, Long customerId, String guestName, String channelName) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_chat_session_create");
        query.registerStoredProcedureParameter("p_session_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_guest_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_channel_name", String.class, ParameterMode.IN);
        query.setParameter("p_session_code", sessionCode);
        query.setParameter("p_customer_id", customerId);
        query.setParameter("p_guest_name", guestName);
        query.setParameter("p_channel_name", channelName);
        return ((Number) query.getSingleResult()).longValue();
    }

    public Long resolveSessionId(String sessionCode) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_chat_session_resolve");
        query.registerStoredProcedureParameter("p_session_code", String.class, ParameterMode.IN);
        query.setParameter("p_session_code", sessionCode);
        List<?> rows = query.getResultList();
        if (rows.isEmpty()) {
            return null;
        }
        Object value = rows.get(0);
        if (value instanceof Object[] row) {
            return ((Number) row[0]).longValue();
        }
        return ((Number) value).longValue();
    }

    public void addMessage(Long sessionId, String senderType, String messageText, Integer promptTokens, Integer completionTokens) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_chat_message_add");
        query.registerStoredProcedureParameter("p_session_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_sender_type", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_message_text", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_prompt_tokens", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_completion_tokens", Integer.class, ParameterMode.IN);
        query.setParameter("p_session_id", sessionId);
        query.setParameter("p_sender_type", senderType);
        query.setParameter("p_message_text", messageText);
        query.setParameter("p_prompt_tokens", promptTokens == null ? 0 : promptTokens);
        query.setParameter("p_completion_tokens", completionTokens == null ? 0 : completionTokens);
        query.execute();
    }

    public List<ChatMessageView> getMessages(String sessionCode) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_chat_session_get_messages");
        query.registerStoredProcedureParameter("p_session_code", String.class, ParameterMode.IN);
        query.setParameter("p_session_code", sessionCode);
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new ChatMessageView(
                            (String) row[0],
                            (String) row[1],
                            (String) row[2],
                            ((Number) row[3]).intValue(),
                            ((Number) row[4]).intValue(),
                            (java.util.Date) row[5]
                    );
                })
                .toList();
    }
}
