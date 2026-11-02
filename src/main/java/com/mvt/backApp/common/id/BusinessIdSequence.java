package com.mvt.backApp.common.id;
import jakarta.persistence.*;import lombok.*;
@Getter @Setter @Entity @Table(name="business_id_sequences")
public class BusinessIdSequence { @Id @Enumerated(EnumType.STRING) private BusinessIdKind kind;
 @Column(name="next_value",nullable=false) private long nextValue; }
