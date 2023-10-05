package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.config.HibernateConfig;
import dk.cphbusiness.daos.EmployeeDao;
import dk.cphbusiness.daos.IDAO;
import dk.cphbusiness.dtos.EmployeeDTO;
import dk.cphbusiness.entities.EmployeeEntity;
import dk.cphbusiness.errorHandling.ApiException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmployeeController implements IController{
    private static IDAO<EmployeeEntity> employeeDAO;
    private static EmployeeController employeeHandler;

    public static EmployeeController getHandler() {
        if(employeeDAO ==null){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        employeeDAO = EmployeeDao.getEmployeeDao(emf);
        }
        if(employeeHandler == null)
            employeeHandler = new EmployeeController();
        return employeeHandler;
    }
    // Private constructor to ensure Singleton
    private EmployeeController(){}

    @Override
    public Handler getAll() {
        return context -> {
            List<EmployeeEntity> employeeEntities = employeeDAO.getAll();
            context.json(employeeEntities);
        };
    }
// get employee by id
    @Override
    public Handler getById() throws EntityNotFoundException {
        return new Handler(){
            @Override
            public void handle(Context ctx) throws Exception {
                String id = ctx.pathParam("id");
                EmployeeEntity employeeEntity = employeeDAO.getById(id);
                ctx.json(employeeEntity);
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
                EmployeeEntity employeeEntity = employeeDTO.asEntity();
                EmployeeEntity createdEmployeeEntity = null;
                try {
                    createdEmployeeEntity = employeeDAO.create(employeeEntity);
                } catch (Exception e) {
                    context.status(404).json(new ApiException(404, e.getMessage()));
                }
                context.json(new EmployeeDTO(createdEmployeeEntity));
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
                        .check(pathParamId -> employeeDAO.validateId(pathParamId), "No EmployeeEntity with provided id found")
                        .get();
                Long id = Long.parseLong(validatedId);
                EmployeeEntity employeeEntity = getValidatedDTO(context).asEntity();
                employeeEntity.setId(id);
                EmployeeEntity updatedEmployeeEntity = employeeDAO.update(employeeEntity);
                context.json(new EmployeeDTO(updatedEmployeeEntity));
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
                        .check(pathParamId -> employeeDAO.validateId(pathParamId), "No EmployeeEntity with provided id found")
                        .get();
                EmployeeEntity deletedEmployeeEntity = employeeDAO.delete(validatedId);
                context.json(new EmployeeDTO(deletedEmployeeEntity));
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
