package org.taskmanager.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taskmanager.config.DBConnectionPool;
import org.taskmanager.exeptions.InvalidArgumentException;
import org.taskmanager.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    public boolean isUserExists(Connection connection, String username){
        final String selectQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (PreparedStatement isUserExistStatement = connection.prepareStatement(selectQuery)) {
            isUserExistStatement.setString(1, username);
            try (ResultSet resultSet = isUserExistStatement.executeQuery()){
                return (resultSet.next() && resultSet.getInt(1) > 0);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке существования пользователя с username={}", username, e);
            throw new RuntimeException(e);
        }
    }

    public boolean isUserExistsById(Connection connection, int id, boolean forUpdate){
        final String selectQuery = "SELECT COUNT(*) FROM Users WHERE user_id = ?" + ((forUpdate) ? " FOR UPDATE" : "");
        try (PreparedStatement isUserExistStatement = connection.prepareStatement(selectQuery)) {
            isUserExistStatement.setInt(1, id);
            try (ResultSet resultSet = isUserExistStatement.executeQuery()){
                return (resultSet.next() && resultSet.getInt(1) > 0);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при проверке существования пользователя с id={}", id, e);
            throw new RuntimeException(e);
        }
    }
    public void createNewUser(User user) {
        String createQuery = "insert into users(username, password, role) values(?, ?, ?)";
        if (!user.isCorrectForCreate(user)) {
            logger.error("Невозможно добавить пользователя с username={}, данныйе не корректны", user.getUsername());
            throw new InvalidArgumentException("невозможно добавить пользователя, данныйе не корректны");
        }
        try (Connection connection = DBConnectionPool.getConnection()){
            connection.setAutoCommit(false);
            try {
                if (isUserExists(connection, user.getUsername())){
                    logger.warn("Пользователь с username={} уже существует. Операция прервана.", user.getUsername());
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Ошибка при откате транзакции для username={}", user.getUsername(), rollbackEx);
                    }
                    return;
                }

                try (PreparedStatement createStatement = connection.prepareStatement(createQuery)){
                    createStatement.setString(1, user.getUsername());
                    createStatement.setString(2, user.getPassword());
                    createStatement.setString(3, user.getRole());
                    createStatement.executeUpdate();
                    connection.commit();
                    logger.info("Пользователь с username={} успешно добавлен с ролью {}", user.getUsername(), user.getRole());
                } catch (Exception e) {
                    logger.error("Ошибка при добавлении пользователя с username={}", user.getUsername(), e);
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Ошибка при откате транзакции для пользователя с username={}", user.getUsername(), rollbackEx);
                    }
                    throw new RuntimeException("Ошибка при добавлении пользователя", e);
                }

            } catch (Exception e) {
                logger.error("Возникла ошибка", e);
                throw new RuntimeException("Возникла ошибка", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e){
                    logger.warn("Ошибка при восстановлении автокоммита для пользователя {}", user.getUsername(), e);
                }

            }
        } catch (SQLException e) {
            logger.error("Не удалось установить соединение с базой данных", e);
            throw new RuntimeException("Не удалось установить соединение с базой данных",e);

        }
    }
    public boolean updateUsername(int id, String username){
        final String FindQuery = "update users set username = ? where user_id = ?";
        return updateUserById(FindQuery, username, id, "username");
    }
    public boolean updateUserPassword(int id, String password){
        final String FindQuery = "update users set password = ? where user_id = ?";
        return updateUserById(FindQuery, password, id, "password");
    }
    public boolean updateUserRole(int id, String role){
        final String FindQuery = "update users set role = ? where user_id = ?";
        return updateUserById(FindQuery, role, id, "role");
    }
    private boolean updateUserById(String query, String value, int id, String fieldName) {
        //String FindQuery = "update users set username = ? where id = ?";
        if (value == null || value.trim().isEmpty() || id < 1){
            logger.warn("Неверный формат входных данных");
            return false;
        }

        try (Connection connection = DBConnectionPool.getConnection()){
            connection.setAutoCommit(false);
            try {
                if (!isUserExistsById(connection, id, true)){
                    logger.warn("Пользователь с id={}, не существует", id);
                    return false;
                }
                try (PreparedStatement updateStatment = connection.prepareStatement(query)){
                    updateStatment.setString(1, value);
                    updateStatment.setInt(2, id);
                    updateStatment.executeUpdate();
                    logger.info("Пользователь с id={} был обновлен {}", id, fieldName);
                    return true;
                }catch (Exception e){
                    logger.error("ошибка при обновлении пользователя", e);
                    try {
                        connection.rollback();
                    } catch (Exception exception){
                        logger.error("Ошибка при откате изменений", exception);
                    }
                    throw new RuntimeException("ошибка при обновлении пользователя", e);

                }
            } catch (Exception e){
                try {
                    connection.rollback();
                } catch (SQLException ex){
                    logger.error("Неполучилось откатить изменения для пользователя id={}, при обнаружении непредвиденной ошибки", id, ex);
                    throw new RuntimeException("Неполучилось откатить изменения для пользователя, при обнаружении непредвиденной ошибки", ex);
                }
                logger.error("Возникла непредвиденная ошибка", e);
                throw new RuntimeException("Возникла непредвиденная ошибка", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e){
                    logger.error("Не удалось включить автокоммит", e);
                    throw new RuntimeException("Не удалось включить автокоммит", e);
                }
            }
        } catch (SQLException e) {
            logger.error("Не удалось установить соединение с БД");
            throw new RuntimeException(e);
        }
    }
}
