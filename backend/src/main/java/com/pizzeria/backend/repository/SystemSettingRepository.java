package com.pizzeria.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pizzeria.backend.entity.SystemSetting;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
}
