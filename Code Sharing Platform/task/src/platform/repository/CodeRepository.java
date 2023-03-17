package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.entity.Code;

public interface CodeRepository extends JpaRepository<Code, String> {

}
