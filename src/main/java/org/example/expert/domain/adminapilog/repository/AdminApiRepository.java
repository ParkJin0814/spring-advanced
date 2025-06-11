package org.example.expert.domain.adminapilog.repository;

import org.example.expert.domain.adminapilog.entity.AdminApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminApiRepository extends JpaRepository<AdminApiLog, Long> {
}
