package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.EmployeeDao;
import dk.cphbusiness.daos.IDAO;
import dk.cphbusiness.dtos.EmployeeDTO;
import dk.cphbusiness.entities.Employee;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class EmployeeHandler {
    private IDAO<Employee> dao;
    public EmployeeHandler getEmployeeHandler() {
        if(dao==null){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        dao = EmployeeDao.getEmployeeDao(emf);
        }
        return this;
    }
    // Private constructor to ensure Singleton
    private EmployeeHandler(){}

    public void getAllEmployees(Context context) {
        List<Employee> employees = dao.getAll();
        context.json(employees);
    }
// get employee by id
    public void getEmployeeById(Context context) {
        String id = context.pathParam("id");
        Employee employee = dao.getById(id);
        context.json(employee);
    }
// create employee
    public void createEmployee(Context context) throws Exception {
        EmployeeDTO employeeDTO = getValidatedDTO(context);
        Employee employee = employeeDTO.asEntity();
//        Employee employee = context.bodyAsClass(Employee.class);
        Employee createdEmployee = dao.create(employee);
        context.json(new EmployeeDTO(createdEmployee));
    }
    // update employee
    public void updateEmployee(Context context){
        String validatedId = context
                .pathParamAsClass("id", String.class)
                .check(pathParamId -> dao.validateId(pathParamId), "No Employee with provided id found")
                .get();
        Long id = Long.parseLong(validatedId);
        Employee employee = getValidatedDTO(context).asEntity();
        employee.setId(id);
        Employee updatedEmployee = dao.update(employee);
        context.json(new EmployeeDTO(updatedEmployee));
    }
    // delete employee
    public void deleteEmployee(Context context){
        String validatedId = context
                .pathParamAsClass("id", String.class)
                .check(pathParamId -> dao.validateId(pathParamId), "No Employee with provided id found")
                .get();
        Long id = Long.parseLong(validatedId);
        Employee deletedEmployee = dao.delete(id);
        context.json(new EmployeeDTO(deletedEmployee));
    }

    private EmployeeDTO getValidatedDTO(Context ctx) {
        return ctx.bodyValidator(EmployeeDTO.class)
//                .check(p -> p.getAge() > 0 && p.getAge() < 120, "Age must be between 0 and 120")
                .check(p -> p.getFirstName().length() > 0, "First name must be longer than 0 characters")
                .check(p -> p.getLastName().length() > 0, "Last name must be longer than 0 characters")
                .check(p -> p.getEmail().matches("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$"), "Email must be valid")
                .get();
    }
}
