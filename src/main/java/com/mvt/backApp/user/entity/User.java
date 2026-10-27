package com.mvt.backApp.user.entity;
import com.mvt.backApp.common.entity.BaseAuditEntity;import jakarta.persistence.*;import lombok.*;import java.util.*;
@Getter @Setter @Entity @Table(name="users") public class User extends BaseAuditEntity {
 @Column(nullable=false,unique=true,length=320) private String email;@Column(name="password_hash",nullable=false) private String passwordHash;
 @Column(name="first_name",nullable=false,length=100) private String firstName;@Column(name="last_name",nullable=false,length=100) private String lastName;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Gender gender;@Column(name="email_verified",nullable=false) private boolean emailVerified;
 @Column(name="must_change_password",nullable=false) private boolean mustChangePassword;@ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="role_id") private Role role;
 @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="user_permissions",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="permission_id")) private Set<Permission> directPermissions=new HashSet<>(); }
