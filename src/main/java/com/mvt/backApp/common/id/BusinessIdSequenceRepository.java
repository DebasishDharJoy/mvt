package com.mvt.backApp.common.id;
import jakarta.persistence.LockModeType;import org.springframework.data.jpa.repository.*;import java.util.Optional;
public interface BusinessIdSequenceRepository extends JpaRepository<BusinessIdSequence,BusinessIdKind>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from BusinessIdSequence s where s.kind=:kind")
 Optional<BusinessIdSequence> findForUpdate(BusinessIdKind kind); }
