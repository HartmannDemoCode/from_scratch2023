package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.EmployeeDao;
import dk.cphbusiness.daos.IDAO;
import dk.cphbusiness.dtos.EmployeeDTO;
import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.errorHandling.ApiException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmployeeHandler implements IController{
    private static IDAO<Employee> dao;
    private static EmployeeHandler employeeHandler;

    public static EmployeeHandler getHandler() {
        if(dao==null){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        dao = EmployeeDao.getEmployeeDao(emf);
        }
        if(employeeHandler == null)
            employeeHandler = new EmployeeHandler();
        return employeeHandler;
    }
    // Private constructor to ensure Singleton
    private EmployeeHandler(){}

    @Override
    public Handler getAll() {
        return context -> {
            List<Employee> employees = dao.getAll();
            context.json(employees);
        };
    }
// get employee by id
    @Override
    public Handler getById() throws EntityNotFoundException {
        return new Handler(){
            @Override
            public void handle(Context ctx) throws Exception {
                String id = ctx.pathParam("id");
                Employee employee = dao.getById(id);
                ctx.json(employee);
            }
        };
    }
// create employee
    @Override
    public Handler create() {
        return new Handler() {
            @Override
            public void handle(Context context) throws Exception {
                EmployeeDTO employeeDTO = getValidatedDTO(context);
                Employee employee = employeeDTO.asEntity();
                Employee createdEmployee = null;
                try {
                    createdEmployee = dao.create(employee);
                } catch (Exception e) {
                    context.status(404).json(new ApiException(404, e.getMessage()));
                }
                context.json(new EmployeeDTO(createdEmployee));
            }
        };
    }
    // update employee
    @Override
    public Handler update(){
        return new Handler() {
            @Override
            public void handle(Context context) throws Exception {
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
    };
    }
    // delete employee
    @Override
    public Handler delete(){
        return new Handler() {
            @Override
            public void handle(@NotNull Context context) throws Exception {
                String validatedId = context
                        .pathParamAsClass("id", String.class)
                        .check(pathParamId -> dao.validateId(pathParamId), "No Employee with provided id found")
                        .get();
                Long id = Long.parseLong(validatedId);
                Employee deletedEmployee = dao.delete(id);
                context.json(new EmployeeDTO(deletedEmployee));
            }
        };
    }

    private EmployeeDTO getValidatedDTO(Context ctx) {
        return ctx.bodyValidator(EmployeeDTO.class)
                .check(emp -> emp.getFirstName().length() > 0, "First name must be longer than 0 characters")
                .check(emp -> emp.getLastName().length() > 0, "Last name must be longer than 0 characters")
                .check(emp -> emp.getEmail().matches("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$"), "Email must be valid")
                .get();
    }
}
