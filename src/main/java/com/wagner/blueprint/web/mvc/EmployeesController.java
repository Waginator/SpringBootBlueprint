package com.wagner.blueprint.web.mvc;

import com.wagner.blueprint.config.Endpoints;
import com.wagner.blueprint.config.ViewNames;
import com.wagner.blueprint.service.EmployeeService;
import com.wagner.blueprint.web.dto.EmployeeDto;
import com.wagner.blueprint.web.dto.request.EmployeeRequestDto;
import com.wagner.blueprint.web.dto.response.PaginationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeesController {

  private final EmployeeService employeeService;
  private static final int ITEMS_PER_PAGE = 2;

  @Autowired
  public EmployeesController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @GetMapping(Endpoints.EMPLOYEES_LIST)
  public ModelAndView showEmployeesList(@RequestParam(name = "page", defaultValue = "1") int page) {
    List<EmployeeDto> employeeDtos = employeeService.findAll(page, ITEMS_PER_PAGE);

    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("employees", employeeDtos);
    viewModel.put("createUrl", Endpoints.EMPLOYEES_CREATE);
    viewModel.put("pagination", createPaginationResponse(page));

    return new ModelAndView(ViewNames.EMPLOYEES_LIST, viewModel);
  }

  @GetMapping(Endpoints.EMPLOYEES_CREATE)
  public ModelAndView showCreateEmployeeForm() {
    Map<String, Object> viewModel = new HashMap<>();
    viewModel.put("createUrl", Endpoints.EMPLOYEES_CREATE);

    return new ModelAndView(ViewNames.EMPLOYEES_CREATE, viewModel);
  }

  @PostMapping(Endpoints.EMPLOYEES_CREATE)
  public ModelAndView createEmployee(@Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // show create form again if there are any errors
      return new ModelAndView(ViewNames.EMPLOYEES_CREATE);
    }

    employeeService.create(employeeRequestDto);

    return new ModelAndView("redirect:" + Endpoints.EMPLOYEES_LIST);
  }

  @GetMapping(Endpoints.EMPLOYEES_EDIT)
  public ModelAndView showEditEmployeeForm(@PathVariable(name = "id") long id) {
    return new ModelAndView(ViewNames.EMPLOYEES_EDIT);
  }

  @ModelAttribute(name = "employeeRequestDto")
  public EmployeeRequestDto employeeRequest() {
    return new EmployeeRequestDto();
  }

  private PaginationResponseDto createPaginationResponse(int currentPage) {
    long employeeCount = employeeService.getCount();
    int numberOfPages  = (int) Math.ceil((double) employeeCount / (double) ITEMS_PER_PAGE);

    return PaginationResponseDto.builder()
            .numberOfPages(numberOfPages)
            .currentPage(currentPage)
            .itemsPerPage(ITEMS_PER_PAGE)
            .build();
  }

}
