package web.application.development.teacher;

import java.util.List;

import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sebastian_daschner.siren4javaee.Entity;
import com.sebastian_daschner.siren4javaee.EntityReader;
import com.sebastian_daschner.siren4javaee.Siren;

import web.application.development.course.Course;
import web.application.development.course.CourseService;
import web.application.development.formatter.Formatter;
import web.application.development.predavanje.Predavanje;
import web.application.development.predavanje.PredavanjeService;

@RestController
public class TeacherController {
	
	@Autowired //marks this as something that needs dependency injection, injects existing topicService
	private TeacherService teacherService;
	@Autowired 
	private PredavanjeService predmetService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private Formatter formatter;
	
	@RequestMapping(value="/teachers", method=RequestMethod.GET) //maps URL /teachers to method getAllTeachers
	public ResponseEntity<Entity> getAllTeachers() {
		JsonObject object = formatter.ReturnJSON(teacherService.getAllTeachers(), new Teacher());
		EntityReader entityReader = Siren.createEntityReader();
		Entity entity = entityReader.read(object);
		return new ResponseEntity<Entity>(entity, HttpStatus.OK);
	}
	
	@RequestMapping(value="/teachers/{teacherId}", method=RequestMethod.GET)
	public HttpEntity<Entity> getTeacher(@PathVariable String teacherId) {
		Teacher teacher = teacherService.getTeacher(teacherId);
		JsonObject object = formatter.ReturnJSON(teacher);
		EntityReader entityReader = Siren.createEntityReader();
		Entity entity = entityReader.read(object);
		return new ResponseEntity<Entity>(entity, HttpStatus.OK);
	}
	
	@RequestMapping(value="/teachers", method=RequestMethod.POST)
	public void addUser(@RequestBody Teacher teacher) { //@RequestBody tells spring that the request pay load is going to contain a user
		teacherService.addTeacher(teacher);
	}
	
	@RequestMapping(value="/teachers/{teacherId}/{courseId}", method=RequestMethod.POST) //adds existing student to group, NO BODY on POST
	public void addStudentToGroup(@PathVariable String teacherId, @PathVariable String courseId) { //@RequestBody tells spring that the request pay load is going to contain a topics
		Teacher teacher = teacherService.getTeacher(teacherId);
		teacher.addCourse(new Course(courseId, "",""));
		teacherService.addCourseToTeacher(teacherId, teacher);
		
		Course course = courseService.getCourse(courseId);
		course.setTeacher(teacher);
		courseService.updateCourse(courseId, course);
	}
	
	@RequestMapping(value="/teachers/{teacherId}/classes/{classId}", method=RequestMethod.POST) //adds existing student to group, NO BODY on POST
	public void assignTeacherToClass(@PathVariable String teacherId, @PathVariable String classId) { //@RequestBody tells spring that the request pay load is going to contain a topics
		Teacher teacher = teacherService.getTeacher(teacherId);
		teacher.assignTeacherToClass(new Predavanje(classId, "",false));
		teacherService.assignTeacherToClass(teacherId, teacher);
		
		Predavanje predmet = predmetService.getPredavanje(classId);
		predmet.assignTeacherToClass(new Teacher(teacherId, "", "", "", false));
		predmetService.assignTeacherToClass(classId, predmet);
	}

	@RequestMapping(value="/teachers/{teacherId}", method=RequestMethod.PUT)
	public void updateUser(@RequestBody Teacher teacher, @PathVariable String teacherId) { //@RequestBody tells spring that the request pay load is going to contain a user
		Teacher temp = teacherService.getTeacher(teacherId);
		List<Course> courses = temp.getCourses();
		teacher.setCourses(courses);
		teacherService.updateTeacher(teacherId, teacher);
	}
	
	@RequestMapping(value="/teachers/{teacherId}", method=RequestMethod.DELETE)
	public void deleteUser(@PathVariable String teacherId) {
		teacherService.deleteTeacher(teacherId);
	}
	
	@RequestMapping(value="/teachers/{teacherId}/{courseId}", method=RequestMethod.DELETE) //removes course from teacher, body has to have course ID
	public void remveStudentFromGroup(@PathVariable String courseId, @PathVariable String teacherId) { //@RequestBody tells spring that the request pay load is going to contain a topics
		Teacher temp = teacherService.getTeacher(teacherId);
		//Course course = new Course(courseId, "","");
		temp.removeCourse(new Course(courseId, "",""));
		teacherService.removeCourseFromTeacher(teacherId, temp);
	}
	

}
