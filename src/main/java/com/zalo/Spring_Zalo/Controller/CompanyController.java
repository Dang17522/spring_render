package com.zalo.Spring_Zalo.Controller;

import com.zalo.Spring_Zalo.DTO.CompanyDto;
import com.zalo.Spring_Zalo.Entities.Company;
import com.zalo.Spring_Zalo.Exception.ResourceNotFoundException;
import com.zalo.Spring_Zalo.Repo.CompanyRepo;
import com.zalo.Spring_Zalo.Response.ApiResponse;
import com.zalo.Spring_Zalo.Service.CompanyService;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/companys")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyRepo companyRepo;
    @PostMapping(value = "/",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto dto){
        Company company = mapToClass(dto);
        Company createCompay = companyRepo.save(company);
        return new ResponseEntity<>(createCompay, HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Integer companyId){
        Company company = companyRepo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company","companyId",companyId));
        return new ResponseEntity<>(company, HttpStatus.OK);
    }
    @PutMapping("/{companyId}")
    public ResponseEntity<Company> updateCompany(@RequestBody CompanyDto company,@PathVariable Integer companyId){
        Company updateCompany = companyService.updateCompany(company,companyId);
        return new ResponseEntity<>(updateCompany, HttpStatus.OK);
    }
   @GetMapping("/")
    public ResponseEntity<Page<Company>> getListcompanyWithPagination(@RequestParam(defaultValue = "5") int pageSize,
                                                                      @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Company> companiesPage = companyRepo.findAll(pageable);

        return ResponseEntity.ok(companiesPage);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllData() {
       List<Company> list = companyRepo.findAll();
        return ResponseEntity.ok(list);
    }

    private Company mapToClass (CompanyDto company){
        return Company.builder()
                .name(company.getName())
                .phoneNumber(company.getPhoneNumber())
                .email(company.getEmail())
                .address(company.getAddress())
                .nameManager(company.getNameManager())
                .build();
    }

    @DeleteMapping("/delete/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable Integer companyId){
        Company company = companyRepo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company","companyId",companyId));
        companyRepo.delete(company);
        ApiResponse apiResponse = new ApiResponse("Delete success",true, HttpStatus.OK.value());
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    
}
