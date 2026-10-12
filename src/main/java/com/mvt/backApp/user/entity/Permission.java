package com.mvt.backApp.user.entity;

import com.mvt.backApp.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission extends BaseAuditEntity {

    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;
}
