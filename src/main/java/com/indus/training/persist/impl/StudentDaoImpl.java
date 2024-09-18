package com.indus.training.persist.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indus.training.persist.dao.IStudentDao;
import com.indus.training.persist.entity.Student;
import com.indus.training.persist.exceptions.StudentMyBatisException;

public class StudentDaoImpl implements IStudentDao {

	private Logger loggerObj = LoggerFactory.getLogger("StudentDaoImpl.class");
	private String resource = "SQLMapConfig.xml";
	private SqlSessionFactory sqlSessionFactory;

	// Constructor to initialize SqlSessionFactory
	public StudentDaoImpl() {
		try {
			InputStream inputStream = Resources.getResourceAsStream(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean insertStudent(Student stuObj) throws StudentMyBatisException {

		if (studentExists(stuObj.getStudentId())) {
			loggerObj.warn("Student with Provided Id already exists");
			return false;
		}
		try (SqlSession session = sqlSessionFactory.openSession()) {
			int rows = session.insert("com.indus.training.persist.dao.IStudentDao.insertStudent", stuObj);
			session.commit();
			return rows > 0;
		} catch (Exception e) {
			loggerObj.error("An error occured while inserting a student", e.getMessage());
			throw new StudentMyBatisException("An error occured while inserting a Student Object");
		}

	}

	@Override
	public Student fetchStudent(int stuId) throws StudentMyBatisException {
		if (!studentExists(stuId)) {
			loggerObj.warn("Student with Provided Id does not exists");
			return null;
		}
		try (SqlSession session = sqlSessionFactory.openSession()) {
			return session.selectOne("com.indus.training.persist.dao.IStudentDao.selectStudentById", stuId);
		} catch (Exception e) {
			loggerObj.error("An error occured while fetching a student", e.getMessage());
			throw new StudentMyBatisException("An error occured while fetching a Student Object");
		}
	}

	@Override
	public boolean deleteStudent(int stuId) throws StudentMyBatisException {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			int rows = session.delete("com.indus.training.persist.dao.IStudentDao.deleteStudent", stuId);
			session.commit();

			if (rows == 0) {
				// No rows affected means the student did not exist
				loggerObj.warn("Student with provided ID " + stuId + " does not exist.");
				return false;
			}
			return true;
		} catch (Exception e) {
			loggerObj.error("An error occurred while deleting student with ID " + stuId, e);
			throw new StudentMyBatisException("Failed to delete student ");
		}
	}

	@Override
	public boolean updateStudentFirstName(int id, String updFirstName) throws StudentMyBatisException {
		if (!studentExists(id)) {
			loggerObj.warn("Student with ID " + id + " does not exist.");
			return false; // Return false if the student does not exist
		}

		try (SqlSession session = sqlSessionFactory.openSession()) {
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("updFirstName", updFirstName);

			int rows = session.update("com.indus.training.persist.dao.IStudentDao.updateStudentFirstName", params);
			session.commit();

			return rows > 0; 
		} catch (Exception e) {
			loggerObj.error("An error occurred while updating the first name of student with ID " + id, e);
			throw new StudentMyBatisException("Failed to update the first name of student");
		}
	}

	@Override
	public boolean updateStudentLastName(int id, String updLastName) throws StudentMyBatisException {
		if (!studentExists(id)) {
			loggerObj.warn("Student with ID " + id + " does not exist.");
			return false; 
		}

		try (SqlSession session = sqlSessionFactory.openSession())   {
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("updLastName", updLastName);
			int rows = session.update("com.indus.training.persist.dao.IStudentDao.updateStudentLastName", params);
			session.commit();
			return rows > 0;
		}
		catch (Exception e) {
			loggerObj.error("An error occurred while updating the last name of student with ID " + id, e);
			throw new StudentMyBatisException("Failed to update the last name of student");
		}
	}

	private boolean studentExists(int stuId) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			Student student = session.selectOne("com.indus.training.persist.dao.IStudentDao.selectStudentById", stuId);
			return student != null;
		}
	}

}
