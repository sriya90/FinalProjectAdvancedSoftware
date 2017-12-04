package com.itmd.asp.classschedulerapp.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.itmd.asp.classschedulerapp.dao.ActivityRepository;
import com.itmd.asp.classschedulerapp.model.Activity;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ActivityService {

	@PersistenceContext
	private EntityManager em;
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<Activity> findActivityByCoursesCode(String courseCode){
   
		return activityRepository.findActivityByCoursesCode(courseCode);
         
    }

    public List<Activity> findActivityByUserName(String UserName){
    	   
    			return activityRepository.findActivityByUserName(UserName);
    	         
    	    }
    public Activity getActivity(Long activityId){
        return activityRepository.getOne(activityId);
    }

    public Activity addActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void editActivity (Long activityId, Activity activityDetails){
    	System.out.println(activityDetails.getActivityId());
    	System.out.println("old"+activityId);
    		activityRepository.saveAndFlush(activityDetails);
    }

    public void deleteActivity(Long activityId) {
        //Activity activity = activityRepository.findOne(activityId);
    	
        activityRepository.delete(activityId);
        //System.out.println("deleted");
    }

	public List<Activity> findall() {
		// TODO Auto-generated method stub
		return activityRepository.findAll();
	}

}
