package com.springnotes;

import com.springnotes.domain.Course;
import com.springnotes.domain.Student;
import com.springnotes.domain.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@SpringBootTest
class SpringnotesApplicationTests {

	@Test
	void testBasicUsage() {
		ApplicationContext context = new ClassPathXmlApplicationContext("student.xml");

		Student student = (Student) context.getBean("student");
		assertThat(student.getNo(), equalTo(15));
		assertThat(student.getName(), equalTo("Tom"));

		Student sameStudent = context.getBean("student", Student.class);
		assertThat(sameStudent.getNo(), equalTo(15));
		assertThat(sameStudent.getName(), equalTo("Tom"));
	}

	@Test
	public void testApplicationContextAware() {
		ApplicationContext context = new ClassPathXmlApplicationContext("teacher.xml");
		Teacher teacher = context.getBean("teacher", Teacher.class);
		List<Course> courses = teacher.getCourses();

		assertThat(courses.size(), equalTo(1));
		assertThat(courses.get(0).getName(), equalTo("math"));
	}

}
