package com.anand.surya.service;

import companydata.*;

import java.io.StringReader;
// import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.anand.surya.business.BusinessLayer;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("CompanyServices")
public class MyResource {
    
    @Context
    UriInfo uriInfo;
    DataLayer dl = null;
    
    @Path("company")
    @DELETE
    @Produces("application/json")
    
    public Response deleteCompany(@QueryParam("company") String company) {
        try{
            
            dl = new DataLayer(company);
            int res = dl.deleteCompany(company);

            if(res == 0){
                return Response.ok("{\"error\":\"Delete Failed. No Company found for the given name\"}").build();
            }
            else{
                return Response.ok("{\"success\":\""+ company + "'s information deleted.\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();

        } finally{
            dl.close();
        }
       
    }

    @Path("department")
    @GET
    @Produces("application/json")
    
    public Response getDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int dept_id) {
        try{
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            dl = new DataLayer(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            
            Department dept = dl.getDepartment(company, dept_id);
            if(dept != null){
            return Response.ok("{\"dept_id\":\""+ dept.getId() + "\",\n\"company\":\""+dept.getCompany()+"\",\n\"dept_name\":\""+dept.getDeptName()+"\",\n\"dept_no\":\""+dept.getDeptNo()+"\",\n\"location\":\""+dept.getLocation()+"\"}").build();
            }
            else{
                return Response.ok("{\"error\":\"No department found for the given Company and dept_id = \""+dept_id+"\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
       
    }

    @Path("departments")
    @GET
    @Produces("application/json")
    
    public Response getDepartments(@QueryParam("company") String company) {
        try{
            dl = new DataLayer(company);
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            List<Department> dept = dl.getAllDepartment(company);
            List<String> resultJson = new ArrayList<String>();
            if(dept != null){
            for (Department d: dept){
                  resultJson.add("{\"dept_id\":\""+ d.getId() + "\",\n\"company\":\""+d.getCompany()+"\",\n\"dept_name\":\""+d.getDeptName()+"\",\n\"dept_no\":\""+d.getDeptNo()+"\",\n\"location\":\""+d.getLocation()+"\"}");
            }
                return Response.ok(resultJson.toString()).build();
            }
            else{
                return Response.ok("{\"error\":\"No Departments found for the given company.\"}").build();
            }   
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
       
    }


    @Path("department")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    
    public Response putDepartment(String deptIn) {
        try{
            BusinessLayer bl = new BusinessLayer();
            JsonReader rdr = Json.createReader(new StringReader(deptIn));
            JsonObject obj = rdr.readObject();
            String company = obj.getString("company");
            String dept_name = obj.getString("dept_name");
            String dept_no = obj.getString("dept_no");
            String location = obj.getString("location");
            int dept_id = obj.getInt("dept_id");

            dl = new DataLayer(company);
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }

            int isDept = bl.checkDeptNo(dept_no, company); 

            if(isDept == 1){
                return Response.ok("{\"error\":\"Update Failed. Dept no must not be repeated. \"}\n").build();
            }
            Integer dId = bl.checkDeptId(dept_id, company);

            if(dId == 0){
                return Response.ok("{\"error\":\" Department Id does not exist. Check and Try Again\"}\n").build();
            }

            Department dept = dl.getDepartment(company, dept_id);
            dept.setDeptName(dept_name);
            dept.setDeptNo(dept_no);
            dept.setLocation(location);
            Department updatedDept = dl.updateDepartment(dept);

            if(updatedDept!=null){
            return Response.ok("{\n\"success\":{\"dept_id\":\""+ updatedDept.getId() + "\",\n\"company\":\""+updatedDept.getCompany()+"\",\n\"dept_name\":\""+updatedDept.getDeptName()+"\",\n\"dept_no\":\""+updatedDept.getDeptNo()+"\",\n\"location\":\""+updatedDept.getLocation()+"\"}\n}").build();
            }
            else{
                return Response.ok("{\"error\":\"Update Failed. Dept no must not be repeated.\"}\n").build();
            }
            
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
      
       
    }

    @Path("department")
    @POST
    @Produces("application/json")
    
    public Response postDepartment(@FormParam("company") String company, @FormParam("dept_name") String dept_name
    ,@FormParam("location") String location, @FormParam("dept_no") String dept_no) {
        try{
            BusinessLayer bl = new BusinessLayer();
            dl = new DataLayer(company);
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            int isDept = bl.checkDeptNo(dept_no, company);

            if(isDept == 1){
                return Response.ok("{\"error\":\"Insert Failed. Dept no already exists.\"}\n").build();
            }
            
            Department newDept = new Department(company, dept_name, dept_no, location);

            Department insertedDept = dl.insertDepartment(newDept);
            if(insertedDept.getId() !=0){
            return Response.ok("{\n\"success\":{\"dept_id\":\""+ insertedDept.getId() + "\",\n\"company\":\""+insertedDept.getCompany()+"\",\n\"dept_name\":\""+insertedDept.getDeptName()+"\",\n\"dept_no\":\""+insertedDept.getDeptNo()+"\",\n\"location\":\""+insertedDept.getLocation()+"\"}\n}").build();
            }
            else{
            return Response.ok("{\"error\":\"Insert Failed. Dept no must not be repeated.\"}\n").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
    }

    @Path("department")
    @DELETE
    @Produces("application/json")
    
    public Response deleteDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int dept_id) {
        try{
            dl = new DataLayer(company);
            int res = dl.deleteDepartment(company, dept_id);

            if(res == 0){
                return Response.ok("{\"error\":\"Delete Failed. No Department found for the ID\"}").build();
            }
            else{
                return Response.ok("{\"success\":\"Department "+ dept_id + " from "+ company+" deleted.\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();

        } finally{
            dl.close();
        }
       
    }

    @Path("employee")
    @GET
    @Produces("application/json")
    
    public Response getEmployee(@QueryParam("company") String company, @QueryParam("emp_id") int emp_id) {
        try{
            dl = new DataLayer(company);
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            Employee emp = dl.getEmployee(emp_id);

            if(emp != null){
                return Response.ok("{\"emp_id\":\""+ emp.getId() + "\",\n\"emp_name\":\""+emp.getEmpName()+"\",\n\"emp_no\":\""+emp.getEmpNo()+"\",\n\"hire_date\":\""+emp.getHireDate()+"\",\n\"job\":\""+emp.getJob()+"\",\n\"salary\":\""+emp.getSalary()+"\",\n\"dept_id\":\""+emp.getDeptId()+"\",\n\"mng_id\":\""+emp.getMngId()+"\"}").build();
            }else{
                return Response.ok("{\"error\":\" No Employee found for the given emp_id\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();

        } finally{
            dl.close();
        }
       
    }

    @Path("employees")
    @GET
    @Produces("application/json")
    
    public Response getEmployees(@QueryParam("company") String company) {
        try{
            dl = new DataLayer(company);
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            List<Employee> emps = dl.getAllEmployee(company);
            List<String> resultJson = new ArrayList<String>();
            
            if(emps != null){
            for (Employee e: emps){
                  resultJson.add("{\"emp_id\":\""+ e.getId() + "\",\n\"emp_name\":\""+e.getEmpName()+"\",\n\"emp_no\":\""+e.getEmpNo()+"\",\n\"hire_date\":\""+e.getHireDate()+"\",\n\"job\":\""+e.getJob()+"\",\n\"salary\":\""+e.getSalary()+"\",\n\"dept_id\":\""+e.getDeptId()+"\",\n\"mng_id\":\""+e.getMngId()+"\"}");
            }
                return Response.ok(resultJson.toString()).build();
            }
            else{
                return Response.ok("{\"error\":\" No Employee found for the given company\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
       
    }


    @Path("employee")
    @POST
    @Produces("application/json")
    
    public Response postEmployee(@FormParam("company") String company, @FormParam("emp_name") String emp_name
    ,@FormParam("emp_no") String emp_no, @FormParam("hire_date") String hire_date, @FormParam("job") String job, @FormParam("salary") Double salary, 
    @FormParam("dept_id") int dept_id,@DefaultValue("0") @FormParam("mng_id") int mng_id) {
        try{
            BusinessLayer bl = new BusinessLayer();
            dl = new DataLayer(company);
           
            int dept = bl.checkDeptId(dept_id, company);
            int mng = bl.checkMngId(mng_id, company);
            int isEmp = bl.checkEmpNo(emp_no, company);
            int hire = bl.checkHireDate(hire_date);
            Boolean comp = bl.checkCompany(company);
            if(isEmp == 1){
                return Response.ok("{\"error\":\" emp_no must be unique.\"}\n").build();
            }

            if(!comp){
                return Response.ok("{\"error\":\" Company is wrong\"}\n").build();
            }
            if(mng ==0){
                return Response.ok("{\"error\":\" No Manager found with emp_id = \""+mng_id+"\"}\n").build();
            }
            if(dept == 0){
                return Response.ok("{\"error\":\" No Department found with ID=\""+dept_id+"\"}\n").build();
            }
            if(hire == 0){
                return Response.ok("{\"error\":\" Hire date must be valid date equal to current or earlier date and must not be saturday or sunday Department.\"}\n").build();
            }
            java.util.Date hdate = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
            java.sql.Date hDate = new java.sql.Date(hdate.getTime());

            Employee newEmployee = new Employee(emp_name, emp_no, hDate, job, salary, dept_id, mng_id);
            Employee insertedEmployee  = dl.insertEmployee(newEmployee);
        
            if(insertedEmployee != null){
                return Response.ok("{\"emp_id\":\""+ insertedEmployee.getId() + "\",\n\"emp_name\":\""+insertedEmployee.getEmpName()+"\",\n\"emp_no\":\""+insertedEmployee.getEmpNo()+"\",\n\"hire_date\":\""+insertedEmployee.getHireDate()+"\",\n\"job\":\""+insertedEmployee.getJob()+"\",\n\"salary\":\""+insertedEmployee.getSalary()+"\",\n\"dept_id\":\""+insertedEmployee.getDeptId()+"\",\n\"mng_id\":\""+insertedEmployee.getMngId()+"\"}").build();
            }
            else{
                return Response.ok("{\"error\":\" Insert Failed. Try Again after sometime.\"}\n").build();
            }
            
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
    }

    @Path("employee")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    
    public Response putEmployee(String updateEmp) {
        try{
            
            BusinessLayer bl = new BusinessLayer();
            
            JsonReader rdr = Json.createReader(new StringReader(updateEmp));
            JsonObject obj = rdr.readObject();
            String company = obj.getString("company");
            String emp_name = obj.getString("emp_name");
            String emp_no = obj.getString("emp_no");
            String hire_date = obj.getString("hire_date");
            String job = obj.getString("job");
            Double salary = Double.valueOf(obj.getInt("salary"));
            int dept_id = obj.getInt("dept_id");
            int emp_id = obj.getInt("emp_id");
            int mng_id = obj.getInt("mng_id");

            dl = new DataLayer(company);

            int isEmp = bl.checkEmpNo(emp_no, company);
            Boolean comp = bl.checkCompany(company);
            Integer dCheck = bl.checkDeptId(dept_id, company);
            Integer mCheck = bl.checkMngId(mng_id, company);
            Integer hCheck = bl.checkHireDate(hire_date);
            Integer eCheck = bl.checkMngId(emp_id, company);

            if(isEmp == 1){
                return Response.ok("{\"error\":\" emp_no must be unique.\"}\n").build();
            }
            if(!comp){
                return Response.ok("{\"error\":\" Company is wrong\"}\n").build();
            }
            if(dCheck == 0){
                return Response.ok("{\"error\":\" No Department found with ID=\""+dept_id+". Check input.\"}\n").build();
            }
            if(mCheck == 0){
                return Response.ok("{\"error\":\" No Manager found with ID =\""+mng_id+"\"}\n").build();
            }
            if(hCheck == 0){
                return Response.ok("{\"error\":\" Hire date must be valid date equal to current or earlier date and must not be saturday or sunday Department.\"}\n").build();
            }
            if(eCheck == 0){
                return Response.ok("{\"error\":\" No Employee found with ID =\""+emp_id+"\"}\n").build();
            }
            
            java.util.Date hdate = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
            java.sql.Date hDate = new java.sql.Date(hdate.getTime());
            // DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            // String dates = df.format(hDate);
            
            Employee e = dl.getEmployee(emp_id);
            e.setDeptId(dept_id);
            e.setEmpName(emp_name);
            e.setEmpNo(emp_no);
            e.setHireDate(hDate);
            e.setJob(job);
            e.setMngId(mng_id);
            e.setSalary(salary);
            Employee updatedEmployee = dl.updateEmployee(e);
            
            if(updatedEmployee != null){
                return Response.ok("{\n\"success\":{\"emp_id\":\""+ updatedEmployee.getId() + "\",\n\"emp_name\":\""+updatedEmployee.getEmpName()+"\",\n\"emp_no\":\""+updatedEmployee.getEmpNo()+"\",\n\"hire_date\":\""+updatedEmployee.getHireDate()+"\",\n\"job\":\""+updatedEmployee.getJob()+"\",\n\"salary\":\""+updatedEmployee.getSalary()+"\",\n\"dept_id\":\""+updatedEmployee.getDeptId()+"\",\n\"mng_id\":\""+updatedEmployee.getMngId()+"\"}\n}").build();
            }
            else{
                return Response.ok("{\"error\":\"Update Failed. Try again.\"}\n").build();
            }


        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}\n").build();

        } finally{
            dl.close();
        }
    }

    @Path("employee")
    @DELETE
    @Produces("application/json")
    
    public Response deleteEmployee(@QueryParam("company") String company, @QueryParam("emp_id") int emp_id) {
        try{
            dl = new DataLayer(company);
            int res = dl.deleteEmployee(emp_id);

            if(res == 0){
                return Response.ok("{\"error\":\"Delete Failed. No Employee found for the ID\"}").build();
            }
            else{
                return Response.ok("{\"success\":\"Employee "+ emp_id + "  deleted.\"}").build();
            }
        } catch (Exception e){
                return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally{
            dl.close();
        }
       
    }

    @Path("timecard")
    @GET
    @Produces("application/json")

    public Response getTimecard(@QueryParam("company") String company, @QueryParam("timecard_id") int timecard_id){

        try{

            dl = new DataLayer(company);
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            Timecard res = dl.getTimecard(timecard_id);

            if(res != null){
            return Response.ok("{\"timecard_id\":\""+ res.getId() + "\",\n\"start_time\":\""+res.getStartTime()+"\",\n\"end_time\":\""+res.getEndTime()+"\",\n\"emp_id\":\""+res.getEmpId()+"\"}").build();
            }
            else{
                return Response.ok("{\"error\":\"No Timecard found for the ID\"}").build();
            }
        } catch(Exception e){
            return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally {
            dl.close(); 
        }
    }

    @Path("timecards")
    @GET
    @Produces("application/json")

    public Response getTimecards(@QueryParam("company") String company, @QueryParam("emp_id") int emp_id){

        try{

            dl = new DataLayer(company);
            BusinessLayer bl = new BusinessLayer();
            Boolean isComp = bl.checkCompany(company);
            if(!isComp){
                return Response.ok("{\"error\":\"Company name is wrong.\"}").build();
            }
            List<Timecard> res = dl.getAllTimecard(emp_id);
            List<String> resultJson = new ArrayList<String>();
            
            if(res.size() > 0){
            for (Timecard t: res){
                  resultJson.add("{\"timecard_id\":\""+ t.getId() + "\",\n\"start_time\":\""+t.getStartTime()+"\",\n\"end_name\":\""+t.getEndTime()+"\",\n\"emp_id\":\""+t.getEmpId()+"\"}");
            } 
            return Response.ok(resultJson.toString()).build();
            }
            else{
                return Response.ok("{\"error\":\"No Timecard found for the employee.\"}").build();
            }
        } catch(Exception e){
            return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally {
            dl.close(); 
        }
    }

    @Path("timecard")
    @POST
    @Produces("application/json")

    public Response postTimecard(@FormParam("company") String company, @FormParam("emp_id") int emp_id
    ,@FormParam("start_time") String start_time, @FormParam("end_time") String end_time){
        try{
            BusinessLayer bl = new BusinessLayer();
            dl = new DataLayer(company);

            Boolean comp = bl.checkCompany(company);
            Integer emp = bl.checkMngId(emp_id, company);
            String res = bl.checkTimestamp(start_time, end_time, company, emp_id, 0);
            

            if(!comp){
                return Response.ok("{\"error\":\" Company is wrong\"}\n").build();
            }
            if(emp == 0){
                return Response.ok("{\"error\":\" No Employee found for this Id\"}\n").build();
            }
            if(res.length() > 0){
                return Response.ok("{\"error\":\""+ res + "\"}").build();
            }
            Timestamp s_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time).getTime());
            Timestamp e_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time).getTime());
            Timecard timeC = new Timecard(s_time, e_time, emp_id);
            Timecard newTime = dl.insertTimecard(timeC);
            
            if(newTime != null){
            return Response.ok("{\n\"success\":{\"timecard_id\":\""+ newTime.getId() + "\",\n\"start_time\":\""+newTime.getStartTime()+"\",\n\"end_time\":\""+newTime.getEndTime()+"\",\n\"emp_id\":\""+newTime.getEmpId()+"\"}\n}").build();
            }
            else{
                return Response.ok("{\"error\":\"Insert Failed. Try Again.\"}").build();
            }
        } catch(Exception e){
            return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally {
            dl.close(); 
        }
    }


    @Path("timecard")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")

    public Response putTimecard(String updateTime){
        try{
            BusinessLayer bl = new BusinessLayer();

            JsonReader rdr = Json.createReader(new StringReader(updateTime));
            JsonObject obj = rdr.readObject();
            String company = obj.getString("company");
            int timecard_id = obj.getInt("timecard_id");
            int emp_id = obj.getInt("emp_id");
            String start_time = obj.getString("start_time");
            String end_time = obj.getString("end_time");
            

            dl = new DataLayer(company);

            Boolean comp = bl.checkCompany(company);
            Integer emp = bl.checkMngId(emp_id, company);
            String res = bl.checkTimestamp(start_time, end_time, company, emp_id, timecard_id);
            Integer tcId = bl.checkTimeId(timecard_id, company);
            

            if(!comp){
                return Response.ok("{\"error\":\" Company is wrong\"}\n").build();
            }
            if(emp == 0){
                return Response.ok("{\"error\":\" No Employee found for this Id\"}\n").build();
            }
            if(res.length() > 0){
            return Response.ok("{\"error\":\""+ res + "\"}").build();
            }
            if(tcId == 0){
                return Response.ok("{\"error\":\" No Timecard found for this Id\"}\n").build();
            }
            Timestamp s_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time).getTime());
            Timestamp e_time = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time).getTime());
            
            Timecard t = dl.getTimecard(timecard_id);
            t.setEmpId(emp_id);
            t.setEndTime(e_time);
            t.setStartTime(s_time);
            Timecard updatedTimecard = dl.updateTimecard(t);

            if(updateTime != null){
            
            return Response.ok("{\n\"success\":{\"timecard_id\":\""+ updatedTimecard.getId() + "\",\n\"start_time\":\""+updatedTimecard.getStartTime()+"\",\n\"end_time\":\""+updatedTimecard.getEndTime()+"\",\n\"emp_id\":\""+updatedTimecard.getEmpId()+"\"}\n}").build();
            }
            else{
                return Response.ok("{\"error\":\" Update Failed\"}\n").build();
            }

        } catch(Exception e){
            return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally {
            dl.close(); 
        }
    }

    @Path("timecard")
    @DELETE
    @Produces("application/json")

    public Response deleteTimecard(@QueryParam("company") String company, @QueryParam("timecard_id") int timecard_id){
        try{
            dl = new DataLayer(company);
            int res = dl.deleteTimecard(timecard_id);

            if(res == 0){
                return Response.ok("{\"error\":\"No Timecard found for the given Id\"}").build();
            }
            else{
                return Response.ok("{\"success\":\"Timecard "+timecard_id+" Deleted\"}\n").build();
            }           
        } catch(Exception e){
            return Response.ok("{\"error\":\""+ e.getMessage() + "\"}").build();
        } finally {
            dl.close();
        }
    }


}
