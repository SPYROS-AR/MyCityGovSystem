package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.mapper.DepartmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.DepartmentScheduleMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestTypeMapper;
import gr.hua.dit.mycitygov.core.service.mapper.UserMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
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
    private final RequestTypeMapper requestTypeMapper;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentScheduleRepository scheduleRepository;
    private final DepartmentScheduleMapper scheduleMapper;
    private final RequestTypeRepository requestTypeRepository;
    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminServiceImpl(RequestRepository requestRepository,
                            RequestTypeMapper requestTypeMapper,
                            DepartmentRepository departmentRepository,
                            DepartmentMapper departmentMapper,
                            DepartmentScheduleRepository scheduleRepository,
                            DepartmentScheduleMapper scheduleMapper,
                            RequestTypeRepository requestTypeRepository,
                            CitizenRepository citizenRepository, UserRepository userRepository,
                            UserMapper userMapper) {
        this.requestRepository = requestRepository;
        this.requestTypeMapper = requestTypeMapper;
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.requestTypeRepository = requestTypeRepository;
        this.citizenRepository = citizenRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
    public List<RequestTypeView> getAllRequestTypes() {
        // Return items by ascending order

        List<RequestType> allRequestTypes = requestTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return allRequestTypes
                .stream()
                .map(requestTypeMapper::toDto) // Μετατροπή
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentView> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserView> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto) //
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentScheduleView> getDepartmentSchedule(Long departmentId) {
        return scheduleRepository.findByDepartmentId(departmentId)
                .stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDepartmentSchedule(Long departmentId, java.time.DayOfWeek day, java.time.LocalTime start, java.time.LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time (" + start + ") must be before end time (" + end + ")");
        }
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        DepartmentSchedule schedule = scheduleRepository.findByDepartmentIdAndDayOfWeek(departmentId, day)
                .orElse(new DepartmentSchedule(dept, day, start, end));

        schedule.setStartTime(start);
        schedule.setEndTime(end);

        scheduleRepository.save(schedule);
    }


    @Override
    @Transactional
    public void updateDepartmentInfo(Long id, String name, String description) {
        Department dept = getDepartmentById(id);
        dept.setName(name);
        dept.setDescription(description);
        departmentRepository.save(dept);
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
