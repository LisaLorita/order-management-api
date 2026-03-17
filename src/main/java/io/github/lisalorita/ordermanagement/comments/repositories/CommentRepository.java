package io.github.lisalorita.ordermanagement.comments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.lisalorita.ordermanagement.comments.entities.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
