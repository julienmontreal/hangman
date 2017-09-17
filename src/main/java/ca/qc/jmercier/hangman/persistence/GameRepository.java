package ca.qc.jmercier.hangman.persistence;

import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity, Integer> {
}