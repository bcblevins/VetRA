package com.bcb.vetra.daos;

import com.bcb.vetra.models.Message;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <strong>Data Access Object for messages.</strong>
 * <br><br>
 * This class is responsible for all database operations related to messages. Includes methods that filter results by username, patient ID, and test ID.
 * <br><br>
 * Models: <i>Message</i>
 */
@Component
public class MessageDao {
    private JdbcTemplate jdbcTemplate;

    public MessageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets all messages sent to or from a specific username.
     * @param username
     * @return List of messages
     */
    public List<Message> getMessagesByUsername(String username) {
        return jdbcTemplate.query("SELECT * " +
                        "FROM message " +
                        "WHERE to_username = ? OR from_username = ?;",
                this::mapToMessage, username, username);
    }

    /**
     * Gets a message by ID.
     * @param id
     * @return Message
     */
    public Message getMessageById(int id) {
        try {
        return jdbcTemplate.queryForObject("SELECT * " +
                        "FROM message  " +
                        "WHERE message_id = ?;",
                this::mapToMessage, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    /**
     * Gets a message by ID if it has been sent to or from a username.
     * @param id
     * @param username
     * @return Message
     */
    public Message getMessageByIdAndUsername(int id, String username) {
        try {
        return jdbcTemplate.queryForObject("SELECT * " +
                        "FROM message  " +
                        "WHERE message_id = ? AND (to_username = ? OR from_username = ?);",
                this::mapToMessage, id, username, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Gets all messages.
     * @return List of messages
     */
    public List<Message> getAll() {
        return jdbcTemplate.query("SELECT * FROM message;", this::mapToMessage);
    }

    /**
     * Gets all messages associated with a specific patientId.
     * @param id
     * @return List of messages
     */
    public List<Message> getMessagesByPatientId(int id) {
        return jdbcTemplate.query("SELECT * " +
                        "FROM message  " +
                        "WHERE patient_id = ?;",
                this::mapToMessage, id);
    }

    /**
     * Gets all messages associated with a specific testId.
     * @param testId
     * @return List of messages
     */
    public List<Message> getMessagesByTestId(int testId) {
        return jdbcTemplate.query("SELECT * " +
                "FROM message " +
                "WHERE test_id = ?;",
                this::mapToMessage,
                testId);
    }

    /**
     * Gets all messages associated with a specific prescriptionId.
     * @param prescriptionId
     * @return List of messages
     */
    public List<Message> getMessagesByPrescriptionId(int prescriptionId) {
        return jdbcTemplate.query("SELECT * " +
                "FROM message " +
                "WHERE prescription_id = ?;",
                this::mapToMessage,
                prescriptionId);
    }

    /**
     * Creates a new message.
     * @param message
     * @return The created message
     */
    public Message create(Message message) {
        Integer id;
        try {
        id = jdbcTemplate.queryForObject(
                "INSERT INTO message (body, from_username, to_username, test_id, prescription_id, patient_id) " +
                        "VALUES (?,?,?,?,?,?) " +
                        "RETURNING message_id;",
                Integer.class,
                message.getBody(),
                message.getFromUsername(),
                message.getToUsername(),
                message.getTestId() == 0 ? null : message.getTestId(),
                message.getPrescriptionId() == 0 ? null : message.getPrescriptionId(),
                message.getPatientId()
        );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return getMessageById(id);
    }

    /**
     * Updates a message.
     * @param message
     * @return The updated message
     */
    public Message update(Message message) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE message SET body = ?, from_username = ?, to_username = ?, test_id = ?, prescription_id = ?, patient_id = ? " +
                        "WHERE message_id = ?;",
                message.getBody(),
                message.getFromUsername(),
                message.getToUsername(),
                message.getTestId(),
                message.getPrescriptionId(),
                message.getPatientId(),
                message.getMessageId()
        );
        return getMessageById(message.getMessageId());
    }

    /**
     * Deletes a message by ID.
     * @param id
     * @return True if the message was deleted, false otherwise
     */
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM message WHERE message_id = ?;", id) > 0;
    }

    /**
     * Maps a ResultSet to a Message object.
     * @param rs
     * @param rowNum
     * @return Message
     * @throws SQLException
     */
    private Message mapToMessage(ResultSet rs, int rowNum) throws SQLException {
        return new Message(
                rs.getInt("message_id"),
                rs.getString("body"),
                rs.getTimestamp("time_stamp").toLocalDateTime(),
                rs.getString("from_username"),
                rs.getString("to_username"),
                rs.getInt("test_id"),
                rs.getInt("prescription_id"),
                rs.getInt("patient_id")
        );
    }

}
