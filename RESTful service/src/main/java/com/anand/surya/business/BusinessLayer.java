package com.anand.surya.business;
import java.util.*;
// import java.sql.Date;
import java.util.Date;
import java.text.ParseException;
// import java.text.ParseException;
import java.text.SimpleDateFormat;
// import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// import com.google.protobuf.Timestamp;
import java.sql.Timestamp;
// import javax.json.JsonObject;

import companydata.*;

public class BusinessLayer {

    DataLayer dl1 = null;

    public int checkDeptNo(String deptNo, String company){

        dl1 = new DataLayer(company);
        List<Department> dept = dl1.getAllDepartment(company);
        List<String> dNo = new ArrayList<String>();
        for(Department d : dept){
          dNo.add(d.getDeptNo());
        }
        if(dNo.contains(deptNo)){
           return 1;
        }
        return 0;  
    }
  

    public Integer checkDeptId(int deptId, String company){

        dl1 = new DataLayer(company);
        Department d = dl1.getDepartment(company, deptId);
        if(d == null){
            return 0;
        }
        else{
            return 1;
        }

        
    }

    public Integer checkMngId(int mngId, String company){

        dl1 = new DataLayer(company);
        List<Employee> emps = dl1.getAllEmployee(company);
        List<Integer> Id = new ArrayList<Integer>();

        for(Employee e : emps){
           Id.add(e.getId());
        }
        if(mngId == 0){
            return 1;
        }
        else if(Id.contains(mngId)){
        return 1;
        }
        else{
        return 0;
        }
        
    }

    public int checkEmpNo(String empNo, String company){

        dl1 = new DataLayer(company);
        List<Employee> emps = dl1.getAllEmployee(company);
        List<String> eNo = new ArrayList<String>();
        for(Employee e : emps){
          eNo.add(e.getEmpNo());
        }
        if(eNo.contains(empNo)){
        return 1;
        }
        return 0;  
    }

    public Integer checkHireDate(String hire_date){
        try{
        Date hdate = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
        LocalDate currentD = LocalDate.now();
        Date currentDate = java.sql.Date.valueOf(currentD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(hdate);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

            if( (hdate.compareTo(currentDate) > 0) || (day == 1 || day == 7)){
            return 0;
            }
            
        }
        catch(Exception e){
            return 0;
        }
        return 1;

    }

    public boolean checkCompany(String company){
        if(company.equals("ar6234")){
            return true;
        }
        return false;
    }

    public String checkTimestamp(String start_time, String end_time, String company, int emp_id, int time_id) throws ParseException{

        dl1 = new DataLayer(company);
        Timestamp s_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time).getTime());
        Timestamp e_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time).getTime());
        Date start = new Date(s_time.getTime());
        Date end = new Date(e_time.getTime());
       
        LocalDate currentDate = LocalDate.now();
        Date currentD = java.sql.Date.valueOf(currentDate);

        Calendar cs = Calendar.getInstance();
        Calendar ce = Calendar.getInstance();
        Calendar time = Calendar.getInstance();
        cs.setTime(start);
        ce.setTime(end);
        time.setTime(currentD);
        time.add(Calendar.DATE,-7);
        Date timeRange = time.getTime();
        int day = cs.get(Calendar.DAY_OF_WEEK);

        
        if(cs.get(Calendar.DAY_OF_MONTH) == ce.get(Calendar.DAY_OF_MONTH) && cs.get(Calendar.MONTH) == ce.get(Calendar.MONTH) && cs.get(Calendar.YEAR) == ce.get(Calendar.YEAR)){
            if(((end.getTime()-start.getTime())/60000) <=60 ){
                return "End Time must be more than 1hr from Start Time";
            }
            if(start.compareTo(currentD) > 0){
                return "Start Date should not be in future";
            }
            if(timeRange.compareTo(start) > 0){
                return "Start Date should be within a week from today";
            }
            if(day == 1 || day == 7){
                return "Start Date cannot be a Saturday or Sunday";
            }
            if(cs.get(Calendar.HOUR_OF_DAY) < 8 || cs.get(Calendar.HOUR_OF_DAY) > 18 ){
                return "Start Time should be between 8:00:00 and 18:00:00";
            }
            if(ce.get(Calendar.HOUR_OF_DAY) < 9 || ce.get(Calendar.HOUR_OF_DAY) > 18){
                return "End Time should be between 9:00:00 and 18:00:00";
            }
        } 
        if(cs.get(Calendar.DAY_OF_MONTH) != ce.get(Calendar.DAY_OF_MONTH) || cs.get(Calendar.MONTH) != ce.get(Calendar.MONTH) || cs.get(Calendar.YEAR) != ce.get(Calendar.YEAR)){
            return "Start Time and End Time must be on the same Day.";
        }

        if(time_id == 0){
            List<Timecard> timecards = dl1.getAllTimecard(emp_id);
            for(Timecard tc : timecards ){
               Date tdate = new Date(tc.getStartTime().getTime()); 
               long dDiff = (tdate.getTime() - start.getTime())/(24 * 60 * 60 * 1000); 
               if(emp_id == tc.getEmpId()  && dDiff == 0){
                   return "Start Time must not be on the same day as other start time  " +s_time+ ".";   
               }
            }
        } else{
            List<Timecard> timecards = dl1.getAllTimecard(emp_id);
         for(Timecard tc : timecards ){
            Date tdate = new Date(tc.getStartTime().getTime()); 
            long dDiff = (tdate.getTime() - start.getTime())/(24 * 60 * 60 * 1000); 
            if(emp_id == tc.getEmpId()  && dDiff == 0 && time_id != tc.getId()){
                return "Start Time must not be on the same day as other start time  " +s_time+ ".";   
            }
         }
        }
        return "";
    }

    
    public Integer checkTimeId(int timecard_id, String company){
        dl1 = new DataLayer(company);

        Timecard tc = dl1.getTimecard(timecard_id);
        if(tc == null){
            return 0;
        }
        return 1;
    }
    

}
