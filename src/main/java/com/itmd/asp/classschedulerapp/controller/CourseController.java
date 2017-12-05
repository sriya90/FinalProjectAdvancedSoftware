package com.itmd.asp.classschedulerapp.controller;

 import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.itmd.asp.classschedulerapp.dao.CourseRepository;
import com.itmd.asp.classschedulerapp.model.Activity;
import com.itmd.asp.classschedulerapp.model.Course;
import com.itmd.asp.classschedulerapp.model.Greeting;
import com.itmd.asp.classschedulerapp.model.User;
import com.itmd.asp.classschedulerapp.service.ActivityService;


@Controller
@Scope("session")

public class CourseController {
    @Autowired
    private CourseRepository courseRepositary;
    private Long currentID;
    private Boolean callFromCourse;
    private Course courses;
    @Autowired
    private ActivityService activityService;
    @PostMapping(value = {"/**/saveCourse"})
    public String save(@Valid @ModelAttribute("course") Course course,HttpSession session,Model model,BindingResult result) {

    		String userName =  (String)session.getAttribute("username");
    		course.setUserName(userName);
    		String courseExitsts = courseRepositary.findByCourseCodeForUser(course.getCourseCode(),userName);

    		if((courseExitsts != null && !courseExitsts.trim().isEmpty()))
    		{
    			result.rejectValue("userName", "messageCode", "Course Already Exists try again");
    	       	model.addAttribute("course", course);

    		}
    		else
    		{

    			courseRepositary.save(course);
    	       	model.addAttribute("course", new Course());

    		}
        List<Course> tests = courseRepositary.findAllByCoursesId(userName);
        model.addAttribute("courseList", tests);
        model.addAttribute("activity", new Activity());
       	List<Activity> listActivity = 	activityService.findActivityByUserName(userName);
		
  	
  	  model.addAttribute("activityList", listActivity);
       
    	  System.out.println("heereadddingcourse");
    	  return "mainPage";
    }

    @GetMapping(value = {"/**/course/view/{courseCode}"})
    public ModelAndView viewCourse(@PathVariable String courseCode,Model model,HttpSession session) {

  	  System.out.println("here showing course"+courseCode);
  	   courses = courseRepositary.findByCoursesCode(courseCode);
  	   session.setAttribute("courseCode", courseCode);
  	  model.addAttribute("CourseName", courses.getCourseName()+" : "+courses.getCourseCode());
  	  model.addAttribute("course", courses);
      model.addAttribute("activity", new Activity());
      
   	   List<Activity> listActivity = 	activityService.findActivityByCoursesCode(courses.getCourseCode());
   	model.addAttribute("activityForCourseList", listActivity);
	 RedirectView rv = new RedirectView("course");
     rv.setExposeModelAttributes(false);
  return   new ModelAndView("course");
    }
    
    @RequestMapping(value = {"/**/addActivity"}, method = RequestMethod.POST)

    public String addActivity(@ModelAttribute Activity activity,HttpSession session,Model model) {
    	 String userName =  (String)session.getAttribute("username");

    	   System.out.println("here showing course"+userName);
    	   activity.setUserName(userName);
    	   activity.setActivityCourse(courses.getCourseCode());
    	 	activityService.addActivity(activity);
    	   	   List<Activity> listActivity = 	activityService.findActivityByCoursesCode(courses.getCourseCode());
    	      	model.addAttribute("activityForCourseList", listActivity);
    	      	 model.addAttribute("CourseName", courses.getCourseName()+" : "+courses.getCourseCode());
    	  model.addAttribute("course",courses);
    	  model.addAttribute("activity", new Activity());

        return "course";
    }
    @GetMapping (value = {"/**/deletefromcourse/{id}"})
    public String delete(@PathVariable (value="id") Long activityId,HttpSession session,Model model,HttpServletRequest request){
        activityService.deleteActivity(activityId);
        System.out.println("deleting activity"+request.toString());
 	   String userName =  (String)session.getAttribute("username");
   	   List<Activity> listActivity = 	activityService.findActivityByCoursesCode(courses.getCourseCode());
      	model.addAttribute("activityForCourseList", listActivity);
      	 model.addAttribute("CourseName", courses.getCourseName()+" : "+courses.getCourseCode());
  model.addAttribute("course",courses);
  model.addAttribute("activity", new Activity());

     return "course";

    }
    
    @GetMapping(value ="/**/gotomain")
    public String gotomain(@ModelAttribute User user,HttpSession session,Model model,RedirectAttributes redirectAttributes) {
  
        model.addAttribute("course", new Course());
        model.addAttribute("activity", new Activity());
        model.addAttribute("editActivity", new Activity());
        
        String userName =  (String)session.getAttribute("username");
        
 		List<Activity> listActivity = 	activityService.findActivityByUserName(userName);
    	
  	  model.addAttribute("course", new Course());
  	  model.addAttribute("activityList", listActivity);
       // tests = courseRepositary.findAll();
       // if(!tests.isEmpty())
        List<Course> tests = courseRepositary.findAllByCoursesId(userName);
        model.addAttribute("courseList", tests);
        model.addAttribute("usernameprof", userName);
        System.out.println("heere"+tests.toString());
        session.setAttribute("Model", model);
 return "mainPage";
}
    @GetMapping(value = {"/**/edit/{id}"})
    public String editActivity(@PathVariable Long id,Model model,HttpSession session) {
    	  Activity activity = activityService.getActivity(id);
    	  currentID = id;
    	  callFromCourse = false;
    	  model.addAttribute("ActivityName", activity.getActivityCourse() + " : "+  activity.getActivityName());
    	  model.addAttribute("editActivity", activity);
    	  List<Course> tests = courseRepositary.findAllByCoursesId(activity.getUserName());
      model.addAttribute("courseList", tests);
       return "activity";
    }
    
    @GetMapping(value = {"/**/editfromcourse/{id}"})
    public String editCourseActivity(@PathVariable Long id,Model model,HttpSession session) {
    	  Activity activity = activityService.getActivity(id);
    	  currentID = id;
    	  callFromCourse = true;
    	  model.addAttribute("ActivityName", activity.getActivityCourse() + " : "+  activity.getActivityName());
    	  model.addAttribute("editActivity", activity);
    	  List<Course> tests = courseRepositary.findAllByCoursesId(activity.getUserName());
      model.addAttribute("courseList", tests);
       return "activity";
    }
    
    @RequestMapping(value = {"/**/editActivityModal"}, method = RequestMethod.POST)
    public String editActivityModal(@ModelAttribute Activity activity,HttpSession session,Model model,HttpServletRequest request) {
    	
    	 System.out.println("heereaddding activity"+activity.getActivityCourse());
   
    	   String userName =  (String)session.getAttribute("username");
    	   activity.setUserName(userName);
    	   activity.setActivityId(currentID);
    	   activityService.editActivity(currentID,activity);
    	   if(callFromCourse)
    	   {
    		   List<Activity> listActivity = 	activityService.findActivityByCoursesCode(courses.getCourseCode());
    		   model.addAttribute("activityForCourseList", listActivity);
    		   model.addAttribute("CourseName", courses.getCourseName()+" : "+courses.getCourseCode());
    		   model.addAttribute("course",courses);
    		   model.addAttribute("activity", new Activity());
    		   callFromCourse = false;
    		   return "/course";
	
    	   }
else
{
	List<Course> tests = courseRepositary.findAllByCoursesId(userName);

    model.asMap().clear();
    	  model.addAttribute("course", new Course());
    	  model.addAttribute("activity", new Activity());
    	   	List<Activity> listActivity = 	activityService.findActivityByUserName(userName);

    	  model.addAttribute("activityList", listActivity);
    	  model.addAttribute("courseList", tests);

     	return "/mainPage";
}
    }
    
}

