package graduation_project.Dailic.repository;

import graduation_project.Dailic.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
