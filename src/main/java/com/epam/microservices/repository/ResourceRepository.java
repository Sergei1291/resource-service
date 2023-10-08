package com.epam.microservices.repository;

import com.epam.microservices.entity.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Long> {

}
