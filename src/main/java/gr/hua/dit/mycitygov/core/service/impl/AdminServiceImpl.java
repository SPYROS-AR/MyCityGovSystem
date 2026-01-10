package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestType;
import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.model.CreateRequestTypeRequest;
import gr.hua.dit.mycitygov.core.service.model.SystemStatistics;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final RequestRepository requestRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;

    public AdminServiceImpl(RequestRepository requestRepository,
                            DepartmentRepository departmentRepository,
                            RequestTypeRepository requestTypeRepository,
                            CitizenRepository citizenRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.departmentRepository = departmentRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.citizenRepository = citizenRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public SystemStatistics getSystemStatistics() {

        long totalRequests = requestRepository.count();

        // Total citizens
        long totalCitizens = citizenRepository.count();


        List<Request> allRequests = requestRepository.findAll();

        // outstanding requests (SUBMITTED, RECEIVED, PROCESSING, PENDING_DOCS)
        long pendingRequests = allRequests.stream()
                .filter(r -> r.getStatus() == Request.Status.SUBMITTED ||
                        r.getStatus() == Request.Status.RECEIVED ||
                        r.getStatus() == Request.Status.PROCESSING ||
                        r.getStatus() == Request.Status.PENDING_DOCS)
                .count();

        // Completed requests (COMPLETED)
        long completedRequests = allRequests.stream()
                .filter(r -> r.getStatus() == Request.Status.COMPLETED)
                .count();

        // Requests per department (Map<Department name, count>)
        Map<String, Long> requestsByDepartment = allRequests.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDepartment().getName(),
                        Collectors.counting()
                ));

        // Overdue requests
        long overdueRequests = allRequests.stream()
                .filter(r -> r.getDueDate() != null &&
                        r.getDueDate().isBefore(LocalDateTime.now()) &&
                        r.getStatus() != Request.Status.COMPLETED &&
                        r.getStatus() != Request.Status.REJECTED)
                .count();


        SystemStatistics statistics = new SystemStatistics(totalRequests, pendingRequests, completedRequests, totalCitizens, requestsByDepartment, overdueRequests);
        return statistics;
    }

    @Override
    public List<RequestType> getAllRequestTypes() {
        // Return items by ascending order
        return requestTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public List<Department> getAllDepartments(){
        return  departmentRepository.findAll();
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void createRequestType(CreateRequestTypeRequest request) {

        // Find department
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.departmentId()));

        // Create entity
        RequestType requestType = new RequestType();
        requestType.setName(request.name());
        requestType.setRequestCategory(request.requestCategory());
        requestType.setSlaDays(request.slaDays());
        requestType.setDepartment(department);
        requestType.setActive(true);

        // Save through repository
        this.requestTypeRepository.save(requestType);
    }

    @Override
    @Transactional
    public void toggleRequestTypeStatus(Long id) {
        RequestType type = requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        type.setActive(!type.isActive()); // Toggle state
        requestTypeRepository.save(type);
    }

    @Override
    @Transactional
    public void reassignRequestType(Long id, Long newDepartmentId) {
        RequestType type = requestTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Department newDept = departmentRepository.findById(newDepartmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        type.setDepartment(newDept);
        requestTypeRepository.save(type);
    }

}
