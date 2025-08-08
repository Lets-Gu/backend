package avengers.lion.wallet.repository;

import avengers.lion.wallet.domain.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    List<PointTransaction> findAllByMemberId(Long memberId);

}
