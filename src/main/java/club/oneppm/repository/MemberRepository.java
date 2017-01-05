package club.oneppm.repository;

import club.oneppm.domain.Member;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Member entity.
 */
@SuppressWarnings("unused")
public interface MemberRepository extends JpaRepository<Member,Long> {

}
