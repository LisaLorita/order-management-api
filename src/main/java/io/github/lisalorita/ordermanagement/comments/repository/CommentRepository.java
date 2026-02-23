package io.github.lisalorita.ordermanagement.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.lisalorita.ordermanagement.comments.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
