package com.bcb.vetra.daos;

import com.bcb.vetra.exception.DaoException;
import com.bcb.vetra.models.Notification;
import com.bcb.vetra.models.Test;
import com.bcb.vetra.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <strong>Data Access Object for diagnostic tests.</strong>
 * <br><br>
 * This class is responsible for all database operations related to diagnostic tests.
 * <br><br>
 * Models: <i>Test</i>
 */
@Component
public class TestDao {
    private final JdbcTemplate jdbcTemplate;
    private NotificationDao notificationDao;
private UserDao userDao;
    public TestDao(DataSource dataSource, NotificationDao notificationDao, UserDao userDao) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.notificationDao = notificationDao;
        this.userDao = userDao;
    }

    /**
     * Gets test by ID.
     *
     * @return List of Test
     */
    public Test getTestById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM test WHERE test_id = ?", this::mapToTest, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Gets all tests for a patient by ID.
     *
     * @param patientId
     * @return List of Test
     */
    public List<Test> getTestsForPatient(int patientId) {
        return jdbcTemplate.query("SELECT * FROM test WHERE patient_id = ?", this::mapToTest, patientId);
    }

    /**
     * Creates a new test.
     * @param test
     *
     * @return Test
     */
    public Test create(Test test) {
        try {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO test (name, time_stamp, patient_id, doctor_username) " +
                            "VALUES (?,?,?,?) " +
                            "RETURNING test_id;",
                    Integer.class,
                    test.getName(),
                    test.getTimestamp(),
                    test.getPatientID(),
                    test.getDoctorUsername()
            );
            User user = userDao.getUserByPatientId(test.getPatientID());
            Notification notification = new Notification(user.getUsername(), test.getPatientID(), 0, 0, id, false);
            notificationDao.create(notification);
            return getTestById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create test.");
        }
    }

    /**
     * Updates a test.
     * @param test
     *
     * @return Test
     */
    public Test update(Test test) {
        int rowsAffected = jdbcTemplate.update(
                "UPDATE test SET name = ?, time_stamp = ?, patient_id = ?, doctor_username = ? " +
                        "WHERE test_id = ?;",
                test.getName(),
                test.getTimestamp(),
                test.getPatientID(),
                test.getDoctorUsername(),
                test.getId()
        );
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getTestById(test.getId());
        }
    }

    /**
     * Deletes a test by ID.
     * @param id
     *
     * @return boolean indicating success
     */
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM test WHERE test_id = ?", id) > 0;
    }

    /**
     * Deletes a test of a patient.
     * @param id id of test
     * @param patientId
     * @return boolean indicating success
     */
    public boolean deleteTestOfPatient(int id, int patientId) {
        return jdbcTemplate.update("DELETE FROM test WHERE test_id = ? AND patient_id = ?", id, patientId) > 0;
    }

    //----------------------
    // Helper methods
    //----------------------

    /**
     * Maps a ResultSet to a Test object.
     *
     * @param resultSet
     * @param rowNumber
     * @return Test
     * @throws SQLException
     */
    public Test mapToTest(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Test(
                resultSet.getInt("test_id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("time_stamp").toLocalDateTime(),
                resultSet.getInt("patient_id"),
                resultSet.getString("doctor_username")
        );
    }

}
