package web.application.development.team;

import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, String> { //implements methods common for all repositories (i.e. getEntity, deleteEntity, etc), <type of entity, type of ID>
	Team findByName(String name);
}
